// XXX
// function testcase() 
//  {
//    var arr = [0, , 2, ];
//    Object.defineProperty(arr, "0", {
//      get : (function () 
//      {
//        delete Array.prototype[1];
//        return 0;
//      }),
//      configurable : true
//    });
//    try
//{      Array.prototype[1] = 1;
//      return - 1 === arr.indexOf(1);}
//    finally
//{      delete Array.prototype[1];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
