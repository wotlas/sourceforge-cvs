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
    - changed to extend (not implement) AIMLProcessor (latter is now an abstract class)
      (includes necessary public field "label")
*/

/*
    More fixes (4.1.3 [02] - November 2001, Noel Bush
    - added check of server property for allowing/disallowing use
*/

/*
    4.1.4 [01] - December 2001, Noel Bush
    - changed to use ActiveJavaScriptInterpreter
*/

package org.alicebot.server.core.processor;

import org.alicebot.server.core.Globals;
import org.alicebot.server.core.interpreter.ActiveJavaScriptInterpreter;
import org.alicebot.server.core.logging.Log;
import org.alicebot.server.core.parser.AIMLParser;
import org.alicebot.server.core.parser.XMLNode;


/**
 *  Handles a
 *  <code><a href="http://www.alicebot.org/TR/2001/WD-aiml/#section-javascript">javascript</a></code>
 *  element.
 *
 *  @version    4.1.3
 *  @author     Jon Baer
 *  @author     Thomas Ringate, Pedro Colla
 */
public class JavaScriptProcessor extends AIMLProcessor
{
    public static final String label = "javascript";


    /**
     *  Returns the result of processing the contents of the <code>javascript</code>
     *  element by the JavaScript interpreter.
     */
    public String process(int level, String userid, XMLNode tag, AIMLParser parser) throws AIMLProcessorException
    {
        // Don't use the system tag if not permitted.
        if (!Globals.jsAccessAllowed())
        {
            Log.userinfo("Use of <javascript> prohibited!", Log.INTERPRETER);
            return EMPTY_STRING;
        }
        if (tag.XMLType == XMLNode.TAG)
        {
            Log.devinfo("Calling JavaScript interpreter " + Globals.javaScriptInterpreter(), Log.INTERPRETER);
            return ActiveJavaScriptInterpreter.getInstance().evaluate(userid, parser.evaluate(level++, userid, tag.XMLChild));
        }
        else
        {
            throw new AIMLProcessorException("<javascript></javascript> must have content!");
        }
    }
}
