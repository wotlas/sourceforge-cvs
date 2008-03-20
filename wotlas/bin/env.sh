#!/bin/sh

set JARS_MODULE=dist

set CLIENT_JARS=

; Choose between the use of build/classes and the standard base directory
;... set CLIENT_JARS=$CLIENT_JARS:build/classes

;or the wotlas jar and the dist/base-ext directory :
set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/wotlas.jar

;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/ant.jar
;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/crimson.jar
;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/jaxp.jar
;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/js.jar
;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/jython.jar
;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/org.mortbay.jetty.jar
;... set CLIENT_JARS=$CLIENT_JARS:$JARS_MODULE/lib/servlet.jar

set SERVER_JARS=

set SERVER_JARS=$SERVER_JARS:build/classes

; Need to fix ResourceManager before !
;... set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/wotlas.jar

set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/ant.jar
set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/crimson.jar
set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/jaxp.jar
set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/js.jar
set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/jython.jar
set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/org.mortbay.jetty.jar
set SERVER_JARS=$SERVER_JARS:$JARS_MODULE/lib/servlet.jar

ECHO CLIENT_JARS=$CLIENT_JARS
ECHO SERVER_JARS=$SERVER_JARS

; pause