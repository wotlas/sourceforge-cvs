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
 
package wotlas.common.objects.interfaces;

import wotlas.common.objects.usefuls.Chapter;

/** 
 * The book interface. Provides method to browse thru. 
 * Should provide a GUI to read/write. 
 *
 * @author Elann
 */

public interface BookInterface
{
 /** The maximum allowed size of a book.
  */
 	public static final short maxNbChaptersPerBook=20;
	
 /*------------------------------------------------------------------------------------*/

  /** Open the book. First chapter active.
   */
    public void open();

  /** Get the current chapter.
   * @return active chapter
   */
    public short getCurrentChapter();

  /** Set the current chapter.
   * @param targetChapter the new chapter.
   */
    public void setCurrentChapter(short targetChapter);

  /** Search the book for a chapter. If the chapter is found, it becomes the current chapter.
   * @param chapterName the name of the chapter searched.
   * @return found index or -1
   */
    public short searchChapter(String chapterName);

  /** Get a chapter by index.
   * @param index the index in the book
   * @return the requested Chapter if available ; null else
   */ 
   	public Chapter getChapter(int index);

  /** Get a chapter by title.
   * @param title the title of the chapter
   * @return the requested Chapter if available ; null else
   */
    public Chapter getChapterByTitle(String title);
	
  /** Get the number of chapters in the book.
   * @return nbChapters
   */
    public short getNbChapters();
	
  /** Set the number of chapters in the book.
   * Should not be called directly.
   * @param nbChapters the new number of chapters. 
   */
    public void setNbChapters(short nbChapters);
		
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

