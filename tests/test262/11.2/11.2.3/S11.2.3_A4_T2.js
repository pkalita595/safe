  try
{    new Number(1)();
    $ERROR('#1.1: new Number(1)() throw TypeError. Actual: ' + (new Number(1)()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = new Number(1);
    x();
    $ERROR('#2.1: var x = new Number(1); x() throw TypeError. Actual: ' + (x()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  