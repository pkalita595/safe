  function testcase() 
  {
    var arr = [20, ];
    Object.defineProperty(arr, "0", {
      get : (function () 
      {
        arr[1] = 1;
        return 0;
      }),
      configurable : true
    });
    return arr.indexOf(1) === - 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  