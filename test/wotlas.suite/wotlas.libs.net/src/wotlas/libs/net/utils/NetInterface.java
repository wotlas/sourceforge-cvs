/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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

package wotlas.libs.net.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import wotlas.utils.Debug;

/**
 * A class that provides static methods to handle network interfaces via Java 1.4's
 * NetworkInterface.
 *
 * @author aldiss
 */

public class NetInterface {

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We return the names of all the network interfaces of this machine.
     * @return itf names, an empty array if none
     */
    static public String[] getInterfaceNames() {
        try {
            String names[] = new String[NetInterface.getInterfaceNumber()];
            Enumeration e = NetworkInterface.getNetworkInterfaces();

            int nb = 0;

            while (e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e.nextElement();
                names[nb] = ni.getName();
                nb++;
            }

            return names;
        } catch (Throwable se) {
            Debug.signal(Debug.ERROR, null, "" + se);
            return new String[0];
        }
    }

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We return the number of network interfaces of this machine.
     * @return nb of interfaces
     */
    static public int getInterfaceNumber() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            int nb = 0;

            while (e.hasMoreElements()) {
                e.nextElement();
                nb++;
            }

            return nb;
        } catch (Throwable se) {
            Debug.signal(Debug.ERROR, null, "" + se);
            return 0;
        }
    }

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We return the number of network addresses for the given interface name on this machine.
     * @param itfName name of the network interface
     * @return nb of addresses, 0 if none or if the itf is not found
     */
    static public int getAddressesNumber(String itfName) {
        try {
            NetworkInterface ni = NetworkInterface.getByName(itfName);

            if (ni == null)
                return 0;

            Enumeration e = ni.getInetAddresses();
            int nb = 0;

            while (e.hasMoreElements()) {
                e.nextElement();
                nb++;
            }

            return nb;
        } catch (Throwable se) {
            Debug.signal(Debug.ERROR, null, "" + se);
            return 0;
        }
    }

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get all the addresses of a specified interface.
     *  @param itfName name of the network interface
     *  @return all the IP address of the given interface, an empty array if none
     */
    public static String[] getInterfaceAddresses(String itfName) {

        try {
            String names[] = new String[NetInterface.getAddressesNumber(itfName)];

            if (names.length == 0)
                return names;

            NetworkInterface ni = NetworkInterface.getByName(itfName);
            Enumeration e = ni.getInetAddresses();
            int nb = 0;

            while (e.hasMoreElements()) {
                InetAddress in = (InetAddress) e.nextElement();
                names[nb] = in.getHostAddress();
                nb++;
            }

            return names;
        } catch (Throwable se) {
            Debug.signal(Debug.ERROR, null, "" + se);
            return new String[0];
        }
    }

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the description of an interface.
     *  @param itfName name of the network interface
     *  @return a description of the interface, if available.
     */
    public static String getInterfaceDescription(String itfName) {
        try {
            NetworkInterface ni = NetworkInterface.getByName(itfName);

            if (ni == null)
                return "[Error: interface not found !]";

            return ni.getDisplayName();
        } catch (Throwable se) {
            Debug.signal(Debug.ERROR, null, "" + se);
            return "[Error: " + se.getMessage() + "]";
        }
    }

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns all the available network information of this machine.
     */
    public static String getNetworkInfo() {
        String itf[] = NetInterface.getInterfaceNames();
        StringBuffer buf = new StringBuffer("Network Interfaces\n------------------\n\n");

        for (int i = 0; i < itf.length; i++) {
            buf.append("  " + i + " - " + itf[i] + "\n");
            buf.append("      Descr. : " + NetInterface.getInterfaceDescription(itf[i]) + "\n");
            buf.append("      IP adr : \n");

            String ip[] = NetInterface.getInterfaceAddresses(itf[i]);

            for (int j = 0; j < ip.length; j++)
                buf.append("           " + j + " - " + ip[j] + "\n");

            buf.append("\n");
        }

        return buf.toString();
    }

    /*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
