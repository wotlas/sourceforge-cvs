@echo off

rem The visibility level.
rem May be public, private, protected, package
rem ==========================================
set visib=package

rem Add verbose to show more
rem Add quiet to show less
rem ======================
set verbose=

rem The main title
rem ==============
set windowtitle="Object Management System"

rem The destination directory
rem =========================
set dir=javadoc

rem The source files
rem No line breaks
rem ==============
set src=objects\*.java objects\armors\*.java objects\containers\*.java objects\interfaces\*.java objects\inventories\*.java objects\magicals\*.java objects\usefuls\*.java objects\valueds\*.java objects\weapons\*.java


rem The command line
rem ================
javadoc %verbose% -d %dir% -author -nodeprecated -%visib% -windowtitle %windowtitle% %src%

echo on