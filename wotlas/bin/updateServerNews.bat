@echo off
echo A - Environment Settings

SET WEB_NAME=Sourceforge.net
SET WEB_LOGIN=mySourceForgeLogin
SET WEB_SHELL=shell.sf.net
SET SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

call env.bat

echo+
echo if you just got environment error messages, quit, right-click on updateServerTable.bat, select Properties, go the the Memory tab and change the initial environment parameter to 4096. Click Ok and double-click on the program shortcut that has been created.
echo+
pause

cd ..
cd bin
cd win32

echo B - Sending html news to %WEB_NAME%
echo news.html ---O %WEB_LOGIN%@%WEB_SHELL%
pscp ../../docs/online-news/news.html %WEB_LOGIN%@%WEB_SHELL%:%SHELL_PATH%

echo+
echo Done.
