@echo off

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

echo Starting Wotlas World Generator...
java -classpath %SERVER_JARS% wotlas.server.setup.WorldGenerator
