# MAKEFILE FOR WOTLAS.
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

SHELL = c:\\command.com


# PACKAGES
# 
# List here all the WOTLAS packages to compile. We groupped the different packages
# to wrestle with Grosoft Windows buffer limitations...
#

UTILS_PACKAGES = wotlas/utils

LIBS_PACKAGES  = wotlas/libs/net \
                 wotlas/libs/net/message \
                 wotlas/libs/net/personality \
                 wotlas/libs/pathfinding \
                 wotlas/libs/persistence

COMMON_PACKAGES = wotlas/common \
                  wotlas/common/universe \
                  wotlas/common/message

SERVER_PACKAGES = wotlas/server

CLIENT_PACKAGES = 


##########################################################################

CLASSDIR = ../classes
SRCDIR = ../src
JAVAC = javac

# We transform our package list into a source list

SRC_UTILS = $(foreach dir,$(UTILS_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_LIBS = $(foreach dir,$(LIBS_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_COMMON = $(foreach dir,$(COMMON_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_SERVER = $(foreach dir,$(SERVER_PACKAGES),$(SRCDIR)/$(dir)/*.java)

SRC_CLIENT = $(foreach dir,$(CLIENT_PACKAGES),$(SRCDIR)/$(dir)/*.java)


# And we compile it all
all: utils libs common server client

utils:
	$(JAVAC) -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_UTILS)

libs:
	$(JAVAC) -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_LIBS)

common:
	$(JAVAC) -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_COMMON)

server:
	$(JAVAC) -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_SERVER)

client:
	$(JAVAC) -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRC_CLIENT)


##########################################################################
