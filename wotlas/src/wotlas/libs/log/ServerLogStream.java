/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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

import java.io.*;

/** A Server Log Stream prints messages to a log file every two minutes and also prints
 *  messages on the standard err output.
 * 
 * @author Aldiss
 * @see wotlas.libs.log.LogStream
 */

public class ServerLogStream extends LogStream
{
 /*------------------------------------------------------------------------------------*/

   /** Constructor with file name. The log is saved to disk every 2 minutes.
    *
    * @param logFileName log file to create or use if already existing.
    * @exception FileNotFoundException if we cannot use or create the given log file.
    */
     public ServerLogStream( String logFileName ) throws FileNotFoundException {
          super( logFileName, false, 120*1000 );
     }

 /*------------------------------------------------------------------------------------*/

  /** Method called each time text is added to the stream.
   *  Useful if you want to display the log somewhere else.
   *
   * @param x text just printed to log.
   */
    protected void printedText( final String x ) {
    	System.err.println( x );
    }

 /*------------------------------------------------------------------------------------*/

}