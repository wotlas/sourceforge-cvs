# MAKEFILE FOR WOTLAS.
#
# WARNING THIS BUILD SOLUTION IS DEPRECATED NOW. PREFER ANT.
#
# You can find and download GNU MAKE for Windows at
# the following URL : http://www.tardis.ed.ac.uk/~skx/win/Free15.html#gnumake
# or if the link is dead go to www.GNUSoftware.com .

# SHELL
#
# On Windows 95/98, SHELL=c:\\command.com
# On Windows NT & 2000, SHELL=c:\\winnt\cmd.exe
# On Unix gnu make should find your sh automatically. 
#

#SHELL=c:\\command.com


# PACKAGES
# 
# List here all the WOTLAS packages to compile. We groupped the different packages
# to wrestle with Grosoft Windows buffer limitations...
#

UTILS_PACKAGES = wotlas/utils \
                 wotlas/utils/aswing

LIBS_PACKAGES  = wotlas/libs/net \
                 wotlas/libs/net/message \
                 wotlas/libs/net/personality \
                 wotlas/libs/pathfinding \
                 wotlas/libs/wizard \
                 wotlas/libs/wizard/step

LIBS_PACKAGES2 = wotlas/libs/graphics2D \
                 wotlas/libs/graphics2D/policy \
                 wotlas/libs/graphics2D/drawable \
                 wotlas/libs/graphics2D/filter \
                 wotlas/libs/persistence \
                 wotlas/libs/sound \
                 wotlas/libs/log

COMMON_PACKAGES = wotlas/common \
                  wotlas/common/universe \
                  wotlas/common/character \
                  wotlas/common/message/account \
                  wotlas/common/router

COMMON_PACKAGES2 = wotlas/common/message/description \
                   wotlas/common/message/movement \
                   wotlas/common/chat \
                   wotlas/common/message/chat \
                   wotlas/common/movement

SERVER_PACKAGES = wotlas/server \
                  wotlas/server/setup \
                  wotlas/server/message/account \
                  wotlas/server/message/description \
                  wotlas/server/message/gateway

SERVER_PACKAGES2 = wotlas/server/message/movement \
                   wotlas/server/message/chat \
                   wotlas/server/chat \
                   wotlas/server/router \
                   wotlas/server/bots

CLIENT_PACKAGES = wotlas/client \
                  wotlas/client/screen \
                  wotlas/client/message/account \
                  wotlas/client/message/description \
                  wotlas/client/gui

CLIENT_PACKAGES2 = wotlas/client/message/movement \
                   wotlas/client/message/chat \
                   wotlas/client/screen/plugin

##########################################################################

CLASSDIR = ../classes
SRCDIR = ../src
JAVAC = javac

# We transform our package list into a source list

SRC_UTILS = $(foreach dir,$(UTILS_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_LIBS = $(foreach dir,$(LIBS_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_LIBS2 = $(foreach dir,$(LIBS_PACKAGES2),$(SRCDIR)/$(dir)/*.java)

SRC_COMMON = $(foreach dir,$(COMMON_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_COMMON2 = $(foreach dir,$(COMMON_PACKAGES2),$(SRCDIR)/$(dir)/*.java)

SRC_SERVER = $(foreach dir,$(SERVER_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_SERVER2 = $(foreach dir,$(SERVER_PACKAGES2),$(SRCDIR)/$(dir)/*.java)

SRC_CLIENT = $(foreach dir,$(CLIENT_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_CLIENT2 = $(foreach dir,$(CLIENT_PACKAGES2),$(SRCDIR)/$(dir)/*.java)


# And we compile it all
all: utils libs libs2 common common2 server server2 client client2

utils:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_UTILS)

libs:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_LIBS)

libs2:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_LIBS2)

common:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_COMMON)

common2:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_COMMON2)

server:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_SERVER)

server2:
	$(JAVAC) -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_SERVER2)

client:
	$(JAVAC) -deprecation -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_CLIENT)

client2:
	$(JAVAC) -deprecation -O -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_CLIENT2)

##########################################################################
