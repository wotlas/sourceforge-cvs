@echo off

rem
rem  Server Address Setup Start-up Options
rem
rem  -base [dir]  : to tell where to take the data (default is '../base').
rem

echo Starting Server Address Setup...
cd ..
cd classes
java -classpath . wotlas.server.setup.ServerAddressSetup
