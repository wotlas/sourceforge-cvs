@echo off

rem
rem  Server Setup Start-up Options
rem
rem  -base [dir]  : to tell where to take the data (default is '../base').
rem

echo Starting Register Utility...
cd ..
cd classes
java -classpath . wotlas.server.setup.ServerSetup
