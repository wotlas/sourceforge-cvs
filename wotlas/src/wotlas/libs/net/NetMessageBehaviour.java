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
 
package wotlas.libs.net;


/** 
 * A NetMessageBehaviour contains the code associated to a NetMessage.
 * A MessageBehaviour must extend the class of its associated NetMessage
 * and implement the NetMessageBehaviour interface.
 *<br>
 * Example: <pre>
 *
 *    class PasswordMsgBehaviour extends PasswordMsg implements NetMessageBehaviour {
 *
 *      public PasswordMsgBehaviour() {
 *        super();
 *      }
 *
 *      public void doBehaviour( Object context ) {
 *           System.out.println("My password:"+ pswd );	
 *	}
 *    }
 *</pre>
 *
 * The empty constructor is mandatory and must call the empty superclass constructor.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetReceiver
 * @see wotlas.libs.net.NetMessage
 */

public interface NetMessageBehaviour
{

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Where you put the code associated to your NetMessage.
   *  The context object passed as an argument by the NetReceiver depends on
   *  your redefinition of NetServer or NetClient (method handleNewConnection).
   *
   * @param context an object giving specific access to other objects needed to process
   *        this message.
   */
     public void doBehaviour( Object context );

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

