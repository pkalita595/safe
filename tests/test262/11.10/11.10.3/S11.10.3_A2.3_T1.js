// TODO [[DefaultValue]]
//   var x = {
//     valueOf : (function () 
//     {
//       throw "x";
//     })
//   };
//   var y = {
//     valueOf : (function () 
//     {
//       throw "y";
//     })
//   };
//   try
// {    x | y;
//     $ERROR('#1.1: var x = { valueOf: function () { throw "x"; } }; var y = { valueOf: function () { throw "y"; } }; x | y throw "x". Actual: ' + (x | y));}
//   catch (e)
// {    if (e === "y")
//     {
//       $ERROR('#1.2: ToInt32(first expression) is called first, and then ToInt32(second expression)');
//     }
//     else
//     {
//       {
//         var __result1 = e !== "x";
//         var __expect1 = false;
//       }
//     }}
