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

// Copyright 2009 the Sputnik authors.  All rights reserved.
// This code is governed by the BSD license found in the LICENSE file.

/**
 * If start is negative, use max(start + length, 0).
 * If deleteCount is negative, use 0
 *
 * @path ch15/15.4/15.4.4/15.4.4.12/S15.4.4.12_A1.2_T3.js
 * @description -length = start < deleteCount < 0, itemCount > 0
 */

var x = [0,1];
var arr = x.splice(-2,-1,2,3);

//CHECK#0
arr.getClass = Object.prototype.toString;
if (arr.getClass() !== "[object " + "Array" + "]") {
  $ERROR('#0: var x = [0,1]; var arr = x.splice(-2,-1,2,3); arr is Array object. Actual: ' + (arr.getClass()));
}

//CHECK#1
if (arr.length !== 0) {
  $ERROR('#1: var x = [0,1]; var arr = x.splice(-2,-1,2,3); arr.length === 0. Actual: ' + (arr.length));
}   

//CHECK#2
if (x.length !== 4) {
  $ERROR('#2: var x = [0,1]; var arr = x.splice(-2,-1,2,3); x.length === 4. Actual: ' + (x.length));
}      

//CHECK#3
if (x[0] !== 2) {
  $ERROR('#3: var x = [0,1]; var arr = x.splice(-2,-1,2,3); x[0] === 2. Actual: ' + (x[0]));
}

//CHECK#4
if (x[1] !== 3) {
  $ERROR('#4: var x = [0,1]; var arr = x.splice(-2,-1,2,3); x[1] === 3. Actual: ' + (x[1]));
}

//CHECK#5
if (x[2] !== 0) {
  $ERROR('#5: var x = [0,1]; var arr = x.splice(-2,-1,2,3); x[2] === 0. Actual: ' + (x[2]));
}

//CHECK#6
if (x[3] !== 1) {
  $ERROR('#6: var x = [0,1]; var arr = x.splice(-2,-1,2,3); x[3] === 1. Actual: ' + (x[3]));
}  
