/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.client.screen.plugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import wotlas.utils.*;
import wotlas.utils.aswing.*;

import wotlas.libs.persistence.*;
import wotlas.common.*;

import wotlas.utils.Debug;

import wotlas.client.*;
import wotlas.client.screen.*;


/** Plug In to create/save/use HTML macros... 
 *
 * @author Aldiss, Fred McMaster
 */

public class MacroPlugIn extends JPanelPlugIn {
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** The name of the client macros config file.
   */
    private final static String MACROS_PREFIX = "macros"+File.separator+"macro-";
    private final static String MACROS_SUFFIX = ".cfg";

  /** Max Number of macros the user can create.
   */
    private final static int MAX_MACROS = 20;

 /*------------------------------------------------------------------------------------*/ 

  /** TextFields for macros
   */
    private ATextField macrosFields[];

  /** RadioButtons for macros
   */
    private ARadioButton macrosRadios[];

  /** JPanel for macros
   */
    private JPanel macrosPanel[];

  /** 'New' macro button.
   */
    private AButton newMacroButton;

  /** 'Use' macro button.
   */
    private AButton useMacroButton;

  /** 'Del' macro button.
   */
    private AButton delMacroButton;

  /** 'Save' macro button.
   */
    private AButton saveMacroButton;

  /** Center panel where the macros are set...
   */
    private JPanel centerPanel;

  /** Exclusive group of macros.
   */
    private ButtonGroup macrosGroup;

 /*------------------------------------------------------------------------------------*/ 
  
  /** Constructor.
   */
    public MacroPlugIn() {
      super();
      setLayout(new BorderLayout());

      ATextArea taInfo = new ATextArea("You can create here HTML macros to use in the chat."
                         +" Each macro has a shortcut @N@ where N is the macro's index.");
      taInfo.setLineWrap(true);
      taInfo.setWrapStyleWord(true);
      taInfo.setEditable(false);
      taInfo.setAlignmentX(0.5f);
      taInfo.setOpaque(false);
      add(taInfo, BorderLayout.NORTH);

      centerPanel = new JPanel( new GridLayout(MAX_MACROS,1,5,5) );
      add(new JScrollPane(centerPanel,
                          JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                          JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                          ),BorderLayout.CENTER);

      macrosGroup = new ButtonGroup();
      macrosFields = new ATextField[MAX_MACROS];
      macrosRadios = new ARadioButton[MAX_MACROS];
      macrosPanel = new JPanel[MAX_MACROS];

      JPanel buttonsPanel = new JPanel(new GridLayout(1,4));
      buttonsPanel.setBackground(Color.white);
      add(buttonsPanel, BorderLayout.SOUTH);

      ImageIcon im_newup  = ClientDirector.getResourceManager().getImageIcon("new-mini-up.gif");
      ImageIcon im_newdo  = ClientDirector.getResourceManager().getImageIcon("new-mini-do.gif");

      ImageIcon im_useup  = ClientDirector.getResourceManager().getImageIcon("use-mini-up.gif");
      ImageIcon im_usedo  = ClientDirector.getResourceManager().getImageIcon("use-mini-do.gif");

      ImageIcon im_delup  = ClientDirector.getResourceManager().getImageIcon("del-mini-up.gif");
      ImageIcon im_deldo  = ClientDirector.getResourceManager().getImageIcon("del-mini-do.gif");

      ImageIcon im_saveup  = ClientDirector.getResourceManager().getImageIcon("save-mini-up.gif");
      ImageIcon im_savedo  = ClientDirector.getResourceManager().getImageIcon("save-mini-do.gif");

      newMacroButton = new AButton(im_newup);
      newMacroButton.setRolloverIcon(im_newdo);
      newMacroButton.setPressedIcon(im_newdo);
      newMacroButton.setBorderPainted(false);
      newMacroButton.setContentAreaFilled(false);
      newMacroButton.setFocusPainted(false);

        newMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {                    	
                  addMacro("");
            }
        });

      useMacroButton = new AButton(im_useup);
      useMacroButton.setRolloverIcon(im_usedo);
      useMacroButton.setPressedIcon(im_usedo);
      useMacroButton.setBorderPainted(false);
      useMacroButton.setContentAreaFilled(false);
      useMacroButton.setFocusPainted(false);

        useMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
            	String macro = getMacro( getSelectedIndex() );
            	ClientDirector.getDataManager().getClientScreen().getChatPanel().sendChatMessage(macro);
            }
        });

      delMacroButton = new AButton(im_delup);
      delMacroButton.setRolloverIcon(im_deldo);
      delMacroButton.setPressedIcon(im_deldo);
      delMacroButton.setBorderPainted(false);
      delMacroButton.setContentAreaFilled(false);
      delMacroButton.setFocusPainted(false);

        delMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
            	removeMacro( getSelectedIndex() );
            }
        });

      saveMacroButton = new AButton(im_saveup);
      saveMacroButton.setRolloverIcon(im_savedo);
      saveMacroButton.setPressedIcon(im_savedo);
      saveMacroButton.setBorderPainted(false);
      saveMacroButton.setContentAreaFilled(false);
      saveMacroButton.setFocusPainted(false);

        saveMacroButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
            	save(getMacros());
            }
        });

      buttonsPanel.add(newMacroButton);
      buttonsPanel.add(useMacroButton);
      buttonsPanel.add(delMacroButton);
      buttonsPanel.add(saveMacroButton);
      add(buttonsPanel, BorderLayout.SOUTH);
    }

 /*------------------------------------------------------------------------------------*/

  /** Called once to initialize the plug-in.
   *  @return if true we display the plug-in, return false if something fails during
   *          this init(), this way the plug-in won't be displayed.
   */
    public boolean init() {
        String macros[] = load();

        for( int i=0; i<macros.length; i++ )
             addMacro( macros[i] );

        return true; // this plug-in always works...
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when we need to reset the content of this plug-in.
   */
    public void reset() {
      // We remove the previous content
        centerPanel.removeAll();
        macrosGroup = new ButtonGroup();

        for( int i=0; i<MAX_MACROS; i++ ) {
            macrosFields[i] = null;
            macrosRadios[i] = null;
            macrosPanel[i] = null;
        }

      // and call init() again
    	init();
    }

 /*------------------------------------------------------------------------------------*/

  /** To search & process a text with macros. A macro can not be used twice in a line.
   *  @param text text to parse for macros '@NN@' calls.
   */
    public String processMacros( String text ) {

      // A macro can not be used twice in
    	boolean macrosFound[] = new boolean[MAX_MACROS];

    	for( int i=0; i<MAX_MACROS; i++ )
    	     macrosFound[i] = false;

        do{
           int pos=0, pos2=-1, id=-1;

         // 1 - Search for the '@NN@' pattern.
           do {
             if(pos2+1==text.length()) return text;
 
             pos=text.indexOf('@',pos2+1);
             if(pos<0 || pos==text.length()-1) return text;
 
             pos2=text.indexOf('@',pos+1);
             if(pos2<0) return text;

             if(pos2-pos>3) continue;

            // Valid id ?
              try{
                 id = Integer.parseInt(text.substring(pos+1,pos2));
              }
              catch(Exception e) {
             	//not a valid id
              }
           }
           while(id<0);

         // 2 - We get the selected macro...
           String macro;

           if(id>MAX_MACROS-1 || macrosFields[id]==null)
              macro = "#macro not found#";
           else if(macrosFound[id])
              macro = "#macro used twice#";
           else {
              macro = macrosFields[id].getText();
              macrosFound[id]= true;
           }

         // 3 - Replace the macro call by the macro's text.
           StringBuffer buf = new StringBuffer(text.substring(0,pos));
           buf.append(macro);

           if(pos2+1<text.length())
              buf.append(text.substring(pos2+1,text.length()));

           text = buf.toString();
        }
        while(true);
    }

 /*------------------------------------------------------------------------------------*/

  /** To add a new macros to our list.
   * @param macro text of the macro
   */
    protected void addMacro( String macro ) {

       int index=0;

       while( index<MAX_MACROS && macrosFields[index]!=null )
              index++;

       if(index==MAX_MACROS) {
            JOptionPane.showMessageDialog( null, "Maximum number of Macros reached !","Warning", JOptionPane.WARNING_MESSAGE);
            return;
       }

       macrosPanel[index] = new JPanel( new BorderLayout() );

       macrosRadios[index]= new ARadioButton(""+index);
       macrosFields[index] = new ATextField(macro,12);

       macrosPanel[index].add(macrosRadios[index],BorderLayout.WEST);
       macrosPanel[index].add(macrosFields[index],BorderLayout.EAST);
       centerPanel.add(macrosPanel[index]);
       macrosGroup.add(macrosRadios[index]);
       macrosRadios[index].setSelected(true);
       centerPanel.validate();
   }

 /*------------------------------------------------------------------------------------*/

  /** To remove a new macros to our list.
   * @param index index of the macros to remove
   */
    protected void removeMacro( int index ) {
    	if(index<0) return;

      // we remove the panel & other components
        centerPanel.remove(macrosPanel[index]);
        macrosGroup.remove(macrosRadios[index]);

        macrosPanel[index] = null;
        macrosRadios[index]= null;
        macrosFields[index] = null;

      // we shift the eventual macros that are on the right
        int i;

        for( i=index; i<MAX_MACROS-1 && macrosPanel[i+1]!=null; i++ ) {
             macrosPanel[i] = macrosPanel[i+1];
             macrosRadios[i] = macrosRadios[i+1];
             macrosFields[i] = macrosFields[i+1];
             macrosRadios[i].setText(""+i);
        }

        if(macrosFields[i]!=null) {
         // we remove the copy of the last element of the list
           macrosPanel[i] = null;
           macrosRadios[i]= null;
           macrosFields[i] = null;
        }

      // we validate & repaint...
        centerPanel.validate();
        centerPanel.repaint();

        if(macrosRadios[index]!=null)
           macrosRadios[index].setSelected(true);
        else if(index-1>=0 && macrosRadios[index-1]!=null)
           macrosRadios[index-1].setSelected(true);
        else if(macrosRadios[0]!=null)
           macrosRadios[0].setSelected(true);

    }

 /*------------------------------------------------------------------------------------*/

  /** To get our macros list.
   * @return all the macros
   */
    protected String[] getMacros() {

       int nb=0;

       while( nb<MAX_MACROS && macrosFields[nb]!=null )
              nb++;

       String list[] = new String[nb];

       for( int i=0; i<nb; i ++ )
            list[i] = macrosFields[i].getText();

       return list;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get a macro from our list.
   * @param index inde of the macro to get
   */
    protected String getMacro(int index) {

       if( index<0 || index>=MAX_MACROS || macrosFields[index]==null )
           return "Macro not found !! ("+index+")";

       return macrosFields[index].getText();
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the selected index in the macro list.
   *  @return the first selected index, -1 if none
   */
    protected int getSelectedIndex() {

        for( int i=0; i<MAX_MACROS && macrosPanel[i]!=null; i++ )
             if( macrosRadios[i].isSelected() )
                 return i;

        return -1; // none selected !!! should never happen
    }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
    * @return a short name for the plug-in
    */
      public String getPlugInName() {
      	  return "Macro";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in's author.
    * @return author name.
    */
      public String getPlugInAuthor() {
          return "Wotlas Team (Aldiss & Fred)";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the tool tip text that will be displayed in the JPlayerPanel.
    * @return a short tool tip text
    */
      public String getToolTipText() {
          return "To create HTML macros";
      }

 /*------------------------------------------------------------------------------------*/

   /** Eventual index in the list of JPlayerPanels
    * @return -1 if the plug-in has to be added at the end of the plug-in list,
    *         otherwise a positive integer for a precise location.
    */
      public int getPlugInIndex() {
          return -1;
      }

 /*------------------------------------------------------------------------------------*/

   /** Tells if this plug-in is a system plug-in that represents some base
    *  wotlas feature.
    * @return true means system plug-in, false means user plug-in
    */
      public boolean isSystemPlugIn() {
      	  return true;
      }

 /*------------------------------------------------------------------------------------*/

  /** Persistence class containing all the macros.
   */
    public static class MacrosList {
       private String macros[];

       public MacrosList() {
       }

       public String[] getMacros() {
           return macros;
       }

       public void setMacros(String[] macros) {
           this.macros = macros;
       }
    }

 /*------------------------------------------------------------------------------------*/

  /** To load the Macro config file. If the file is not found we return an empty list.
   *  @return the loaded macros list, an empty array if no config file was found
   */
    public String[] load() {
         String fileName = ClientDirector.getResourceManager().getConfig(
                           MACROS_PREFIX
                           +ClientDirector.getDataManager().getMyPlayer().getPrimaryKey()
                           +MACROS_SUFFIX );

         try{
            if( new File(fileName).exists() ) {
                MacrosList list = (MacrosList) PropertiesConverter.load(fileName);
                return list.getMacros();
            }
            else
                Debug.signal( Debug.NOTICE, null, "No macros config found..." );
         }
         catch (PersistenceException pe) {
            Debug.signal( Debug.ERROR, null, "Failed to load macros config : " + pe.getMessage() );
         }

         String listStr[] = new String[1];
         listStr[0] = "";
         return listStr;
    }

 /*------------------------------------------------------------------------------------*/

  /** To load the Macro config file. If the file is not found we return an empty list.
   * @param macros macros list to save
   * @return true if the save succeeded, false otherwise
   */
    public boolean save(String[] macros) {
        MacrosList list = new MacrosList();
        list.setMacros(macros);
       
         try{
             PropertiesConverter.save(list, ClientDirector.getResourceManager().getConfig(
                           MACROS_PREFIX
                           +ClientDirector.getDataManager().getMyPlayer().getPrimaryKey()
                           +MACROS_SUFFIX ) );
             return true;
         }
         catch (PersistenceException pe) {
             Debug.signal( Debug.ERROR, this, "Failed to save macros : " + pe.getMessage() );
             return false;
         }
    }

 /*------------------------------------------------------------------------------------*/

}
