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
import wotlas.common.objects.valueds.ValuedObject;
import wotlas.common.Player;

import wotlas.utils.Debug;

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
  
 /** Is it on ?
  */
  private boolean equipped;
 
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public Book()
	{
	 className="Book";
	 objectName="default book";
	 
	 chapters=null;
	 nbChapters=0;
	 currentChapter=-1;
	 title="Untitled";
	 equipped=false;	 	 
	}															
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Use the object.<br>
   * For a book, just calls open().  
   */
    public void use()
	{
	 if (!equipped)
	 	return;
	 open();
	}

  /** Put the object "on". Needed before action is possible.
   */
    public void equip()
	{
	 /* Put it on */
	 equipped=true;
	}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Open the book.<br>
   * First chapter active. <br>
   * GUI should be launched at the end of this method.
   */
    public void open()
	{
	 if (nbChapters==-1)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to open an empty book");
	  return;	  
	 }
	 
	 if (currentChapter!=-1)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to open already opened book");
	  return;	  
	 }
	 	
	 currentChapter=0;					/* Chapter 0 should be something special */
	 
	 /* Launch the GUI */
	}

  /** Ready the book.
   * Calls open()  
   */
    public void makeReady()
	{
	 open();
	}

  /** Get the current chapter.
   * @return active chapter
   */
    public short getCurrentChapter()
	{
	 return currentChapter;
	}
	
  /** Set the current chapter.<br>
   * Range checking done here.
   * @param targetChapter the new chapter.
   */
    public void setCurrentChapter(short targetChapter)
	{
	 if (this.nbChapters<targetChapter || targetChapter<0)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to go to unexistant chapter");
	  return;	 
	 }
	 
	 this.currentChapter=targetChapter;
	}
	
  /** Get a chapter by index.<br>
   * Range checking done here. Warning sent if invalid.
   * @param index the index in the book
   * @return the requested Chapter if available ; null else
   */ 
   	public Chapter getChapter(int index)
	{
	 if (nbChapters<index || index<0)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to get an unexistant chapter");
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
	
  /** Set the number of chapters in the book.<br>
   * Should not be called directly. Array initialisation not done here.
   * @param nbChapters the new number of chapters. 
   */
    public void setNbChapters(short nbChapters)
	{
	 this.nbChapters=nbChapters;
	}

  /** Get the document's text.<br>
   * If the book is open, returns the current paragraph.<br>
   * Otherwise returns the book's title (cover) which is chapters[0].
   * @return current readable text
   */
    public String readText()
	{
	 if (currentChapter<0)
	 	return title;

	 Chapter currChapter=chapters[currentChapter];
	 Paragraph currParagraph=currChapter.getParagraph(currChapter.getCurrentParagraph());
		
	 return currParagraph.getText();		
	}
	
	
  /** Write to the document.
   * @param text the text to write
   */
    public void writeText(String text)
	{
	 if (currentChapter<0)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to write in a closed book");
	  return;		
	 }

	 Chapter currChapter=chapters[currentChapter];
	 Paragraph currParagraph=currChapter.getParagraph(currChapter.getCurrentParagraph());
		
	 currParagraph.appendString(text);			
	}
	

  /** Search the book for a chapter.<br> 
   * If the chapter is found, it becomes the current chapter. <br>Else returns -1.
   * @param chapterName the name of the chapter searched.
   * @return found index or -1
   */
    public short searchChapter(String chapterName)
	{
	 short index=0;
	 while (chapters[index].getChapterTitle()!=chapterName && index<nbChapters)
	 	   index++;
	  
	 if (index>nbChapters)
	 	index=-1;
	 else	   
		 setCurrentChapter(index);
		 
	 return index;
	}
	
  /** Get a chapter by title.
   * @param title the title of the chapter
   * @return the requested Chapter if available ; null else
   */
    public Chapter getChapterByTitle(String title)
	{
	 int index=0;
	 while (chapters[index].getChapterTitle()!=title && index<nbChapters)
	 	   index++;
	  
	 if (index>nbChapters)
	 	return null;
	 else	   
	 	return chapters[index];	
	}


  /** Set a chapter.<br>
   * Create a new chapter at the end of the book.
   * @param chapter the new chapter 
   */ 
   	public void setChapter(Chapter chapter)
	{
	 if (nbChapters==maxNbChaptersPerBook)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to add too much chapters");
	  return;	 
	 }
	 Chapter tmp[]=new Chapter[++nbChapters];
	 System.arraycopy( chapters, 0, tmp, 0, chapters.length );
	 chapters=tmp;		  
	 chapters[nbChapters-1]=chapter;
	}

  /** Add a chapter.<br>
   * Create a new chapter at the end of the book.
   * @return true if a new chapter was created
   */ 
   	public boolean addChapter()
	{
	 if (nbChapters==maxNbChaptersPerBook)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to add too much chapters");
	  return false;
	 }
	 
	 Chapter tmp[]=new Chapter[++nbChapters];
	 System.arraycopy( chapters, 0, tmp, 0, chapters.length );
	 chapters=tmp;		  
	 chapters[nbChapters-1]=new Chapter();
	 return true;	 	 
	}
	
  /** Remove a chapter from the book.<br>
   * Range checking done here.
   * @param index the index of the chapter to delete. 
   */
    public void delChapter(short index)
	{	
	 if (nbChapters<index || index<0)
	 {
	  Debug.signal(Debug.WARNING,this,"Trying to delete an unexistant chapter");
	  return;
	 }
	 chapters[index]=null;
	 Chapter tmp[]=new Chapter[--nbChapters];
	 System.arraycopy(chapters,0,tmp,0,index);	 
	 System.arraycopy(chapters,index+1,tmp,index,chapters.length-index-1);
	 chapters=tmp;	
	}
	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

