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

package wotlas.libs.graphics2D.policy;

import wotlas.libs.graphics2D.*;

import java.awt.*;

/** A simple window policy that always centers the refDrawable on the screen.
 *
 * @author Aldiss
 * @see wotlas.libs.graphics2D.WindowPolicy
 */

public class CenterWindowPolicy implements WindowPolicy{

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Our gDirector.
    */
     private GraphicsDirector gDirector;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To init this WindowPolicy.
    *
    * @param gDirector the associated graphics director
    */
     public void init( GraphicsDirector gDirector ) {
         this.gDirector = gDirector;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Tick method that signals to the WindowPolicy that it can update its parameters.
    */
     public void tick() {
        Rectangle screen = gDirector.getScreenRectangle();
        Dimension background = gDirector.getBackgroundDimension();
        Rectangle refDrawable = gDirector.getRefDrawable().getRectangle();

      // we center the screen cordinates
        screen.x = refDrawable.x - ( screen.width - refDrawable.width )/2;
        screen.y = refDrawable.y - ( screen.height - refDrawable.height )/2;

      // we correct the center if it's out of the backround dimension.
        if(screen.x<0) screen.x = 0;
        if(screen.y<0) screen.y = 0;
        if(screen.x>=background.width-screen.width) screen.x = background.width-screen.width-1;
        if(screen.y>=background.height-screen.height) screen.y = background.height-screen.height-1;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}