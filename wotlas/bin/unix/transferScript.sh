#!/bin/sh

echo A - Creating Environment 

# ------------------------------------------------------------------------

# Edit for your config here (We are using scp for secure file transfer):

SET WEB_NAME=sourceforge.net
SET WEB_SHELL=shell.sf.net
SET SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

SET WEB_LOGIN=mySourceForgeLogin
SET WEB_PASSWORD=mySourceForgePassWord

SET SERVER_ID=0
SET BASE_PATH=../../base

# ------------------------------------------------------------------------

echo B - Sending server-$SERVER_ID.cfg.adr to $WEB_NAME@$WEB_SHELL

scp -batch -pw $WEB_PASSWORD $BASE_PATH/servers/server-$SERVER_ID.cfg.adr $WEB_LOGIN@$WEB_SHELL:$SHELL_PATH

# ------------------------------------------------------------------------

echo Done.
