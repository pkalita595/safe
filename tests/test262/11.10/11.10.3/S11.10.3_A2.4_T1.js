  var x = 1;
  {
    var __result1 = ((x = 0) | x) !== 0;
    var __expect1 = false;
  }
  var x = 1;
  {
    var __result2 = (x | (x = 0)) !== 1;
    var __expect2 = false;
  }
  