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

import java.io.*;
import java.net.*;

//import wotlas.libs.log.*;

import java.util.Properties;

/** Various tools for project development.
 *
 * @author Aldiss
 */

public class ProjectTools {

 /*------------------------------------------------------------------------------------*/

   /** Number of lines of code.
    */
    private int nbLines;
   
   /** number of JavaFiles
    */
    private int nbFiles;

   /** Parse for empty lines & comments ?
    */
    private boolean dontParse=false;

 /*------------------------------------------------------------------------------------*/

   /** Parse for empty lines & comments ?
    */
    public void setDontParse( boolean dontParse ) {
    	this.dontParse = dontParse;
    }

 /*------------------------------------------------------------------------------------*/

   /** This method is for development only. It displays the number of line of code
    *  of a whole project and the number of Java files.
    *  
    *  We skip comments that are between / * * / and lines that contain only spaces
    *  or tabs.
    *
    *  @param rootPath the root of the project where we'll seek java files
    */
    public String getJavaFilesInfo( String rootPath ) {
    	
    	File rootDir = new File(rootPath);
    	nbLines=0;
    	nbFiles=0;
    	
    	getSomeJavaFilesInfo(rootDir);
    	return "Project contains "+nbFiles+" Java files, representing "+nbLines+" lines of code.";
    }

 /*------------------------------------------------------------------------------------*/

  /** To get info from the files of the specified directory. This method is recursive.
   */
    private void getSomeJavaFilesInfo( File pathDir ) {
    	
    	File list[] = pathDir.listFiles();

        if(list==null) return;

        for( int i=0; i<list.length; i++ ) {
             if(list[i].isDirectory()) {
                Debug.signal(Debug.NOTICE,null,"* Exploring "+list[i].getName()+" directory...");             	
                getSomeJavaFilesInfo(list[i]);
                continue;
             }

             if(!list[i].getName().endsWith(".java"))
                continue;

           // Ok we have a Java file to parse
             String javaFile = FileTools.loadTextFromFile(list[i].getPath());
             
             if(javaFile==null)
                Debug.signal(Debug.ERROR,null,"   -> Failed to open Java file : "+list[i].getPath());
             else
                Debug.signal(Debug.NOTICE,null,"   -> parsing "+list[i].getName()+"...");             	

             nbFiles++;
             int beg=0, end=0;
             boolean isComment = false;

             while( (end=javaFile.indexOf("\n",beg))>=0 ) {
             	
             	 String line = javaFile.substring(beg, end).trim();
             	 
             	 if(dontParse) {
             	    nbLines++;
                    beg=end+1;
             	    continue;
                 }

             	 if(line.length()==0) {
                    beg=end+1;
             	    continue;
                 }

                 // tab spaces only ?
                 char cLine[] = line.toCharArray();
                 boolean hasLetters = false;

                 for( int c=0; c<cLine.length; c++ )
                      if(cLine[c]!=' ' && cLine[c]!='\t') {
                        hasLetters=true;
                        break;
                      }

                 if(!hasLetters) {
                    beg=end+1;
                    continue;
                 }

                 // comment ?
                 if( !isComment ) {
                     if( line.startsWith("/*") ) {
                         if( line.indexOf("*/")>=0 ) {
                             isComment=false;
                             beg=end+1;
                             continue;
                         }

                         isComment=true;
                     }
                     else
                       if( line.startsWith("//") ) {
                           beg=end+1;
                           continue;
                       }
                 }
                 else {
                     if( line.indexOf("*/")>=0 ) {
                         isComment=false;
                         beg=end+1;
                         continue;
                      }
                 }

                 if(!isComment)
                    nbLines++;

                 beg=end+1;
             }

           // error detection
             if(isComment)
                Debug.signal(Debug.ERROR,null,"Bad comment detection in "+list[i].getPath());
        }

    }

 /*------------------------------------------------------------------------------------*/

   /** Main for our wotlas project
    */
   public static void main(String argv[]) {
       ProjectTools project = new ProjectTools();
       project.setDontParse(true);
       /*
       try{
            Debug.setPrintStream( new ServerLogStream( "project-info.log" ) );
       }catch( java.io.FileNotFoundException e ) {
          e.printStackTrace();
          return;
       }
       */

       Debug.signal( Debug.NOTICE, null, "\n\n--Results--\n\n  "+project.getJavaFilesInfo("../src/wotlas") );
       Debug.exit();
   }

 /*------------------------------------------------------------------------------------*/

}