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

package org.alicebot.server.core.processor.loadtime;

import org.alicebot.server.core.parser.StartupFileParser;
import org.alicebot.server.core.parser.XMLNode;


/**
 *  The <code>bots</code> element is a container for bot
 *  configuration definitions.
 */
public class BotsProcessor extends StartupElementProcessor
{
    public static final String label = "bots";


    public String process(int level, String botid, XMLNode tag, StartupFileParser parser) throws InvalidStartupElementException
    {
        return parser.evaluate(level++, botid, tag.XMLChild);
    }
}

