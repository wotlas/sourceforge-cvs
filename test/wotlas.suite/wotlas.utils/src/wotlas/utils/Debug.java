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

package wotlas.utils;

import java.io.PrintStream;

/** A Debug Utility.<br>
 *( a tracer support would be interessant to add )
 * 
 * @author Aldiss
 */

public class Debug {

    /*------------------------------------------------------------------------------------*/

    /** 
     * A debug level used for notification messages,
     * does not represent any serious problem. 
     */
    public static final byte NOTICE = 0;

    /** 
     * A debug level warning of an undesireable occurrence,
     * but the program has recovered and is continuing OK.
     */
    public static final byte WARNING = 1;

    /** 
     * A debug level indicating an important error,
     * the program will probably recover but some things are now wrong.
     */
    public static final byte ERROR = 2;

    /** 
     * A debug level indicating a critical problem,
     * the program will very likely stop working.
     */
    public static final byte CRITICAL = 3;

    /** 
     * A debug level indicating complete failure,
     * the program is about to crash.  
     */
    public static final byte FAILURE = 4;

    /*------------------------------------------------------------------------------------*/

    /**
     * Current level we are working at.
     * Under this level we filter messages.
     */
    private static byte level = Debug.NOTICE;

    /**
     * Display exception stack trace ?
     */
    private static boolean displayExceptionStack = true;

    /**
     * the output stream we are going to use
     */
    private static PrintStream out = System.err;

    /*------------------------------------------------------------------------------------*/

    /** To print a debug message.
     *
     * @param code FAILURE, CRITICAL, ...
     * @param src_obj object reporting the error.
     * @param info a short text about the error.
     */
    public synchronized static void signal(byte code, Object src_obj, String info) {
        if (code < Debug.level)
            return;

        String debug_msg = "";

        switch (code) {
            case FAILURE:
                debug_msg += "FAILURE";
                break;
            case CRITICAL:
                debug_msg += "CRITICAL";
                break;
            case ERROR:
                debug_msg += "ERROR";
                break;
            case WARNING:
                debug_msg += "WARNING";
                break;
            case NOTICE:
                debug_msg += "NOTICE";
                break;
            default:
                debug_msg += "UNKNOWN";
                break;
        }

        if (src_obj != null)
            debug_msg += " - " + src_obj.getClass().getName() + "\n   " + info;
        else
            debug_msg += " - " + info;

        Debug.out.println(debug_msg);
    }

    /*------------------------------------------------------------------------------------*/

    /** To print a debug message with the content of an exception.
     *
     * @param code FAILURE, CRITICAL, ...
     * @param src_obj class reporting the error.
     * @param e exception raised.
     */
    public synchronized static void signal(byte code, Object src_obj, Exception e) {
        if (e != null) {
            if (e.getMessage() != null)
                Debug.signal(code, src_obj, e.getMessage());
            else
                Debug.signal(code, src_obj, e.toString());
        } else
            Debug.signal(code, src_obj, "<null exception>");

        if (Debug.displayExceptionStack && e != null)
            Debug.out.println("EXCEPTION STACK: " + Debug.getStackTrace(e));
    }

    /*------------------------------------------------------------------------------------*/

    /** Changes the level of debugging. We don't print errors under the specified level.
     *
     * @param level code ( NOTICE, WARNING, ... ) under which we filter.
     */
    static public void setLevel(byte level) {
        Debug.level = level;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set/unset the display of exception stack trace.
     *
     * @param display set to true if you want to see exception stack trace.
     */
    static public void displayExceptionStack(boolean display) {
        Debug.displayExceptionStack = display;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the stack trace of an exception.
     *
     * @param e exception to inspect
     */

    static public String getStackTrace(Exception e) {
        java.io.StringWriter s = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(s));
        String trace = s.toString();

        if (trace == null || trace.length() == 0 || trace.equals("null"))
            return e.toString();
        else
            return trace;
    }

    /*------------------------------------------------------------------------------------*/

    /** To exit properly.
     */
    static public void exit() {
        // We close our printStream ( can be a log so we do things properly )
        Debug.out.flush();
        System.exit(0);
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the PrintStream for this Debug Utility.
     *  @param out PrintStream to use.
     */
    static public void setPrintStream(PrintStream out) {
        Debug.out = out;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the PrintStream of this Debug Utility.
     *  @return out PrintStream we use.
     */
    static public PrintStream getPrintStream() {
        return Debug.out;
    }

    /*------------------------------------------------------------------------------------*/

    /** To flush the printStream.
     *  @param out PrintStream to use.
     */
    static public void flushPrintStream() {
        Debug.out.flush();
    }

    /*------------------------------------------------------------------------------------*/

}
