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

package wotlas.utils.aswing;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;

/** Antialiased ListCellRenderer for JList
 *
 * @author Aldiss
 */
public class AListCellRenderer extends JLabel implements ListCellRenderer {

 /*------------------------------------------------------------------------------------*/
 
  /** Constructor without arguments.
   */
   public AListCellRenderer() {
      super();
      setBackground( Color.white );
      setOpaque(true);
      setAlignmentX( CENTER_ALIGNMENT );
      setHorizontalAlignment( SwingConstants.CENTER );
      setForeground( new Color(70,70,100) );
   }
 
 /*------------------------------------------------------------------------------------*/

  /** Component getter
   */
     public Component getListCellRendererComponent(
         JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {

         setText(value.toString());
         setBackground( isSelected ? new Color(200,200,240) : Color.white );

         if(isSelected)
            setBorder( BorderFactory.createLineBorder(new Color(150,150,225)) );
         else
            setBorder( BorderFactory.createEmptyBorder() );

         return this;
     }

 /*------------------------------------------------------------------------------------*/
 
  /** Mutated Paint Method.
   */
  public void paint( Graphics g ) {
    Graphics2D g2D = (Graphics2D) g; 
    RenderingHints saveRenderHints = g2D.getRenderingHints(); // save
    
    RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                        RenderingHints.VALUE_ANTIALIAS_ON);
    renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
    g2D.setRenderingHints( renderHints );
    super.paint(g);
    g2D.setRenderingHints( saveRenderHints ); // restore
  }

 /*------------------------------------------------------------------------------------*/

}  
