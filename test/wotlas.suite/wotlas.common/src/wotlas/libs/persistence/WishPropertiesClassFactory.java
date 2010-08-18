/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wotlas.libs.persistence;

import wotlas.utils.WishGameExtension;

/**
 *
 * @author SleepingOwl
 */
public interface WishPropertiesClassFactory extends WishGameExtension {

    public Class propertyClassForName(String className) throws PersistenceException;
}
