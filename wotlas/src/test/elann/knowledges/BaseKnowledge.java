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
 
package wotlas.common.knowledges;

//import java.rmi.Remote;
//import java.rmi.RemoteException;

import wotlas.common.character.levels; // THIS CLASS DOES NOT EXISTS

/** 
 * This is the base class for all knowledge classes.<br>
 * This class defines common attributes.
 * @author Elann
 */

public class BaseKnowledge
{

 /*------------------------------------------------------------------------------------*/



  /** The name of the knowledge.
   */
      private String knowledgeName;

  /** The kind of knowledge (the base class name).
   */
      private String kindName;
	  
  /** The maximum duration of use.
   */
      private short duration;

  /** The required levels. THE CLASS LEVEL DOESN'T EXISTS 
   */
      private Level[] requiredLevels;

  /** The pre-required knowledges to learn this one.
   */
      private BaseKnowledge[] preRequiredKnowledges;
	  

 /*------------------------------------------------------------------------------------*/

  /** Default constructor
   */
     public BaseKnowledge();
	 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the pre-required knowledges to learn this one.
   * @return preRequiredKnowledges
   */
    public BaseKnowledge[] getPreRequiredKnowledges()
    {
        return preRequiredKnowledges;
    }

  /** Set the pre-required knowledges to learn this one.
   * @param preRequiredKnowledges the new pre-required knowledges
   */
    public void setPreRequiredKnowledges(BaseKnowledge[] preRequiredKnowledges)
    {
        this.preRequiredKnowledges=preRequiredKnowledges;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

