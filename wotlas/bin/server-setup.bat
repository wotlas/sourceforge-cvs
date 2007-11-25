@echo off


call env.bat
cd ..

echo Starting Wotlas Admin Setup...
java -classpath %SERVER_JARS%  wotlas.server.ServerDirector -admin
