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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** A step of a wizard 
 *
 * @author Petrus
 * @see wotlas.client.gui.JWizard 
 */

public abstract class JWizardStep extends JPanel
{

 /*------------------------------------------------------------------------------------*/

  /** Contexte of the wizard
   */
  private Object context;
  
  /** Title of the step
   */
  private String title;

 /*------------------------------------------------------------------------------------*/

  /** called when the step is to be shown
   */
  public abstract void onShow(Object context);
  
  /** called when Next button is clicked
   */
  public abstract void onNext(Object context);
  
  /** called when Previous button is clicked
   */
  public abstract void onPrevious(Object context);

 /*------------------------------------------------------------------------------------*/

  /** To get the title
   */
  public String getTitle() {
    return title;
  }
  
  /** To set the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

 /*------------------------------------------------------------------------------------*/

  /** Consctructor
   */
  public JWizardStep() {    
    super();    
    title = " ";
    setBackground(Color.white);
  }
  
  /** Consctructor
   */
  public JWizardStep(String title) {
    super();
    this.title = title;
  }

 /*------------------------------------------------------------------------------------*/
  
}