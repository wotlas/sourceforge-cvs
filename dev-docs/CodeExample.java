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
 
package wotlas.server;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;


import wotlas.common.net.Message;
import wotlas.common.io.MapLoader;
import wotlas.utils.Tools;


/** 
 * A short description of what this class does.
 * 
 * @author Aldiss
 * @see wotlas.common.io.MapLoader 
 * @see java.net.Socket
 */

public class CodeExample extends AnotherClass implements MyInterface1, MyInterface2
{

 /*------------------------------------------------------------------------------------*/


  /** A short description.
   */
      private short my_var;


 /*------------------------------------------------------------------------------------*/

  /** Constructor short description
   *
   * @param var1 a short description
   * @param var2 a short description
   */
     public CodeExample( int var1, int var2 ) {
        // A simple init.
           my_var = 13;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Method Short Description.
   *
   * @param name the name from which we want an ID.
   * @return the object ID
   * @exception StrangeException something went wrong with this call
   */
    public int getAnID( String name ) throws StrangeException
    {
       // Empty String ?
          if( name.length()>0 )
              return name.length() + my_var;

        return my_var;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

