#
# How do I compile the wotlas project ?
#

Well, You have 2 solutions :


SOLUTION 1 - ANT
----------------



SOLUTION 2 - GNU MAKE
---------------------

Note that this solution is no longuer supported... Our makefile has not been updated since wotlas v1.2.3. If you still want to use Gnu Make here are the steps  to follow :

1 - Make sure you have the jdk1.3 (or higher version) already installed. You don't need to edit your CLASSPATH to compile wotlas.

2 - You need the GNU Make utility. For unix you should already have it. For Windows it's a little more difficult to find. So here are four URLs :

    http://wotlas.sf.net/gnu
    http://www.delorie.com/djgpp
    http://www.tardis.ed.ac.uk/~skx/win/Free15.html#gnumake
    http://www.GNUSoftware.com

For windows put make.exe in c:\windows or any directory specified in your PATH variable. For unix put make in /usr/bin.

3 - Edit the wotlas/build/makefile. The ONLY line you need to eventually modify is the SHELL variable. Everything is explained in the "makefile" file of this directory.

4 - Open a shell window ( DOS shell for Windows ), go to the build/ directory and enter "make". That's all ! All the compiled classes are placed in wotlas/classes/ .
