#!/bin/sh

cd ..
cd lib
java -jar wotlas-client.jar 2> ../base/logs/error-log-$(date).txt
