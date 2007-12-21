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

import wotlas.utils.Debug;

/** 
 * The class of chapters.
 * Used in books and parchments.
 * @author Elann
 * @see wotlas.common.objects.usefuls.Book
 * @see wotlas.common.objects.usefuls.Parchment
 * @see wotlas.common.objects.usefuls.Paragraph
 */

public class Chapter {

    /*------------------------------------------------------------------------------------*/

    /** The maximum allowed size of a chapter.
     */
    public static final int maxNbParagraphsPerChapter = 20;

    /** The content of the chapter.
     */
    private Paragraph[] paragraphs;

    /** The current number of paragraphs in the chapter.
     */
    private short nbParagraphs;

    /** The current paragraph in the chapter.
     */
    private short currentParagraph;

    /** The title of the chapter.
     */
    private String chapterTitle;

    /*------------------------------------------------------------------------------------*/

    /** The default constructor. <br>Sets the number of paragraphs to 0.
     */
    public Chapter() {
        this.nbParagraphs = 0;
        this.paragraphs = null;
        this.chapterTitle = "Untitled";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Add a new empty paragraph in the chapter.
      */
    public void addParagraph() {
        if (this.nbParagraphs == 0)
            this.paragraphs = new Paragraph[1];
        else if (this.nbParagraphs < Chapter.maxNbParagraphsPerChapter) {
            Paragraph tmp[] = new Paragraph[++this.nbParagraphs];
            System.arraycopy(this.paragraphs, 0, tmp, 0, this.paragraphs.length);
            this.paragraphs = tmp;
        } else
            Debug.signal(Debug.WARNING, this, "Trying to add too many paragraphs in a chapter");
    }

    /** Remove a paragraph from the chapter.
     * @param index the index of the paragraph to remove
     */
    public void removeParagraph(int index) {
        if (index > this.nbParagraphs || index < 0) {
            Debug.signal(Debug.WARNING, this, "Trying to remove an inexistant paragraph");
            return;
        }

        Paragraph tmp[] = new Paragraph[--this.nbParagraphs];
        System.arraycopy(this.paragraphs, 0, tmp, 0, index);
        System.arraycopy(this.paragraphs, index + 1, tmp, index, this.paragraphs.length - index - 1);
        this.paragraphs = tmp;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Get a paragraph in the chapter by index.<br>
     * Range checking done here. Warning sent if invalid.
     * @param index the index of the paragraph
     * @return the requested paragraph if available ; null else
     */
    public Paragraph getParagraph(int index) {
        if (this.nbParagraphs < index || index < 0) {
            Debug.signal(Debug.WARNING, this, "Trying to get an unexistant paragraph");
            return null;
        }

        return this.paragraphs[index];
    }

    /** Get the current number of paragraphs in the chapter.
     * @return nbParagraphs
     */
    public short getNbParagraphs() {
        return this.nbParagraphs;
    }

    /** Set the current number of paragraphs in the chapter.<br>
     * Should not be called directly.
     * @param nbParagraphs the new number of paragraphs
     */
    public void setNbParagraphs(short nbParagraphs) {
        this.nbParagraphs = nbParagraphs;
    }

    /** Get the current paragraph in the chapter.
     * @return currentParagraph
     */
    public short getCurrentParagraph() {
        return this.currentParagraph;
    }

    /** Set the current paragraph in the chapter.
     * Should not be called directly.
     * @param currentParagraphs the new active paragraph
     */
    public void setCurrentParagraph(short currentParagraph) {
        this.currentParagraph = currentParagraph;
    }

    /** Get the title of the chapter.
     * @return chapterTitle
     */
    public String getChapterTitle() {
        return this.chapterTitle;
    }

    /** Set the title of the Chapter
     * @param chapterTitle the new title
     */
    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
