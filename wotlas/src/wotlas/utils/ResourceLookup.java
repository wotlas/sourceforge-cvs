/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2008 WOTLAS Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package wotlas.utils;

import java.net.URL;
// netbeans integration :
//import org.openide.util.Lookup;
/**
 * Class use to find resources in jars loaded.<br/>
 * In netbeans module, classes and resources loading use a specific ClassLoader
 * that prevents to use a module resource that is not declared in module 
 * dependencies. Actually we are using wotlas.common.ResourceManager to load 
 * many resources, but we want the 'wotlas.common' module to be the most 
 * independent possible, we need to use the SystemClassLoader to load resources.
 * <b>Be aware it is a programming trick that would not be kept in the 
 * future.</b>
 * @author SleepingOwl
 */
public final class ResourceLookup {

    /**
     * Not instanciable.
     */
    private ResourceLookup() {
        super();
    }

    /**
     * Returns the wanted resource url from the jars loaded.
     * @param cla class used to find class loader and jars;
     * @param resourceName resource name with FULL resource path.
     * @return url or null if the image was not found.
     */
    public static final URL getClassResourceUrl(final Class cla, final String resourceName) {
        ClassLoader cl;
        URL url = null;

        if (cla != null) {
            // Classic resource loading.
            cl = cla.getClassLoader();
            url = getSystemResourceUrl(cl, resourceName);
        }
        if (url == null) {
            // Netbeans System Classloader.
            // netbeans integration :
            //cl = Lookup.getDefault().lookup(ClassLoader.class);
            cl = ClassLoader.getSystemClassLoader();
            url = getSystemResourceUrl(cl, resourceName);
        }
        return url;
    }

    /**
     * Returns the wanted resource url from the jars loaded.
     * @param cl class loader used to find resource
     * @param resourceName resource name with FULL resource path.
     * @return url or null if the image was not found.
     */
    protected static final URL getSystemResourceUrl(final ClassLoader cl, final String resourceName) {
        final String refName = resourceName.substring(1);
        final URL url = cl.getResource(refName);
        return url;
    }
}
