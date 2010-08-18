@echo off
#set buildfile=build-products.xml
#

cls
set buildfile=build-release.xml
del build.log

REM ... ant -d -v -f %buildfile% 

REM ... 
call ant -d -v -f %buildfile% -l build.log %1 %2 %3 %4 %5 %6 %7 %8 %9
REM ... 
notepad build.log



