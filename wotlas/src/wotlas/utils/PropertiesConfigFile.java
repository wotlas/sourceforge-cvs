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

package wotlas.utils;

import java.io.File;
import java.util.Properties;
import java.util.Enumeration;

/** Represents a Properties object that is loaded from a property file.
 *  Beware ! If the constructor fails to find the config file it will stop the JVM with
 *  an error message.<br>
 *
 *  Any setProperty() call will fail if the property doesnot already exist. Also
 *  note that updates are automatically saved to disk.
 *
 * @author Aldiss
 */

public class PropertiesConfigFile extends Properties {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Full path to our config file.
    */
    protected String configFilePath;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Constructor with the full config file path.
    * @param configFilePath file path to config.
    */
    public PropertiesConfigFile( String configFilePath ) {
    	super();

        this.configFilePath = configFilePath;

     // Load the config file & get ist properties
        Properties configProps = FileTools.loadPropertiesFile( configFilePath );

        if( configProps==null ) {
            Debug.signal( Debug.FAILURE, null, "Failed to load "+configFilePath+" !" );
            Debug.exit();
        }

     // We copy the loaded properties in our list
        Enumeration enum = configProps.propertyNames();

        while( enum.hasMoreElements() ) {
            String key = (String) enum.nextElement();
            setProperty( key, configProps.getProperty(key), false );
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To update a property of the config file. This method does NOTHING if the
    *  property doesn't exist.
    *
    *  @param key property key
    *  @param value new value
    */
    public Object setProperty( String key, String value ) {
    	if(getProperty(key)==null)
    	   return null;
    	
        return setProperty( key, value, true ); // set & save
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To update/set a property of the config file.
    *
    *  @param key property key
    *  @param value new value
    *  @param save do we have to save the change in the source config file ?
    */
    protected Object setProperty( String key, String value, boolean save ) {
         Object obj = super.setProperty( key, value );

      // Update the config file content... we replace the property value.
         if(!save)
            return obj;

         String oldConfig = FileTools.loadTextFromFile( configFilePath );

         if( oldConfig==null ) {
            Debug.signal( Debug.ERROR, this, "Failed to load "+configFilePath+" !" );
            return obj;
         }

         oldConfig = FileTools.updateProperty( key, value, oldConfig );

         if( !FileTools.saveTextToFile( configFilePath, oldConfig ) )
             Debug.signal( Debug.ERROR, this, "Failed to save "+configFilePath+" !" );

         return obj;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns an integer value from a property that is supposed to be a
    *  positive integer.
    *
    *  @param key property key
    *  @return the integer value, -1 if the property has a bad format or is a
    *          negative integer.
    */
    public int getIntegerProperty( String key ) {
    	String val = getProperty(key);

    	if(val==null)
    	   return -1;

    	try {
          int i_val = Integer.parseInt(val);

          if(i_val<0)
             return -1;
          return i_val;
        }catch(Exception e) {
          Debug.signal(Debug.ERROR, this, ""+key+" doesn't have a valid integer format.");
          return -1;
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tests if the given key points out a valid property ( non null or empty ).
   * @return true if the property exists and is not empty. False otherwise.
   */
   public boolean isValid(String key) {
    	String val = getProperty(key);

    	if(val==null || val.length()==0)
    	   return false;
    	return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tests if the given key points out a valid integer property ( non null or empty ).
   * @return true if the property exists and is not empty. False otherwise.
   */
   public boolean isValidInteger(String key) {
    	return getIntegerProperty(key)!=-1;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
