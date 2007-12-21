@echo off

echo Starting Server Admin Setup...
cd ..
cd classes
java -classpath . wotlas.server.ServerDirector -admin
