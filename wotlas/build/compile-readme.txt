#
# How do I compile the wotlas project ?
#

Well, You have 2 solutions :


SOLUTION 1 - ANT
----------------

ANT does exactly what Gnu Make does but :

   (1) it's a Java utility so it will run on any system
   (2) the makefile is replaced by a build.xml which is EASIER to write and as much powerful.
   (3) Ant has won the JavaWorld Editors' Choice 2001 Award for Most Useful Java
       Community-Developed Technology. It's a technology that has reached maturity.

If you want to learn how Jakarta Ant works, here is a great link :
         http://www.jfipa.org/publications/ANTGuide

To compile wotlas :

  1 - Edit build.bat or build.sh and enter your JDK complete path where you asked to.
  2 - Run build.bat or build.sh , that's all !

  For more options, open a shell window and enter 'build usage' to see the list of available targets.
  Here are some :

      build all      ( same as "build" alone )
      build server   ( builds the whole server )
      build client    ( builds the whole client )
      build libs  ...  ( builds all the wotlas libs )
      build clean      ( removes the already compiled classes )

If you use Javac you'll witness that Ant is THREE times slower than GNU Make. So if you want to speed this up use jikes ! It's a java compiler (developed by IBM) which is TEN times faster than javac.

To compile wotlas with jikes :

 (1) Download jikes from :
     http://oss.software.ibm.com/developerworks/opensource/jikes/

 (2) copy the jikes.exe to your c:\windows ( or jikes to /usr/bin )

 (3) edit build.xml and replace 
         <property name="build.compiler" value="classic"/>
     by
         <property name="build.compiler" value="jikes"/>



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
