@echo OFF
del _tests_results.txt
del /S /Q extraction\test1\*.*
del miscellaneous\*.out
cls

@echo ============================================================
@echo   Rainbow Batch Tests
@echo   All the results are consolidated in _tests_results.txt
@echo   Answer n+[Enter] when prompted
@echo ============================================================
pause
@echo ON

java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\BOM_Add.rnb -x oku_bomconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\LineBreak_ToDos.rnb -x oku_linebreakconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\EncConv_Test1.rnb -x oku_encodingconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\EncConv_Test2.rnb -x oku_encodingconversion -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p miscellaneous\EncConv_Test3.rnb -x oku_encodingconversion -np
comp miscellaneous\*.out miscellaneous\*.gold >> _tests_results.txt

java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test1_Extraction.rnb -x oku_extraction -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test1_Translation.rnb -x oku_textrewriting -np
java -jar ..\dist_win32-x86\lib\rainbow.jar -p extraction\Test1_Merging.rnb -x oku_merging -np
comp extraction\test1_out\done\*.* extraction\test1_gold\*.* >> _tests_results.txt

start _tests_results.txt

