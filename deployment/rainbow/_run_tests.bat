del _tests_results.txt
del ..\..\filters\net.sf.okapi.filters.regex.tests\data\*.out
java -jar dist_win32-x86\lib\rainbow.jar -p ..\..\filters\net.sf.okapi.filters.regex.tests\data\BatchTests.rnb -x oku_textrewriting -np
comp ..\..\filters\net.sf.okapi.filters.regex.tests\data\*.out ..\..\filters\net.sf.okapi.filters.regex.tests\data\*.gold > _tests_results.txt

del ..\..\filters\net.sf.okapi.filters.properties.tests\data\*.out
java -jar dist_win32-x86\lib\rainbow.jar -p ..\..\filters\net.sf.okapi.filters.properties.tests\data\BatchTests.rnb -x oku_textrewriting -np
comp ..\..\filters\net.sf.okapi.filters.properties.tests\data\*.out ..\..\filters\net.sf.okapi.filters.properties.tests\data\*.gold >> _tests_results.txt

del ..\..\filters\net.sf.okapi.filters.xliff.tests\data\*.out
java -jar dist_win32-x86\lib\rainbow.jar -x oku_textrewriting -sl en -tl fr -opt ..\..\filters\net.sf.okapi.filters.xliff.tests\data\rewriteAll.opt -np  ..\..\filters\net.sf.okapi.filters.xliff.tests\data\PAS-10-Test01.xlf -o ..\..\filters\net.sf.okapi.filters.xliff.tests\data\PAS-10-Test01.xlf.out
comp ..\..\filters\net.sf.okapi.filters.xliff.tests\data\*.out ..\..\filters\net.sf.okapi.filters.xliff.tests\data\*.gold >> _tests_results.txt

del tests\*.out
java -jar dist_win32-x86\lib\rainbow.jar -p tests\BOM_Add.rnb -x oku_bomconversion -np
java -jar dist_win32-x86\lib\rainbow.jar -p tests\LineBreak_ToDos.rnb -x oku_linebreakconversion -np
java -jar dist_win32-x86\lib\rainbow.jar -p tests\EncConv_Test1.rnb -x oku_encodingconversion -np
java -jar dist_win32-x86\lib\rainbow.jar -p tests\EncConv_Test2.rnb -x oku_encodingconversion -np
comp tests\*.out tests\*.gold >> _tests_results.txt

del /S /Q tests\extraction\test1\*.*
java -jar dist_win32-x86\lib\rainbow.jar -p tests\extraction\Test1_Extraction.rnb -x oku_extraction -np
java -jar dist_win32-x86\lib\rainbow.jar -p tests\extraction\Test1_Translation.rnb -x oku_textrewriting -np
java -jar dist_win32-x86\lib\rainbow.jar -p tests\extraction\Test1_Merging.rnb -x oku_merging -np
comp tests\extraction\test1_out\done\*.* tests\extraction\test1_gold\*.* >> _tests_results.txt

