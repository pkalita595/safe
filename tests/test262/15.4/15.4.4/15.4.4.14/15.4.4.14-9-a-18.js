// XXX
//  function testcase() 
//  {
//    var arr = [0, 1, 2, ];
//    try
//{      Object.defineProperty(Array.prototype, "2", {
//        get : (function () 
//        {
//          return "prototype";
//        }),
//        configurable : true
//      });
//      Object.defineProperty(arr, "1", {
//        get : (function () 
//        {
//          arr.length = 2;
//          return 1;
//        }),
//        configurable : true
//      });
//      return 2 === arr.indexOf("prototype");}
//    finally
//{      delete Array.prototype[2];}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
