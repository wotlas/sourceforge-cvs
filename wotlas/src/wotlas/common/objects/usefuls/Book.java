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
import wotlas.utils.Debug;

/** 
 * The class of books.
 * 
 * @author Elann
 * @see wotlas.common.objects.usefuls.Document
 * @see wotlas.common.objects.usefuls.Chapter
 * @see wotlas.common.objects.interfaces.BookInterface
 */

public class Book extends Document implements BookInterface {

    /*------------------------------------------------------------------------------------*/

    /** The content of the book.
     */
    protected Chapter[] chapters;

    /** The current number of chapters in the book.
     */
    protected short nbChapters;

    /** The active chapter in the book.
     */
    protected short currentChapter;

    /** The title of the book.
     */
    protected String title;

    /*------------------------------------------------------------------------------------*/

    /** The default constructor.<br>
     * Initialize members but does not allocate chapters.
     */
    public Book() {
        super();

        this.className = "Book";
        this.objectName = "default book";

        this.chapters = null;
        this.nbChapters = 0;
        this.currentChapter = -1;
        this.title = "Untitled";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Use the object.<br>
     * For a book, just calls open().  
     */
    @Override
    public void use() {
        if (!this.equipped)
            return;

        if (!this.ready)
            return;

        open();
    }

    /** Ready the book.
     */
    @Override
    public void ready() {
        /* no op */
        this.ready = true;
    }

    /** Put the book "on".<br>
     */
    public void equip() {
        /* no op */
        this.equipped = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Open the book.<br>
     * First chapter active. <br>
     * GUI should be launched at the end of this method.
     */
    public void open() {
        if (this.nbChapters == -1) {
            Debug.signal(Debug.WARNING, this, "Trying to open an empty book");
            return;
        }

        if (this.currentChapter != -1) {
            Debug.signal(Debug.WARNING, this, "Trying to open already opened book");
            return;
        }

        this.currentChapter = 0; /* Chapter 0 should be something special */

        /* Launch the GUI */
    }

    /** Get the current chapter.
     * @return active chapter
     */
    public short getCurrentChapter() {
        return this.currentChapter;
    }

    /** Set the current chapter.<br>
     * Range checking done here.
     * @param targetChapter the new chapter.
     */
    public void setCurrentChapter(short targetChapter) {
        if (this.nbChapters < targetChapter || targetChapter < 0) {
            Debug.signal(Debug.WARNING, this, "Trying to go to unexistant chapter");
            return;
        }

        this.currentChapter = targetChapter;
    }

    /** Get a chapter by index.<br>
     * Range checking done here. Warning sent if invalid.
     * @param index the index in the book
     * @return the requested Chapter if available ; null else
     */
    public Chapter getChapter(int index) {
        if (this.nbChapters < index || index < 0) {
            Debug.signal(Debug.WARNING, this, "Trying to get an unexistant chapter");
            return null;
        }

        return this.chapters[index];
    }

    /** Get the number of chapters in the book.
     * @return nbChapters
     */
    public short getNbChapters() {
        return this.nbChapters;
    }

    /** Set the number of chapters in the book.<br>
     * Should not be called directly. Array initialisation not done here.
     * @param nbChapters the new number of chapters. 
     */
    public void setNbChapters(short nbChapters) {
        this.nbChapters = nbChapters;
    }

    /** Get the document's text.<br>
     * If the book is open, returns the current paragraph.<br>
     * Otherwise returns the book's title (cover) which is chapters[0].
     * @return current readable text
     */
    public String readText() {
        if (this.currentChapter < 0)
            return this.title;

        Chapter currChapter = this.chapters[this.currentChapter];
        Paragraph currParagraph = currChapter.getParagraph(currChapter.getCurrentParagraph());

        return currParagraph.getText();
    }

    /** Write to the document.
     * @param text the text to write
     */
    public void writeText(String text) {
        if (this.currentChapter < 0) {
            Debug.signal(Debug.WARNING, this, "Trying to write in a closed book");
            return;
        }

        Chapter currChapter = this.chapters[this.currentChapter];
        Paragraph currParagraph = currChapter.getParagraph(currChapter.getCurrentParagraph());

        currParagraph.appendString(text);
    }

    /** Search the book for a chapter.<br> 
     * If the chapter is found, it becomes the current chapter. <br>Else returns -1.
     * @param chapterName the name of the chapter searched.
     * @return found index or -1
     */
    public short searchChapter(String chapterName) {
        short index = 0;
        while (this.chapters[index].getChapterTitle() != chapterName && index < this.nbChapters)
            index++;

        if (index > this.nbChapters)
            index = -1;
        else
            setCurrentChapter(index);

        return index;
    }

    /** Get a chapter by title.
     * @param title the title of the chapter
     * @return the requested Chapter if available ; null else
     */
    public Chapter getChapterByTitle(String title) {
        int index = 0;
        while (this.chapters[index].getChapterTitle() != title && index < this.nbChapters)
            index++;

        if (index > this.nbChapters)
            return null;
        else
            return this.chapters[index];
    }

    /** Set a chapter.<br>
     * Create a new chapter at the end of the book.
     * @param chapter the new chapter 
     */
    public void setChapter(Chapter chapter) {
        if (this.nbChapters == BookInterface.maxNbChaptersPerBook) {
            Debug.signal(Debug.WARNING, this, "Trying to add too much chapters");
            return;
        }
        Chapter tmp[] = new Chapter[++this.nbChapters];
        System.arraycopy(this.chapters, 0, tmp, 0, this.chapters.length);
        this.chapters = tmp;
        this.chapters[this.nbChapters - 1] = chapter;
    }

    /** Add a chapter.<br>
     * Create a new chapter at the end of the book.
     * @return true if a new chapter was created
     */
    public boolean addChapter() {
        if (this.nbChapters == BookInterface.maxNbChaptersPerBook) {
            Debug.signal(Debug.WARNING, this, "Trying to add too much chapters");
            return false;
        }

        Chapter tmp[] = new Chapter[++this.nbChapters];
        System.arraycopy(this.chapters, 0, tmp, 0, this.chapters.length);
        this.chapters = tmp;
        this.chapters[this.nbChapters - 1] = new Chapter();
        return true;
    }

    /** Remove a chapter from the book.<br>
     * Range checking done here.
     * @param index the index of the chapter to delete. 
     */
    public void delChapter(short index) {
        if (this.nbChapters < index || index < 0) {
            Debug.signal(Debug.WARNING, this, "Trying to delete an unexistant chapter");
            return;
        }
        this.chapters[index] = null;
        Chapter tmp[] = new Chapter[--this.nbChapters];
        System.arraycopy(this.chapters, 0, tmp, 0, index);
        System.arraycopy(this.chapters, index + 1, tmp, index, this.chapters.length - index - 1);
        this.chapters = tmp;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
