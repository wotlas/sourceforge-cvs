#!/bin/sh
# To build the wotlas javadoc
# Do not modify this file, Edit the file named "package"
# in this directory

javadoc -author -sourcepath ../../src/ -d ../apis -group "Server" "wotlas.server*" -group "Client" "wotlas.client*" -group "Common" "wotlas.common*" -group "Libraries" "wotlas.libs*" -group "Utilities" "wotlas.utils*" @packages

