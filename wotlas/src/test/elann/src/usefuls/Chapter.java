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

import wotlas.common.objects.usefuls.Paragraph;

/** 
 * The class of chapters.
 * Used in books.
 * @author Elann
 * @see wotlas.common.objects.usefuls.Book
 * @see wotlas.common.objects.interfaces.BookInterface
 */

class Chapter
{

 /*------------------------------------------------------------------------------------*/

  /** The maximum allowed size of a chapter.
   */
   public static final int maxNbParagraphsPerChapter=20;
  
  /** The content of the chapter.
   */
   private Paragraph[] paragraphs;
  
  /** The current number of paragraphs in the chapter.
   */
   private short nbParagraphs;
  
  /** The title of the chapter.
   */
   private String chapterTitle;

 
 /*------------------------------------------------------------------------------------*/

  /** The default constructor.
   */			
    public Chapter()
	{
	 this.nbParagraphs=0;	 
	}															
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Get a paragraph in the chapter by index.
   * @param index the index of the paragraph
   */	
    public Paragraph getParagraph(int index);
	
  /** Get a paragraph in the chapter by title.
   * @param title the title of the paragraph
   */	
    public Paragraph getParagraphByTitle(String title);
	
  /** Get the current number of paragraphs in the chapter.
   * @return nbParagraphs
   */
	public short getNbParagraphs();
 
  /** Set the current number of paragraphs in the chapter.
   * Should not be called directly.
   * @param nbParagraphs the new number of paragraphs
   */
	public void setNbParagraphs(short nbParagraphs);
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
}

