@echo off

rem
rem  EditTile Start-up Options
rem
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is '../base').
rem

echo Starting Wotlas editor...
cd ..
cd classes
java -classpath . wotlas.editor.EditTile -debug
