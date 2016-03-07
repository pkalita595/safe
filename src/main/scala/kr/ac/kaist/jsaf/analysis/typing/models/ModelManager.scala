/*******************************************************************************
    Copyright (c) 2013-2014, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/
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

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.{NotYetImplemented, Config}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.{Shell, ShellParameters}
import kr.ac.kaist.jsaf.ts.TSModel
import kr.ac.kaist.jsaf.nodes_util.IRFactory

object ModelManager {
  private var model_map = Map[String, Model]()

  var fset_builtin = Map[FunctionId, String]()


  // !!!!  call once !!!!
  private def initModelMap(cfg: CFG): Unit = {

    /* builin model */
    model_map = Map[String, Model](("Builtin" -> new BuiltinModel(cfg)))

    /* dom model */
    if (Config.domMode ||
        Shell.params.command == ShellParameters.CMD_HTML ||
        Shell.params.command == ShellParameters.CMD_HTML_PRE ||
        Shell.params.command == ShellParameters.CMD_HTML_SPARSE) {
      model_map = model_map + ("DOM" -> new DOMModel(cfg))
    }

    /* jQuery model */
    if (Config.jqMode) {
      println("jQuery model enabled.")
      model_map = model_map + ("jQuery" -> new JQueryModel(cfg))
    }

    /* TS model */
    if (Config.tsMode)
      model_map = model_map + ("TS" -> new TSModel(cfg))
  }

  def initialize(cfg: CFG, heap: Heap): Heap = {
    /* init model */
    initModelMap(cfg)
    
    // dynamically loaded scripts
    val startnode = cfg.getScriptStartNode
    if(!startnode.isEmpty){
      // loop head
      val n_head = cfg.newBlock(cfg.getGlobalFId)
      startnode.foreach(n => {
        val ns_pred = cfg.getPred(n)
        ns_pred.foreach(np => {
          cfg.removeEdge(np, n)
          if(!cfg.getLastInst(np).getInfo.get.getSpan.getFileName.contains("#loading#")){
            cfg.addEdge(np, n_head) 
            cfg.addEdge(np, ((cfg.getGlobalFId, LExit)))
          }
        })
        cfg.addEdge(n_head, n)
      })
      // loop end
      val n_end = cfg.newBlock(cfg.getGlobalFId)
      val endnode = cfg.getScriptEndNode
      endnode.foreach(n => {
        val ns_succ = cfg.getSucc(n)
        ns_succ.foreach(ns => {
          cfg.removeEdge(n, ns)
          val inst = cfg.getFirstInst(ns)
          if(inst == null || !inst.getInfo.get.getSpan.getFileName.contains("#loading#"))
            cfg.addEdge(n_end, ns)  
        })
        cfg.addEdge(n, n_end)
      })
      cfg.addEdge(n_end, n_head)

    }
   
    if(Config.disableEventMode == false) {
      /* add async call to CFG */
      // last nodes
      val ns_last = cfg.getPred(((cfg.getGlobalFId, LExit)))
      // loop head
      val n_head = cfg.newBlock(cfg.getGlobalFId)
      // loop end
      val n_end = cfg.newBlock(cfg.getGlobalFId)
      // add async calls
      val ns_calls = model_map.foldLeft((List[Node](),List[Node]()))((nodes, kv) => {
        val ns_async = kv._2.addAsyncCall(cfg, n_head)
        (nodes._1 ++ ns_async._1,nodes._2 ++ ns_async._2)
      })
      if (!ns_calls._1.isEmpty) {
        // last node -> loop head
 //       if(Config.loopSensitive) {
 //         ns_last.toList.foreach(n => cfg.addLoopEdge(n, n_head))
//          val dummyInfo = IRFactory.makeInfo(IRFactory.dummySpan("Loop"))
//          cfg.addInst(n_head, CFGCond(cfg.newInstId, dummyInfo, CFGVarRef(dummyInfo, CFGTempId("scrollMaxY", GlobalVar)), true))
//        }
 //       else
          cfg.addEdge(ns_last.toList, n_head)
        // loop head -> n_end
        cfg.addEdge(n_head, n_end)
        // async after call -> n_end
        cfg.addEdge(ns_calls._1, n_end)
        // async after call -> exc-exit 
        cfg.addExcEdge(ns_calls._2,(cfg.getGlobalFId,LExitExc))
        // loop end -> exit
        cfg.addEdge(n_end, ((cfg.getGlobalFId, LExit)))
        
        /*
        // loop head -> exit
        cfg.addEdge(n_head, ((cfg.getGlobalFId, LExit)))
        // async after call -> exit
        cfg.addEdge(ns_calls._1, ((cfg.getGlobalFId, LExit)))
        // async after call -> exc-exit 
        cfg.addExcEdge(ns_calls._2,(cfg.getGlobalFId,LExitExc))
        */
      }
    }
    


   // DotWriter.write(cfg, "temp.dot", "temp.svg", "dot")
    /* init heap*/
    model_map.foldLeft(heap)((_h, kv) => kv._2.initialize(_h))
  }

  def getModel(name: String): Model = model_map(name)

  def getFIdMap(): Map[FunctionId, String] = {
    model_map.foldLeft[Map[FunctionId, String]](Map())((m, kv) => m ++ kv._2.getFIdMap())
  }
  def getFIdMap(name:String): Map[FunctionId, String] = model_map(name).getFIdMap()

  def isModelFId(fid : FunctionId) = {
    model_map.exists((kv) => kv._2.isModelFid(fid))
  }
  def isModelFId(model: String, fid : FunctionId) = {
    model_map(model).getFIdMap().contains(fid)
  }

  def getFuncName(fid: FunctionId): String = {
    for((_, model) <- model_map) {
      model.getFIdMap.get(fid) match {
        case Some(funcName) => return funcName
        case None =>
      }
    }
    null
  }

  // TODO
  def isModelLoc(loc : Loc) = {
    true
  }

}
