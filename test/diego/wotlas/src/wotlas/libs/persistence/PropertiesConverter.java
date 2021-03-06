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
package wotlas.libs.persistence;

import java.io.*;
import java.util.*;

import java.beans.*;
import java.lang.reflect.*;

import wotlas.utils.Debug;

/**
 * Save and Load an object to a "dot-properties" file.
 * To be saved/loaded a property should:
 * - have a public getter and setter.
 * - be not transient.
 * Null indexed values are saved.
 * @author Hari, Diego
 */

public class PropertiesConverter
{

   /**
   * Warnings or not.
   **/
   private static boolean warning = false;
   public static void setWarning(boolean warning) { PropertiesConverter.warning = warning; }
   public static boolean isWarning() { return PropertiesConverter.warning; }

   /**
   * Set this value to true to get DEBUG trace
   **/
   private static final boolean DEBUG = false;
   
   /**
   * Save an object into a "dot-properties" format file.
   * @param object the object to save.
   * @param name the path of the file where to save the object.
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static void save(Object object, String name)
                      throws PersistenceException
   {
      BufferedOutputStream os;

      try {
         os = new BufferedOutputStream(new FileOutputStream(name));
      }
      catch (FileNotFoundException ex) {
         Debug.signal(Debug.ERROR, name, ex);
         throw new PersistenceException(ex);
      }
      save(object, os);
      
      try{
        os.close(); // close stream : ADDED by ALDISS
      }catch(IOException e) {
      	e.printStackTrace();
      }
   }
   
   /** save an object BackupReady in a binary file
    * 
    * @param obj
    * @return
    */
	public static void Backup(Object object, String name)
					   throws PersistenceException
	{
	    BufferedOutputStream os;
	    try {
		    os = new BufferedOutputStream(new FileOutputStream(name));
	    } catch (FileNotFoundException ex) {
		    Debug.signal(Debug.ERROR, name, ex);
		    throw new PersistenceException(ex);
	    }
	    //save(object, os);
	   	try{
			ObjectOutputStream nested = new ObjectOutputStream(os);
			nested.writeObject(object);
			nested.flush();

			os.close(); // close stream : ADDED by ALDISS
	    } catch(IOException e) {
			e.printStackTrace();
		}
	}
   
   /**
   * Save an object into a "dot-properties" format file.
   * @param object the object to save.
   * @param os     the output stream where to save the object.
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static void save(Object object, OutputStream os)
                      throws PersistenceException
   {
      save(object, object.getClass(), os);
   }

   /**
   * Test the saving of an object class into a "dot-properties" format file.
   * @param clazz  the class of object to save.
   * @param name the path of the file where to save the object.
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static void testSave(Class clazz, String name)
                      throws PersistenceException
   {
      BufferedOutputStream os;

      try {
         os = new BufferedOutputStream(new FileOutputStream(name));
      }
      catch (FileNotFoundException ex) {
         Debug.signal(Debug.ERROR, name, ex);
         throw new PersistenceException(ex);
      }
      testSave(clazz, os);                    
   }

   /**
   * Test the saving of an object class into a "dot-properties" format file.
   * @param clazz  the class of object to save.
   * @param os     the output stream where to save the object.
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static void testSave(Class clazz, OutputStream os)
                      throws PersistenceException
   {
      save(null, clazz, os);
   }

   /**
   * Save an object into a "dot-properties" format file.
   * @param object the object to save (null for testing class).
   * @param clazz  the class of object to save.
   * @param os     the output stream where to save the object.
   * @exception PersistenceException when a problem occurs during saving.
   */
   private static void save(Object object, Class clazz, OutputStream os)
                       throws PersistenceException
   {
      Properties toBeSaved; 

      toBeSaved = new Properties();
      // Save the content of the object
      if (toProperties(object, clazz, toBeSaved, "") > 0) {
         try {
            //toBeSaved.store(os, "WotLas 1.0 Saved Object");
            sortedStore(os, "WotLas 1.0 Saved Object", toBeSaved);
         }
         catch (IOException ex) {
            Debug.signal(Debug.ERROR, os, ex);
            throw new PersistenceException(ex);
         }
      }
      else {
         Debug.signal(Debug.ERROR, object, "Nothing saved");
      }
   }

   /**
   * Restore an object from a binary file format file.
   * @param is     the input stream from where to load the object.
   * @return the object loaded from the file
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static Object Restore(InputStream is)
					  throws PersistenceException
   {
 	  Object obj = null;
	  try {
		 ObjectInputStream nested = new ObjectInputStream(is);
		 obj  = nested.readObject();
	  } catch(ClassNotFoundException e) {
	     e.printStackTrace();
		Debug.signal(Debug.ERROR, is, e);
	  } catch (IOException ex) {
		 Debug.signal(Debug.ERROR, is, ex);
		 throw new PersistenceException(ex);
	  }
      
	  // Build the object from its content
	  return obj;
   }
   
   /**
   * Restore an object from a binary format file.
   * @param name the path of the file from which to load the object.
   * @return the object loaded from the file
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static Object Restore(String name)
					  throws PersistenceException
   {
	  BufferedInputStream is;

	  try {
		 is = new BufferedInputStream(new FileInputStream(name));
	  }
	  catch (FileNotFoundException ex) {
		 Debug.signal(Debug.ERROR, name, ex);
		 throw new PersistenceException(ex);
	  }

	  Object obj = null;
	  try{
	    ObjectInputStream nested = new ObjectInputStream(is);
	    obj  = nested.readObject();
	  	// Object obj = load(is); 

		is.close(); // close stream : ADDED by ALDISS
	  }catch(ClassNotFoundException e) {
		e.printStackTrace();
	  }catch(IOException e) {
		e.printStackTrace();
	  }

	  return obj;
   }

   /**
   * Load an object from a "dot-properties" format file.
   * @param name the path of the file from which to load the object.
   * @return the object loaded from the file
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static Object load(String name)
                      throws PersistenceException
   {
      BufferedInputStream is;

      try {
         is = new BufferedInputStream(new FileInputStream(name));
      }
      catch (FileNotFoundException ex) {
         Debug.signal(Debug.ERROR, name, ex);
         throw new PersistenceException(ex);
      }

      Object obj = load(is); 

      try{
        is.close(); // close stream : ADDED by ALDISS
      }catch(IOException e) {
      	e.printStackTrace();
      }

      return obj;
   }

   /**
   * Load an object from a "dot-properties" format file.
   * @param is     the input stream from where to load the object.
   * @return the object loaded from the file
   * @exception PersistenceException when a problem occurs during saving.
   */
   public static Object load(InputStream is)
                      throws PersistenceException
   {
      Properties toBeRestored; 

      toBeRestored = new Properties();
      try {
         toBeRestored.load(is);
      }
      catch (IOException ex) {
         Debug.signal(Debug.ERROR, is, ex);
         throw new PersistenceException(ex);
      }
      
      // Build the object from its content
      return fromProperties(toBeRestored, "");
   }

   /**
   * Shows if a field is transient or not.
   * @param clazz      the class containing the field.
   * @param name       the name of the field to check.
   * @return true if the field is transient, not otherwise.
   **/
   private static boolean isTransient(Class clazz, String name) {
      // getFields return all the accessible (public) field inherited
      // of the class. So we must use getDeclaredFields (which returns all
      // the fields, even private) of the class, but which omit the superclasses
      // fields: this is the reason for the loop.
      for (Class curClass = clazz; curClass != null; curClass = curClass.getSuperclass()) {
         Field field;
         try {
           field = curClass.getDeclaredField(name);
           return Modifier.isTransient(field.getModifiers());
         }
         catch (NoSuchFieldException ex) {
           // The field is not found in this class, try in superclass
         }
      }
      // The XPosition bug: if a field is named xPosition, the property
      // name is XPosition (though the setter/getter are [s/g]etXPosition()
      // => Search not case sensitive if the field is not found
      // The field is not found in the class, return that is transient
      for (Class curClass = clazz; curClass != null; curClass = curClass.getSuperclass()) {
         Field[] fields;
         fields = curClass.getDeclaredFields();
         for (int i = 0; i < fields.length; i++) {
             if (name.equalsIgnoreCase(fields[i].getName()))
                return Modifier.isTransient(fields[i].getModifiers());
         }
      }
      if (PropertiesConverter.warning)
         Debug.signal(Debug.WARNING, clazz, "\"" + name + "\" not found as a field");
      return true;
   }

   /**
   * Recursively parse the properties of an object to save.
   * @param object      the object to save (may be null for class testing).
   * @param objectClass the class of object to save.
   * @param properties  the object properties to save.
   * @param prefix      the prefix for saving the property.
   * @return the number of saved properties.
   **/
   private static int toProperties(Object object, Class objectClass, Properties properties, String prefix)
   {
      PropertyDescriptor[] descriptors; // Properties of the object to save
      int                  nbSaved;     // Number of saved properties
      
      // Get all the getters of the object.
      descriptors = getProperties(objectClass);
      nbSaved = 0;
      // Save the class of the object
      properties.setProperty(prefix + "class", objectClass.getName());
      if (descriptors != null) {
         // Among the descriptors, get the one which have a read method
         // and a write method (it is useless to save if we will not be able
         // to restore).
         Method  readMethod;

         for (int i = 0; i < descriptors.length; i++) {
            if (DEBUG)
               System.out.println("Candidate property:" + prefix + descriptors[i].getName() + " in class " + objectClass.getName());
            if (((readMethod = descriptors[i].getReadMethod()) != null) &&
                (descriptors[i].getWriteMethod() != null)) {
               // Class property or 
               // Getter and setter are present: check that the property is
               // not transient
               if (!isTransient(objectClass, descriptors[i].getName())) {
                  // Not transient: save it
                  if (DEBUG)
                     System.out.println("Storable property:" + prefix + descriptors[i].getName());
                  nbSaved += saveProperty(object,
                                          properties,
                                          prefix,
                                          descriptors[i],
                                          readMethod);
               }               
               else if (DEBUG) {
                 System.out.println("Property:" + prefix + descriptors[i].getName() + " is transient");
               }
            }
            else if (DEBUG) {
               if (readMethod == null)
                 System.out.println("Property:" + prefix + descriptors[i].getName() + " has no read method");
               else
                 System.out.println("Property:" + prefix + descriptors[i].getName() + " has no write method");
            }
         }
      }      
      return nbSaved;
   }

   /**
   * Save a single property of an object.
   * @param object       the object to save (may be null for class testing).
   * @param properties   the object properties to save.
   * @param prefix       the prefix for saving the property.
   * @param descriptor   the property descriptor.
   * @param readMethod   the method for getting the value.
   * @return the number of saved properties.
   **/
   private static int saveProperty(Object object, Properties properties,
                                   String prefix, PropertyDescriptor descriptor,
                                   Method readMethod)
   {
      Object value;                      // The value of the object property
      int    nbSaved;                    // Number of saved properties
      String propertyName;               // Name of the property to save

      nbSaved = 0;
      propertyName = prefix + descriptor.getName();
      try {      
         if (descriptor instanceof IndexedPropertyDescriptor) {
            // This is an indexed property: save until array index out of bound 
            int nbCells;

            nbCells = 0;            
            try {
               Integer[] arg;

               arg = new Integer[0];
               for (nbCells = 0; true; nbCells++) {
                   if (object == null) {
                      // Test mode (using the class): consider one cell only
                      if (nbCells == 0)
                         value = null;
                      else
                         break; // Only first cell in test mode
                   }
                   else {
                      // Real mode (using the object)
                      arg[0] = new Integer(nbCells);
                      value = readMethod.invoke(object, arg);
                   }
                   // Save the value according to its class
                   nbSaved += saveValue(properties,
                                        propertyName + "." + nbCells,
                                        value,
                                        descriptor.getPropertyType().getComponentType());
               }
            }
            catch (IndexOutOfBoundsException ex) {
               // We have reached the end of the indexed properties values
            }
            catch (InvocationTargetException ex) {
               // We suppose that the exception warn of the end of the array,
               // but we log it anyway
               if (PropertiesConverter.warning)
                  Debug.signal(Debug.WARNING, object, ex);
            }
            // Save the number of values
            nbSaved += saveValue(properties,
                                 propertyName,
                                 new Integer(nbCells),
                                 (object == null) ? Integer.TYPE : null);
         }
         else {
            // Non-indexed method: save the value according to its class
            if (object == null)
               // Test mode (using the class)
               value = null;
            else
               value = readMethod.invoke(object, null);
            if ((value != null) && 
                (value.getClass().isArray() || readMethod.getReturnType().isArray())) {
               Object[] values;

               // Non-indexed, but returns an array: save individual values
               values = (Object[]) value;
               // Save the number of values
               nbSaved += saveValue(properties,
                                    propertyName,
                                    new Integer(values.length),
                                    (object == null) ? Integer.TYPE : null);
               for (int i = 0; i < values.length; i++) {
                   // Save the value according to its class
                   nbSaved += saveValue(properties,
                                        propertyName + "." + i,
                                        values[i],
                                        (object == null) ? descriptor.getPropertyType().getComponentType() : null);
               }
            }
            else {
               // Single value
               nbSaved += saveValue(properties,
                                    propertyName,
                                    value,
                                    (object == null) ? descriptor.getPropertyType() : null);
            }
         }
      }
      catch (InvocationTargetException ex) {
         // The read method should be callable
         Debug.signal(Debug.WARNING, object, ex);
      }      
      catch (IllegalAccessException ex) {
         // The read method should be public
         Debug.signal(Debug.WARNING, object, ex);
      }      
      catch (IllegalArgumentException ex) {
         // The read method should be callable with no arguments
         Debug.signal(Debug.WARNING, object, ex);
      }
      return nbSaved;
   }


   /**
   * Save a single value of an object.
   * @param properties   the object properties to save.
   * @param propertyName the name for saving the property.
   * @param value        the value of the property to save.
   * @param clazz        optional. When present, the class of value (test mode,
   *                     value not significant).
   * @return the number of saved properties.
   **/
   private static int saveValue(Properties properties,
                                String propertyName,
                                Object value,
                                Class  clazz)
   {
      if (clazz == null)
         // Actual mode
         return saveActualValue(properties, propertyName, value);
      else
         // Test mode (class without object instance)
         return saveTestValue(properties, propertyName, clazz);
   }
   
   /**
   * Save a single value of an object.
   * @param properties   the object properties to save.
   * @param propertyName the name for saving the property.
   * @param value        the value of the property to save.
   * @return the number of saved properties.
   **/
   private static int saveActualValue(Properties properties,
                                      String propertyName,
                                      Object value)
   {
      int    nbSaved; // Number of saved values

      nbSaved = 0;
      if (value != null) {
         Class valueClass;

         valueClass = value.getClass();
         if (DEBUG)
            System.out.println("Candidate value:" + propertyName + " (" + valueClass.toString() + ")");
         if ((valueClass.isPrimitive()) ||
             (value instanceof Boolean) ||
             (value instanceof Character) ||
             (value instanceof Byte) ||
             (value instanceof Integer) ||
             (value instanceof Short) ||
             (value instanceof Long) ||
             (value instanceof Float) ||
             (value instanceof Double) ||
             (value instanceof StringBuffer)) {
            // Primitive or string buffer type:
            // directly register the value as a string
            properties.setProperty(propertyName, value.toString());
            nbSaved = 1;
         }
         else if (value instanceof String) {
            properties.setProperty(propertyName, (String) value);
            nbSaved = 1;
         }
         else
            // Complex object: recursively add its properties
            nbSaved = toProperties(value, value.getClass(), properties, propertyName + ".");
      }
      return nbSaved;
   }
   
   /**
   * Save an arbitrary value of an object for testing.
   * @param properties   the object properties to save.
   * @param propertyName the name for saving the property.
   * @param clazz        The class of value (test mode).
   * @return the number of saved properties.
   **/
   private static int saveTestValue(Properties properties,
                                    String propertyName,
                                    Class  clazz)
   {
      int    nbSaved; // Number of saved values

      nbSaved = 0;
      String className;

      className = clazz.getName();
      if (DEBUG)
         System.out.println("Candidate value:" + propertyName + " (" + className + ")");
      if (clazz.isPrimitive() ||
          (className.equals("java.lang.Boolean")) ||
          (className.equals("java.lang.Character")) ||
          (className.equals("java.lang.Byte")) ||
          (className.equals("java.lang.Integer")) ||
          (className.equals("java.lang.Short")) ||
          (className.equals("java.lang.Long")) ||
          (className.equals("java.lang.Float")) ||
          (className.equals("java.lang.Double")) ||
          (className.equals("java.lang.StringBuffer")) ||
          (className.equals("java.lang.String"))) {
          properties.setProperty(propertyName, "value(" + className + ")");
          nbSaved = 1;
      }
      else
          // Complex object: recursively add its properties
          nbSaved = toProperties(null, clazz, properties, propertyName + ".");
      return nbSaved;
   }
   
   /**
   * Recursively parse the properties of an object to restore.
   * @param properties the object properties to restore.
   * @param prefix     the prefix for restoring the property.
   * @return the object build from properties.
   * @exception PersistenceException when a problem occurs during loading.
   **/
   private static Object fromProperties(Properties properties, String prefix)
                         throws PersistenceException
   {
      PropertyDescriptor[] descriptors; // Properties of the object to restore
      String               className;   // Object class
      Class                objectClass; // Class of object
      Object               object;      // Restored object
      
      // Build the object and gets its properties.
      className = properties.getProperty(prefix + "class");
      if (className == null)
         return null;
      try {
         objectClass = Class.forName(className);
      }
      catch (ClassNotFoundException ex) {
         Debug.signal(Debug.ERROR, className, ex);
         throw new PersistenceException(ex);
      }
      try {
         object = objectClass.newInstance();
      }
      catch (Exception ex) {
         Debug.signal(Debug.ERROR, objectClass, ex);
         throw new PersistenceException(ex);
      }
      descriptors = getProperties(objectClass);
      if (descriptors != null) {
         // Among the descriptors, get the one which have a read method
         // and a write method.
         Method  writeMethod;

         for (int i = 0; i < descriptors.length; i++) {
            if (DEBUG)
               System.out.println("Candidate property:" + prefix + descriptors[i].getName());
            writeMethod = null;
            if ((descriptors[i].getReadMethod() != null) &&
                ((writeMethod = descriptors[i].getWriteMethod()) != null)) {
               // Class property or 
               // Getter and setter are present: check that the property is
               // not transient
               if (!isTransient(objectClass, descriptors[i].getName())) {
                  // Not transient: save it
                  if (DEBUG)
                     System.out.println("Loadable property:" + prefix + descriptors[i].getName());
                  loadProperty(object,
                               properties,
                               prefix + descriptors[i].getName(),
                               writeMethod,
                               descriptors[i].getPropertyType(),
                               (descriptors[i] instanceof IndexedPropertyDescriptor));
               }               
               else if (DEBUG) {
                 System.out.println("Property:" + prefix + descriptors[i].getName() + " is transient");
               }
            }
            else if (DEBUG) {
               if (writeMethod == null)
                 System.out.println("Property:" + prefix + descriptors[i].getName() + " has no write method");
               else
                 System.out.println("Property:" + prefix + descriptors[i].getName() + " has no read method");
            }
         }
      }      
      return object;
   }

   /**
   * Load a single property of an object.
   * @param object        the object to load.
   * @param properties    the object properties to load.
   * @param propertyName  the name for loading the property.
   * @param writeMethod   the method for setting the value.
   * @param propertyClass the class of the property.
   * @param indexed       shows if the property is indexed or not.
   * @return the number of loaded properties.
   * @exception PersistenceException when a problem occurs during loading.
   **/
   private static void loadProperty(Object object, Properties properties,
                                    String propertyName,
                                    Method writeMethod,
                                    Class  propertyClass,
                                    boolean indexed)
                       throws PersistenceException
   {
      Object[] arg;
      
      try {      
         if ((indexed) || (propertyClass.isArray())) {
            // This is an indexed/array property: load the number of the cells
            Integer   nbCellsObject;
            int       nbCells;
            Class     actualClass;

            actualClass = propertyClass.getComponentType();
            if (actualClass == null)
               actualClass = propertyClass;
            nbCellsObject = (Integer) loadValue(properties,
                                                propertyName,
                                                Integer.TYPE);
            if (nbCellsObject == null)
               return; // No cells for this array
            nbCells = nbCellsObject.intValue();
            if (indexed) {
               arg = new Object[2];
               // This is an indexed property: write each cell
               for (int cell = 0; cell < nbCells; cell++) {
                  // Load each cell
                  arg[1] = loadValue(properties,
                                     propertyName + "." + cell,
                                     actualClass);
                  arg[0] = new Integer(cell);
                  writeMethod.invoke(object, arg);
               }
            }
            else {
               // This is an array property: write all cells
               Object array;

               arg = new Object[1];
               array = Array.newInstance(actualClass, nbCells);
               for (int cell = 0; cell < nbCells; cell++) {
                  // Load each cell
                  Object value;
                  value = loadValue(properties,
                                    propertyName + "." + cell,
                                    actualClass);
                  Array.set(array, cell, value);
               }
               arg[0] = array;
               writeMethod.invoke(object, arg);
            }
         }
         else {
            // Single value
            arg = new Object[1];
            arg[0] = loadValue(properties,
                               propertyName,
                               propertyClass);
            if (arg[0] != null)
               writeMethod.invoke(object, arg);
         }
      }
      catch (InvocationTargetException ex) {
         // The read method should be callable
         Debug.signal(Debug.WARNING, object, ex);
      }      
      catch (IllegalAccessException ex) {
         // The read method should be public
         Debug.signal(Debug.WARNING, object, ex);
      }      
      catch (IllegalArgumentException ex) {
         // The read method should be callable with no arguments
         Debug.signal(Debug.WARNING, object, ex);
      }
   }

   /**
   * Load a single value of an object.
   * @param properties     the object properties.
   * @param propertyName   the name of the property.
   * @param propertyClass  the class of the property.
   * @return the value of the property.
   * @exception PersistenceException when a problem occurs during loading.
   **/
   private static Object loadValue(Properties properties,
                                   String propertyName,
                                   Class  propertyClass)
                         throws PersistenceException
   {
      Object result;
      String value;

      value = properties.getProperty(propertyName);
      if (DEBUG)
         System.out.println("Loaded:" + propertyName + " = " + value + "/" + propertyClass);
      result = null;
      if (value != null) {
        if ((propertyClass.getName().equals("java.lang.Boolean")) ||
            (propertyClass.equals(Boolean.TYPE)))
           result = new Boolean(value);
        else if ((propertyClass.getName().equals("java.lang.Character")) ||
                 (propertyClass.equals(Character.TYPE)))
           result = new Character(value.charAt(0));
        else if ((propertyClass.getName().equals("java.lang.Byte")) ||
                 (propertyClass.equals(Byte.TYPE)))
           result = new Byte(value);
        else if ((propertyClass.getName().equals("java.lang.Integer")) ||
                 (propertyClass.equals(Integer.TYPE)))
           result = new Integer(value);
        else if ((propertyClass.getName().equals("java.lang.Short")) ||
                 (propertyClass.equals(Short.TYPE)))
           result = new Short(value);
        else if ((propertyClass.getName().equals("java.lang.Long")) ||
                 (propertyClass.equals(Long.TYPE)))
           result = new Long(value);
        else if ((propertyClass.getName().equals("java.lang.Float")) ||
                 (propertyClass.equals(Float.TYPE)))
           result = new Float(value);
        else if ((propertyClass.getName().equals("java.lang.Double")) ||
                 (propertyClass.equals(Double.TYPE)))
           result = new Double(value);
        else if (propertyClass.getName().equals("java.lang.StringBuffer"))
           result = new StringBuffer(value);
        else if (propertyClass.getName().equals("java.lang.String"))
           result = value;
        else {
           // Abnormal: should not occur
           Debug.signal(Debug.WARNING, propertyName,  "IGNORED");
           return null;
        }
      }
      else
         // Complex object: recursively add its properties
         result = fromProperties(properties, propertyName + ".");
      return result;
   }
   
   /**
   * Provide the properties of an object to save or to load.
   * @param objectClass the class of object to save or to load.
   * @return an array of properties.
   * @execption IntrospectionException when an error occurs during introspection.
   **/
   private static PropertyDescriptor[] getProperties(Class objectClass)
   {
      BeanInfo objectInfo; // Information on the object to save

      try {
         objectInfo = Introspector.getBeanInfo(objectClass);
         return objectInfo.getPropertyDescriptors();
      }
      catch (IntrospectionException ex) {
         Debug.signal(Debug.ERROR, objectClass, ex);
      }
      return null;
   }

   /*------------------------------------------------------------------------
   *                         Sorted properties
   ------------------------------------------------------------------------*/
   private static void sortedStore(OutputStream out, String header, Properties properties)
    throws IOException
    {
        BufferedWriter      awriter;
        ArrayList           sortedList;
        awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
        if (header != null)
            writeln(awriter, "#" + header);
        sortedList = new ArrayList(properties.size());
        writeln(awriter, "#" + new Date().toString());
        for (Enumeration e = properties.keys(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            String val = (String)properties.get(key);
            key = saveConvert(key);
            val = saveConvert(val);
            sortedList.add(key + "=" + val);
        }
        Collections.sort(sortedList);
        for (int i = 0; i < sortedList.size(); i++)
            writeln(awriter, (String) sortedList.get(i));
        awriter.flush();
    }

    private static final String specialSaveChars = "=: \t\r\n\f#!";
    /** A table of hex digits */
    private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };

    /*
     * Converts unicodes to encoded \\uxxxx
     * and writes out any of the characters in specialSaveChars
     * with a preceding slash
     */
    private static String saveConvert(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; ) {
            aChar = theString.charAt(x++);
            switch(aChar) {
                case '\\':outBuffer.append('\\'); outBuffer.append('\\');
                          continue;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          continue;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          continue;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          continue;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          continue;
                default:
                    if ((aChar < 20) || (aChar > 127)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >> 8) & 0xF));
                        outBuffer.append(toHex((aChar >> 4) & 0xF));
                        outBuffer.append(toHex((aChar >> 0) & 0xF));
                    }
                    else {
                        if (specialSaveChars.indexOf(aChar) != -1)
                            outBuffer.append('\\');
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    private static void writeln(BufferedWriter bw, String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }

    /**
     * Convert a nibble to a hex character
     * @param	nibble	the nibble to convert.
     */
    private static char toHex(int nibble) {
  	   return hexDigit[(nibble & 0xF)];
    }

   /**
   * Test method.
   **/
   public static void main(String[] argv) throws java.io.IOException, PersistenceException, ClassNotFoundException {

      testSave(Class.forName(argv[0]), "C:/temp/" + argv[0] + ".txt");
/*   
      wotlas.common.universe.TownMap example[];
      wotlas.common.universe.WorldMap world;

      world = new wotlas.common.universe.WorldMap();
      world.setFullName("An example of world");
      world.setShortName("UEoW");
      world.setWorldMapID(56);
      example = new wotlas.common.universe.TownMap[2];
      example[0] = new wotlas.common.universe.TownMap();
      example[0].setFullName("TarValon");
      example[0].setShortName("TV");
      example[1] = new wotlas.common.universe.TownMap();
      example[1].setFullName("Ebou Dar");
      example[1].setShortName("EB");
      world.setTownMaps(example);
      save(world, "C:/Temp/world.txt");
      world = (wotlas.common.universe.WorldMap) load("C:/Temp/world.txt");
      save(world, "C:/Temp/world2.txt");
*/   
   }
}

