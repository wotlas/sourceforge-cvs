@echo off

set ENVTEST=This just checks whether there is additional environment space.
if "%ENVTEST%"=="" goto increase_env
set ENVTEST=

rem ------------------------------------------------------------------------------
rem EDIT FOR YOUR CONFIG :

rem Enter your java JDK directory and remove the 'rem'
set JAVA_HOME=c:\progra~1\javasoft\j2se1.4

rem ------------------------------------------------------------------------------

set ANT_HOME=../

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem Need to check if we are using the 4NT shell...
if "%eval[2+2]" == "4" goto setup4NT

rem On NT/2K grab all arguments at once
set ANT_CMD_LINE_ARGS=%*
goto doneStart

:setup4NT
set ANT_CMD_LINE_ARGS=%$
goto doneStart

:win9xStart
rem Slurp the command line arguments.  This loop allows for an unlimited number of 
rem agruments (up to the command line limit, anyway).

set ANT_CMD_LINE_ARGS=

:setupArgs
if %1a==a goto doneStart
set ANT_CMD_LINE_ARGS=%ANT_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart
rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.

if "%ANT_HOME%"=="" goto noAntHome
goto checkJava

:noAntHome
echo ANT_HOME is not set and ant could not be located. Please set ANT_HOME.
goto end

:checkJava
set _JAVACMD=%JAVACMD%
set LOCALCLASSPATH=%CLASSPATH%
for %%i in ("%ANT_HOME%\lib\*.jar") do call "%ANT_HOME%\build\lcp.bat" %%i

if "%JAVA_HOME%" == "" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java
if exist "%JAVA_HOME%\lib\tools.jar" call "%ANT_HOME%\build\lcp.bat" %JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" call "%ANT_HOME%\build\lcp.bat" %JAVA_HOME%\lib\classes.zip
goto checkJikes

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java
echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If build fails because sun.* classes could not be found
echo   you will need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

:checkJikes
if not "%JIKESPATH%" == "" goto runAntWithJikes

:runAnt
"%_JAVACMD%" -classpath "%LOCALCLASSPATH%" -Dant.home="%ANT_HOME%" %ANT_OPTS% org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%
goto end

:runAntWithJikes
echo Using Jikes to compile...
"%_JAVACMD%" -classpath "%LOCALCLASSPATH%" -Dant.home="%ANT_HOME%" -Djikes.class.path="%JIKESPATH%" %ANT_OPTS% org.apache.tools.ant.Main %ANT_CMD_LINE_ARGS%
goto end

:increase_env
echo Increasing environment space
if not exist %comspec% goto nocomspec

:comspec
rem %comspec% points to an existing command interpreter
%comspec% /E:4096 /C build.bat %1 %2 %3 %4 %5
goto end

:nocomspec
rem %comspec% is not set, trying command.com
command /E:4096 /C build.bat %1 %2 %3 %4 %5
goto end

:end
set LOCALCLASSPATH=
set _JAVACMD=
set ANT_CMD_LINE_ARGS=

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
if exist "%HOME%\antrc_post.bat" call "%HOME%\antrc_post.bat"

