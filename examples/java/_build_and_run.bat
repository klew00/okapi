call ant
pause
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.html -s pseudo myFile.pseudo.html
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.html -s upper myFile.upper.html
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.html -s pseudo -s upper myFile.both.html
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.odt -s pseudo
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.properties -s pseudo
java -cp .;../../lib/okapi-lib.jar;Main.jar Main myFile.xml -s pseudo
pause
