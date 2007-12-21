@echo off
set ENVTEST=This just checks whether there is additional environment space.
if "%ENVTEST%"=="" goto increase_env
set ENVTEST=

rem --------------------------------------------------------------------------------------------------------------------------
rem
rem         EDIT FOR YOUR CONFIG HERE    ( We are using pscp for secure file transfer ):
rem

SET WEB_NAME=sourceforge.net
SET WEB_SHELL=shell.sf.net
SET SHELL_PATH=/home/groups/w/wo/wotlas/htdocs/game

SET WEB_LOGIN=You will be prompted for your login.
SET WEB_PASSWORD=You will be prompted for your passsword.

SET SERVER_ID=0
SET BASE_PATH=../../base

rem
rem   If you want to use another transfer program note that it must support a batch mode,
rem   because this script is used without any user prompt ( even for password ).
rem
rem --------------------------------------------------------------------------------------------------------------------------
rem
rem        PSCP FILE TRANSFER     (  If you have a firewall, please configure it for ssh )
rem

echo Sending server-%SERVER_ID%.cfg.adr to %WEB_NAME%@%WEB_SHELL%

pscp -batch -pw %WEB_PASSWORD% %BASE_PATH%/servers/server-%SERVER_ID%.cfg.adr %WEB_LOGIN%@%WEB_SHELL%:%SHELL_PATH%

rem --------------------------------------------------------------------------------------------------------------------------

echo+
echo Done.
goto end

:increase_env
echo Increasing environment space
if not exist %comspec% goto nocomspec

:comspec
rem %comspec% points to an existing command interpreter
%comspec% /E:4096 /C transferScript.bat %1 %2 %3 %4 %5
goto end

:nocomspec
rem %comspec% is not set, trying command.com
command /E:4096 /C transferScript.bat %1 %2 %3 %4 %5
goto end

:end