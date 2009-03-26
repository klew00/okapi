@echo OFF
del _tests_results.txt
del /S /Q extraction\test1_out\*.*
del /S /Q extraction\test2_out\*.*
del miscellaneous\*.out
del miscellaneous\TransComp_MT.txt.html
cls

@echo ============================================================
@echo   Rainbow Batch Tests
@echo   All the results are consolidated in _tests_results.txt
@echo   Answer n+[Enter] when prompted
@echo ============================================================
pause
@echo ON

java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\XSLT_Test.rnb -x oku_xsltransform -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\BOM_Add.rnb -x oku_bomconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\LineBreak_ToDos.rnb -x oku_linebreakconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\EncConv_Test1.rnb -x oku_encodingconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\EncConv_Test2.rnb -x oku_encodingconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\EncConv_Test3.rnb -x oku_encodingconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\TransComp_Test1.rnb -x oku_transcomparison -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\URI_decoded.rnb -x oku_uriconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\URI_encoded_default.rnb -x oku_uriconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\URI_encoded_default_all_extended.rnb -x oku_uriconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\URI_encoded_all_but_uri_marks.rnb -x oku_uriconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\URI_encoded_all_but_uri_marks_and_res.rnb -x oku_uriconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\SearchReplaceFilter_Test.rnb -x oku_searchandreplace -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\SearchReplaceNoFilter_Test.rnb -x oku_searchandreplace -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\SearchReplaceNoFilterRegEx_Test.rnb -x oku_searchandreplace -np
comp miscellaneous\*.out miscellaneous\*.gold >> _tests_results.txt

java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test1_Extraction.rnb -x oku_extraction -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test1_Translation.rnb -x oku_textrewriting -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test1_Merging.rnb -x oku_merging -np
comp extraction\test1_out\done\*.* extraction\test1_gold\*.* >> _tests_results.txt

java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test2_Extraction.rnb -x oku_extraction -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test2_Translation.rnb -x oku_textrewriting -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test2_Merging.rnb -x oku_merging -np
comp extraction\test2_out\done\*.* extraction\test2_gold\*.* >> _tests_results.txt

start _tests_results.txt

