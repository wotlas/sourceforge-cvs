#!/bin/sh
#
#      Script to update a wotlas server's IP on the wotlas.sourceforge.net web site
#      We are using SCP for secure file transfer. Please read our wotlas-faq.html
#      question 1.20 to learn how to use this script.
#

SET WEB_NAME=mylogin@sourceforge.net
SET SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

SET WOTLAS_HOME=/home/user/wotlas
SET SERVER_ID=0


# 1 - Find my Internet IP
curl --silent http://checkip.dyndns.org | grep "Current IP Address" | awk 
'{print $4}' > $WOTLAS_HOME/server-$SERVER_ID.cfg.adr

# Send the .cfg.adr file to wotlas.sourceforge.net
scp -F $WOTLAS_HOME/config.wotlas $WOTLAS_HOME/server-$SERVER_ID.cfg.adr $WEB_NAME:$SHELL_PATH
