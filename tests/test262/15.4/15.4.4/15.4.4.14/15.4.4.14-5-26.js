// XXX
//  function testcase() 
//  {
//    var stepTwoOccurs = false;
//    var stepFiveOccurs = false;
//    var obj = {
//      
//    };
//    Object.defineProperty(obj, "length", {
//      get : (function () 
//      {
//        stepTwoOccurs = true;
//        if (stepFiveOccurs)
//        {
//          throw new Error("Step 5 occurred out of order");
//        }
//        return 20;
//      }),
//      configurable : true
//    });
//    var fromIndex = {
//      valueOf : (function () 
//      {
//        stepFiveOccurs = true;
//        return 0;
//      })
//    };
//    try
//{      Array.prototype.indexOf.call(obj, undefined, fromIndex);
//      return stepTwoOccurs && stepFiveOccurs;}
//    catch (ex)
//{      return false;}
//
//  }
//  {
//    var __result1 = testcase();
//    var __expect1 = true;
//  }
//  
