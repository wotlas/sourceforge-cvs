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

/*  Code cleanup (28 Oct 2001, Noel Bush)
    - formatting cleanup
    - complete javadoc (not for implemented methods, though)
*/

/*
    Further fixes and optimizations (4.1.3 [01] - November 2001, Noel Bush)
    - CHANGED key types to Strings to fit change in Nodemapper interface!!!
    - added remove() method
    - changed TreeMap to HashMap, because order does not matter!
*/

package org.alicebot.server.core.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 *  <p>
 *  This non-trivial implementation of {@link Nodemapper Nodemapper}
 *  uses a {@link java.util.TreeMap TreeMap} internally, but only
 *  allocates it when the number of keys is two or more.
 *  </p>
 *  <p>
 *  The <code>Nodemaster</code> saves space when many of the
 *  {@link Nodemapper Nodemappers} have only one branch, as is
 *  often the case in a real-world @{link Graphmaster Graphmaster}.
 *  </p>
 */
public class Nodemaster implements Nodemapper
{
    protected int size = 0;
    protected String key;
    protected Object value;
    protected HashMap Hidden;

    public Object put(String key, Object value)
    {
        if (size == 0)
        {
            this.key = key.toUpperCase();
            this.value = value;
            size = 1;
            return value;
        }
        else if (size == 1)
        {
            Hidden = new HashMap();
            Hidden.put(this.key.toUpperCase(), this.value);
            size = 2;
            return Hidden.put(key.toUpperCase(), value);
        }
        else
        {
            return Hidden.put(key.toUpperCase(), value);
        }
    }


    public void remove(String key)
    {
        if (size == 1)
        {
            this.value = null;
            size = 0;
            return;
        }
        else
        {
            Hidden.remove(key.toUpperCase());
            size--;
            return;
        }
    }


    public Object get(String key)
    {
        if (size <= 1)
        {
            if (key.equalsIgnoreCase(this.key))
            {
                return this.value;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return Hidden.get(key.toUpperCase());
        }
    }


    public Set keySet()
    {
        if (size <= 1)
        {
            Set result = new HashSet();
            if (this.key != null)
            {
                result.add(this.key);
            }
            return result;
        }
        else
        {
            return Hidden.keySet();
        }
    }


    public boolean containsKey(String key)
    {
        if (size <= 1)
        {
            return (key.equalsIgnoreCase(this.key));
        }
        else
        {
            return Hidden.containsKey(key.toUpperCase());
        }
    }
}

