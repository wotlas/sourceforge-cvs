/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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

package wotlas.libs.graphics2D.policy;

import java.awt.Dimension;
import java.awt.Rectangle;
import wotlas.libs.graphics2D.GraphicsDirector;
import wotlas.libs.graphics2D.WindowPolicy;

/** A simple window policy that always centers the refDrawable on the screen.
 *
 * @author MasterBob,Aldiss,Petrus
 * @see wotlas.libs.graphics2D.WindowPolicy
 */

public class LimitWindowPolicy implements WindowPolicy {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Our gDirector.
     */
    private GraphicsDirector gDirector;

    /**
     * the LIMIT represent a pourcent
     * if we are closer of this pourcent of the edge of the screen we will start the correction
     *
     * enCorrection define if we are correcting the view or not.
     * the correction will continue unless we are twice of the pourcentage of LIMIT farther from the edge of the screen
     */
    private boolean needCorrection = false;
    private final int LIMIT = 25;

    /**
     * will represent the delta of the correction of the window judjing by the deplacement of the refDrawable
     *
     */
    private int speedX = 0;
    private int speedY = 0;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To init this WindowPolicy.
     *
     * @param gDirector the associated graphics director
     */
    public void init(GraphicsDirector gDirector) {
        this.gDirector = gDirector;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tick method that signals to the WindowPolicy that it can update its parameters.
     */
    public void tick() {
        if (this.gDirector.getRefDrawable() == null)
            return;

        Rectangle screen = this.gDirector.getScreenRectangle();
        Dimension background = this.gDirector.getBackgroundDimension();
        Rectangle refDrawable = this.gDirector.getRefDrawable().getRectangle();

        //System.out.println("screen.x = " + screen.x);
        //System.out.println("screen.width = " + screen.width);
        //System.out.println("background.width = " + background.width);
        //System.out.println("refDrawable.x = " + refDrawable.x);
        //System.out.println("refDrawable.width = " + refDrawable.width);

        //calculate the pourcentage to the edge on the 4 directions
        //the top left corner of the refDrawable is taken for references for limitHaut and limitGauche
        //the bottom right corner of the refDrawable is taken for references for limitBas and limitDroit
        int limitHaut = (refDrawable.y - screen.y) * 100 / screen.height;
        int limitBas = 100 - ((refDrawable.y + refDrawable.height) - screen.y) * 100 / screen.height;
        int limitGauche = (refDrawable.x - screen.x) * 100 / screen.width;
        int limitDroit = 100 - ((refDrawable.x + refDrawable.width) - screen.x) * 100 / screen.width;

        //this test is needed at the begening to center the screen if the refDrawable is out
        //of the screen widht and hight
        //and if there are any pb (negative pourcent !!!) we center the view.
        //usefull if there are teleportation !!
        //furthermore the screen must have a sufficient size compare to the refDrawable
        if (limitHaut < 0 || limitBas < 0 || limitGauche < 0 || limitDroit < 0 || refDrawable.width * 4 > screen.width || refDrawable.height * 4 > screen.height) {
            screen.x = refDrawable.x - (screen.width - refDrawable.width) / 2;
            screen.y = refDrawable.y - (screen.height - refDrawable.height) / 2;
        } else {
            //test if we need a correction and calcul the speed
            if (limitHaut < this.LIMIT || limitBas < this.LIMIT || limitGauche < this.LIMIT || limitDroit < this.LIMIT) {
                this.needCorrection = true;
                if (limitHaut < this.LIMIT)
                    this.speedY = -(this.LIMIT - limitHaut) * screen.height / 100;
                if (limitBas < this.LIMIT)
                    this.speedY = (this.LIMIT - limitBas) * screen.height / 100;
                if (limitGauche < this.LIMIT)
                    this.speedX = -(this.LIMIT - limitGauche) * screen.width / 100;
                if (limitDroit < this.LIMIT)
                    this.speedX = (this.LIMIT - limitDroit) * screen.width / 100;
            }

            //test if we don't need any more correction
            //The correction will end if we have returned to the center of the screen
            if (limitHaut > 50 && this.speedY < 0)
                this.speedY = 0;
            if (limitBas > 50 && this.speedY > 0)
                this.speedY = 0;
            if (limitGauche > 50 && this.speedX < 0)
                this.speedX = 0;
            if (limitDroit > 50 && this.speedX > 0)
                this.speedX = 0;

            if (this.speedY == 0 && this.speedX == 0)
                this.needCorrection = false;

            // do the correction if necessary
            if (this.needCorrection) {
                screen.x += this.speedX;
                screen.y += this.speedY;
            }
        }

        // we correct the center if it's out of the backround dimension.
        if (screen.x < 0)
            screen.x = 0;
        if (screen.y < 0)
            screen.y = 0;

        if (background.width < screen.width)
            screen.x = -(screen.width - background.width) / 2;
        else if ((screen.x + screen.width) >= background.width)
            screen.x = background.width - screen.width - 1;

        if (background.height < screen.height)
            screen.y = -(screen.height - background.height) / 2;
        else if ((screen.y + screen.height) >= background.height)
            screen.y = background.height - screen.height - 1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}