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
 
package wotlas.common.objects.usefuls;

import wotlas.common.objects.interfaces.*;

/** 
 * The class of parchments.
 * 
 * @author Elann
 * @see wotlas.common.objects.usefuls.Document
 */

public class Parchment extends Document
{

 /*------------------------------------------------------------------------------------*/

 /** Is it on ?
  */
  private boolean equipped;
  
 /** The text of the parchment. HTML formatted. Perhaps a Chapter ?
  */
  private String text;

 
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public Parchment()
	{
	 this.className="Parchment";
	 this.objectName="default parchment";
	}															
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Use the object.<br> 
   */
    public void use()
	{
	 makeReady();
	}

  /** Put the object "on". Needed before action is possible.
   */
    public void equip()
	{
	 equipped=true;
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
	
  /** Ready the document.
   * Unfold for a parchment, open for a book, ...  
   */
    public void makeReady()
	{
	 /* open the GUI ? */
	}
	
 
  /** Write to the document.
   * @param text the text to write
   */
    public void writeText(String text)
	{
	 /* append ... */
	}
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the document's text.
   * @return current readable text
   */
    public String readText()
	{
	 return text;
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

