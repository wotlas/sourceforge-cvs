cls
@ECHO OFF
cls

echo *----------------------------------------------------------*
echo *** WOTLAS PACKAGES ***

echo Building net library...
javac -sourcepath ../src -d ../classes ../src/wotlas/libs/net/*.java
javac -sourcepath ../src -d ../classes ../src/wotlas/libs/net/message/*.java
javac -sourcepath ../src -d ../classes ../src/wotlas/libs/net/personality/*.java


echo Building persistence library...
javac -sourcepath ../src -d ../classes ../src/wotlas/libs/persistence/*.java


echo Building pathfinding library...
javac -sourcepath ../src -d ../classes ../src/wotlas/libs/pathfinding/*.java


echo Building utilities...
javac -sourcepath ../src -d ../classes ../src/wotlas/utils/*.java


echo Building common files...
javac -sourcepath ../src -d ../classes ../src/wotlas/common/universe/*.java


echo *----------------------------------------------------------*
echo *** Net Examples ***


echo Building chat example...
javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes  ../src/test/aldiss/chat/*.java

javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes  ../src/test/aldiss/chat/msgclient/*.java

javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes  ../src/test/aldiss/chat/msgserver/*.java

echo Building performance example...
javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes ../src/test/aldiss/performance/*.java

javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes  ../src/test/aldiss/performance/msgclient/*.java

javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes ../src/test/aldiss/performance/msgserver/*.java

javac -sourcepath ../src/test/aldiss -classpath ../classes -d ../classes  ../src/test/aldiss/performance/common/*.java


echo *----------------------------------------------------------*
echo End.
