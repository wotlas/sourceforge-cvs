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

//import wotlas.common.character.Levels; // THIS CLASS DOES NOT EXISTS

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
      protected String knowledgeName;

  /** The kind of knowledge (the base class name).
   */
      protected String kindName;
	  
  /** The maximum duration of use.
   */
      protected short maxDuration;

  /** The required levels. THE CLASS LEVEL DOESN'T EXISTS 
   */
      protected String[]/*Level[]*/ requiredLevels;

  /** The pre-required knowledges to learn this one.
   */
      protected BaseKnowledge[] preRequiredKnowledges;
	  

 /*------------------------------------------------------------------------------------*/

  /** Default constructor
   */
     public BaseKnowledge()
	 {
	  maxDuration=-1;
	  knowledgeName="default knowledge";
	  kindName="default kind";
	  preRequiredKnowledges=null;
	  requiredLevels=null;
	 }
	 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the knowledge name.
   * @return knowledgeName
   */
    public String getKnowledgeName()
    {
        return knowledgeName;
    }

  /** Set the knowledge name.
   * @param knowledgeName the new knowledge name
   */
    public void setKnowledgeName(String knowledgeName)
    {
        this.knowledgeName=knowledgeName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the knowledge kind name.
   * @return kindName
   */
    public String getKindName()
    {
        return kindName;
    }

  /** Set the knowledge kind name.
   * @param kindName the new knowledge kind name
   */
    public void setKindName(String kindName)
    {
        this.kindName=kindName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the maximum duration of continuous use.
   * @return maxDuration
   */
    public short getMaxDuration()
    {
        return maxDuration;
    }

  /** Set the maximum duration of continuous use.
   * @param maxDuration the new maximum duration
   */
    public void setMaxDuration(short maxDuration)
    {
        this.maxDuration=maxDuration;
    }

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

  /** Get the pre-required levels to learn this knowledge.
   * @return preRequiredLevels
   */
    public String[]/*Level[]*/ getRequiredLevels()
    {
        return requiredLevels;
    }

  /** Set the pre-required levels to learn this knowledge.
   * @param preRequiredLevels the new pre-required levels
   */
    public void setRequiredLevels(String[]/*Level[]*/ requiredLevels)
    {
        this.requiredLevels=requiredLevels;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

