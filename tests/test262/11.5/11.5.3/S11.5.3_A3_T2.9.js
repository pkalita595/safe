  {
    var __result1 = isNaN(true % null) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = null % true !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(new Boolean(true) % null) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = null % new Boolean(true) !== 0;
    var __expect4 = false;
  }
  