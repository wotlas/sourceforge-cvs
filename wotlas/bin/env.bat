@ECHO OFF
REM....


set JARS_MODULE=dist

set CLIENT_JARS=

set CLIENT_JARS=%CLIENT_JARS%;build\classes
REM... Need to fix ResourceManager before !
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas.jar

REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\ant.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\crimson.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\jaxp.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\js.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\jython.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\org.mortbay.jetty.jar
REM... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\lib\servlet.jar

set SERVER_JARS=

set SERVER_JARS=%SERVER_JARS%;build\classes
REM... Need to fix ResourceManager before !
REM... set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\wotlas.jar

set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\ant.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\crimson.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\jaxp.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\js.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\jython.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\org.mortbay.jetty.jar
set SERVER_JARS=%SERVER_JARS%;%JARS_MODULE%\lib\servlet.jar

ECHO CLIENT_JARS=%CLIENT_JARS%
ECHO SERVER_JARS=%SERVER_JARS%

REM... pause