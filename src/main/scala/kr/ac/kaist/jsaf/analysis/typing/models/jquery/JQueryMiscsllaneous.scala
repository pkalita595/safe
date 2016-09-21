/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
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

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper => AH, _}
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError, FunctionId}

object JQueryMiscsllaneous extends ModelData {

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("get",     AbsBuiltinFunc("jQuery.prototype.get", 1)),
    ("index",   AbsBuiltinFunc("jQuery.prototype.index", 1)),
    ("size",    AbsBuiltinFunc("jQuery.prototype.size", 0)),
    ("toArray", AbsBuiltinFunc("jQuery.prototype.toArray", 0))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("jQuery.prototype.get" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2.locs
          /* first argument */
          val arg1 = getArgValue(h, ctx, args, "0")
          /* toArray() == get(null) == get(undefined) */
          val (h1, ctx1, v1) =
            if (arg1.pv._1 </ UndefBot || arg1.pv._2 </ NullBot)
              toArray(h, ctx, cfg, cp._1._1)
            else
              (HeapBot, ContextBot, ValueBot)

          /* get */
          val v2 =
            if (arg1.pv._1 <= UndefBot && arg1.pv._2 <= NullBot && arg1.pv._4 </ NumBot) {
              val n_num = arg1.pv._4
              val n_index1 =
                if (T <= Operator.bopLess(Value(n_num), Value(AbsNumber.alpha(0))).pv._3) {
                  val v_len = lset_this.foldLeft(ValueBot)((v,l) => Helper.Proto(h,l,AbsString.alpha("length")))
                  Operator.bopPlus(Value(v_len.pv._4), Value(n_num)).pv._4
                }
                else
                  NumBot
              val n_index2 =
                if (F <= Operator.bopLess(Value(n_num), Value(AbsNumber.alpha(0))).pv._3)
                  n_num
                else
                  NumBot
              val n_index = n_index1 + n_index2
              lset_this.foldLeft(ValueBot)((v,l) => Helper.Proto(h, l, Helper.toString(PValue(n_index))))
            }
            else
              ValueBot

          val h_ret = h1 + h
          val ctx_ret = ctx1 + ctx
          val v_ret = v1 + v2
          if (arg1 </ ValueBot)
            ((Helper.ReturnStore(h_ret, v_ret), ctx_ret), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.index" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // TODO: imprecise
          ((Helper.ReturnStore(h, Value(UInt)), ctx), (he, ctxe))
        })),
      ("jQuery.prototype.size" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2.locs
          val v_size = lset_this.foldLeft(ValueBot)((v, l) =>
            v + Helper.Proto(h, l, AbsString.alpha("length"))
          )
          ((Helper.ReturnStore(h, v_size), ctx), (he, ctxe))
        })),
      ("jQuery.prototype.toArray" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          //  this.toArray() == Array.protootype.slice.call( this )
          val (h_ret, ctx_ret, v_ret) = toArray(h, ctx, cfg, cp._1._1)

          if (!(h_ret <= HeapBot))
            ((Helper.ReturnStore(h_ret,v_ret), ctx_ret), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  private def toArray(h: Heap, ctx: Context, cfg: CFG, fid: FunctionId):(Heap, Context, Value) = {
    //  this.toArray() == Array.protootype.slice.call( this )
    /* new addr */
    val lset_env = h(SinglePureLocalLoc)("@env")._2.locs
    val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
    if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
    val addr_env = (fid, set_addr.head)
    val addr1 = cfg.getAPIAddress(addr_env, 0)
    /* new loc */
    val l_arr = addrToLoc(addr1, Recent)
    val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

    /* jQuery obejct */
    val lset_this = h_1(SinglePureLocalLoc)("@this")._2.locs

    val o_arr = lset_this.foldLeft(Obj.bottom)((o, l) => {
      val n_len = Operator.ToInteger(Helper.Proto(h_1, l, AbsString.alpha("length")))
      val o_new = Helper.NewArgObject(n_len)
      n_len.getSingle match {
        case Some(n) =>
          o + (0 until n.toInt).foldLeft(o_new)((_o, i) => {
            val s_index = AbsString.alpha(i.toString)
            val v_props = Helper.Proto(h_1, l, s_index)
            _o.update(s_index, PropValue(ObjectValue(v_props, T, T, T)))
          })
        case None =>
          if (n_len </ NumBot) {
            val v_props = Helper.Proto(h_1, l, NumStr)
            o + o_new.update(Helper.toString(PValue(n_len)), PropValue(ObjectValue(v_props, T, T, T)))
          }
          else
            o
      }
    })

    if (o_arr </ Obj.bottom)
      (h_1.update(l_arr, o_arr), ctx_1, Value(l_arr))
    else
      (HeapBot, ContextBot, ValueBot)
  }

  private def toArray_pre(h: Heap, ctx: Context, cfg: CFG, PureLocalLoc: Loc, fid: FunctionId):(Heap, Context, Value) = {
    //  this.toArray() == Array.protootype.slice.call( this )
    /* new addr */
    val lset_env = h(PureLocalLoc)("@env")._2.locs
    val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
    if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
    val addr_env = (fid, set_addr.head)
    val addr1 = cfg.getAPIAddress(addr_env, 0)
    /* new loc */
    val l_arr = addrToLoc(addr1, Recent)
    val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)

    /* jQuery obejct */
    val lset_this = h_1(PureLocalLoc)("@this")._2.locs

    val o_arr = lset_this.foldLeft(Obj.bottom)((o, l) => {
      val n_len = Operator.ToInteger(PreHelper.Proto(h_1, l, AbsString.alpha("length")))
      val o_new = PreHelper.NewArgObject(n_len)
      n_len.getSingle match {
        case Some(n) =>
          o + (0 until n.toInt).foldLeft(o_new)((_o, i) => {
            val s_index = AbsString.alpha(i.toString)
            val v_props = PreHelper.Proto(h_1, l, s_index)
            _o.update(s_index, PropValue(ObjectValue(v_props, T, T, T)))
          })
        case None =>
          if (n_len </ NumBot) {
            val v_props = PreHelper.Proto(h_1, l, NumStr)
            o + o_new.update(PreHelper.toString(PValue(n_len)), PropValue(ObjectValue(v_props, T, T, T)))
          }
          else
            o
      }
    })

    if (o_arr </ Obj.bottom)
      (h_1.update(l_arr, o_arr), ctx_1, Value(l_arr))
    else
      (HeapBot, ContextBot, ValueBot)
  }

  private def toArray_def(h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId): LPSet = {
    //  this.toArray() == Array.protootype.slice.call( this )
    /* new addr */
    val lset_env = h(SinglePureLocalLoc)("@env")._2.locs
    val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
    if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
    val addr1 = cfg.getAPIAddress((fid, set_addr.head), 0)
    /* new loc */
    val l_arr = addrToLoc(addr1, Recent)
    val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
    val LP1 = AH.Oldify_def(h, ctx, addr1)

    /* jQuery obejct */
    val lset_this = h_1(SinglePureLocalLoc)("@this")._2.locs

    val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
      val n_len = Operator.ToInteger(Helper.Proto(h_1, l, AbsString.alpha("length")))
      val o_new = Helper.NewArgObject(n_len)
      val LP2_1 = AH.NewArrayObject_def.foldLeft(LPBot)((lp, prop)=> lp + (l_arr, prop))
      val LP2_2 = n_len.getSingle match {
        case Some(n) =>
          (0 until n.toInt).foldLeft(LPBot)((lps, i) =>
            lps + (l_arr, i.toString)
          )
        case None =>
          if (n_len </ NumBot)
            AH.absPair(o_new, l_arr, Helper.toString(PValue(n_len)))
          else
            LPBot
      }
      lpset ++ LP2_1 ++ LP2_2
    })

    LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
  }

  private def toArray_use(h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId): LPSet = {
    //  this.toArray() == Array.protootype.slice.call( this )
    /* new addr */
    val lset_env = h(SinglePureLocalLoc)("@env")._2.locs
    val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
    if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
    val addr1 = cfg.getAPIAddress((fid, set_addr.head), 0)
    val LP1 = getAddrList_use()
    /* new loc */
    val l_arr = addrToLoc(addr1, Recent)
    val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
    val LP2 = AH.Oldify_use(h, ctx, addr1)

    /* jQuery obejct */
    val lset_this = h_1(SinglePureLocalLoc)("@this")._2.locs
    val LP3 = LPSet(SinglePureLocalLoc, "@this")

    val LP4 = lset_this.foldLeft(LPBot)((lpset, l) => {
      val n_len = Operator.ToInteger(Helper.Proto(h_1, l, AbsString.alpha("length")))
      val LP4_1 = AH.Proto_use(h_1, l, AbsString.alpha("length"))
      val LP4_2 = n_len.getSingle match {
        case Some(n) =>
          (0 until n.toInt).foldLeft(LPBot)((lps, i) =>
            lps ++ AH.Proto_use(h_1, l, AbsString.alpha(i.toString))
          )
        case None =>
          if (n_len </ NumBot)
            AH.Proto_use(h_1, l, NumStr)
          else
            LPBot
      }
      lpset ++ LP4_1 ++ LP4_2
    })

    LP1 ++ LP2 ++ LP3 ++ LP4 + (SinglePureLocalLoc, "@return")
  }
}
