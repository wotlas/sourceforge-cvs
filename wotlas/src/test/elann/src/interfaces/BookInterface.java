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

/** 
 * The book interface. Provides method to browse thru. 
 * Should provide a GUI to read/write. 
 *
 * @author Elann
 */

public interface BookInterface
{

 /*------------------------------------------------------------------------------------*/


  /** Open the book. First chapter active.
   */
    public void open();

  /** Get the current chapter. Should be used by the GUI. --- String should be evolved ---
   * @return active chapter
   */
    public String getCurrentChapter();

  /** Turn pages 'til next chapter.
   */
    public void seekNextChapter();

  /** Search the book for a chapter.
   * @param chapterName the name of the chapter searched. 
   */
    public void searchChapter(String chapterName);

	
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

