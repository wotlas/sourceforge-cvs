/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package wotlas.libs.log;

import wotlas.utils.Tools;

import java.io.*;
import java.awt.event.*;
import javax.swing.Timer;

/** A Log Stream prints message to a log file periodically.
 * 
 * @author Aldiss
 * @see java.io.PrintStream
 */

public abstract class LogStream extends PrintStream implements ActionListener
{
 /*------------------------------------------------------------------------------------*/

  /** Our timer
   */
     private Timer timer;

 /*------------------------------------------------------------------------------------*/

   /** Constructor with file name. The log is saved to disk when the periodBeforeSave
    *  has elapsed.
    *
    * @param logFileName log file to create or use if already existing.
    * @param append if true we add our log message at the beginning of the specified
    *               log file. If false we erase the previous content of the log file.
    * @param periodBeforeSave period of time (in ms) before we save the log to disk.
    * @exception FileNotFoundException if we cannot use or create the given log file.
    */
     public LogStream( String logFileName, boolean append, int periodBeforeSave )
     throws  FileNotFoundException {
         super( new BufferedOutputStream( new FileOutputStream( logFileName, append ),
                                          64*1024 ) );

         println("Log opened the "+Tools.getLexicalDate()+" at "+Tools.getLexicalTime());
         timer = new Timer( periodBeforeSave, this );
         timer.start();
     }

 /*------------------------------------------------------------------------------------*/

  /** To print a string to this stream.
   *
   * @param x string to be printed.
   */
     public void println(String x) {
        super.println(x);
        
        printedText(x);
     }

 /*------------------------------------------------------------------------------------*/

  /** Method called each time text is added to the stream.
   *  Useful if you want to display the log somewhere else.
   *
   * @param x text just printed to log.
   */
    abstract protected void printedText( final String x );

 /*------------------------------------------------------------------------------------*/

  /** To close this stream.
   */
     public void close() {
        super.println("Log cleanly closed the "+Tools.getLexicalDate()+" at "+Tools.getLexicalTime());
        super.close();
     }

 /*------------------------------------------------------------------------------------*/

 /** Timer Event interception.
  */
   synchronized public void actionPerformed( ActionEvent e) {
     if(e.getSource()!=timer)
       return;

     flush(); // save
   }

 /*------------------------------------------------------------------------------------*/

}