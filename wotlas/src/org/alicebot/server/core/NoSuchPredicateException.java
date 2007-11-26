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


/**
 *  Indicates that there is no predicate with a requested name.
 */
public class NoSuchPredicateException extends Exception
{
    /** The name for which there was no predicate. */
    private static String name;

    /** The index at which there was no value. */
    private static int index = -1;


    /**
     *  Constructs a new NoSuchPredicateException for
     *  the given name.
     *
     *  @param name the name for which there was no predicate
     */
    public NoSuchPredicateException(String name)
    {
        this.name = name;
    }


    /**
     *  Constructs a new NoSuchPredicateException for
     *  the given name and index.
     *
     *  @param name     the name for which there was no predicate
     *                  with a value at the given index
     *  @param index    the index at which there was no value
     */
    public NoSuchPredicateException(String name, int index)
    {
        this.name = name;
        this.index = index;
    }


    public String getMessage()
    {
        if (index != -1)
        {
            return "No predicate with name \"" + name + "\" with a value at index " + index + ".";
        }
        else
        {
            return "No predicate with name \"" + name + "\".";
        }
    }
}

