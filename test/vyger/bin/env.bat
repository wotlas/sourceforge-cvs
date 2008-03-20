@ECHO OFF
REM....


set JARS_MODULE=wotlas.suite\build\cluster\modules

set CLIENT_JARS=
set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-client.jar
set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-common.jar
set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-common-jars.jar
set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-ext.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-server.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-server-bots-alice.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-server-bots-alice-jars.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\ext\crimson.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\ext\js.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\ext\jython.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\ext\org.mortbay.jetty.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\ext\servlet.jar

set SERVER_JARS=
REM... set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-client.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-common.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-common-jars.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-ext.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-server.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-server-bots-alice.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas-server-bots-alice-jars.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\ext\crimson.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\ext\js.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\ext\jython.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\ext\org.mortbay.jetty.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\ext\servlet.jar

ECHO CLIENT_JARS=%CLIENT_JARS%
ECHO SERVER_JARS=%SERVER_JARS%

REM... pause