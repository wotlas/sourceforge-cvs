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

package wotlas.common;

import java.io.File;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;


/** Manages the different resources found in wotlas.
 *
 * @author Aldiss
 */

public class ResourceManager {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Where the fonts, graphics, musics, sounds, can be found...
    *  The sub-directories should be : music, fonts, sounds, universe, fonts, graphics
    *  graphics/imagelib, wizard, gui, home, servers.
    */
    private String basePath;

   /** Where the config files are stored
    */
    private String configPath;

   /** Where the help files are stored
    */
    private String helpPath;

   /** Where the log files are stored
    */
    private String logPath;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Constructor with the first path : the config path where config files are stored.
    * @param configPath where the config files are stored
    */
    public ResourceManager(  String basePath, String configPath, String helpPath, String logPath ) {
           this.basePath = basePath+File.separator;
           this.configPath = configPath+File.separator;
           this.helpPath = helpPath+File.separator;
           this.logPath = logPath+File.separator;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the complete path to the base resource given its name.
    *  @param resourceName the name of a resource like 'music/tarvalon.mid' or 'gui/pin.gif'
    *  @return full resource path
    */
    public String getBase( String resourceName ) {
    	return basePath+resourceName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the complete path to the log resource given its name.
    *  @param resourceName the name of a resource like 'client.log'
    *  @return full resource path
    */
    public String getLog( String resourceName ) {
    	return logPath+resourceName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the complete path to the config resource given its name.
    *  @param resourceName the name of a resource like 'client.cfg'
    *  @return full resource path
    */
    public String getConfig( String resourceName ) {
    	return configPath+resourceName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the complete path to the help resource given its name.
    *  @param resourceName the name of a resource like 'index.html'
    *  @return full resource path
    */
    public String getHelp( String resourceName ) {
    	return helpPath+resourceName;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the wanted image icon from the base's "gui" directory.
    *
    *  @param imageName the image's name like "wotlas.jpg" or "chat/smiley.gif"
    *  @return ImageIcon, null if the image was not found.
    */
    public ImageIcon getImageIcon( String imageName ) {
    	return new ImageIcon( basePath+"gui"+File.separator+imageName );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the wanted image from the base directory.
    *
    *  @param imageName the image's name like "gui/wotlas.jpg" or "graphics/imagelib/im.gif"
    *  @return Image, null if the image was not found.
    */
    public Image getBaseImage( String imageName ) {
        return Toolkit.getDefaultToolkit().getImage( basePath+imageName );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
