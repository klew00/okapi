del ..\..\filters\net.sf.okapi.filters.regex.tests\data\*.out
java -jar dist_win32-x86\lib\rainbow.jar -p ..\..\filters\net.sf.okapi.filters.regex.tests\data\BatchTests.rnb -x oku_textrewriting -np
comp ..\..\filters\net.sf.okapi.filters.regex.tests\data\*.out ..\..\filters\net.sf.okapi.filters.regex.tests\data\*.gold

del ..\..\filters\net.sf.okapi.filters.properties.tests\data\*.out
java -jar dist_win32-x86\lib\rainbow.jar -p ..\..\filters\net.sf.okapi.filters.properties.tests\data\BatchTests.rnb -x oku_textrewriting -np
comp ..\..\filters\net.sf.okapi.filters.properties.tests\data\*.out ..\..\filters\net.sf.okapi.filters.properties.tests\data\*.gold

del ..\..\filters\net.sf.okapi.filters.xliff.tests\data\*.out
java -jar dist_win32-x86\lib\rainbow.jar -x oku_textrewriting -sl en -tl fr -opt ..\..\filters\net.sf.okapi.filters.xliff.tests\data\rewriteAll.opt -np  ..\..\filters\net.sf.okapi.filters.xliff.tests\data\PAS-10-Test01.xlf -o ..\..\filters\net.sf.okapi.filters.xliff.tests\data\PAS-10-Test01.xlf.out
comp ..\..\filters\net.sf.okapi.filters.xliff.tests\data\*.out ..\..\filters\net.sf.okapi.filters.xliff.tests\data\*.gold





