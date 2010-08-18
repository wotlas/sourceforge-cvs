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
echo C - Sending server configs to $WEB_NAME

echo server-1.cfg ---O $WEB_LOGIN@$WEB_SHELL
scp ../../base/servers/server-1.cfg $WEB_LOGIN@$WEB_SHELL:$SHELL_PATH

echo server-2.cfg ---O $WEB_LOGIN@$WEB_SHELL
scp ../../base/servers/server-2.cfg $WEB_LOGIN@$WEB_SHELL:$SHELL_PATH

echo ""
echo D - Sending server table to $WEB_NAME

echo server-table.cfg ---O $WEB_LOGIN@$WEB_SHELL
pscp ../../base/configs/remote/server-table.cfg $WEB_LOGIN@$WEB_SHELL:$SHELL_PATH

echo ""
echo Done.
