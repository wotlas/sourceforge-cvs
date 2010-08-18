/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wotlas.server.persistence;

import wotlas.libs.persistence.PersistenceException;
import wotlas.libs.persistence.WishPropertiesClassFactory;

/**
 *
 * @author SleepingOwl
 */
public class ServerPropertiesClassFactory implements WishPropertiesClassFactory {

    public Class propertyClassForName(String className) throws PersistenceException {
        if (className==null || !className.startsWith("wotlas.server")) {
            return null;
        }
        Class objectClass = null;
        Exception ex = null;
        try {
            objectClass = Class.forName(className);
            return objectClass;
        } catch (ClassNotFoundException cnfe) {
            // Direct class instanciation does not function in netbeans.
            throw new PersistenceException(cnfe);
        }
        
    }

}
