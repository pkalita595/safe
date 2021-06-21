/*******************************************************************************
 Copyright (c) 2016, Oracle and/or its affiliates.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of KAIST, S-Core, Oracle nor the names of its contributors
   may be used to endorse or promote products derived from this software without
   specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.shellneo

import java.io.{BufferedWriter, File, FileWriter}

import kr.ac.kaist.jsaf.analysis.cfg.CFGBuilder
import org.rogach.scallop._
import kr.ac.kaist.jsaf.analysis.typing.{AddressManager, Config, InitHeap, PreConfig, Typing, domain}
import kr.ac.kaist.jsaf.{ProjectProperties, Shell}
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.nodes.{IRRoot, Program}
import kr.ac.kaist.jsaf.nodes_util.{JSFromHTML, NodeUtil}
import kr.ac.kaist.jsaf.scala_src.nodes.{SProgram, STopLevel}
import edu.rice.cs.plt.tuple.{Option => JOption}
import kr.ac.kaist.jsaf.analysis.imprecision.ImprecisionTracker
import kr.ac.kaist.jsaf.analysis.typing.domain.CmdLineStringConfig
import kr.ac.kaist.jsaf.analysis.typing.models.DOMBuilder
import kr.ac.kaist.jsaf.tests.TestHelper
import kr.ac.kaist.jsaf.useful.{MemoryMeasurer, Pair}
import org.cyberneko.html.parsers.DOMParser
import org.w3c.dom.Document
import org.xml.sax.InputSource

import scala.collection.JavaConverters._

private class ShellConf(args: Seq[String]) extends ScallopConf(args) {
  val inputFiles = trailArg[List[String]](required = false, descr = ".js file(s) for analysis")
  val htmlFile = opt[String]("html", descr = "html file for analysis")
  val quiet = opt[Boolean]("quiet", descr = "less debug output")
  val domMode = opt[Boolean]("dom", descr = "dom mode")
  val exitDump = opt[Boolean]("exitdump")
  val cfgDump = opt[Boolean]("cfgdump")
  val heapVerbose = opt[Int]("heap-verbose", validate = (0 until 4) contains _ )
  val trace = opt[Boolean]("trace", descr = "trace output for AI semantics")
  val debugAfter = opt[Int](descr = "start debug after a certain iteration")
  val test = opt[Boolean]("test", descr = "expose abstract types for testing")
  val jquery = opt[Boolean]("jquery", descr = "enable jQuery model")
  val maxStrSet = opt[Int]("max-strset-size", descr = "max string set size", validate = _ > 0, default = Some(1))
  val maxIterations = opt[Int]("max-iter", short = 'l', descr = "stop after n iterations max")
  val timeout = opt[Int]("timeout", descr = "timeout in seconds", validate = _ > 0)
  val stats = opt[Boolean]("stats", descr = "dump full statistics")
  val strdom = opt[String]("strdom", descr = "enable advanced string domains",
  validate = _.split(",").toSet subsetOf Set("co", "ss", "au", "no", "ci", "ps", "sf", "ns", "js", "sh", "hy"))
  val benchmark = opt[Boolean]("benchmark", descr = "benchmark default settings (KAIST-lsa/regex)")
  val noImprecisionLog = opt[Boolean]("no-imprecision-log", descr = "do not ouput imprecision logging")
  val noImprecisionStop = opt[Boolean]("no-imprecision-stop", descr = "do not stop on catastrophic imprecision")
  requireOne(inputFiles, htmlFile)
}

object RunAnalysis {
  def main(args: Array[String]) = {
    val conf = new ShellConf(args)
    conf.verify()

    configure(conf)

    val (program, doc) =
      if (!conf.htmlFile.isSupplied)
        (parseJS(conf.inputFiles()), nullHTML)
      else
        parseHTML(conf.htmlFile())

    analyze(program, doc, conf)
  }

  val nullHTML = {
    val parser: DOMParser = new DOMParser
    parser.setFeature("http://xml.org/sax/features/namespaces", false)
    val str = new String("<html/>")
    parser.parse(new InputSource(new java.io.ByteArrayInputStream(str.getBytes())))
    parser.getDocument
  }

  def configure(conf: ShellConf) = {
    // Initialize AddressManager
    AddressManager.reset()

    // String setup (early)
    if (conf.strdom.isSupplied)
      Shell.params.opt_strdom = conf.strdom()
    else
      Shell.params.opt_strdom = "sf" // default domain
    PreConfig.strings = new CmdLineStringConfig(Shell.params.opt_strdom)

    val firstFile = if (conf.htmlFile.isDefined) conf.htmlFile() else conf.inputFiles().head
    Config.setFileName(firstFile)

    Config.setDefaultForinUnrollingCount(1)
    Config.setLoopSensitiveMode(true)
    Config.setContextSensitivityMode(Config.Context_Loop)
    Config.setContextSensitivityDepth(10)

    Shell.params.opt_MaxStrSetSize =
      if (conf.maxStrSet.supplied)
        conf.maxStrSet() // supplied on command line
      //else if (conf.benchmark())
        //32 // default value for benchmark mode
      else
        conf.maxStrSet()  // default value

    // Initialize AbsString cache
    domain.AbsString.initCache

    if (conf.domMode() || conf.htmlFile.isSupplied) {
      System.out.println("DOM mode enabled.")
      Config.setDomMode
      Shell.params.opt_LocClone = true
      Config.setDOMPropMode
    }

    Shell.params.opt_ExitDump = conf.exitDump()
    if (conf.heapVerbose.isDefined)
      Config.setVerbose(conf.heapVerbose())
    Config.traceAI = conf.trace()
    Config.testMode = conf.test()
    Config.jqMode = conf.jquery()

    if (conf.timeout.isSupplied)
      Shell.params.opt_Timeout = conf.timeout()
    else if (conf.benchmark())
      Shell.params.opt_Timeout = 600 // 10 minute default timeout in benchmark mode

    Config.maxIterations = conf.maxIterations.getOrElse(0)
    Config.startDebugAtIteration = conf.debugAfter.toOption
    
    // imprecision log enabled by default
    conf.noImprecisionLog.orElse(Some(true)).get match {
      case Some(disabled) if disabled => ImprecisionTracker.disableLog
      case _ => ImprecisionTracker.enableLog
    }
    // stop unless disabled
    ImprecisionTracker.stopEnabled = !conf.noImprecisionStop.get.getOrElse(false)
  }

  def parseJS(files: Seq[String]) = {
    if (files.exists(fname => fname.endsWith(".html")))
      throw new UserError("html files not supported")
    Parser.fileToAST(files.asJava)
  }

  def parseHTML(fileName: String): (Program, Document) = {
    val jshtml = new JSFromHTML(fileName)
    (jshtml.parseScripts, jshtml.getDocument())
  }

  def analyze(_prog: Program, html: Document, conf: ShellConf) = {

    println("Context-sensitivity mode is \"" + kr.ac.kaist.jsaf.analysis.typing.CallContext.getModeName + "\".")

    var program = _prog

    // concatenate modeled ASTs
    val SEP = File.separator
    val base = ProjectProperties.BASEDIR + SEP
    var modeledFiles: List[String] = List[String](base + "bin/models/builtin/__builtin__.js")
    var inputFiles: List[String] = List()
    if (Config.domMode) {
      modeledFiles :::= List(base + "bin/models/dom/__dom__.js")
      // benchmark mode excludes concrete environment "input"
      if (!conf.benchmark())
        inputFiles :::= List(base + "bin/inputs/__input__.js")
    }

    Config.setModeledFiles(Config.getModeledFiles ++ modeledFiles ++ inputFiles)
    val modeledASTs: Program = Parser.fileToAST((modeledFiles ++ inputFiles).asJava)
    program = (modeledASTs, program) match {
      case (SProgram(info0, STopLevel(fds0, vds0, body0)), SProgram(info1, STopLevel(fds1, vds1, body1))) =>
        SProgram(info1, STopLevel(fds0 ++ fds1, vds0 ++ vds1, body0 ++ body1))
    }

    val irErrors = Shell.ASTtoIR(Config.fileName, program, JOption.none[String], JOption.none[kr.ac.kaist.jsaf.nodes_util.Coverage])
    val irOpt: JOption[IRRoot] = irErrors.first
    program = irErrors.third // Disambiguated and hoisted and with written

    // Check the translation result
    if (irOpt.isNone)
      throw new UserError("translation failed")
    val ir: IRRoot = irOpt.unwrap

    // Build CFG
    val builder = new CFGBuilder(ir)
    val cfg = builder.build
    val errors = builder.getErrors
    if (!(errors.isEmpty)) {
      Shell.reportErrors(NodeUtil.getFileName(ir), Shell.flattenErrors(errors), JOption.none[Pair[FileWriter, BufferedWriter]])
    }

    if (conf.cfgDump())
      cfg.dump()

    printf("# Initial peak memory(mb): %.2f\n", MemoryMeasurer.peakMemory)

    val init = new InitHeap(cfg)
    init.initialize

    // Set the initial state with DOM objects
    if (Config.domMode) {
      new DOMBuilder(cfg, init, html).initialize(false)
    }

    // Create Typing
    val typing = new Typing(cfg, conf.quiet(), Shell.params.opt_LocClone)
    Config.setTypingInterface(typing)

    // Check global variables in initial heap against list of predefined variables.
    init.checkPredefined

    val analyzeStartTime = System.nanoTime

    // Analyze
    typing.analyze(init)

    // Disable precision tracking for post-mortem analysis (statistics)
    ImprecisionTracker.disableAll()

    // In quiet mode, the analysis does not print the iteration count, do it here
    if (conf.quiet()) {
      println("# Fixpoint iteration(#): " + typing.numIter)
    }

    printf("# Peak memory(mb): %.2f\n", MemoryMeasurer.peakMemory)
    printf("# Result heap memory(mb): %.2f\n", MemoryMeasurer.measureHeap)
    printf("# Analysis took %.2fs\n", (System.nanoTime - analyzeStartTime) / 1000000000.0)
    println(s"# String set size: ${Shell.params.opt_MaxStrSetSize}")
    println("\n* Statistics *")
    println("# Total state count: " + typing.getStateCount)
    typing.statistics(conf.stats())

    if (Config.testMode) {
      TestHelper.printTestObjects(typing)
      TestHelper.jQueryStats(typing)
    }
  }
}