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

package wotlas.libs.graphics2D;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

/** An ImageLibrary manages shared images ( i.e. used by more than
 *  one object ). It also proposes static methods for image loading.
 *
 *  <p>It relies on a image database that is loaded into memory at creation time.
 *  The database must have the following structure:</p>
 *   <pre>
 *     - it is a tree composed of directories and images. Each directory can contain
 *       sub-directories or/and images.
 *
 *     - the directories must have the following format : <DIR_NAME>-<XXX>[-option]
 *       XXX represents the directory ID (must start at 0 and increase one by one).
 *       Available options are :
 *
 *          o -jit : "just in time". All the images that are in the directory AND its
 *            sub-directories are NOT loaded into memory until they are needed. This
 *            option is always automatically propagated to sub-directories.
 *
 *          o -exc : "exclusion". Only one of the sub-directories can be loaded into
 *            memory at the same time. When you load one of them the others are
 *            automatically unloaded (flushed). It is logical that the images of a
 *            "-exc" directory are not loaded at start-up. You need to load them manually.
 *            
 *            This option is especially useful when handling huge images that don't need
 *            to be in memory at the same time.
 *
 *       Example :  animals-0
 *                      |
 *                      --- cats-0-jit    // lots of small cat images, loaded just in time
 *                      |
 *                      --- dogs-1-exc    // huge dog images
 *                           |
 *                           --- poddle-0
 *                           |
 *                           --- ousti-1
 *                           |
 *                           --- cork-2
 *
 *     - images must have the following format : <IMAGE_NAME>-<XXX>.<jpg|gif>
 *       XXX represents the image ID (must start at 0 and increase one by one).
 *
 *       Example : cats-0-jit
 *                    |
 *                    --- cat-0.jpg
 *                    |
 *                    --- strange_cat-1.jpg
 *                    |
 *                    --- an_other_cat-2.gif
 *                    |
 *                    --- big_cats-0  // sub-directory, note that the IDs between images
 *                                    // and directories are separated.
 *
 *     - for animations all the images must be in the same directory. There musn't
 *       be other images in the directory. You can create an Animation object to manage
 *       your animation.
 *   </pre>
 *
 *   <p>As you see directories and images have IDs. These IDs help for array creation and 
 *      direct access to images. Images in the ImageLibrary are INT_ARGB buffered images.
 *   </p>
 *   <p>With the example above an ImageIdentifier representing the cat-0.jpg could be created
 *      either by calling :</p>
 *      <pre>
 *        String path[] = { "animals-0", "cats-0" };
 *        ImageIdentifier imCat = new ImageIdentifier( path );
 *
 *        or
 *
 *        String path[] = { "animals-0", "cats-0", "cat-0.jpg" };
 *        ImageIdentifier imCat = new ImageIdentifier( path );
 *
 *        or
 *
 *        ImageIdentifier imCat = new ImageIdentifier( "animals-0/cats-0/cat-0.jpg" );
 *       </pre>
 *
 *   <p> Note that in both case the internal representation of the ImageIdentifier will be
 *       { [0, 0], 0 }. Note also that it is optional to give the directory options such as '-jit'
 *       or '-exc'. You can either enter 'cats-0' or 'cats-0-jit'. If you specify a precise image
 *       name, the extension is mandatory : we entered 'cat-0.jpg' not 'cat-0'.
 *   </p>
 *
 *   <p> Developer Tips: This image library can be used as a singleton if your application
 *       has a need for it. Otherwise we recommend that you extend this class and add a
 *       public constructor. You could then have, for example, an application that has more
 *       than one ImageLibrary. For security just set the 'defaultImageLibrary' to null in the
 *       sub-class constructor.
 *   </p>
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.ImageIdentifier
 */

public class ImageLibrary {

 /*------------------------------------------------------------------------------------*/

  /** Debug Mode. If set to true, recompile the class and it will display extra
   *  warning/error messages.
   */
     protected static final boolean DEBUG_MODE = false;

  /** Do we have to warn about db entries that have bad format ? or just ignore them ?
   */
     protected static boolean ignoreBadFormatEntries = true;

   /** Our Default ImageLibrary.
    */
     protected static ImageLibrary defaultImageLibrary;

 /*------------------------------------------------------------------------------------*/

  /** Root Directory of the Image Library. ImageLibDir is an internal class.
   */
     protected ImageLibDir rootDir;

  /** Path where user fonts are stored (ex: ../base/fonts that contains "Lucida.ttf").
   */
     protected String userFontPath;

 /*------------------------------------------------------------------------------------*/
  
  /** Do we have to display db entries with bad format ?
   * 
   * @param ignore if false we warn about disk entries of the ImageLibrary that
   *        have a bad format (exceptions are thrown).
   */
   public static void ignoreBadFormatEntries( boolean ignore ) {
          ignoreBadFormatEntries = ignore;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the image database path. We load all the images in memory.
   *
   * @param imageDataBasePath the path to the image database.
   * @param userFontPath the path where user fonts are stored (ex: lucida.ttf).
   * @exception ImageLibraryException if an error occurs while loading the images.
   */
   protected ImageLibrary( String imageDataBasePath, String userFontPath )
   throws ImageLibraryException {
   
          this.userFontPath = userFontPath;

       // 1 - Check DataBase
          File homeDir = new File(imageDataBasePath);
  
          if( !homeDir.isDirectory() || !homeDir.exists() )
             throw new ImageLibraryException("Image DataBase Not found : "+imageDataBasePath
                                    +" is not a valid directory.");
       // We create the database structure (recursive creation)
          rootDir = new ImageLibDir( homeDir, null );

       // We load the images of the database structure
          int loadedImages = rootDir.initImageLoad();
         
          if(DEBUG_MODE) System.out.println("Notice: ImageLibrary created with "+loadedImages+" images.");
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default image databasepath.
   *
   * @return the default image databasepath.
   */
   public String getImageDataBasePath() {
         return rootDir.dir.getPath();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the font path where all the user Fonts are stored. This path is used by
   *  some of the classes of the wotlas.libs.graphics2D.drawable package that draw text.
   *
   * @return the default font path.
   */
   public String getUserFontPath() {
         return userFontPath;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Creates an ImageLibrary or returned the previously created one.
   *  We load all the images from the specified image database.<p>
   *
   *  <p>To manually load directories/images which parent directories have the format
   *  "-XX-jit" or "-XX-exc" you can use the loadImage() method.<br>
   *
   *  <p>You can manually unload images from "-jit" or "-exc" "set" directories by calling
   *  the unloadImage() method.
   *
   *  IMPORTANT: for directory names that end with "-XX" or "-XX-<option>" the XX index must start
   *  at zero and increment with no jumps between numbers.
   *
   * @param imageDataBasePath the path to the image database.
   * @return the created (or previously created) image library.
   * @exception ImageLibraryException if an error occurs while loading the images.
   */
   public static ImageLibrary createImageLibrary( String imageDataBasePath, String userFontPath )
   throws ImageLibraryException {
         if( defaultImageLibrary==null )
             defaultImageLibrary = new ImageLibrary( imageDataBasePath, userFontPath );

         return defaultImageLibrary;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default image library.
   *
   * @return the default image library.
   */
   public static ImageLibrary getDefaultImageLibrary() {
         return defaultImageLibrary;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image from the database. If we have to load the given image we check that
   *  it is in a "JIT" directory. If it's the case we load the image and return the one
   *  wanted, if not we return null and an error is declared.
   *
   * @param imId complete image identifier
   * @return image found in the library.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public BufferedImage getImage( ImageIdentifier imId )
   throws ImageLibraryException{

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is        
           for( int index=0; index<imId.dirIds.length; index++ )
                dir = dir.childDirs[imId.dirIds[index]];

        // is the image available ?
           BufferedImage bufIm = dir.images[imId.imageId];

           if(bufIm!=null)
              return bufIm; // success !
        }
        catch( Exception e ) {
       	   throw new ImageLibraryException(""+e);
        }

     // We try to load the image ( must be a JIT image ).
        if( dir.dirOption!=ImageLibDir.OPT_JIT ) {
            if(DEBUG_MODE)
               System.out.println("ERROR: Trying to load an image that is not in a JIT directory : "+imId);
            return null; // directory has no "Just in Time" option we return null;
        }

        return dir.loadImage( imId.imageId );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image in the database. You can use this method to load images that are
   *  in "-JIT" directories or "-EXC" directories. If a directory that has the '-exc' option
   *  is found on the path to the image, we unload all the other sub-directories starting
   *  from this directory.
   *
   * @param imId complete image identifier
   * @return the image just loaded in the library.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public BufferedImage loadImage( ImageIdentifier imId )
   throws ImageLibraryException {

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is
           for( int index=0; index<imId.dirIds.length; index++ ) {
                dir = dir.childDirs[imId.dirIds[index]];

             // We check for the "-EXC" option                
                if(dir.dirOption==ImageLibDir.OPT_EXC && index<imId.dirIds.length-1) {
                   // we unload other sub-directories
                      for( int i=0; i<dir.childDirs.length; i++ )
                           if(i!=imId.dirIds[index+1])
                              dir.childDirs[i].unloadAllImages(true);
                }
           }

        // is the image already available ?
           BufferedImage bufIm = dir.images[ imId.imageId ];

           if(bufIm!=null)
              return bufIm; // success !
       }
       catch( Exception e ) {
         throw new ImageLibraryException(""+e);
       }

    // We try to load the image...
       return dir.loadImage( imId.imageId );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load all the images of a directory in the database. You can use this method to load
   * images that are in "-JIT" directories or "-EXC" directories. If a directory that has the
   * '-exc' option is found on the path to the image, we unload all the other sub-directories
   * starting from this directory.
   *
   * @param imId complete image identifier pointing to a directory of the database
   * @param loadSubDirsAlso if true we also load the sub-directories
   * @return the number of images loaded in the library.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public int loadImagesFromDirectory( ImageIdentifier imId, boolean loadSubDirsAlso )
   throws ImageLibraryException {

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is
           for( int index=0; index<imId.dirIds.length; index++ ) {
                dir = dir.childDirs[imId.dirIds[index]];

             // We check for the "-EXC" option                
                if(dir.dirOption==ImageLibDir.OPT_EXC && index<imId.dirIds.length-1) {
                   // we unload other sub-directories
                      for( int i=0; i<dir.childDirs.length; i++ )
                           if(i!=imId.dirIds[index+1])
                              dir.childDirs[i].unloadAllImages(true);
                }
           }
       }
       catch( Exception e ) {
          throw new ImageLibraryException(""+e);
       }

    // is the image already available ?
       return dir.loadAllImages(loadSubDirsAlso);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To check that the specified ImageIdentifier points out a valid Imagelibrary entry.
   *
   * @param imId complete image identifier
   * @return true if the image is in the library (already loaded), false if not.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public boolean checkImage( ImageIdentifier imId )
   throws ImageLibraryException {

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is supposed to be
           for( int index=0; index<imId.dirIds.length; index++ )
                dir = dir.childDirs[imId.dirIds[index]];

        // is the image available ?
           if( dir.images[ imId.imageId ]!=null)
               return true;
       }
       catch( Exception e ) {
           throw new ImageLibraryException(""+e);
       }

       return false;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To unload an image from memory.
   * @param imID image identifier pointing the imageSet to unload.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public void unloadImage( ImageIdentifier imId )
   throws ImageLibraryException {

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is
           for( int index=0; index<imId.dirIds.length; index++ )
                dir = dir.childDirs[imId.dirIds[index]];
       }
       catch( Exception e ) {
           throw new ImageLibraryException(""+e);
       }

    // we unload the image.
       dir.unloadImage( imId.imageId );
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To unload all the images of a directory in the database.
   *
   * @param imId complete image identifier pointing to a directory of the database
   * @param unloadSubDirsAlso if true we also load the sub-directories
   * @return the number of images loaded in the library.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public int unloadImagesFromDirectory( ImageIdentifier imId, boolean unloadSubDirsAlso )
   throws ImageLibraryException {

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is
           for( int index=0; index<imId.dirIds.length; index++ )
                dir = dir.childDirs[imId.dirIds[index]];
       }
       catch( Exception e ) {
           throw new ImageLibraryException(""+e);
       }

    // we unload all the images
       return dir.unloadAllImages(unloadSubDirsAlso);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image's width. IMPORTANT: We suppose the image is in the database
   *  (except if it's a JIT image).
   *
   * @param imId image identifier
   * @return width
   * @exception ImageLibraryException if the imId is invalid.
   */
   public int getWidth( ImageIdentifier imId )
   throws ImageLibraryException {

      BufferedImage bufIm = getImage( imId );
      if( bufIm==null )
          return -1;

      return bufIm.getWidth(null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image's height. IMPORTANT: We suppose the image is in the database
   *  (except if it's a JIT image).
   *
   * @param imId image identifier
   * @return height
   * @exception ImageLibraryException if the imId is invalid.
   */
   public int getHeight( ImageIdentifier imId )
   throws ImageLibraryException {

      BufferedImage bufIm = getImage( imId );
      if( bufIm==null )
          return -1;

      return bufIm.getHeight(null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** For Animations. We return the number of images of the specified ImageIdentifier's
   *  directory. Animation objects call this method to initialize.
   *
   * @param imId image identifier
   * @return length of the dir.images[] array given by the imId.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public int getAnimationLength( ImageIdentifier imId )
   throws ImageLibraryException {

      ImageLibDir dir = rootDir;

       try{
        // we select the directory where the image is supposed to be
           for( int index=0; index<imId.dirIds.length; index++ )
                dir = dir.childDirs[imId.dirIds[index]];
       }
       catch( Exception e ) {
           throw new ImageLibraryException(""+e);
       }

      return dir.images.length;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the ImageIdentifier of an image given a key-word. We don't load any images,
   *  we just check the image file names that are in the given directory.
   *
   *  This is especially useful when you are searching for an image's mask. If the image
   *  mask is in the same directory and has the word 'mask' somewhere in its
   *  file name, getImageIdentifier( myDirectoryId, "mask" ) will return its
   *  entire identifier.
   *
   * @param imDirectory complete image identifier representing the directory where to search.
   * @param keyword word to search.
   * @return if found, the complete ImageIdentifier of the first image matching the keyword,
   *         null otherwise.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public ImageIdentifier getImageIdentifier( ImageIdentifier imDirectory, String keyword )
   throws ImageLibraryException {

           ImageLibDir dir = rootDir;

        // we select the directory where the image is supposed to be
           try{
             for( int index=0; index<imDirectory.dirIds.length; index++ )
                  dir = dir.childDirs[imDirectory.dirIds[index]];
           }
           catch( Exception e ) {
               throw new ImageLibraryException("Bad ImageIdentifier: "+e);
           }

        // we search among the images we have.
           File imageFiles[] = dir.dir.listFiles();
           if( imageFiles==null ) return null;

           for( int l=0; l<imageFiles.length; l++ ) {
             if( imageFiles[l].isDirectory())
                 continue;

             int id = getImageID( imageFiles[l].getName() );
             if( id<0 ) continue; // not a valid image file

            // Is it the image we wanted ?
               if( imageFiles[l].getName().indexOf( keyword ) >=0 ) {
                   ImageIdentifier result = new ImageIdentifier( imDirectory );
                   result.imageId = (short) id;
                   return result;
               }
           }

       return null; // not found
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the File object that represents an ImageIdentifier of our database.
   *  We don't load any images, just return the associated File object.
   *
   * @param imId complete image identifier representing an image of the database.
   * @return if found, the complete File path to the image on disk, null otherwise.
   * @exception ImageLibraryException if the imId is invalid.
   */
   public File getImageFile( ImageIdentifier imId )
   throws ImageLibraryException {

           ImageLibDir dir = rootDir;

        // we select the directory where the image is supposed to be
           try{
             for( int index=0; index<imId.dirIds.length; index++ )
                  dir = dir.childDirs[imId.dirIds[index]];
           }
           catch( Exception e ) {
               throw new ImageLibraryException("Bad ImageIdentifier: "+e);
           }

        // we search among the images we have.
           File imageFiles[] = dir.dir.listFiles();
           if( imageFiles==null ) return null;

           for( int l=0; l<imageFiles.length; l++ ) {
             if( imageFiles[l].isDirectory())
                 continue;

             int id = getImageID( imageFiles[l].getName() );
             if( id<0 ) continue; // not a valid image file

            // Is it the image we wanted ?
               if( id==imId.imageId )
                   return imageFiles[l];
           }

       return null; // not found
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given a directory name of our database ( format <name>-<number>-<option> )
   *  we return the <number> part.
   *
   * @param name directory name
   * @return ID>=0 if valid, -1 if not valid.
   */
   protected static int getDirectoryID( String dirName ) {

       String name = dirName.toLowerCase();
       String s_val = null;

    // 1 - we retrieve the substring
       if(name.endsWith("-jit")||name.endsWith("-exc")) {
           int last = name.lastIndexOf('-');
           if(last<2) return -1; // error: invalid index

           int first = name.lastIndexOf('-', last-1);
           if(first<0) return -1; // error: invalid format

           s_val = name.substring( first+1, last );
       }
       else {
           int first = name.lastIndexOf('-');
           if( first<0 || first==name.length()-1 ) return -1;

           s_val = name.substring( first+1, name.length() );
       }

    // 2 - We parse the substring
       try{
           return Integer.parseInt( s_val );
       }catch(NumberFormatException bne) {
       	   return -1; // invalid format
       }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given an image name of our database ( format <name>-<number>.<jpg|gif> )
   *  we return the <number> part.
   *
   * @param name image name
   * @return ID>=0 if valid, -1 if not valid.
   */
   static protected int getImageID( String imageName ) {

       String name = imageName.toLowerCase();

    // 1 - we retrieve the substring
       if( !name.endsWith(".jpg") && !name.endsWith(".gif") )
           return -1; // bad format

       int lastPoint = name.lastIndexOf('.');
       if(lastPoint<2) return -1; // error: invalid index

       int first = name.lastIndexOf('-', lastPoint-1);
       if(first<0) return -1; // error: invalid format

    // 2 - We parse the substring            
       try{
           return Integer.parseInt( name.substring( first+1, lastPoint ) );
       }catch(NumberFormatException bne) {
       	   return -1; // invalid format
       }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given a directory of our database and a number ( format <name>-<number>-<option> )
   *  we return the immediate sub-directory name that has the specified number.
   *
   * @param path directory path where to search
   * @param idToFind <number> part of the format to find.
   * @return directory name that has the given ID, null if not found
   */
   static protected String getDirectoryNameFromID( String path, int idToFind ) {

      File list[] = new File(path).listFiles();
      if(list==null) return null;

      for( int i=0; i<list.length; i++ ) {
             if( !list[i].isDirectory() )
                 continue;

             if( getDirectoryID( list[i].getName() )==idToFind )
                 return list[i].getName();
      }

      return null; // not found
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image given its name. We don't check for any name format, we just try
   *  to load the image.
   *
   * @param the path to the image
   * @return the loaded image...
   */
    static public Image loadImage( String path ) {
       Image im;
       MediaTracker tracker = new MediaTracker(new Label());

         im = Toolkit.getDefaultToolkit().getImage(path);
         tracker.addImage(im,0);

         try{
               tracker.waitForID(0);
         }
         catch(InterruptedException e) { e.printStackTrace(); }

       return im;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load all the images of a directory. The image name must follow the format :
   *  <name>-<number>.<ext> where ext is either "jpg" or "gif".
   *
   *  Important : the returned array can have null fields if non-image files were found
   *              in the specified directory.
   *
   * @param path the path to the images
   * @return the loaded images... null if there are none
   */
    static public Image[] loadImages( String path ) {
    	return loadImages( new File(path) );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load all the images of a directory. The image name must follow the format :
   *  <name>-<number>.<ext> where ext is either "jpg" or "gif".
   *
   *  Important : the returned array can have null fields if non-image files were found
   *              in the specified directory.
   *
   * @param path the path to the images
   * @return the loaded images... null if there are none
   */
    static public Image[] loadImages( File path ) {
      File list[] = path.listFiles();
      
      if(list==null)
         return null;

      Image im[] = new Image[list.length];
      MediaTracker tracker = new MediaTracker(new Label());
      Toolkit tk = Toolkit.getDefaultToolkit();

         for( int i=0; i<list.length; i++) {

            if( list[i].isDirectory() )
                continue;

            int id = getImageID( list[i].getName() );

            if(id<0) {
               if(DEBUG_MODE) System.out.println( "Warning: Bad Image Name Format ! ( "+list[i]+" )");
               continue;
            }

            im[id] = tk.getImage( path.getPath()+File.separator+list[i].getName() );
            tracker.addImage(im[id],i);
         }

         try{
             tracker.waitForAll();
         }catch(InterruptedException e) { e.printStackTrace(); }
          
       return im;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image and transform it into a ARGB buffered image.
   *
   * @param the path to the image
   * @return the loaded buffered image...
   */
    static public BufferedImage loadBufferedImage( String path ) {
    	return loadBufferedImage( path, BufferedImage.TYPE_INT_ARGB );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image and transform it into a buffered image of the specified type.
   *
   * @param the path to the image
   * @return the loaded buffered image...
   */
    static public BufferedImage loadBufferedImage( String path, int imageType ) {
     // We load the image.
        Image im = loadImage( path );
        
        if(im==null)
           return null;

     // We transform this image into a buffered image
        BufferedImage bufIm = new BufferedImage( im.getWidth(null), im.getHeight(null), imageType );
        Graphics2D offBf = bufIm.createGraphics();
        offBf.drawImage( im,0,0,null );

      return bufIm;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load all the images of a directory. The image name must follow the format :
   *  <name>-<number>.<ext> where ext is either "jpg" or "gif". The returned array has
   *  no holes.
   *
   * @param path the path to the images
   * @return the loaded images transformed into buffered images...
   */
    static public BufferedImage[] loadBufferedImages( String path ) {
        return loadBufferedImages( new File(path) );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load all the images of a directory. The image name must follow the format :
   *  <name>-<number>.<ext> where ext is either "jpg" or "gif". The returned array has
   *  no holes. The <number> must start at 0.
   *
   *  Important : the returned array can have null fields if the image numbers have jump.
   *
   * @param path the path to the images
   * @return the loaded images transformed into buffered images... a 0 length array if
   *         there are no images. We never return null.
   */
    static public BufferedImage[] loadBufferedImages( File path ) {
     // We load all the images.
        Image im[] = loadImages( path );
        
        if(im==null)
           return new BufferedImage[0];

     // We count how many images we have...
        int imageCount=0;

        for( int c=0; c<im.length; c++ )
           if( im[c]!=null ) imageCount=c+1; // we keep the max

        if(imageCount==0)
           return new BufferedImage[0];

     // We transform these images into buffered images
        BufferedImage bufIm[] = new BufferedImage[imageCount];
      
        for( int c=0; c<im.length; c++ )
           if( im[c]!=null ) {
       	       bufIm[c] = new BufferedImage( im[c].getWidth(null),
                                             im[c].getHeight(null), BufferedImage.TYPE_INT_ARGB );

               Graphics2D offBf = bufIm[c].createGraphics();
               offBf.drawImage( im[c],0,0,null);
          }

     return bufIm;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To find an image with a given index ( filename format is <name>-<index>.<jpg | gif>)
   *  in a given file list.
   *
   * @param imageFiles list of files to investigate...
   * @param imageIndex index of the image to find
   * @param imageType if you don't know what to put here set it to BufferedImage.TYPE_INT_ARGB
   *        it's the type of the image we are going to create.
   * @return null if the image is not found
   */
    static public BufferedImage findImageIn( File imageFiles[], int imageIndex, int imageType ){
       if(imageFiles==null) return null;

       for( int l=0; l<imageFiles.length; l++ ) {
             if( imageFiles[l].isDirectory() )
                 continue;

             int id = getImageID( imageFiles[l].getName() );
             if(id<0) continue;

          // Is it the image we wanted ?
             if( id == imageIndex )
                 return loadBufferedImage( imageFiles[l].getPath(), imageType );
       }

       return null;  // not found
    }

 /*------------------------------------------------------------------------------------*/

 /** Represents a directory of the Image Library. It can contains other ImageLibDir and/or
  * images. There are no GETTERS/SETTERS because this class is for internal use only. Data
  * can be accessed directly.
  */

 class ImageLibDir {

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Directory Option : no option
   */
    public final static byte OPT_NONE = 0;

  /** Directory Option : JIT = just in time, directory content loaded only when needed.
   */
    public final static byte OPT_JIT = 1;

  /** Directory Option : EXC = exclude, directory content loaded only by the user, one subdir
   *  only at the same time. Other unused directories are automatically unloaded.
   */
    public final static byte OPT_EXC = 2;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Directory File representation.
   */
    protected File dir;

  /** Directory Option (OPT_NONE, OPT_JIT, OPT_EXC).
   */
    protected byte dirOption;

  /** Parent directory. If null it means this dir is root.
   */
    protected ImageLibDir parentDir;

  /** Eventual Sub-directories
   */
    protected ImageLibDir childDirs[];

  /** Eventual images of this directory.
   */
    protected BufferedImage images[];

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor.
   * @param dir file representing the directory of this ImageLibDir object.
   * @param parentDir parent directory, null if this is the root directory
   * @exception ImageLibraryException if ignoreBadFormatEntries=false and the image db has bad entries.
   */
    public ImageLibDir( File dir, ImageLibDir parentDir )
    throws ImageLibraryException {

        this.dir = dir;
        this.parentDir = parentDir;

      // 1 - We get the eventual option of this directory
         if( dir.getName().endsWith("-jit") )
             dirOption = OPT_JIT;
         else if( dir.getName().endsWith("-exc") )
             dirOption = OPT_EXC;
         else if( parentDir!=null && parentDir.dirOption==OPT_JIT )
             dirOption = OPT_JIT;  // this option is always propagated to sub-directories.
         else
             dirOption = OPT_NONE;
       
      // 2 - Is there a child structure to create ?
         File childs[] = dir.listFiles();

         if(childs==null) {
            // Empty directory
               if(DEBUG_MODE) System.out.println("WARNING: Empty directory found ( "+dir+" )");
       
               childDirs = new ImageLibDir[0];
               images = new BufferedImage[0];
               return;
         }

      // 3 - We check the child structure
         int nbChildDirs = 0;
         int nbImages = 0;

         for( int c=0; c<childs.length; c++ )
            if( childs[c].isDirectory() ) {
              // CHILD DIRECTORIES
                int id = ImageLibrary.getDirectoryID(childs[c].getName());

                if( id<0 ) {
                    if( ignoreBadFormatEntries ) {
                        if(DEBUG_MODE) System.out.println("ERROR: ImageLibrary directory has a bad format : "+childs[c]);
                    }
                    else
                        throw new ImageLibraryException("ImageLibrary directory has a bad format : "+childs[c]);
                }
                else if( id>=nbChildDirs )
                        nbChildDirs = id+1;
            }
            else {
              // IMAGES OF THIS DIRECTORY
                int id = ImageLibrary.getImageID(childs[c].getName());

                if( id<0 ) {
                    if( ignoreBadFormatEntries )
                        if(DEBUG_MODE) System.out.println("ERROR: ImageLibrary image has a bad format : "+childs[c]);
                    else
                        throw new ImageLibraryException("ImageLibrary image has a bad format : "+childs[c]);
                }
                else if( id>=nbImages )
                        nbImages = id+1;
            }

       // 4 - We can now create our child structure. Images will be loaded later.
          childDirs = new ImageLibDir[nbChildDirs];
          images = new BufferedImage[nbImages];

          for( int c=0; c<childs.length; c++ )
            if( childs[c].isDirectory() ) {
             // We recursively construct the child structure
                int id = ImageLibrary.getDirectoryID(childs[c].getName());
                if( id<0 ) continue;

                childDirs[id] = new ImageLibDir( childs[c], this );
            }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Called to load the image library the first time. We propagate the call to
   *  our childs. We stop the load of childs if we encounter a JIT or EXC directory.
   *  @return the number of images loaded
   */
    public int initImageLoad() {
         int loaded=0;

      // No image to load for these options
         if( dirOption==OPT_JIT || dirOption==OPT_EXC )
             return 0;

         if(images.length!=0) {
            images = loadBufferedImages( dir );

            for(int i=0; i<images.length; i++ )
                if(images[i]!=null) loaded++;
         }

      // Child directories init propagation
         for( int i=0; i<childDirs.length; i++ )
              loaded += childDirs[i].initImageLoad();
         
         return loaded;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get an image of our directory. We don't perform any checks.
    *  @param index index of the image.
    *  @return the image that has the wanted index
    */
    public BufferedImage getImage( int index ) {
           return images[index];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load an image of our directory.
    *  @param index index of the image.
    *  @return the image we just loaded.
    */
    public BufferedImage loadImage( int index ) {
           images[index] = findImageIn( dir.listFiles(), index, BufferedImage.TYPE_INT_ARGB );
           return images[index];
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load all the image of our directory, and if suddirs=true of all our
    *  sub-directories (unless we find JIT or EXC options on their directories !).
    *  @param loadSubDirsAlso if true we propagate an initImageLoad() to sub-directories.
    *  @return the number of images loaded
    */
    public int loadAllImages( boolean loadSubDirsAlso ) {
      // We load valid images
         int loaded = 0;

         if(images.length!=0) {
            images = loadBufferedImages( dir );

            for(int i=0; i<images.length; i++ )
                if(images[i]!=null) loaded++;
         }

      // Child directories load propagation ( by init() call )
         if(loadSubDirsAlso)
            for( int i=0; i<childDirs.length; i++ )
                 loaded += childDirs[i].initImageLoad();
         
         return loaded;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To unload an image of our directory.
    *  @param index index of the image.
    */
    public void unloadImage( int index ) {
    	if(images[index]!=null) {
    	   images[index].flush();
    	   images[index] = null;
    	}
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To unload all the images of our directory. If unloadSubDirsAlso=true we also
    *  unload sub directories.
    *  @param unloadSubDirsAlso if true we propagate the unloadAll call to sub-directories.
    *  @return the numbers of images unloaded from the library.
    */
    public int unloadAllImages( boolean unloadSubDirsAlso ) {
       int unloaded = 0;

       for( int i=0; i<images.length; i++ )
           if(images[i]!=null) {
              images[i].flush();
              images[i] = null;
              unloaded++;
           }

       if(unloadSubDirsAlso)
          for( int i=0; i<childDirs.length; i++ )
               unloaded += childDirs[i].unloadAllImages(true);

       return unloaded;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 }

}
