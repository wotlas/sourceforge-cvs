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
 * @see wotlas.common.objects.usefuls.Chapter
 */

public class Parchment extends Document
{

 /*------------------------------------------------------------------------------------*/

 /** The text of the parchment.
  */
  protected Chapter text;
  
 /** The name of the parchment.
  */
  protected String title;

 
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public Parchment()
	{
	 className="Parchment";
	 objectName="default parchment";
	 title="Untitled";
	 
	 text=new Chapter();
	 text.setChapterTitle(title);
	}															
 
  /** Constructor with title.
   * @param title the parchment's title 
   */			
    public Parchment(String title)
	{
	 className="Parchment";
	 objectName="default parchment";
	 this.title=title;
	 
	 text=new Chapter();
	 text.setChapterTitle(title);
	}															

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** Use the object.<br> 
   */
    public void use()
	{
	 if (!equipped)
	 	return;
		
	 if (!ready)
	 	return;		
	 /* Open the GUI */
	}

  /** Put the object "on".
   */
    public void equip()
	{
	 equipped=true;
	}
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
	
  /** Ready the document.
   * Currently no op.  
   */
    public void ready()
	{
	 /* no op */
	}
	
 
  /** Write to the document.
   * @param line the text to add
   */
    public void writeText(String line)
	{
	 text.getParagraph(text.getCurrentParagraph()).appendString(line);
	}
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get the document's text.
   * @return current readable text
   */
    public String readText()
	{
	 return text.getParagraph(text.getCurrentParagraph()).getText();
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

