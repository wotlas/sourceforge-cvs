@echo off

rem
rem  Client Start-up Options
rem
rem  -classic     : to display the standard log window.
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is '../base').
rem

echo Starting Wotlas Client...
cd ..
cd classes
java -classpath . wotlas.client.ClientDirector -debug
