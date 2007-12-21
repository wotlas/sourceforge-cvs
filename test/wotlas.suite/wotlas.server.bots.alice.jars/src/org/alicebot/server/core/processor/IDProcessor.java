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

package org.alicebot.server.core.processor;

import org.alicebot.server.core.parser.AIMLParser;
import org.alicebot.server.core.parser.XMLNode;


/**
 *  Handles an
 *  <code><a href="http://www.alicebot.org/TR/2001/WD-aiml/#section-id">id</a></code>
 *  element.
 *
 *  @version    4.1.3
 *  @author     Noel Bush
 */
public class IDProcessor extends AIMLProcessor
{
    public static final String label = "id";


    public String process(int level, String userid, XMLNode tag, AIMLParser parser) throws AIMLProcessorException
    {
        if (tag.XMLType == XMLNode.EMPTY)
        {
            return userid;
        }
        else
        {
            throw new AIMLProcessorException("<id/> cannot have content!");
        }
    }
}

