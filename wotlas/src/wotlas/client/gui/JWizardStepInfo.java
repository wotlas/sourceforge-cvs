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

package wotlas.client.gui;

import wotlas.utils.aswing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** A step of a wizard<br>
 * with a AtextArea (info)
 *
 * @author Petrus
 * @see wotlas.client.gui.JWizardStep
 */

public class JWizardStepInfo extends JWizardStep
{
  private ATextArea tarea;

  /** called when the step is to be shown
   */
  public void onShow(Object context) {
        
  }

  /** called when Next button is clicked
   */
  public boolean onNext(Object context) {
    return true;
  }

  /** called when Previous button is clicked
   */
  public boolean onPrevious(Object context) {
    return true;
  }

  /** Constructor
   */
  public JWizardStepInfo(String sInfo)
  {
    super("...");
    setBackground(Color.white);
    
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    tarea = new ATextArea(sInfo);
    tarea.setBackground(Color.white);
    tarea.setLineWrap(true);
    tarea.setWrapStyleWord(true);
    tarea.setEditable(false);
    tarea.setAlignmentX(LEFT_ALIGNMENT);  
    add(tarea);        
      
    add(Box.createVerticalGlue());
  }
}
