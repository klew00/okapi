del /Q example01\target\*.*
call javac -d example01/target example01/src/main/java/*.java -cp ../lib/okapi-lib-0.3-SNAPSHOT.jar
call jar cfm example01/target/okapi-example-01-0.3-SNAPSHOT.jar example01/META-INF/MANIFEST.MF -C example01/target .

del /Q example02\target\*.*
call javac -d example02/target example02/src/main/java/*.java -cp ../lib/okapi-lib-0.3-SNAPSHOT.jar
call jar cfm example02/target/okapi-example-02-0.3-SNAPSHOT.jar example02/META-INF/MANIFEST.MF -C example02/target .

del /Q example03\target\*.*
call javac -d example03/target example03/src/main/java/*.java -cp ../lib/okapi-lib-0.3-SNAPSHOT.jar
call jar cfm example03/target/okapi-example-03-0.3-SNAPSHOT.jar example03/META-INF/MANIFEST.MF -C example03/target .

del /Q example04\target\*.*
call javac -d example04/target example04/src/main/java/*.java -cp ../lib/okapi-lib-0.3-SNAPSHOT.jar
call jar cfm example04/target/okapi-example-04-0.3-SNAPSHOT.jar example01/META-INF/MANIFEST.MF -C example04/target .

del /Q example05\target\*.*
call javac -d example05/target example05/src/main/java/*.java -cp ../lib/okapi-lib-0.3-SNAPSHOT.jar
call jar cfm example05/target/okapi-example-05-0.3-SNAPSHOT.jar example01/META-INF/MANIFEST.MF -C example05/target .

pause
