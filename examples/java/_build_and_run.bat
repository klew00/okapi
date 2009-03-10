call ant
pause
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.html -s pseudo out_pseudo.html
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.html -s upper out_uper.html
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.html -s pseudo -s upper out_both.html
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.odt -s pseudo
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.properties -s pseudo
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.xml -s pseudo
pause
