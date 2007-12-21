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

/*
    Code cleanup (4.1.3 [00] - October 2001, Noel Bush)
    - formatting cleanup
    - complete javadoc
    - made all imports explicit
*/

/*
    Further optimizations {4.1.3 [0]1 - November 2001, Noel Bush)
    - changed to extend StartupTagProcessor
    - moved to loadtime subpackage
*/

package org.alicebot.server.core.processor.loadtime;

import org.alicebot.server.core.Globals;
import org.alicebot.server.core.util.Trace;
import org.alicebot.server.core.parser.StartupFileParser;
import org.alicebot.server.core.parser.XMLNode;
import org.alicebot.server.core.util.Toolkit;


/**
 *  Presently supports configuration of a single bot.
 *  Later this will be expanded to handle any number of bots.
 */
public class BotProcessor extends StartupElementProcessor
{
    public static final String label = "bot";


    public String process(int level, String botid, XMLNode tag, StartupFileParser parser) throws InvalidStartupElementException
    {
        String botID = Toolkit.getAttributeValue(ID, tag.XMLAttr);

        if (!botID.equals(EMPTY_STRING))
        {
            if (Boolean.valueOf(Toolkit.getAttributeValue(ENABLED, tag.XMLAttr)).booleanValue())
            {
                if (Globals.getBotID() == null)
                {
                    Globals.setBotID(botID);
                    Trace.devinfo("Configuring bot \"" + botID + "\".");
                    return parser.evaluate(level++, botid, tag.XMLChild);
                }
                else
                {
                    Trace.userinfo("Sorry, this version of Program D only supports one bot.");
                    Trace.userinfo("Cannot load \"" + botID + "\" right now.");
                    Trace.userinfo("Just wait for a later version!");
                    return EMPTY_STRING;
                }
            }
        }
        return EMPTY_STRING;
    }
}

