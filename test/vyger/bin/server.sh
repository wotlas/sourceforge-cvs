#!/bin/sh

cd ..
cd classes
java -classpath .;..\lib\jython.jar wotlas.server.ServerDirector
