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

import wotlas.utils.*;
import wotlas.libs.persistence.*;
import wotlas.libs.log.LogResourceLocator;
import wotlas.libs.graphics2D.ImageResourceLocator;
import wotlas.libs.graphics2D.FontResourceLocator;
import wotlas.libs.sound.MusicResourceLocator;
import wotlas.libs.sound.SoundResourceLocator;
import wotlas.libs.wizard.WizardResourceLocator;
import wotlas.libs.aswing.ASwingResourceLocator;

import java.io.*;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.Properties;


/** Manages the different resources found in wotlas. Resources are searched in a JAR
 *  or via the provided (or default) basse path. If we are in a JAR file ( server or
 *  client jar ) we call 'external resources' resources that are stored outside the
 *  JAR. 
 *
 * @author Aldiss
 */

public class ResourceManager implements LogResourceLocator, ImageResourceLocator, FontResourceLocator,
                                        MusicResourceLocator, SoundResourceLocator, WizardResourceLocator,
                                        ASwingResourceLocator {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Wotlas Version Number for the code.
   */
     public static final String WOTLAS_VERSION = "1.3";

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Default location where are stored game resources when NOT packed in a Jar.
   *  We use this default value if (1) we are not in a JAR file,
   *  (2) no other base path is provided at start-up.
   */
     public static final String DEFAULT_BASE_PATH = "../base";

  /** Default location where are stored help docs when NOT packed in a Jar.
   *  We only use this default value if we are not in a JAR file.
   */
     public static final String DEFAULT_HELP_DOCS_PATH = "../docs/help";

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Wotlas client JAR file name.
    *  IF this jar file is not found we'll assume the project resources are not in
    *  a jar file.
    */
     public static final String WOTLAS_CLIENT_JAR = "wotlas-client.jar";

   /** Wotlas Server JAR file name.
    *  IF this jar file is not found we'll assume the project resources are not in
    *  a jar file.
    */
     public static final String WOTLAS_SERVER_JAR = "wotlas-server.jar";

   /** Wotlas root dir for resources located in a JAR. We'll search in this
    *  directory for every kind of resources.
    *  MUST START WITH A "/".
    */
     public static final String WOTLAS_JAR_ROOT_RESOURCE_DIR = "/base";

   /** Wotlas root dir for docs located in the JAR
    *  MUST START WITH A "/".
    */
     public static final String WOTLAS_JAR_ROOT_DOCS_DIR = "/docs/help";

   /** Tells in which dir we can store external files.
    */
     public static final String WOTLAS_JAR_EXTERNAL_DIR = "base-ext";  // must be different from other root dirs

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Configs Directory Name
    */
     public static final String CONFIGS_DIR = "configs";

   /** Fonts Directory Name
    */
     public static final String FONTS_DIR = "fonts";

   /** GUI Images Directory Name
    */
     public static final String GUI_IMAGES_DIR = "gui";

   /** Image Library Directory Name
    */
     public static final String IMAGE_LIBRARY_DIR = "graphics/imagelib";

   /** Logs Directory Name
    */
     public static final String LOGS_DIR = "logs";

   /** Configs Macros Directory Name
    */
     public static final String MACROS_DIR = "configs/macros";

   /** Music Directory Name
    */
     public static final String MUSICS_DIR = "music";

   /** Home Directory Name (where players are stored)
    */
     public static final String PLAYERS_HOME_DIR = "home";

   /** Server Configs Directory Name
    */
     public static final String SERVER_CONFIGS_DIR = "servers";

   /** GUI SMILEYS Images Directory Name
    */
     public static final String SMILEYS_IMAGES_DIR = "gui/chat";

   /** Sounds Directory Name
    */
     public static final String SOUNDS_DIR = "sounds";

   /** Universe Data Directory Name
    */
     public static final String UNIVERSE_DATA_DIR = "universe";

   /** Wizard Steps Directory Name
    */
     public static final String WIZARD_STEPS_DIR = "wizard";

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Where the fonts, graphics, musics, sounds, can be found...
    *  This path is only used if we are not in a JAR file.
    */
     private String basePath;

   /** Tells if we are in a JAR file or not.
    */
     private boolean inJar;

   /** Tells if we are in a wotlas client JAR file or not.
    */
     private boolean inClientJar;

   /** Tells if we are in a wotlas server JAR file or not.
    */
     private boolean inServerJar;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Constructor that uses default resource locations. Check the default
    *  JAR file names we'll seek (see the beginning of this class).
    *
    *  IMPORTANT : if the wotlas jar file is not found in the classpath
    *  we'll use the local classes and the default directory pathes.
    */
     public ResourceManager() {

           inJar = false;

        // Are we in a client JAR File ?
           inClientJar = Tools.hasJar(WOTLAS_CLIENT_JAR);

           if( inClientJar ) {
              inJar = true;
              createExternalClientDirectories();
              return; // ResourceManager created
           }

        // Are we in a server JAR File ?
           inServerJar = Tools.hasJar(WOTLAS_SERVER_JAR);

           if( inServerJar ) {
              inJar = true;
              createExternalClientDirectories();
              return; // ResourceManager created
           }


        // OK we are not in a JAR. We'll use the default external location for resources
           basePath = DEFAULT_BASE_PATH;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Changes the base path for resources.
    *  To use only if the resources are NOT in a JAR file.
    * @param basePath where the resources are located
    */
     public void setBasePath( String basePath ) {
     	if(inJar) {
     	   Debug.signal(Debug.WARNING, this, "Attempt to change the external basePath ! No need to we are in a JAR !");
     	   return;
     	}
     	
        this.basePath = basePath;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To tell if we are in a JAR.
    * @return true if we are in a JAR.
    */
     public boolean inJar() {
     	return inJar;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To tell if a path point out on an external resource of a JAR.
    *  We just compare the beginning of the file to the WOTLAS_JAR_EXTERNAL_DIR
    *
    * @param pathName path to analyze
    * @return true if this is an external resource, false otherwise
    */
     public boolean isExternal( String pathName ) {
        if( pathName.startsWith( WOTLAS_JAR_EXTERNAL_DIR ) )
            return true;
        return false;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates external directories if needed (if we are in a JAR).
    */
     public void createExternalClientDirectories() {
        if( !inClientJar )
            return;

        new File( getExternalConfigsDir() ).mkdirs();
        new File( getExternalMacrosDir() ).mkdirs();
        new File( getExternalLogsDir() ).mkdirs();
        new File( getExternalServerConfigsDir() ).mkdirs();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Creates external directories if needed (if we are in a JAR).
    */
     public void createExternalServerDirectories() {
        if( !inServerJar )
            return;

        new File( getExternalConfigsDir() ).mkdirs();
        new File( getExternalLogsDir() ).mkdirs();
        new File( getExternalServerConfigsDir() ).mkdirs();
        new File( getExternalPlayersHomeDir() ).mkdirs();
        new File( getUniverseDataDir() ).mkdirs();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To complete this directory name with the appropriate path begining.
    * @param dirName directory name of the resource directory
    */
     protected String getResourceDir( String dirName ) {
     	 if( inJar )
     	     return WOTLAS_JAR_ROOT_RESOURCE_DIR+"/"+dirName+"/";
         return basePath+File.separator+dirName+File.separator;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To complete this external directory name with the appropriate path begining.
    *  By external we mean outside of the JAR if there is one. If there is none
    *  the directory returned is the same that would be returned by getResourceDir()
    *
    * @param dirName directory name of the resource directory
    */
     protected String getExternalResourceDir( String dirName ) {
     	 if( inJar )
     	     return WOTLAS_JAR_EXTERNAL_DIR+"/"+dirName+"/";
         return basePath+File.separator+dirName+File.separator;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Configs Directory (in the inside the eventual JAR).
    */
     public String getConfigsDir() {
     	 return getResourceDir( CONFIGS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the External Configs Directory (external means outside the eventual JAR).
    */
     public String getExternalConfigsDir() {
     	 return getExternalResourceDir( CONFIGS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Fonts Directory.
    */
     public String getFontsDir() {
     	 return getResourceDir( FONTS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the GUI Images Directory.
    */
     public String getGuiImageDir() {
     	 return getResourceDir( GUI_IMAGES_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the Image Library Directory.
    */
     public String getImageLibraryDir() {
     	 return getResourceDir( IMAGE_LIBRARY_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the Logs Directory Name.
    */
     public String getExternalLogsDir() {
     	 return getExternalResourceDir( LOGS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Configs Macros Directory.
    */
     public String getExternalMacrosDir() {
     	 return getExternalResourceDir( MACROS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the Music Directory.
    */
     public String getMusicsDir() {
     	 return getResourceDir( MUSICS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /**  To get the Home Directory (where players are stored)
    */
     public String getExternalPlayersHomeDir() {
     	 return getExternalResourceDir( PLAYERS_HOME_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Server Configs Directory (outside the jar)
    */
     public String getExternalServerConfigsDir() {
     	 return getExternalResourceDir( SERVER_CONFIGS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the GUI SMILEYS Images Directory.
    */
     public String getGuiSmileysDir() {
     	 return getResourceDir( SMILEYS_IMAGES_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Sounds Directory.
    */
     public String getSoundsDir() {
     	 return getResourceDir( SOUNDS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Universe Data Directory. Server Universe data is always loaded from
    *  outside the eventual JAR.
    */
     public String getUniverseDataDir() {
     	 if( inClientJar )
     	     return WOTLAS_JAR_ROOT_RESOURCE_DIR+"/"+UNIVERSE_DATA_DIR+"/";
         else if( inServerJar )
     	     return WOTLAS_JAR_EXTERNAL_DIR+"/"+UNIVERSE_DATA_DIR+"/";

         return basePath+File.separator+UNIVERSE_DATA_DIR+File.separator;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Wizard Steps Directory.
    */
     public String getWizardStepsDir() {
     	 return getResourceDir( WIZARD_STEPS_DIR );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get Help Docs Directory.
    */
     public String getHelpDocsDir() {
         if(inJar)
            return WOTLAS_JAR_ROOT_DOCS_DIR+"/";
         return DEFAULT_HELP_DOCS_PATH+File.separator;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To list all the files in the universe directory ( on one level only ).
    *  The provided dirName must be a directory/sub-directory name under the
    *  universe directory. Because we know we are in the Universe directory we
    *  can perform intelligent choices whether to search in a JAR or not :
    *  Server Universe data is ALWAYS outside the JAR.
    *
    *  @param dirName directory name (must be a complete path)
    *  @param ext extension of the files to search, enter "" to get all the files.
    *  @return the files (not sub-dirs) that have the specified extension.
    */
     public String[] listUniverseFiles( String dirName, String ext ) {
     	   if( ext==null )
     	       ext ="";
     	
           if( inClientJar ) {
               return Tools.listFilesInJar( WOTLAS_CLIENT_JAR, dirName, ext );
           }
           else
               return FileTools.listFiles( dirName, ext );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To list all the directories of a directory ( on one level only ).
    *  The provided dirName must be a directory/sub-directory name under the
    *  universe directory. Because we know we are in the Universe directory we
    *  can perform intelligent choices whether to search in a JAR or not :
    *  Server Universe data is ALWAYS outside the JAR.
    *
    *  @param dirName directory name (must be a complete path)
    *  @return the sub-dirs of the given dirName (on one level only).
    */
     public String[] listUniverseDirectories( String dirName ) {     	
           if( inClientJar ) {
               return Tools.listFilesInJar( WOTLAS_CLIENT_JAR, dirName, null );
           }
           else
               return FileTools.listDirs( dirName  );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load an object from a property file. The filePath MUST be corresponding to a
    *  valid resource path.
    *
    * @param fileName a file name that has been appended to the end of a directory path
    *        provided by this ResourceManager.
    */
     public Object loadObject( String filePath ) {
           if( inJar && !isExternal(filePath) ) {
              InputStream is = this.getClass().getResourceAsStream( filePath );
              if(is==null) return null;

              Object o = null;

                try{
                   o = PropertiesConverter.load( is );
                }
                catch( PersistenceException pe ) {
                   Debug.signal( Debug.ERROR, this, pe );
                }

                try{
                   is.close();
                }catch(IOException e) {
                   e.printStackTrace();
                }

              return o;
           }
           else {
                try{
                   return PropertiesConverter.load( filePath );
                }
                catch( PersistenceException pe ) {
                   Debug.signal( Debug.ERROR, this, ""+pe );
                   return null;
                }
           }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To save an object to a property file. The filePath MUST be corresponding to an
    *  external path.
    *
    * @param o object to save.
    * @param fileName a file name that has been appended to the end of a directory path
    *        provided by this ResourceManager.
    */
     public boolean saveObject( Object o, String filePath ) {
         try{
             PropertiesConverter.save( o, filePath );
             return true;
         }
         catch( PersistenceException pe ) {
             Debug.signal( Debug.ERROR, this, pe );
             return false;
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load text from a file. The filePath MUST be corresponding to a valid resource
    *  path.
    *
    * @param fileName a file name that has been appended to the end of a directory path
    *        provided by this ResourceManager.
    */
     public String loadText( String filePath ) {
           if( inJar && !isExternal(filePath) ) {
              InputStream is = this.getClass().getResourceAsStream( filePath );
              if(is==null) return "";

              return FileTools.loadTextFromStream( is );
           }
           else
              return FileTools.loadTextFromFile( filePath );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To save text to a file. The filePath MUST be corresponding to an external path.
    *
    * @param fileName a file name that has been appended to the end of a directory path
    *        provided by this ResourceManager.
    * @param text text to save.
    */
     public boolean saveText( String filePath, String text ) {
           return FileTools.saveTextToFile( filePath, text );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load properties from a file. The filePath MUST be corresponding to a valid resource
    *  path.
    *
    * @param fileName a file name that has been appended to the end of a directory path
    *        provided by this ResourceManager.
    */
     public Properties loadProperties( String filePath ) {
           if( inJar && !isExternal(filePath) ) {
              InputStream is = this.getClass().getResourceAsStream( filePath );
              if(is==null) return new Properties();

              return FileTools.loadPropertiesFromStream( is );
           }
           else
              return FileTools.loadPropertiesFile( filePath );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To save text to a file. The filePath MUST be corresponding to an external path.
    *
    * @param props properties to save.
    * @param fileName a file name that has been appended to the end of a directory path
    *        provided by this ResourceManager.
    * @param header some text to display as header in the file.
    */
     public boolean saveProperties( Properties props, String filePath, String header ) {
           return FileTools.savePropertiesFile( props, filePath, header );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To list all the files of a directory ( on one level only ).
    *  USE THIS METHOD ONLY IF YOU KNOW THAT THE DIRECTORY GIVEN HERE COULD BE
    *  IN A JAR (if there is a jar, if none we search in the provided base path).
    *
    *  IF you know that the directory is NOT in a JAR then use a simple File.listFiles().
    *
    *  @param dirName directory name (must be a complete path)
    *  @return all the files of the specified directory (not sub-dirs).
    */
     public String[] listFiles( String dirName ) {
         return listFiles( dirName, "" );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To list all the files of a directory ( on one level only ).
    *  USE THIS METHOD ONLY IF YOU KNOW THAT THE DIRECTORY GIVEN HERE COULD BE
    *  IN A JAR (if there is a jar, if none we search in the provided base path).
    *
    *  IF you know that the directory is NOT in a JAR then use a simple File.listFiles().
    *
    *  @param dirName directory name (must be a complete path)
    *  @param ext extension of the files to search, enter "" to get all the files.
    *  @return the files (not sub-dirs) that have the specified extension.
    */
     public String[] listFiles( String dirName, String ext ) {
     	   if( ext==null )
     	       ext ="";
     	
           if( inClientJar && !isExternal(dirName) ) {
               return Tools.listFilesInJar( WOTLAS_CLIENT_JAR, dirName, ext );
           }
           else if( inServerJar && !isExternal(dirName) ){
               return Tools.listFilesInJar( WOTLAS_SERVER_JAR, dirName, ext );
           }
           else
               return FileTools.listFiles( dirName, ext );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To list all the directories of a directory ( on one level only ).
    *  USE THIS METHOD ONLY IF YOU KNOW THAT THE DIRECTORY GIVEN HERE COULD BE
    *  IN A JAR (if there is a jar, if none we search in the provided base path).
    *
    *  IF you know that the directory is NOT in a JAR then use a simple File.listFiles().
    *
    *  @param dirName directory name (must be a complete path)
    *  @return the sub-dirs of the given dirName (on one level only).
    */
     public String[] listDirectories( String dirName ) {     	
           if( inClientJar && !isExternal(dirName)) {
               return Tools.listFilesInJar( WOTLAS_CLIENT_JAR, dirName, null );
           }
           else if( inServerJar && !isExternal(dirName) ){
               return Tools.listFilesInJar( WOTLAS_SERVER_JAR, dirName, null );
           }
           else
               return FileTools.listDirs( dirName  );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a font resource as a stream.
    * @param fontPath font path we'll get as a stream
    * @return InputStream on the wanted font, null if the font was not found
    */
     public InputStream getFontStream( String fontPath ) {

        String fDir = getFontsDir();

        if( !fontPath.startsWith(fDir) )
            fontPath = fDir+fontPath;

        if( inJar )
            return this.getClass().getResourceAsStream( fontPath );
        else {
           try{
                return new FileInputStream( fontPath );
           }catch( FileNotFoundException fe ) {
                Debug.signal(Debug.ERROR, this, fe );
                return null;
           }
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get an inputstream on the wanted music.
    * @param musicName music name such as "tarvalon.mid"
    * @return InputStream on the wanted music, null if the music was not found
    */
     public InputStream getMusicStream( String musicName ) {

        String mDir = getMusicsDir();

        if( !musicName.startsWith(mDir) )
            musicName = mDir+musicName;

        if( inJar )
            return this.getClass().getResourceAsStream( musicName );
        else {
           try{
                return new FileInputStream( musicName );
           }catch( FileNotFoundException fe ) {
                Debug.signal(Debug.ERROR, this, fe );
                return null;
           }
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get an inputstream on the wanted sound.
    * @param soundName sound name such as "boing.wav"
    * @return InputStream on the wanted sound, null if the sound was not found
    */
     public InputStream getSoundStream( String soundName ) {

        String sDir = getSoundsDir();

        if( !soundName.startsWith(sDir) )
            soundName = sDir+soundName;

        if( inJar )
            return this.getClass().getResourceAsStream( soundName );
        else {
           try{
                return new FileInputStream( soundName );
           }catch( FileNotFoundException fe ) {
                Debug.signal(Debug.ERROR, this, fe );
                return null;
           }
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get an inputstream from a wanted file
    * @param filePath complete file path
    * @return InputStream on the wanted file, null if the file was not found
    */
     public InputStream getFileStream( String filePath ) {

        if( inJar )
            return this.getClass().getResourceAsStream( filePath );
        else {
           try{
                return new FileInputStream( filePath );
           }catch( FileNotFoundException fe ) {
                Debug.signal(Debug.ERROR, this, fe );
                return null;
           }
        }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the wanted image icon from the base's GUI directory.
    *
    *  @param imageName imageName with or without the complete resource path.
    *  @return ImageIcon, null if the image was not found.
    */
    public ImageIcon getImageIcon( String imageName ) {

        String imDir = getGuiImageDir();

        if( !imageName.startsWith(imDir) )
            imageName = imDir+imageName;

        if( inJar ) {
            URL url = this.getClass().getResource(imageName);
            return new ImageIcon( url );
        }
        else
           return new ImageIcon( imageName );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the wanted image from the base GUI directory.
    *
    *  @param imageName imageName with or without the complete resource path.
    *  @return Image, null if the image was not found.
    */
    public Image getGuiImage( String imageName ) {

        String imDir = getGuiImageDir();

        if( !imageName.startsWith(imDir) )
            imageName = imDir+imageName;

        if( inJar ) {
            URL url = this.getClass().getResource(imageName);
            return Toolkit.getDefaultToolkit().getImage( url );
        }
        else
            return Toolkit.getDefaultToolkit().getImage( imageName );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Returns the wanted image from the Image Library directory.
    *
    *  @param imagePath image name from the library with FULL resource path.
    *  @return Image, null if the image was not found.
    */
    public Image getLibraryImage( String imagePath ) {
        if( inJar ) {
            URL url = this.getClass().getResource(imagePath);
            return Toolkit.getDefaultToolkit().getImage( url );
        }
        else
            return Toolkit.getDefaultToolkit().getImage( imagePath );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
