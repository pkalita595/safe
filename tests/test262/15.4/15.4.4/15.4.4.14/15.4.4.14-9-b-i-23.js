// XXX
//  function testcase() 
//  {
//    var targetObj = {
//      
//    };
//    try
//{      var oldLen = fnGlobalObject().length;
//      fnGlobalObject()[0] = targetObj;
//      fnGlobalObject()[100] = "100";
//      fnGlobalObject()[200] = "200";
//      fnGlobalObject().length = 200;
//      return 0 === Array.prototype.indexOf.call(fnGlobalObject(), targetObj) && 100 === Array.prototype.indexOf.call(fnGlobalObject(), "100") && - 1 === Array.prototype.indexOf.call(fnGlobalObject(), "200");}
//    finally
//{      delete fnGlobalObject()[0];
//      delete fnGlobalObject()[100];
//      delete fnGlobalObject()[200];
//      fnGlobalObject().length = oldLen;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
