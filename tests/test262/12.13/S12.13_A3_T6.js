  var i = 0;
  function adding1() 
  {
    i++;
    return 1;
  }
  try
{    throw (adding1());}
  catch (e)
{    if (e !== 1)
      $ERROR('#1: Exception ===1. Actual:  Exception ===' + e);}

  var i = 0;
  function adding2() 
  {
    i++;
    return i;
  }
  try
{    throw adding2();}
  catch (e)
{    }

  if (i !== 1)
    $ERROR('#2: i===1. Actual: i===' + i);
  var i = 0;
  function adding3() 
  {
    i++;
  }
  try
{    throw adding3();}
  catch (e)
{    }

  if (i !== 1)
    $ERROR('#3: i===1. Actual: i===' + i);
  function adding4(i) 
  {
    i++;
    return i;
  }
  try
{    throw (adding4(1));}
  catch (e)
{    if (e !== 2)
      $ERROR('#4: Exception ===2. Actual:  Exception ===' + e);}

  