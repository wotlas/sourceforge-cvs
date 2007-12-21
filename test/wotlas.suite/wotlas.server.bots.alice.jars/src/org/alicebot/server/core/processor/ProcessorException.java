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


/**
 *  Should be thrown by processors when they
 *  find content that they cannot handle.
 *
 *  @since 4.1.3
 *
 *  @author Noel Bush
 */
public class ProcessorException extends Exception
{
    public ProcessorException(String message)
    {
        super(message);
    }
}

