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
# List here all the WOTLAS packages to compile.
#

PACKAGES  = wotlas/libs/net \
            wotlas/libs/net/message \
            wotlas/libs/net/personality \
            wotlas/libs/pathfinding \
            wotlas/libs/persistence \
            wotlas/utils

##########################################################################

CLASSDIR = ../classes
SRCDIR = ../src
JAVAC = javac

# We transform our package list into a source list
SRCS = $(foreach dir,$(PACKAGES),$(SRCDIR)/$(dir)/*.java)

# And we compile it all
all:
	$(JAVAC) -sourcepath $(SRCDIR) -d $(CLASSDIR) $(SRCS)

##########################################################################
