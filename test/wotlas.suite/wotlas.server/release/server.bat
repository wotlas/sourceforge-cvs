@echo off

rem
rem  Server Start-up Options
rem
rem  -erroronly   : to only display ERROR, CRITICAL and FAILURE messages.
rem  -daemon      : No debug messages are displayed, logs are directly saved to disk.
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is '../base' or '../base-ext' when loading from jar).
rem


set SERVER_JARS=
set SERVER_JARS=%SERVER_JARS%;lib\org-openide-util.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.base.common.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.base.randland.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.client.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.common.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.libs.graphics2d.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.libs.net.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.server.jar
set SERVER_JARS=%SERVER_JARS%;lib\wotlas.utils.jar

echo Starting Wotlas Server...
java -cp %SERVER_JARS% wotlas.server.ServerDirector  %1 %2 %3 %4 %5 %6 %7 %8 %9

