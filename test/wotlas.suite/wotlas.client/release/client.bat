@echo off

rem
rem  Client Start-up Options
rem
rem  -classic     : to display the standard log window.
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is '../base' or '../base-ext' when loading from jar).
rem

set CLIENT_JARS=
set CLIENT_JARS=%CLIENT_JARS%;lib\org-openide-util.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.base.common.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.base.randland.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.client.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.common.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.libs.graphics2d.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.libs.net.jar
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.utils.jar


echo Starting Wotlas Client...
java -cp %CLIENT_JARS% wotlas.client.ClientDirector %1 %2 %3 %4 %5 %6 %7 %8 %9