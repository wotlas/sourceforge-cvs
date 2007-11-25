@echo off

rem
rem  Server Start-up Options
rem
rem  -erroronly   : to only display ERROR, CRITICAL and FAILURE messages.
rem  -daemon      : No debug messages are displayed, logs are directly saved to disk.
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is 'base').
rem

call env.bat
cd ..

echo Starting Wotlas Server...
java -classpath %SERVER_JARS% wotlas.server.ServerDirector
