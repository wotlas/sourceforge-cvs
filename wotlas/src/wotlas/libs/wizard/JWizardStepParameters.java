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

package wotlas.libs.wizard;

import java.io.*;

import wotlas.libs.persistence.*;   // these two imports are only needed for the two
import wotlas.utils.Debug;          // loadFromFile() saveToFile() methods

/** Represents all the parameters needed to initialize a JWizardStep.
 *  This class can be sent/retrieve from a stream ( see encode/decode methods)
 *  and can also be stored on disk using the persistence library.
 *
 * @author Aldiss
 */

public class JWizardStepParameters {

 /*------------------------------------------------------------------------------------*/

  /** JWizardStep Class associated to these parameters
   */
   private String stepClass;

  /** Properties for step's creation and operation
   *  First column is the property key, second column is the property value.
   */
   private String stepProperties[][];

  /** JWizardStep's title
   */
   private String stepTitle;

  /** Is it a dynamic (not stored in JWizardStepFactory's buffer)
   *  or static JWizardStep (stored in JWizardStepFactory's buffer) ?
   */
   private boolean isDynamic;

  /** Is the "previous" button enabled ?
   */
   private boolean isPrevButtonEnabled;

  /** Is the "next" button enabled ?
   */
   private boolean isNextButtonEnabled;

  /** Is this the last step of the wizard ? if true the "Next"
   *  button will be replaced by an "Ok" button.
   */
   private boolean isLastStep;

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor. Sets default values, no step class and an empty title.
   */
   public JWizardStepParameters() {
   	this(null," ",false,null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with step class. The step is declared static with no properties and
   *  an empty title. 
   *  "Next" & "Previous" buttons are enabled, this step is not set as the last one.
   *
   * @param stepClass JWizardStep's java class.
   */
   public JWizardStepParameters(String stepClass) {
   	this(stepClass," ",false,null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with step class and title. The step is declared static with no properties.
   *  "Next" & "Previous" buttons are enabled, this step is not set as the last one.
   *
   * @param stepClass JWizardStep's java class.
   * @param stepTitle Title of the JWizardStep
   */
   public JWizardStepParameters(String stepClass, String stepTitle) {
        this(stepClass,stepTitle,false,null);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with step class, title, dynamic/static state and properties.
   *  "Next" & "Previous" buttons are enabled, this step is not set as the last one.
   *
   * @param stepClass JWizardStep's java class.
   * @param stepTitle Title of the JWizardStep
   * @param isDynamic if true the Step won't be buffered by the factory (content may change)
   *        if false the step's data is supposed to never change and will be buffered.
   * @param stepProperties properties used for Step init/operations. Set to null if there are
   *        none. First column is the property key, second column is the property value.
   */
   public JWizardStepParameters(String stepClass, String stepTitle, boolean isDynamic,
                               String stepProperties[][]) {
      this.stepClass = stepClass;
      this.stepTitle = stepTitle;

      this.isDynamic = isDynamic;
      this.stepProperties = stepProperties;
      
      isPrevButtonEnabled = true;
      isNextButtonEnabled = true;
      isLastStep = false; 
   }

 /*------------------------------------------------------------------------------------*/

  /** JWizardStep Class associated to these parameters
   */
   public String getStepClass(){
   	return stepClass;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** JWizardStep Class associated to these parameters
   */
   public void setStepClass(String stepClass){
   	this.stepClass = stepClass;
   }

 /*------------------------------------------------------------------------------------*/

  /** Properties for step's creation and operation
   *  First column is the property key, second column is the property value.
   */
   public String[][] getStepProperties(){
   	return stepProperties;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Properties for step's creation and operation
   *  First column is the property key, second column is the property value.
   */
   public void setStepProperties(String[][] stepProperties){
   	this.stepProperties = stepProperties;
   }

 /*------------------------------------------------------------------------------------*/

  /** JWizardStep's title
   */
   public String getStepTitle(){
   	return stepTitle;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** JWizardStep's title
   */
   public void setStepTitle(String stepTitle){
        this.stepTitle = stepTitle;
   }

 /*------------------------------------------------------------------------------------*/

  /** Is it a dynamic (not stored in cache by the JWizardStepFactory)
   *  or static JWizardStep (stored in JWizardStepFactory) ?
   */
   public boolean getIsDynamic() {
   	return isDynamic;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Is it a dynamic (not stored in cache by the JWizardStepFactory)
   *  or static JWizardStep (stored in JWizardStepFactory) ?
   */
   public void setIsDynamic(boolean isDynamic) {
   	this.isDynamic = isDynamic;
   }

 /*------------------------------------------------------------------------------------*/

  /** Is the "previous" button enabled ?
   */
   public boolean getIsPrevButtonEnabled() {
   	return isPrevButtonEnabled;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Is the "previous" button enabled ?
   */
   public void setIsPrevButtonEnabled(boolean isPrevButtonEnabled) {
   	this.isPrevButtonEnabled = isPrevButtonEnabled;
   }

 /*------------------------------------------------------------------------------------*/

  /** Is the "next" button enabled ?
   */
   public boolean getIsNextButtonEnabled() {
        return isNextButtonEnabled;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Is the "next" button enabled ?
   */
   public void setIsNextButtonEnabled(boolean isNextButtonEnabled) {
   	this.isNextButtonEnabled = isNextButtonEnabled;
   }

 /*------------------------------------------------------------------------------------*/

  /** Is this the last step of the wizard ? if true the "Next"
   *  button will be replaced by an "Ok" button.
   */
   public boolean getIsLastStep(){
   	return isLastStep;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Is this the last step of the wizard ? if true the "Next"
   *  button will be replaced by an "Ok" button.
   */
   public void setIsLastStep(boolean isLastStep) {
   	this.isLastStep = isLastStep;
   }

 /*------------------------------------------------------------------------------------*/

  /** To put this JWizardStepParameters on a data stream. 
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
   public void encode( DataOutputStream ostream ) throws IOException {
      ostream.writeUTF(stepClass);
      ostream.writeUTF(stepTitle);

      ostream.writeBoolean(isDynamic);

      if(stepProperties==null)
         ostream.writeShort(0);
      else {
         ostream.writeShort((short)stepProperties.length);

         for( short i=0; i<stepProperties.length; i++ ) {
              ostream.writeUTF(stepProperties[i][0]);
              ostream.writeUTF(stepProperties[i][1]);
         }
      }
      
      ostream.writeBoolean(isPrevButtonEnabled);
      ostream.writeBoolean(isNextButtonEnabled);
      ostream.writeBoolean(isLastStep);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get this JWizardStepParameters from a data stream. 
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
   public void decode( DataInputStream istream ) throws IOException {
      stepClass = istream.readUTF();
      stepTitle = istream.readUTF();

      isDynamic = istream.readBoolean();

      short len = istream.readShort();

      if(len==0)
         stepProperties=null;
      else {
         stepProperties = new String[len][2];

         for( short i=0; i<len; i++ ) {
              stepProperties[i][0] = istream.readUTF();
              stepProperties[i][1] = istream.readUTF();
         }
      }
      
      isPrevButtonEnabled = istream.readBoolean();
      isNextButtonEnabled = istream.readBoolean();
      isLastStep = istream.readBoolean();
   }

 /*------------------------------------------------------------------------------------*/

   /** To find a property value from its key.
    * @return null if not found
    */
   public String getProperty(String key) {
        if(stepProperties==null)
           return null;

        for( int i=0; i<stepProperties.length; i++ )
             if(stepProperties[i][0].equals(key))
                return stepProperties[i][1]; // found
        
        return null; // not found
   }

 /*------------------------------------------------------------------------------------*/

   /** To add/set a property to our step properties list.
    *  If the property exists it is replaced, otherwise it is added.
    *
    *  @return the eventual previous value that was replaced, null if none.
    */
   public String setProperty(String key,String value) {
        if(stepProperties==null) {
           stepProperties = new String[1][2];
           stepProperties[0][0] = key;
           stepProperties[0][1] = value;
           return null;
        }

     // does the key exists ?
        for( int i=0; i<stepProperties.length; i++ )
             if(stepProperties[i][0].equals(key)) {
                String previous = stepProperties[i][1];
                stepProperties[i][1] = value;
                return previous;
             }

     // We create a new entry...
        String newProperties[][] = new String[stepProperties.length+1][2];
        
        for( int i=0; i<stepProperties.length; i++ ) {
             newProperties[i][0] = stepProperties[i][0];
             newProperties[i][1] = stepProperties[i][1];
        }

        newProperties[stepProperties.length][0] = key;
        newProperties[stepProperties.length][1] = value;
        stepProperties = newProperties;
        return null; // new entry
   }

 /*------------------------------------------------------------------------------------*/

   /** To load JWizardStepParameters from a file. This method uses the Wotlas
    *  Persistent Library and the Wotlas Debug utility.
    *
    * @param path full path to the file containing the JWizardStepParameters.
    * @return null if the file could not be loaded.
    */
    public static JWizardStepParameters loadFromFile( String path ) {
      try{
          return (JWizardStepParameters) PropertiesConverter.load( path );
      }
      catch( Exception pe ) {
          Debug.signal( Debug.ERROR, null, "Failed to load file: "+pe.getMessage() );
          return null;
      }
   }

 /*------------------------------------------------------------------------------------*/

   /** To save this JWizardStepParameters to a file. This method uses the Wotlas
    *  Persistent Library and the Wotlas Debug utility.
    *
    * @param path full path to the file where to store the JWizardStepParameters.
    * @return true if the save succeeded, false otherwise
    */
    public boolean saveToFile( String path ) {
      try{
          PropertiesConverter.save( this, path );
          return true;
      }
      catch( Exception pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to load file: "+pe.getMessage() );
          return false;
      }
   }

 /*------------------------------------------------------------------------------------*/

}