@echo off

rem
rem  Server Start-up Options
rem
rem  -erroronly   : to only display ERROR, CRITICAL and FAILURE messages.
rem  -daemon      : No debug messages are displayed, logs are directly saved to disk.
rem  -debug       : to print a lot of debug information.
rem  -base [dir]  : to tell where to take the data (default is '../base').
rem

cd ..
cd classes

f:\s1studio_jdk\j2sdk1.4.1\jre\..\bin\java -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=admin:1564,suspend=y -Djava.compiler=NONE "-Xbootclasspath:f:\s1studio_jdk\j2sdk1.4.1\jre\..\lib\tools.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\rt.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\i18n.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\sunrsasign.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\jsse.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\jce.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\charsets.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\classes;;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\ext\dnsns.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\ext\ldapsec.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\ext\localedata.jar;f:\s1studio_jdk\j2sdk1.4.1\jre\lib\ext\sunjce_provider.jar" -classpath "d:\JAVA_IDE\system;d:\JAVA_IDE\system;F:\s1studio_jdk\s1studio\system;F:\s1studio_jdk\s1studio\modules\ext\j2ee-1.3.jar;F:\s1studio_jdk\s1studio\modules\ext\jaas-1.0.jar;D:\JAVA_IDE\modules\autoload\activation.jar;F:\s1studio_jdk\s1studio\modules\ext\jms-1.0.2b.jar;F:\s1studio_jdk\s1studio\modules\ext\jta-spec1_0_1.jar;D:\JAVA_IDE\modules\autoload\mail.jar;F:\s1studio_jdk\s1studio\lib\openide.jar;D:\JAVA_IDE\modules\ext\AbsoluteLayout.jar;F:\s1studio_jdk\s1studio\modules\ext\sql.jar;F:\s1studio_jdk\s1studio\modules\ext\rowset.jar;F:\s1studio_jdk\s1studio\lib\ext\jdbc20x.zip;F:\s1studio_jdk\s1studio\modules\ext\junit.jar;F:\s1studio_jdk\s1studio\modules\ext\junit-ext.jar;F:\s1studio_jdk\s1studio\modules\ext\servlet-2.3.jar;F:\s1studio_jdk\s1studio\beans\TimerBean.jar;D:\JAVA_IDE\beans\TimerBean.jar;d:\JAVA_IDE\tomcat401_base;D:\CVS data of WOTLAS Diego\wotlas\classes" wotlas.server.ServerDirector
