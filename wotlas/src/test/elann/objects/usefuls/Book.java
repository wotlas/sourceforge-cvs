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

import wotlas.common.objects.interfaces.BookInterface;
import wotlas.common.objects.usefuls.Chapter;

/** 
 * The class of books.
 * 
 * @author Elann
 * @see wotlas.common.objects.usefuls.Document
 * @see wotlas.common.objects.usefuls.Chapter
 * @see wotlas.common.objects.interfaces.BookInterface
 */

public class Book extends Document implements BookInterface
{

 /*------------------------------------------------------------------------------------*/

 /** The content of the book.
  */
  private Chapter[] chapters;
  
 /** The current number of chapters in the book.
  */
  private short nbChapters;

 /** The active chapter in the book.
  */
  private short currentChapter;

 /** The title of the book.
  */
  private String title;
 
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public Book()
	{
	 this.className="Book";
	 this.objectName="default book";
	 
	 this.nbChapters=0;
	 this.currChapter=-1;	 
	}															
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Open the book. First chapter active.
   */
    public void open()
	{
	 if (this.nbChapters!=-1)
	 {
//	  Debug.signal(Debug.WARNING,this,"Trying to open an empty book or a book already open");
	  return;	  
	 }
	 	
	 this.currentChapter=0;					/* Chapter 0 should be something special */
	}

  /** Ready the book.
   * Calls open()  
   */
    public void makeReady()
	{
	 this.open();
	}

  /** Get the current chapter.
   * @return active chapter
   */
    public short getCurrentChapter()
	{
	 return currentChapter;
	}
	
  /** Set the current chapter.
   * @param targetChapter the new chapter.
   */
    public void setCurrentChapter(short targetChapter)
	{
	 if (this.nbChapter<targetChapter || targetChapter<0)
	 {
//	  Debug.signal(Debug.WARNING,this,"Trying to go to unexistant chapter");
	  return;	 
	 }
	 
	 this.currentChapter=targetChapter;
	}
	
  /** Get a chapter by index.
   * @param index the index in the book
   * @return the requested Chapter if available ; null else
   */ 
   	public Chapter getChapter(int index)
	{
	 if (this.nbChapter<targetChapter || targetChapter<0)
	 {
//	  Debug.signal(Debug.WARNING,this,"Trying to get an unexistant chapter");
	  return null;	 
	 }
	 
	  return chapters[index];
	}

  /** Get the number of chapters in the book.
   * @return nbChapters
   */
    public short getNbChapters()
	{
	 return nbChapters;
	}
	
  /** Set the number of chapters in the book.
   * Should not be called directly.
   * @param nbChapters the new number of chapters. 
   */
    public void setNbChapters(short nbChapters)
	{
	 this.nbChapters=nbChapters;
	}

  /** Get the document's text.
   * @return current readable text
   */
    public String readText()
	{
	 if (this.currentChapter<0)
	 	return title;

	 Chapter currChapter=this.chapters[this.currentChapter];
	 Paragraph currParagraph=currChapter.getParagraph(currChapter.getCurrentParagraph());
		
	 return currParagraph.getText();		
	}
	
	
  /** Write to the document.
   * @param text the text to write
   */
    public void writeText(String text)
	{
	 if (this.currentChapter<0)
	 {
//	  Debug.signal(Debug.WARNING,this,"Trying to write in a closed book");
	 	return;		
	 }

	 Chapter currChapter=this.chapters[this.currentChapter];
	 Paragraph currParagraph=currChapter.getParagraph(currChapter.getCurrentParagraph());
		
	 currParagraph.appendString(text);			
	}
	
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

