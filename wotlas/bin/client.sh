#!/bin/sh

cd ..
cd classes
java -classpath . wotlas.client.ClientDirector -classic 2> ../base/logs/error-log-$(date).txt
