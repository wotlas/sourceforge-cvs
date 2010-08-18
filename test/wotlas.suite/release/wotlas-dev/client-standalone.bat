@echo off

rem
rem  Client Start-up Options
rem
rem  -classic     : to display the standard log window.
rem  -debug       : to print a lot of debug information.
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
set CLIENT_JARS=%CLIENT_JARS%;lib\wotlas.server.jar


echo Starting Wotlas Client...
java -cp %CLIENT_JARS% wotlas.client.ClientDirector -debug
