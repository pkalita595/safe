  {
    var __result1 = ("x" >= "x") !== true;
    var __expect1 = false;
  }
  {
    var __result2 = ("x" >= "") !== true;
    var __expect2 = false;
  }
  {
    var __result3 = ("abcd" >= "ab") !== true;
    var __expect3 = false;
  }
  {
    var __result4 = ("abc\u0064" >= "abcd") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = ("x" + "y" >= "x") !== true;
    var __expect5 = false;
  }
  var x = "x";
  {
    var __result6 = (x + 'y' >= x) !== true;
    var __expect6 = false;
  }
  {
    var __result7 = ("a\u0000a" >= "a\u0000") !== true;
    var __expect7 = false;
  }
  {
    var __result8 = (" x" >= "x") !== false;
    var __expect8 = false;
  }
  