/*******************************************************************************
    Copyright (c) 2013, S-Core.
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

import kr.ac.kaist.jsaf.{Shell, ShellParameters}
import kr.ac.kaist.jsaf.compiler.Predefined
import junit.framework.{Test, TestSuite}
import kr.ac.kaist.jsaf.analysis.typing.Config

class TypingJQTest

object TypingJQTest {
  val TESTS_DIR = "tests/typing_tests/jquery"

  val EXCLUDE = Set(
    "XXX",
    "NYI"
  )

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    val suite = new TestSuite("jQuery Semantics Test")
    val testcases = collectTestcase(TESTS_DIR)
    for (tc <- testcases) {
      //$JUnit-BEGIN$
      val jqtest = new SemanticsDOMTest(TESTS_DIR, tc, "dense")
      jqtest.configureFunc = () => {
        // legacy shell param
        Shell.params.Set(Array[String]("html", "-context-1-callsite", "-test", "-jq"))
        // setup testing options
        Config.setTestMode(true)
        Config.setAssertMode(true)
        // enable DOM
        Config.setDomMode
        // enable jQuery
        Config.setJQueryMode
      }
      suite.addTest(jqtest)
      //$JUnit-END$
    }
    suite
  }

  private def collectTestcase(dirname: String) = {
    val dir = FileTests.directoryAsFile(dirname)
    val filtered = dir.list.toSeq.filter(fname =>
      fname.endsWith(".html") &&
        !EXCLUDE.exists(prefix => fname.startsWith(prefix)))
    filtered.sorted
  }
}