/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wotlas.common;

import wotlas.utils.Debug;
import wotlas.utils.WishPlayerDataFactory;

/**
 *
 * @author SleepingOwl
 */
public class PlayerDataCommonFactory implements WishPlayerDataFactory {

    /** To get an instance of an object from its class name. We assume that the
     *  object has an empty constructor.
     *
     *  @param className a string representing the class name of the filter
     *  @return an instance of the object, null if we cannot get an instance.
     */
    public Object getInstance(String className) {
        if (className == null || !className.startsWith("wotlas.common")) {
            return null;
        }
        try {
            Class myClass = Class.forName(className);
            return myClass.newInstance();
        } catch (Exception ex) {
            Debug.signal(Debug.ERROR, null, "Failed to create new instance of " + className + ", " + ex);
            return null;
        }
    }
}
