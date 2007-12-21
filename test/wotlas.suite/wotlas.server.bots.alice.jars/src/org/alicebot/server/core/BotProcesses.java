/*
    Alicebot Program D
    Copyright (C) 1995-2001, A.L.I.C.E. AI Foundation
    
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.
    
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, 
    USA.
*/

package org.alicebot.server.core;

import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.alicebot.server.core.util.Trace;

/**
 *  Controls processes that run in separate threads
 *  and need to be shut down before the bot exits.
 */
public class BotProcesses
{
    /** The registry of all bot processes. */
    private static Vector registry = new Vector();


    /**
     *  Adds a process to the registry and starts it.
     *
     *  @param process  the process to add
     *  @param name     a name by which its thread will be identified
     */
    public static void start(BotProcess process, String name)
    {
        registry.add(process);
        Thread botProcess = new Thread(process, name);

        // Set the thread as a daemon, in case the server terminates abnormally.
        botProcess.setDaemon(true);

        // Start the thread.
        botProcess.start();
    }


    /**
     *  Returns an iterator on the registry.
     *
     *  @return an iterator on the registry
     */
    public static Iterator getRegistryIterator()
    {
        return registry.iterator();
    }


    /**
     *  Shuts down all registered processes.
     */
    public static void shutdownAll()
    {
        Trace.devinfo("Shutting down all BotProcesses.");
        Iterator iterator = registry.iterator();
        while (iterator.hasNext())
        {
            BotProcess process = (BotProcess)iterator.next();
            Trace.devinfo("Shutting down " + process);
            process.shutdown();
        }
        Trace.devinfo("Finished shutting down BotProcesses.");
    }


    /**
     *  Prevents instantiation of this class.
     */
    private BotProcesses()
    {
    }
}
