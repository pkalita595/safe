  var x = true;
  {
    var __result1 = -- x !== 1 - 1;
    var __expect1 = false;
  }
  var x = new Boolean(false);
  {
    var __result2 = -- x !== 0 - 1;
    var __expect2 = false;
  }
  