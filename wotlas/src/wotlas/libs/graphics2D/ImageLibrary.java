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

/** An ImageLibrary posseses all the images that are shared ( i.e. used by more than
 *  one entity ). It also proposes static methods for image loading.
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

        loadImageDataBase();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Creates an ImageLibrary or returned the previously created one.
   *  We load all the images from the specified database path.<p>
   *
   *  The image database must have following structure :<p>
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
   *    or "jpg".<p>
   *
   *  If a "set" directory name has the format "-XX-jit" (jit for "just in time")
   *  it means that its sub "action" directory will only be loaded when they are needed
   *  ( they are not loaded into memory when the ImageLibrary is being created ).
   *
   *  If a "set" directory name has the format "-XX-exc" (exc for exclusive) it means
   *  that only one of its sub "action" directory can be loaded into memory at the same
   *  time. At first we don't load any of those "action" directory. When one of them
   *  is required we load it and the previously loaded one (if any) is flushed. This
   *  option is especially useful when handling large images that don't need to be
   *  in memory at the same time.
   *
   *  You can force the ImageLibrary to load "action" directories from "set" directories
   *  that have the format "-XX-jit" or "-XX-exc" by calling the loadImageSet() or
   *  loadImageAction() methods.
   *
   *  For directory names that end with "-XX" or "-XX-<option>" the XX must start at zero
   *  and increment with no jumps in numbers.
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
                                 +"is not a valid directory.");
   
    // 2 - Category creation
       File cats[] = homeDir.listFiles();

       if(cats==null) {
          Debug.signal( Debug.ERROR, this, "No Categories found in Image Database !");
          return;
       }

       images = new BufferedImage[cats.length][][][];

       for( int c=0; c<cats.length; c++ )
       {
            if( !cats[c].isDirectory() || cats[c].getName().lastIndexOf('-') <0 )
               continue;

         // 2.1 - We get the category ID
            int catID = getIDFromName( cats[c].getName() );

            if(catID<0 || catID >= cats.length )
               throw new IOException( "Category ID in "+cats[c].getName()+" has bad range." );

         // 2.2 - Set creation
            File sets[] = cats[c].listFiles();

            if(sets==null) {
               Debug.signal( Debug.ERROR, this, "No Sets found in category "+cats[c].getName());
               continue;
            }

            images[catID] = new BufferedImage[sets.length][][];

            for( int s=0; s<sets.length; s++ )
            {
               if( !sets[s].isDirectory() || sets[s].getName().lastIndexOf('-')<0 )
                     continue;

             // 2.2.1 - we get the set ID...
               int setID = getIDFromName( sets[s].getName() );
            
               if(setID<0 || setID >= sets.length )
                  throw new IOException( "Set ID in "+sets[s].getName()+" has bad range." );

             // 2.2.2 - Action creation
                File actions[] = sets[s].listFiles();

                if(actions==null) {
                   Debug.signal( Debug.ERROR, this, "No Actions found in set "+sets[s].getName());
                   continue;
                }

                images[catID][setID] = new BufferedImage[actions.length][];

                for( int a=0; a<actions.length; a++ )
                {
                    if( !actions[a].isDirectory() || actions[a].getName().lastIndexOf('-')<0 )
                         continue;

                 // 2.2.2.1 - We get the ActionID
                    int actID = getIDFromName( actions[a].getName() );
            
                    if( actID<0 || actID >= actions.length )
                        throw new IOException( "Action ID in "+actions[a].getName()+" has bad range." );
            
                 // 2.2.2.2 - Index creation
                    File index[] = actions[a].listFiles();

                    if(index==null) {
                       Debug.signal( Debug.ERROR, this, "No images found in action "+actions[a].getName());
                       continue;
                    }

                 // 2.2.2.3 - Image loading
                    if( !sets[s].getName().endsWith("-jit") && !sets[s].getName().endsWith("-exc") ) {
                        String imagesHome = imageDataBasePath+File.separator+cats[c].getName()
                                   +File.separator+sets[s].getName()+File.separator+actions[a].getName();

                        images[catID][setID][actID] = loadBufferedImages( imagesHome );
                        
                        if( images[catID][setID][actID] == null)
                          Debug.signal( Debug.ERROR, this, "No images loaded from "+imagesHome);
                    }
                }
            }
       }

      Debug.signal( Debug.NOTICE, this, "Loaded "+nbImagesLoaded+" Images in database");
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

           if(last==0)
              throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");

           int first = name.lastIndexOf('-', last-1);

           if(first<0)
              throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");

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
       }catch(NumberFormatException bne){
           throw new IOException("Invalid format :"+name+". Should be <name>-<number>-<option>]");
       }

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
   static private String getNameFromID( String path, int idToFind ) throws IOException{

      File list[] = new File(path).listFiles();
      
      if(list==null)
         return null;

      for( int i=0; i<list.length; i++ ) {
         if( !list[i].isDirectory() )
             continue;

         if( getIDFromName( list[i].getName() ) == idToFind )
             return list[i].getName();
      }

      return null; // not found
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load a "-jit"'s "set" directory from the image database.
   *
   * @param imID image identifier pointing to an imageSet.
   * @exception IOException if something goes wrong
   */
   public void loadImageSet( ImageIdentifier imID ) throws IOException{

    // 1 - category
       String cat = getNameFromID( imageDataBasePath, imID.imageCategory );
 
       if( cat==null )
           throw new IOException( "Failed to load Image Set, bad Category :"+imID );

    // 2 - set
       String set = getNameFromID( imageDataBasePath+File.separator+cat,
                                   imID.imageSet ); 
       if( set==null )
           throw new IOException( "Failed to load Image Set, bad Set :"+imID );

       if( !set.endsWith("-jit") )
           throw new IOException( "ImageSet has no '-jit' option ! check your ImageIdentifier :"+imID );

    // 3 - actions
       File actions[] = new File( imageDataBasePath+File.separator+cat+File.separator+set).listFiles();

       if(actions==null)
           throw new IOException( "Failed to load Image actions, no actions :"+imID );

       for( int a=0; a<actions.length; a++ )
       {
          if( !actions[a].isDirectory() || actions[a].getName().lastIndexOf('-')<0 )
              continue;

        // We get the ActionID
           int actID = getIDFromName( actions[a].getName() );
    
           if( actID<0 || actID >= actions.length )
                throw new IOException( "Action ID in "+actions[a].getName()+" has bad range." );

        // And load the images
           String imagesHome = imageDataBasePath+File.separator+cat
                                +File.separator+set+File.separator+actions[a].getName();

           images[imID.imageCategory][imID.imageSet][actID] = loadBufferedImages(imagesHome );

           if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null)
               Debug.signal( Debug.ERROR, this, "No images loaded from "+imagesHome);
       }

   // 4 - We print some stats...
      Debug.signal( Debug.NOTICE, this, "Loaded "+nbImagesLoaded+ " Images in database");
      nbImagesLoaded=0;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an "action" directory which "set" directory ends with the "-jit" or "-exc".
   *  If the option is "-exc" we first unload any loaded "action" directory from this
   *  "set" directory.
   *
   * @param imID image identifier pointing to an imageSet.
   * @exception IOException if something goes wrong
   */
   public void loadImageAction( ImageIdentifier imID ) throws IOException{

    // 1 - category
       String cat = getNameFromID( imageDataBasePath, imID.imageCategory );
 
       if( cat==null )
           throw new IOException( "Failed to load Image Set, bad Category :"+imID );

    // 2 - set
       String set = getNameFromID( imageDataBasePath+File.separator+cat,
                                   imID.imageSet ); 
       if( set==null )
           throw new IOException( "Failed to load Image Set, bad Set :"+imID );

    // 3 - action
       String act = getNameFromID( imageDataBasePath+File.separator+cat+File.separator+set,
                                   imID.imageAction ); 
       if( act==null )
           throw new IOException( "Failed to load Image Action, bad action :"+imID );

       if( set.endsWith("-exc") ) {
         // we unload actions that have previously been loaded
            for( int a=0; a<images[imID.imageCategory][imID.imageSet].length; a++ )
                 if(images[imID.imageCategory][imID.imageSet][imID.imageAction]!=null) {
                     for( int i=0;
                          i<images[imID.imageCategory][imID.imageSet][imID.imageAction].length;
                          i++ )
                         if(images[imID.imageCategory][imID.imageSet][imID.imageAction][i]!=null)
                            images[imID.imageCategory][imID.imageSet][imID.imageAction][i].flush();

                     images[imID.imageCategory][imID.imageSet][imID.imageAction] = null;
                 }
       }
       else if(!set.endsWith("-jit") )
           throw new IOException( "ImageSet has neither '-jit' or '-exc' option ! check your ImageIdentifier :"+imID );

    // 4 - image loading
       String imagesHome = imageDataBasePath+File.separator+cat+File.separator+set+File.separator+act;

       images[imID.imageCategory][imID.imageSet][imID.imageAction] = loadBufferedImages( imagesHome );

       if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null)
           throw new IOException("No images loaded from "+imagesHome );

   // 5 - Print some stats
      Debug.signal( Debug.NOTICE, this, "Loaded "+nbImagesLoaded+ " Images in database");
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
      if( images[imID.imageCategory][imID.imageSet][imID.imageAction] == null ) {
        // ok, this means we have to load image data now...
           try{
               loadImageSet( imID );
           }catch( IOException e ) {
               Debug.signal( Debug.ERROR, this, "An error occured while loading "+imID+" set");                
               return null;
           }
      }

      return images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex];
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image's width. IMPORTANT: We suppose the image is in the database.
   *
   * @param imID image identifier
   * @return width
   */
  public int getWidth( ImageIdentifier imID ) {
      return images[imID.imageCategory][imID.imageSet][imID.imageAction][imID.imageIndex].getWidth(null);
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get an image's width. IMPORTANT: We suppose the image is in the database.
   *
   * @param imID image identifier
   * @return width
   */ 
   public int getHeight( ImageIdentifier imID ) {
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
       return images[imID.imageCategory][ imID.imageSet][imID.imageAction].length;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image.
   *
   * @param the path to the image
   * @return the loaded image...
   */
    static Image loadImage( String path ) {
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
    static Image[] loadImages( String path )
    {
      File list[] = new File(path).listFiles();
      
      if(list==null)
         return null;

      Image im[] = new Image[list.length];
      MediaTracker tracker = new MediaTracker(new Label());
      Toolkit tk = Toolkit.getDefaultToolkit();

         for( int i=0; i<list.length; i++)
         {
            if( list[i].isDirectory() || ( !list[i].getName().endsWith("jpg") && 
                !list[i].getName().endsWith("gif") ) )
                continue;

            im[i] = tk.getImage( path+File.separator+list[i].getName() );
            tracker.addImage(im[i],i);
            nbImagesLoaded++;
         }

         try{
             tracker.waitForAll();
         }catch(InterruptedException e) { e.printStackTrace(); }
          
       return im;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load an image and transform it into a buffered image.
   *
   * @param the path to the image
   * @return the loaded buffered image...
   */
    static BufferedImage loadBufferedImage( String path )
    {
     // We load the image.
        Image im = loadImage( path );
        
        if(im==null)
           return null;

     // We transform this image into a buffered image
        BufferedImage bufIm = new BufferedImage( im.getWidth(null),
    	                      im.getHeight(null), BufferedImage.TYPE_INT_ARGB );

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
    static BufferedImage[] loadBufferedImages( String path )
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

   /** To load an image from its ImageId. The image is searched as follows :
    *
    *  - if there is an existing image library we try a getImage() call. It will eventually
    *    load the image if it was not already in memory. If the given ImageIndentifier is
    *    not a valid entry in the ImageLibrary an error message will be displayed on screen.
    *
    *  - if there is no existing image library ( or if the previous operation failed & returned
    *    null ) we seek for the specified imageDatabasePath.
    *
    *  If these two tries fail we return null. If an ImageLibrary exists but the specified
    *  ImageIndex is not a valid entry in this ImageIndex
    *
    * @param imID complete image identifier
    * @param imageDatabasePath path to search for an Image databse if no Image Library is found.
    * @return if found, a BufferedImage of the image, null otherwise.
    * @exception IOException only if the image library has a bad format.
    */
    static public BufferedImage loadBufferedImage( ImageIdentifier imID, String imageDataBasePath )
    throws IOException
    {
       // Step 1 - Any Image Library ?
          if( imageLibrary!=null ) {
              BufferedImage bufIm = imageLibrary.getImage(imID);

              if( bufIm!=null )
                  return bufIm;
          }

       // Step 2 - We seek in the specified database
          String path = new String( imageDataBasePath );
          String cat = getNameFromID( path, imID.imageCategory );
          if( cat==null ) return null;

          path += File.separator+cat;
          String set = getNameFromID( path, imID.imageSet ); 
          if( set==null ) return null;

          path += File.separator+set;
          String action = getNameFromID( path, imID.imageAction );
          if( action==null ) return null;

          path += File.separator+action;
          File listIm[] = new File( path ).listFiles();
          if( listIm==null ) return null;

          for( int l=0; l<listIm.length; l++ ) {
             if( listIm[l].isDirectory() || listIm[l].getName().lastIndexOf('-')<0 )
                 continue;

              String imageName = null;

              if( listIm[l].getName().endsWith(".jpg") || listIm[l].getName().endsWith(".gif") )
                  imageName = listIm[l].getName().substring( 0, listIm[l].getName().lastIndexOf('.') );
              else
                  continue;

           // Is it the image we wanted ?
              if(  getIDFromName( imageName ) == imID.imageIndex )
                   return loadBufferedImage( path+File.separator+listIm[l].getName() );
          }

       return null; // not found
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
