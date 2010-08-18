/**
 * 
 */
package wotlas.libs.net;

import wotlas.utils.WishGameExtension;
import wotlas.utils.WotlasGameDefinition;

/**
 * This object is intend to launch a standalone server using the piped input/output streams to communicate with the client in the same JRE.
 * @author SleepingOwl
 *
 */
public interface WishNetStandaloneServer extends WishGameExtension, Runnable {

    /** Launch the server and stop when it is completely started.*/
    public void run();

    /**
     * @param netCfg
     * @param gameDefinition
     */
    public void init(NetConfig netCfg, WotlasGameDefinition gameDefinition);
}
