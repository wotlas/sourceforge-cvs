#!/bin/sh

# ------------------------------------------------------------------------

# Edit for your config here (We are using scp for secure file transfer):
#
#      We are using SCP for secure file transfer. Please read our wotlas-faq.html
#      question 1.20 to learn how to use this script.
#

SET WEB_NAME=mylogin@sourceforge.net
SET SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

SET WOTLAS_HOME=/home/myapps/wotlas
SET SERVER_ID=0

# Send the .cfg.adr file to wotlas.sourceforge.net
scp -F $WOTLAS_HOME/config.wotlas $WOTLAS_HOME/base/servers/server-$SERVER_ID.cfg.adr $WEB_NAME:$SHELL_PATH

