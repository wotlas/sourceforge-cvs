@echo off
echo A - Environment Settings

SET WEB_NAME=Sourceforge.net
SET WEB_LOGIN=mySourceForgeLogin
SET WEB_SHELL=shell.sf.net
SET SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

echo+
echo if you just got environment error messages, quit, right-click on updateServerTable.bat, select Properties, go the the Memory tab and change the initial environment parameter to 4096. Click Ok and double-click on the program shortcut that has been created.
echo+
pause

echo B - Server Table Local Update

cd ..
cd classes
java -classpath . wotlas.server.setup.UpdateServerTable
pause

cd ..
cd bin
cd win32

echo+
echo C - Sending server configs to %WEB_NAME%

echo server-1.cfg ---O %WEB_LOGIN%@%WEB_SHELL%
pscp ../../base/servers/server-1.cfg %WEB_LOGIN%@%WEB_SHELL%:%SHELL_PATH%

echo server-2.cfg ---O %WEB_LOGIN%@%WEB_SHELL%
pscp ../../base/servers/server-2.cfg %WEB_LOGIN%@%WEB_SHELL%:%SHELL_PATH%

echo+
echo D - Sending server table to %WEB_NAME%

echo server-table.cfg ---O %WEB_LOGIN%@%WEB_SHELL%
pscp ../../src/config/remote/server-table.cfg %WEB_LOGIN%@%WEB_SHELL%:%SHELL_PATH%

echo+
echo Done.
