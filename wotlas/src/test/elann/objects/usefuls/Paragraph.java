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

/** 
 * The class of paragraphs.
 * Used in chapters in books.
 * @author Elann
 * @see wotlas.common.objects.usefuls.Book
 * @see wotlas.common.objects.usefuls.Chapter
 */

public class Paragraph
{

 /*------------------------------------------------------------------------------------*/

 /** The actuel text of the paragraph. 
  * HTML format.
  */
  private String text;

 /** The list of possible strike out styles. Defines if the text is readable, quite, barely or not at all.
  * Many thanks to Elaida ... ;-)
  */
  public static final String[] strikeOutStyles={"no strike","clear strike","bold strike","heavy strike"};
 
 /** The current strike out style.
  */
  private short strikeOutStyle;
  
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public Paragraph()
	{ 
	  this.strikeOutStyle=0;
	}															
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /** Get the actual text of the paragraph.
  * @return the HTML-formatted text of the paragraph
  */ 
   public String getText()
   {
    return text;
   }

 /** Set the actual text of the paragraph.
  * @param text the new HTML-formatted text of the paragraph
  */ 
   public void setText(String text)
   {
    this.text=text;
   }

 /** Append a string at the end of the paragraph.
  * @param newString the new HTML-formatted string to add
  */
   public void appendString(String newString)
   {
    /* no op */
   }

 /** Get the strike out style of the paragraph.
  * @return the current strike out style
  */
   public short getStrikeOutStyle()
   {
   	return strikeOutStyle;
   }

 /** Set the strike out style of the paragraph.
  * @param style the new strike out style
  */
   public void setStrikeOutStyle(short style)
   {
   	this.strikeOutStyle=strikeOutStyle;
   }
   
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

