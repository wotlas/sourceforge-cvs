#############################################
### How do I compile the wotlas project ? ###
#############################################

1 - Make sure you have the jdk1.3 (or higher version) already installed. You don't need to edit your CLASSPATH to compile wotlas.

2 - You need the GNU Make genious utility. For unix you should already have it. For Windows it's a little more difficult to find. So here are two URLs : http://www.tardis.ed.ac.uk/~skx/win/Free15.html#gnumake
# or if the link is dead go to www.GNUSoftware.com. For windows put make.exe in c:\windows or any directory specified in your PATH variable. For unix put make in /usr/bin.

3 - Edit the wotlas/build/makefile. The ONLY line you need to eventually modify is the SHELL variable. Everything is explained in the "makefile" file.

4 - Open a shell window ( DOS shell for Windows ), go to the wotlas/build/ directory and enter "make". That's all ! All the compiled classes are placed in wotlas/classes/ .
