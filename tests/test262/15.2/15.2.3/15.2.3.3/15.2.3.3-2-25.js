function testcase() 
{
  var obj = {
    "1e-7" : 1
  };
  var desc = Object.getOwnPropertyDescriptor(obj, 1E-7);
  return desc.value === 1;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}

