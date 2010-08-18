@ECHO OFF
cls

echo Launching Graphics2d demo...

REM ... set JARS_MODULE=..\build\cluster\modules

set CLIENT_JARS=
REM ... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-libs-graphics2d.jar
REM ... set CLIENT_JARS=%CLIENT_JARS%;%JARS_MODULE%\wotlas-libs-graphics2d-demo.jar
set CLIENT_JARS=%CLIENT_JARS%;wotlas.libs.graphics2d.jar
set CLIENT_JARS=%CLIENT_JARS%;wotlas.libs.graphics2d.demo.jar


java -cp %CLIENT_JARS% wotlas.libs.graphics2d.demo.GraphicsDemo

echo Demo stopped.