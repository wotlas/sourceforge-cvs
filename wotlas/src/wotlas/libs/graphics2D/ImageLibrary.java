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

import wotlas.utils.Debug;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

/** An ImageLibrary manages shared images ( i.e. used by more than
 *  one entity ). It also proposes static methods for image loading.<p>
 *
 *  It relies on a image Database that must have a special structure ( see
 *  the createImagelibrary() method for more details ).<p>
 *
 *  Images in the ImageLibrary are INT_ARGB buffered images.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.ImageIdentifier
 */

public class ImageLibrary {

 /*------------------------------------------------------------------------------------*/
 
   /** Our Default ImageLibrary.
    */
     private static ImageLibrary imageLibrary;

   /** An image counter.
    */
     private static int nbImagesLoaded;

  /** Do we have to display db entries with bad format ?
   */
     private static boolean displayBadFormatEntries;

 /*------------------------------------------------------------------------------------*/

  /** Image classified by Category/Set/Action/Index
   */
     private BufferedImage images[][][][];

  /** Our Image Database path.
   */
     private String imageDataBasePath;

 /*------------------------------------------------------------------------------------*/

   /** Constructor with the image database path. We load all the images in memory.
    *
    * @param imageDataBasePath the path to the image database.
    * @exception IOException if an error occurs while loading the images.
    */
     private ImageLibrary( String imageDataBasePath ) throws IOException {
     	this.imageDataBasePath = imageDataBasePath;
     	displayBadFormatEntries = false;

        loadImageDataBase();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Do we have to display db entries with bad format ?
   * 
   * @param display if true we display disk entries of the ImageLibrary that
   *        have a bad format.
   */
    public void displayBadFormatEntries( boolean display ) {
    	displayBadFormatEntries = display;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Creates an ImageLibrary or returned the previously created one.
   *  We load all the images from the specified image database.<p>
   *
   *  The image database must have the following structure :<p>
   *
   *  - "category" directories. Each "category" directory name must end with "-XX"
   *    where XX is the "category" ID number.<p>
   *
   *  - "category" directories contain "set" directories. Each "set" directory name
   *    must end with "-XX" where XX is the "set" ID number.<p>
   *
   *  - "set" directories contain "action" directories. Each "action" directory name
   *    must end with "-XX" where XX is the "action" ID number.<p>
   *
   *  - "action" directories contain the images. Each image name must end with
   *    with "-XX.YYY" where XX is the "index" of the image and YYY is either "gif"
   *    or "jpg".<p><br>
   *
   *  If a "set" directory name has the format "-XX-jit" (jit for "just in time")
   *  it means that its sub "action" directory will only be loaded when they are needed
   *  ( they are not loaded into memory when the ImageLibrary is being created ).<br>
   *
   *  <p>If a "set" directory name has the format "-XX-exc" (exc for exclusive or exclude)
   *  it means that we don't load any of its "action" directories automatically, you have to
   *  load them manually. ALSO only ONE of these sub "action" directories can be loaded into
   *  memory at the same time.  When you load one of them the others are automatically flushed.
   *  This option is especially useful when handling huge images that don't need to be
   *  in memory at the same time.<br>
   *
   *  <p>To manually load "action" directories/images which "set" directories have the format
   *  "-XX-jit" or "-XX-exc" you can use the loadImageSet(), loadImageAction() or loadImageIndex()
   *  methods.<br>
   *
   *  <p>You can manually unload images from "-jit" or "-exc" "set" directories by calling
   *  the unloadImageSet(), unloadImageAction() or unloadImageIndex() method.
   *
   *  IMPORTANT: for directory names that end with "-XX" or "-XX-<option>" the XX index must start
   *  at zero and increment with no jumps between numbers.
   *
   * @param imageDataBasePath the path to the image database.
   * @return the created (or previously created) image library.
   * @exception IOException if an error occurs while loading the images.
   */
   public static ImageLibrary createImageLibrary( String imageDataBasePath )
   throws IOException {
         if( imageLibrary == null )
             imageLibrary = new ImageLibrary( imageDataBasePath );
         
         return imageLibrary;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default image library.
   *
   * @return the default image library.
   */
   public static ImageLibrary getDefaultImageLibrary() {
         return imageLibrary;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load the image database. We don't load "set directories" that ends with the "-jit"
   *  or "-exc" options.
   */
   private void loadImageDataBase() throws IOException{

    // 1 - Check DataBase
       File homeDir = new File(imageDataBasePath);
   
       if( !homeDir.isDirectory() || !homeDir.exists() )
           throw new IOException("Image DataBase Not found : "+imageDataBasePath
                                 +" is not a valid directory.");
   
    // 2 - Category creation
       File cats[] = homeDir.listFiles();

       if(cats==null) {
          Debug.signal( Debug.WARNING, this, "No Categories found in Image Database !");
          return;
       }

       images = new BufferedImage[cats.length][][][];

       for( int c=0; c<cats.length; c++ )
       {
            if( !cats[c].isDirectory() || cats[c].getName().lastIndexOf('-') <0 )
               continue;

         // 2.1 - We get the category ID
            int catID = getIDFromName( cats[c].getName() );

            if(catID >= cats.length )
               throw new IOException( "Category ID in "+cats[c].getName()+" has bad range." );

         // 2.2 - Set creation
            File sets[] = cats[c].listFiles();

            if(sets==null) {
               Debug.signal( Debug.WARNING, this, "No Sets found in category "+cats[c].getName());
               continue;
            }

            images[catID] = new BufferedImage[sets.length][][];

            for( int s=0; s<sets.length; s++ )
            {
               if( !sets[s].isDirectory() || sets[s].getName().lastIndexOf('-')<0 )
                     continue;

             // 2.2.1 - we get the set ID...
               int setID = getIDFromName( sets[s].getName() );
            
               if(setID >= sets.length )
                  throw new IOException( "Set ID in "+sets[s].getName()+" has bad range." );

             // 2.2.2 - Action creation
                File actions[] = sets[s].listFiles();

                if(actions==null) {
                   Debug.signal( Debug.WARNING, this, "No Actions found in set "+sets[s].getName());
                   continue;
                }

                images[catID][setID] = new BufferedImage[actions.length][];

                for( int a=0; a<actions.length; a++ )
                {
                    if( !actions[a].isDirectory() || actions[a].getName().lastIndexOf('-')<0 )
                         continue;

                 // 2.2.2.1 - We get the ActionID
                    int actID = getIDFromName( actions[a].getName() );
            
                    if( actID >= actions.length )
                        throw new IOException( "Action ID in "+actions[a].getName()+" has bad range." );
            
                 // 2.2.2.2 - Index creation
                    File index[] = actions[a].listFiles();

                    if(index==null) {
                       Debug.signal( Debug.WARNING, this, "No images found in action "+actions[a].getName());
                       continue;
                    }

                 // 2.2.2.3 - Image loading
                    if( !sets[s].getName().endsWith("-jit") && !sets[s].getName().endsWith("-exc") ) {
                        images[catID][setID][actID] = loadBufferedImages( actions[a].getPath() );

                        if( images[catID][setID][actID] == null)
                          Debug.signal( Debug.WARNING, this, "No images loaded from "+actions[a].getPath());
                    }
                }
            }
       }

    // 3 - We print some stats
       Debug.signal( Debug.NOTICE, this, "ImageLibrary started with "+nbImagesLoaded+" images in memory.");
       nbImagesLoaded=0;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given a directory name of our database ( format <name>-<number>-<option> )
   *  we return the <number> part.
   *
   * @param name directory name
   * @return ID
   * @exception IOException if the given name has a bad format.
   */
   static private int getIDFromName( String name ) throws IOException{

       String s_val = null;

    // 1 - we retrieve the substring
       if(name.endsWith("-jit")||name.endsWith("-exc")) {
           int last = name.lastIndexOf('-');
           if(last==0) throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");

           int first = name.lastIndexOf('-', last-1);
           if(first<0) throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");

           s_val = name.substring( first+1, last );
       }
       else {
           int first = name.lastIndexOf('-');
           if( first<0 || first==name.length()-1 )
              throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");

           s_val = name.substring( first+1, name.length() );
       }

    // 2 - We parse the substring
       int ID = -1;
            
       try{
           ID = Integer.parseInt( s_val );
       }catch(NumberFormatException bne) {
           throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");
       }

       if(ID<0)
          throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");

      return ID;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Given a directory of our database and a number ( format <name>-<number>-<option> )
   *  we return the immediate sub-directory name that has the specified number.
   *
   * @param path directory path
   * @param idToFind <number> part of the format to find.
   * @return file name that has the given ID, null if not found
   * @exception IOException if the given path has file names with bad format.
   */
   static private String getNameFromID( String path, int idToFind ) throws IOException {

      File list[] = new File(path).listFiles();
      if(list==null) return null;

      for( int i=0; i<list.length; i++ ) {
         if( !list[i].isDirectory() )
             continue;

         try{
             if( getIDFromName( list[i].getName() ) == idToFind )
                 return list[i].getName();
         }
         catch( IOException ioe ) {
             if(displayBadFormatEntries)
                Debug.signal( Debug.ERROR, null, ""+ioe);
         }
      }

      return null; // not found
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Given an ImageIdentifier we return its associated "set" directory.
    *
    * @param imageDataBasePath path to the image database
    * @param imID image identifier to use to find the associated set directory.
    * @return the "set" directory associated to this ImageIdentifier.
    * @exception IOException if the given image database has path names with bad format.
    */
    static private String getSetDirectory( String imageDataBasePath, ImageIdentifier imID )
    throws IOException {

     // 1 - category
        String cat = getNameFromID( imageDataBasePath, imID.imageCategory );
 
        if( cat==null )
            throw new IOException( "Failed to load Image Set, bad Category :"+imID );

     // 2 - set
        String set = getNameFromID( imageDataBasePath+File.separator+cat,
                                   imID.imageSet ); 
        if( set==null )
            throw new IOException( "Failed to load Image Set, bad Set :"+imID );

        return imageDataBasePath+File.separator+cat+File.separator+set;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an entire "-jit"'s "set" directory from the image database.
   *
   * @param imID image identifier pointing to an imageSet.
   * @exception IOException if something goes wrong
   */
   public void loadImageSet( ImageIdentifier imID ) throws IOException{

    // 1 - We retrieve the "set" directory
       String set = getSetDirectory( imageDataBasePath, imID );

       if( !set.endsWith("-jit") )
           throw new IOException( "ImageSet has no '-jit' option ! check your ImageIdentifier :"+imID );

    // 2 - actions directory
       File actions[] = new File(set).listFiles();
       if(actions==null) throw new IOException( "Failed to load Image actions, no actions :"+imID );

       for( int a=0; a<actions.length; a++ ) {
           if( !actions[a].isDirectory() || actions[a].getName().lastIndexOf('-')<0 )
               continue;

        // We get the ActionID
           int actID = getIDFromName( actions[a].getName() );

           if( actID >= actions.length )
                throw new IOException( "Action ID in "+actions[a].getName()+" has bad range." );

        // And load the images
           images[imID.imageCategory][imID.imageSet][actID] = loadBufferedImages( actions[a].getPath() );

           if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null )
               Debug.signal( Debug.WARNING, this, "No images loaded from "+actions[a].getPath());
       }

    // 3 - We print some stats...
       Debug.signal( Debug.NOTICE, this, "Added "+nbImagesLoaded+" Images to database");
       nbImagesLoaded=0;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an "action" directory which "set" directory ends with the "-jit" or "-exc".
   *  option. If the option is "-exc" we first unload any loaded "action" directory from
   *  this "set" directory. We return immediately if the wanted action directory has already
   *  been loaded.
   *
   * @param imID image identifier pointing to an imageAction.
   * @exception IOException if something goes wrong
   */
   public void loadImageAction( ImageIdentifier imID ) throws IOException{

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction] != null )
           return;

    // 1 - We retrieve the "set" directory
       String set = getSetDirectory( imageDataBasePath, imID );

    // 2 - action directory
       String act = getNameFromID( set, imID.imageAction ); 
       if( act==null ) throw new IOException( "Failed to load Image Action, bad action :"+imID );

       if( set.endsWith("-exc") )
           unloadImageSet( imID );  // we unload actions that have previously been loaded
       else if( !set.endsWith("-jit") )
           throw new IOException( "ImageSet has neither '-jit' or '-exc' option ! check your ImageIdentifier :"+imID );

    // 3 - image loading
       String imagesHome = set+File.separator+act;
       images[imID.imageCategory][imID.imageSet][imID.imageAction] = loadBufferedImages( imagesHome );

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null)
           throw new IOException("No images loaded from "+imagesHome );

    // 5 - Print some stats
       Debug.signal( Debug.NOTICE, this, "Added "+nbImagesLoaded+ " images to database.");
       nbImagesLoaded=0;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image from an "action" directory. The "set" directory must end with the
   *  "-exc". If there are other "action" directories already loaded, we first unload them.
   *
   *  If the given image has already been loaded, we return immediately.
   *
   * @param imID image identifier pointing to an imageSet.
   * @exception IOException if something goes wrong
   */
   public void loadImageIndex( ImageIdentifier imID ) throws IOException{

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction] != null &&
           images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex] != null )
           return;

    // 1 - We retrieve the "set" directory
       String set = getSetDirectory( imageDataBasePath, imID );

    // 2 - action directory
       String act = getNameFromID( set, imID.imageAction ); 
       if( act==null ) throw new IOException( "Failed to load Image Action, bad action :"+imID );

       if( set.endsWith("-exc") ) {
         // we unload actions that have previously been loaded.
         // if OUR imageAction had already some of its images loaded, we avoid them to
         // be unloaded when we call the unloadSet() method.
            BufferedImage tmp[] = images[imID.imageCategory][imID.imageSet][imID.imageAction];  // save
            images[imID.imageCategory][imID.imageSet][imID.imageAction] = null;

            unloadImageSet( imID );

            images[imID.imageCategory][imID.imageSet][imID.imageAction] = tmp;  // restore
       }
       else
            throw new IOException( "ImageSet has no '-exc' option ! check your ImageIdentifier :"+imID );

    // 4 - image loading
       File index[] = new File(set+File.separator+act).listFiles();
       if(index==null) throw new IOException( "No images found in action "+set+File.separator+act);

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null )
           images[imID.imageCategory][imID.imageSet][imID.imageAction] = new BufferedImage[index.length];

       images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex]
                                  = findImageIn( index, imID.imageIndex, BufferedImage.TYPE_INT_ARGB );
       nbImagesLoaded=0;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image from the database. If the given imID is not found in the database
   *  we check if the imageSet has the "JIT" option. If it's the case we load the images
   *  and return the one wanted, if not we return null and an error is declared.
   *
   * @param imID completeimage identifier
   * @return image found in the library.
   */
   public BufferedImage getImage( ImageIdentifier imID ) {
      if( !checkImage(imID) )
          return null;

      return images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To check that the specified ImageIdentifier points out a valid Imagelibrary entry.
   *  If not we try to load the image from disk.
   *
   * @param imID completeimage identifier
   * @return true if the image is in the library ( or has just been loaded successfully )
   */
   public boolean checkImage( ImageIdentifier imID ) {

      if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null ) {
        // ok, this means we have to load this imageAction data now...
           try{
               loadImageAction( imID );
           }catch( IOException e ) {
               if(displayBadFormatEntries)
                  Debug.signal( Debug.ERROR, this, "An error occured while loading "+imID+" (action) :"+e);
               return false;
           }
      }

      if( images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex] == null ) {
        // we need to load the image now...
           try{
               loadImageIndex( imID );
           }catch( IOException e ) {
               if(displayBadFormatEntries)
                  Debug.signal( Debug.ERROR, this, "An error occured while loading "+imID+" (index) :"+e);
               return false;
           }
      }

      return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To unload an imageSet from memory.
   *  @param imID image identifier pointing the imageSet to unload.
   */
   public void unloadImageSet( ImageIdentifier imID ) {

       for( int a=0; a<images[imID.imageCategory][imID.imageSet].length; a++ )
            if(images[imID.imageCategory][imID.imageSet][a]!=null) {
               for( int i=0; i<images[imID.imageCategory][imID.imageSet][a].length; i++ )
                    if(images[imID.imageCategory][imID.imageSet][a][i]!=null)
                       images[imID.imageCategory][imID.imageSet][a][i].flush();

               images[imID.imageCategory][imID.imageSet][a] = null;
            }
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To unload an imageAction from memory.
   *  @param imID image identifier pointing the imageAction to unload.
   */
   public void unloadImageAction( ImageIdentifier imID ) {

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction]==null )
           return;

       for( int i=0; i<images[imID.imageCategory][imID.imageSet][imID.imageAction].length; i++ )
            if( images[imID.imageCategory][imID.imageSet][imID.imageAction][i]!=null )
                images[imID.imageCategory][imID.imageSet][imID.imageAction][i].flush();

       images[imID.imageCategory][imID.imageSet][imID.imageAction] = null;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To unload an imageIndex from memory.
   *  @param imID image identifier pointing the imageIndex to unload.
   */
   public void unloadImageIndex( ImageIdentifier imID ) {

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null ) return;
       if( images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex] == null ) return;

       images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex].flush();
       images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex] = null;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image's width. IMPORTANT: We suppose the image is in the database.
   *
   * @param imID image identifier
   * @return width
   */
   public int getWidth( ImageIdentifier imID ) {
      if( !checkImage(imID) )
          return -1;

      return images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex].getWidth(null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image's width. IMPORTANT: We suppose the image is in the database.
   *
   * @param imID image identifier
   * @return width
   */ 
   public int getHeight( ImageIdentifier imID ) {
      if( !checkImage(imID) )
          return -1;

      return images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex].getHeight(null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** For Animations. We return the number of images of the specified ImageIdentifier's
   *  imageAction.
   *
   * @param imID image identifier
   * @return length of the imID.imageAction array.
   */
   public int getIndexLength( ImageIdentifier imID ) {
      if( !checkImage(imID) )
          return -1;

      return images[imID.imageCategory][ imID.imageSet][imID.imageAction].length;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image.
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

       nbImagesLoaded++;
       return im;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load all the images of a directory. The image name must follow the format :
   *  <name>-<number>.<ext> where ext is either "jpg" or "gif".
   *
   *  Important : the returned array can have null fields if non-image files were found
   *              in the specified directory.
   *
   * @param the path to the images
   * @return the loaded images...
   */
    static public Image[] loadImages( String path )
    {
      File list[] = new File(path).listFiles();
      
      if(list==null)
         return null;

      Image im[] = new Image[list.length];
      MediaTracker tracker = new MediaTracker(new Label());
      Toolkit tk = Toolkit.getDefaultToolkit();

         for( int i=0; i<list.length; i++)
         {
            String name = list[i].getName().toLowerCase();

            if( list[i].isDirectory() || ( !name.endsWith(".jpg") && !name.endsWith(".gif") ) )
                continue;

            int id=0;

            try{
               id = getIDFromName( name.substring( 0, name.lastIndexOf('.')) );
            }catch( IOException e ) {
               if(displayBadFormatEntries)
                  Debug.signal( Debug.WARNING, null, "Bad Image Name Format ! :"+e);
               continue;
            }

            im[id] = tk.getImage( path+File.separator+list[i].getName() );
            tracker.addImage(im[id],i);
            nbImagesLoaded++;
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
    static public BufferedImage loadBufferedImage( String path, int imageType )
    {
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
   *  <name>-<number>.<ext> where ext is either "jpg" or "gif".
   *
   *  Important : the returned array can have null fields if non-image files were found
   *              in the specified directory.
   *
   * @param the path to the images
   * @return the loaded images transformed into buffered images...
   */
    static public BufferedImage[] loadBufferedImages( String path )
    {
     // We load all the images.
        Image im[] = loadImages( path );
        
        if(im==null)
           return null;

     // We count how many images we have...
        int imageCount=0;
        
        for( int c=0; c<im.length; c++ )
           if( im[c]!=null ) imageCount++;

        if(imageCount==0)
           return null;

     // We transform these images into buffered images
        BufferedImage bufIm[] = new BufferedImage[imageCount];
        int count=0;
      
        for( int c=0; c<im.length; c++ )
           if( im[c]!=null ) {
       	       bufIm[count] = new BufferedImage( im[c].getWidth(null),
    	                      im[c].getHeight(null), BufferedImage.TYPE_INT_ARGB );

               Graphics2D offBf = bufIm[count].createGraphics();
               offBf.drawImage( im[c],0,0,null);
               count++;
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
   * @exception IOException if files have a bad format, or if the file is not found.
   */
    static public BufferedImage findImageIn( File imageFiles[], int imageIndex, int imageType )
    throws IOException {

       for( int l=0; l<imageFiles.length; l++ ) {
             if( imageFiles[l].isDirectory() || imageFiles[l].getName().lastIndexOf('-')<0 )
                 continue;

             String imageName = imageFiles[l].getName().toLowerCase();

             if( imageName.endsWith(".jpg") || imageName.endsWith(".gif") )
                  imageName = imageName.substring( 0, imageName.lastIndexOf('.') );
             else
                  continue;

          // Is it the image we wanted ?
             if(  getIDFromName( imageName ) == imageIndex )
                  return loadBufferedImage( imageFiles[l].getPath(), imageType );
       }

       throw new IOException("No image found for "+imageIndex );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load an ARGB image from its ImageIdentifier. The image is searched as follows :
    *
    *  - if there is an existing image library we try a getImage() call. It will eventually
    *    load the image(s) if it was not already in memory. If the given ImageIndentifier is
    *    not a valid entry in the ImageLibrary an error message will be displayed on screen.
    *
    *  - if there is no existing image library ( or if the previous operation failed & returned
    *    null ) we seek for the specified imageDatabasePath.
    *
    *  If these two tries fail we return null. If an ImageLibrary exists but the specified
    *  ImageIndex is not a valid entry in this ImageIndex
    *
    * IMPORTANT : note again that if an ImageLibrary exists, there is a high probability that
    *             this method call we'll also launch the loading of the other images found in
    *             the same 'imageAction' Directory as the specified Image.
    *             If it's not what you want use the other loadBufferedImage method below.
    *
    * @param imID complete image identifier
    * @param imageDatabasePath path to search for an Image database if no Image Library is found.
    * @return if found, a BufferedImage of the image, null otherwise.
    * @exception IOException only if the image library has a bad format.
    */
    static public BufferedImage loadBufferedImage( ImageIdentifier imID, String imageDataBasePath )
    throws IOException {
       // Step 1 - Any Image Library ?
          if( imageLibrary!=null ) {
              BufferedImage bufIm = imageLibrary.getImage(imID);

              if( bufIm!=null )
                  return bufIm;
          }

       // Step 2 - We seek in the specified database
          String set = getSetDirectory( imageDataBasePath, imID );
          String action = getNameFromID( set, imID.imageAction );

          if( action==null ) return null;

          File listIm[] = new File( set+File.separator+action ).listFiles();
          if( listIm==null ) return null;

          return findImageIn( listIm, imID.imageIndex, BufferedImage.TYPE_INT_ARGB );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To load an image of a specified image type from its ImageIdentifier.
    *  The image is searched using the given imageDatabase path. Because images in the
    *  ImageLibrary are INT_ARGB buffered images, this explains why other types of images
    *  can't be loaded into it. Therefore you should place these images in a separate
    *  "Action" directory which "set" directory has the "-exc" option.
    *
    * @param imID complete image identifier
    * @param imageDatabasePath path to search for an Image database.
    * @param imageType image type : BufferedImage.INT_ARGB for instance.
    * @return if found, a BufferedImage of the image, null otherwise.
    * @exception IOException only if the image library has a bad format.
    */
    static public BufferedImage loadBufferedImage( ImageIdentifier imID, String imageDataBasePath,
                                                   int imageType ) throws IOException {
       String set = getSetDirectory( imageDataBasePath, imID );
       String action = getNameFromID( set, imID.imageAction );

       if( action==null ) return null;

       File listIm[] = new File( set+File.separator+action ).listFiles();
       if( listIm==null ) return null;

       return findImageIn( listIm, imID.imageIndex, imageType );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the ImageIdentifier of an image given a key-word. We don't load any images,
    *  we just check the image file names that are in the given 'imageAction' directory.
    *
    *  This is especially useful when you are searching for an image's mask. If the image
    *  mask is in the same 'imageAction' directory and has the word 'mask' somewhere in its
    *  file name, getImageIdentifier( myImageId, myDataBasePath, "mask" ) will return its
    *  entire identifier.
    *
    * @param imActionID complete image action identifier where to search.
    * @param imageDatabasePath path to search for an Image databse.
    * @param keyword word to search.
    * @return if found, the complete ImageIdentifier of the first image matching the keyword,
    *         null otherwise.
    * @exception IOException only if the image library has a bad format.
    */
    static public ImageIdentifier getImageIdentifier( ImageIdentifier imActionID,
                                                      String imageDataBasePath,
                                                      String keyword ) throws IOException {
       String set = getSetDirectory( imageDataBasePath, imActionID );
       String action = getNameFromID( set, imActionID.imageAction );

       if( action==null ) return null;

       File imageFiles[] = new File( set+File.separator+action ).listFiles();
       if( imageFiles==null ) return null;

       for( int l=0; l<imageFiles.length; l++ ) {
             if( imageFiles[l].isDirectory() || imageFiles[l].getName().lastIndexOf('-')<0 )
                 continue;

             String imageName = imageFiles[l].getName().toLowerCase();

             if( imageName.endsWith(".jpg") || imageName.endsWith(".gif") )
                  imageName = imageName.substring( 0, imageName.lastIndexOf('.') );
             else
                  continue;

          // Is it the image we wanted ?
             if( imageName.indexOf( keyword ) >=0 ) {
                ImageIdentifier result = new ImageIdentifier( imActionID );
                result.imageIndex = (short) getIDFromName( imageName );
                return result;
             }
       }

       return null; // not found
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
