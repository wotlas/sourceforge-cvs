#!/bin/sh

echo A - Environment Settings

WEB_NAME=Sourceforge.net
WEB_LOGIN=mySourceForgeLogin
WEB_SHELL=shell.sf.net
SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

echo B - Server Table Local Update

cd ..
cd classes
java -classpath . wotlas.server.setup.UpdateServerTable

cd ..
cd bin
cd unix

echo ""
echo C - Sending html news to $WEB_NAME

echo news.html ---O $WEB_LOGIN@$WEB_SHELL
scp ../../docs/online-news/news.html $WEB_LOGIN@$WEB_SHELL:$SHELL_PATH

echo ""
echo Done.
