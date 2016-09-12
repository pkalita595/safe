/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
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

package kr.ac.kaist.jsaf.tests

import java.io.File
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.{Shell, ShellParameters}

//import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.CFGBuilder
import kr.ac.kaist.jsaf.analysis.cfg.LExit
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.{ModelManager, BuiltinModel, DOMBuilder}
import kr.ac.kaist.jsaf.compiler.Disambiguator
import kr.ac.kaist.jsaf.compiler.Hoister
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.compiler.Translator
import kr.ac.kaist.jsaf.compiler.WithRewriter
import kr.ac.kaist.jsaf.nodes.IRRoot
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.nodes_util.JSFromHTML

class SemanticsDOMTest(dir: String, tc: String, typing_mode: String) extends SemanticsTest(dir, tc, typing_mode) {


  var configureFunc: () => Unit = defaultConfig

  def defaultConfig(): Unit = {
    // legacy shell param
    Shell.params.Set(Array[String]("html", "-context-1-callsite", "-test"))
    // setup testing options
    Config.setTestMode(true)
    Config.setAssertMode(true)
    // enable DOM
    Config.setDomMode
  }

  override def analyze(file: File): TypingInterface = {


    // Initialize AddressManager
    AddressManager.reset()

    configureFunc()

    // html preprocess, parse
    //val jshtml = new JSFromHTML(file.getPath)
    val jshtml = new JSFromHTML(file.getCanonicalPath)
    var program: Program = jshtml.parseScripts()

    // hoist
    val hoister = new Hoister(program)
    program = hoister.doit().asInstanceOf[Program]

    // disambiguate
    val disambiguator = new Disambiguator(program, false)
    program = disambiguator.doit().asInstanceOf[Program];

    // with rewrite
    val withRewriter = new WithRewriter(program, false);
    program = withRewriter.doit().asInstanceOf[Program]

    // translate to IR
    val translator = new Translator(program, toJavaOption(None));
    val ir: IRRoot = translator.doit().asInstanceOf[IRRoot];

    // build CFG
    val builder = new CFGBuilder(ir);
    val cfg: CFG = builder.build();

    // initialize heap
    //val model = new BuiltinModel(cfg)
    //model.initialize()
    val init = new InitHeap(cfg)
    init.initialize()

    val dom_model = new DOMBuilder(cfg, init, jshtml.getDocument())
    dom_model.initialize(false);

    // typing
    val typing =
      typing_mode match {
        case "dense"   => new Typing(cfg, false, false)
        case _  => throw new UserError("not supported")
      }

    Config.setAssertMode(true)
    typing.analyze(init)

    typing_mode match {
      case "dense" =>
        typing.analyze(init)
      case _ => throw new UserError("not supported")
    }

    // return resulting Typing instance
    typing
  }
}
