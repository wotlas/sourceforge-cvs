
package test.graphics.ants;

import wotlas.libs.graphics2D.ImageLibraryReference;

/** Ids for an easier use of the ImageLibrary...
 *
 * @author Bertrand Le Nistour
 */

public interface ImLibRef extends ImageLibraryReference
{
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Image category for moving entities.
   */
    public final static short ENTITIES_CATEGORY = 0;
  
    public final static short ANTS_SET = 0;

    public final static short ANT_WALKING_ACTION = 0;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Image category for grounds.
   */
    public final static short GROUND_CATEGORY = 1;

    public final static short MUDDY_GROUNDS_SET = 0;

    public final static short MUDDY_GROUND_ACTION = 0;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We declare here the priorities we are going to use for drawables.
   */
    public final static short GROUND_PRIORITY = 0; // lowest priority, drawn first

    public final static short ANT_PRIORITY = 50;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}