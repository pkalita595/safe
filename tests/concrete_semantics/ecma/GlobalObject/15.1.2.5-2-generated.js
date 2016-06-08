var SECTION = "15.1.2.5-2-generated";
var VERSION = "ECMA_1";
startTest();
var TITLE   = "unescape(string)";

SAFE_TESTS = 85

new TestCase( SECTION, "unescape( %0 )", "%0", unescape("%0"));
new TestCase( SECTION, "unescape( %1 )", "%1", unescape("%1"));
new TestCase( SECTION, "unescape( %2 )", "%2", unescape("%2"));
new TestCase( SECTION, "unescape( %3 )", "%3", unescape("%3"));
new TestCase( SECTION, "unescape( %4 )", "%4", unescape("%4"));
new TestCase( SECTION, "unescape( %5 )", "%5", unescape("%5"));
new TestCase( SECTION, "unescape( %6 )", "%6", unescape("%6"));
new TestCase( SECTION, "unescape( %7 )", "%7", unescape("%7"));
new TestCase( SECTION, "unescape( %8 )", "%8", unescape("%8"));
new TestCase( SECTION, "unescape( %9 )", "%9", unescape("%9"));
new TestCase( SECTION, "unescape( %A )", "%A", unescape("%A"));
new TestCase( SECTION, "unescape( %B )", "%B", unescape("%B"));
new TestCase( SECTION, "unescape( %C )", "%C", unescape("%C"));
new TestCase( SECTION, "unescape( %D )", "%D", unescape("%D"));
new TestCase( SECTION, "unescape( %E )", "%E", unescape("%E"));
new TestCase( SECTION, "unescape( %F )", "%F", unescape("%F"));
new TestCase( SECTION, "unescape( %u0 )", "%u0", unescape("%u0"));
new TestCase( SECTION, "unescape( %u1 )", "%u1", unescape("%u1"));
new TestCase( SECTION, "unescape( %u2 )", "%u2", unescape("%u2"));
new TestCase( SECTION, "unescape( %u3 )", "%u3", unescape("%u3"));
new TestCase( SECTION, "unescape( %u4 )", "%u4", unescape("%u4"));
new TestCase( SECTION, "unescape( %u5 )", "%u5", unescape("%u5"));
new TestCase( SECTION, "unescape( %u6 )", "%u6", unescape("%u6"));
new TestCase( SECTION, "unescape( %u7 )", "%u7", unescape("%u7"));
new TestCase( SECTION, "unescape( %u8 )", "%u8", unescape("%u8"));
new TestCase( SECTION, "unescape( %u9 )", "%u9", unescape("%u9"));
new TestCase( SECTION, "unescape( %uA )", "%uA", unescape("%uA"));
new TestCase( SECTION, "unescape( %uB )", "%uB", unescape("%uB"));
new TestCase( SECTION, "unescape( %uC )", "%uC", unescape("%uC"));
new TestCase( SECTION, "unescape( %uD )", "%uD", unescape("%uD"));
new TestCase( SECTION, "unescape( %uE )", "%uE", unescape("%uE"));
new TestCase( SECTION, "unescape( %uF )", "%uF", unescape("%uF"));
new TestCase( SECTION, "unescape( %u040 )", "%u040", unescape("%u040"));
new TestCase( SECTION, "unescape( %u08D )", "%u08D", unescape("%u08D"));
new TestCase( SECTION, "unescape( %u0DA )", "%u0DA", unescape("%u0DA"));
new TestCase( SECTION, "unescape( %u127 )", "%u127", unescape("%u127"));
new TestCase( SECTION, "unescape( %u174 )", "%u174", unescape("%u174"));
new TestCase( SECTION, "unescape( %u1C1 )", "%u1C1", unescape("%u1C1"));
new TestCase( SECTION, "unescape( %u20E )", "%u20E", unescape("%u20E"));
new TestCase( SECTION, "unescape( %u25B )", "%u25B", unescape("%u25B"));
new TestCase( SECTION, "unescape( %u2A9 )", "%u2A9", unescape("%u2A9"));
new TestCase( SECTION, "unescape( %u2F6 )", "%u2F6", unescape("%u2F6"));
new TestCase( SECTION, "unescape( %u343 )", "%u343", unescape("%u343"));
new TestCase( SECTION, "unescape( %u390 )", "%u390", unescape("%u390"));
new TestCase( SECTION, "unescape( %u3DD )", "%u3DD", unescape("%u3DD"));
new TestCase( SECTION, "unescape( %u42A )", "%u42A", unescape("%u42A"));
new TestCase( SECTION, "unescape( %u477 )", "%u477", unescape("%u477"));
new TestCase( SECTION, "unescape( %u4C4 )", "%u4C4", unescape("%u4C4"));
new TestCase( SECTION, "unescape( %u512 )", "%u512", unescape("%u512"));
new TestCase( SECTION, "unescape( %u55F )", "%u55F", unescape("%u55F"));
new TestCase( SECTION, "unescape( %u5AC )", "%u5AC", unescape("%u5AC"));
new TestCase( SECTION, "unescape( %u5F9 )", "%u5F9", unescape("%u5F9"));
new TestCase( SECTION, "unescape( %u646 )", "%u646", unescape("%u646"));
new TestCase( SECTION, "unescape( %u693 )", "%u693", unescape("%u693"));
new TestCase( SECTION, "unescape( %u6E0 )", "%u6E0", unescape("%u6E0"));
new TestCase( SECTION, "unescape( %u72D )", "%u72D", unescape("%u72D"));
new TestCase( SECTION, "unescape( %u77B )", "%u77B", unescape("%u77B"));
new TestCase( SECTION, "unescape( %u7C8 )", "%u7C8", unescape("%u7C8"));
new TestCase( SECTION, "unescape( %u815 )", "%u815", unescape("%u815"));
new TestCase( SECTION, "unescape( %u862 )", "%u862", unescape("%u862"));
new TestCase( SECTION, "unescape( %u8AF )", "%u8AF", unescape("%u8AF"));
new TestCase( SECTION, "unescape( %u8FC )", "%u8FC", unescape("%u8FC"));
new TestCase( SECTION, "unescape( %u949 )", "%u949", unescape("%u949"));
new TestCase( SECTION, "unescape( %u996 )", "%u996", unescape("%u996"));
new TestCase( SECTION, "unescape( %u9E4 )", "%u9E4", unescape("%u9E4"));
new TestCase( SECTION, "unescape( %uA31 )", "%uA31", unescape("%uA31"));
new TestCase( SECTION, "unescape( %uA7E )", "%uA7E", unescape("%uA7E"));
new TestCase( SECTION, "unescape( %uACB )", "%uACB", unescape("%uACB"));
new TestCase( SECTION, "unescape( %uB18 )", "%uB18", unescape("%uB18"));
new TestCase( SECTION, "unescape( %uB65 )", "%uB65", unescape("%uB65"));
new TestCase( SECTION, "unescape( %uBB2 )", "%uBB2", unescape("%uBB2"));
new TestCase( SECTION, "unescape( %uBFF )", "%uBFF", unescape("%uBFF"));
new TestCase( SECTION, "unescape( %uC4D )", "%uC4D", unescape("%uC4D"));
new TestCase( SECTION, "unescape( %uC9A )", "%uC9A", unescape("%uC9A"));
new TestCase( SECTION, "unescape( %uCE7 )", "%uCE7", unescape("%uCE7"));
new TestCase( SECTION, "unescape( %uD34 )", "%uD34", unescape("%uD34"));
new TestCase( SECTION, "unescape( %uD81 )", "%uD81", unescape("%uD81"));
new TestCase( SECTION, "unescape( %uDCE )", "%uDCE", unescape("%uDCE"));
new TestCase( SECTION, "unescape( %uE1B )", "%uE1B", unescape("%uE1B"));
new TestCase( SECTION, "unescape( %uE68 )", "%uE68", unescape("%uE68"));
new TestCase( SECTION, "unescape( %uEB6 )", "%uEB6", unescape("%uEB6"));
new TestCase( SECTION, "unescape( %uF03 )", "%uF03", unescape("%uF03"));
new TestCase( SECTION, "unescape( %uF50 )", "%uF50", unescape("%uF50"));
new TestCase( SECTION, "unescape( %uF9D )", "%uF9D", unescape("%uF9D"));
new TestCase( SECTION, "unescape( %uFEA )", "%uFEA", unescape("%uFEA"));