@echo off

rem
rem  Client Start-up Options
rem
rem  -classic     : to display the standard log window.
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is 'base').
rem

call env.bat
cd ..

echo Starting Wotlas Client...
java -classpath %CLIENT_JARS% wotlas.client.ClientDirector -debug
