/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wotlas.common;

/**
 * 
 * @author Olivier
 */
public class PrimaryKeyGenerator {

    static private long genUniqueKeyId;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    static synchronized public String GenUniqueKeyId() {
        return System.currentTimeMillis() + "" + PrimaryKeyGenerator.genUniqueKeyId++;
    }
}
