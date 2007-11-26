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


package org.alicebot.server.core.targeting;

    
import java.io.BufferedReader;
import java.lang.reflect.Field;

import org.alicebot.server.core.parser.GenericReader;
import org.alicebot.server.core.parser.GenericReader.TransitionMade;
import org.alicebot.server.core.util.DeveloperErrorException;
import org.alicebot.server.core.util.Trace;


/**
 *  Reads a targets data file.
 *  This version is based on {@link org.alicebot.server.core.parser.AIMLReader}.
 *  Obviously this and its companion {@link TargetsReaderListener}
 *  duplicate a lot from AIMLReader and AIMLLoader,
 *  so once this is stabilized these should all be combined.
 *
 *  @author Noel Bush
 */
public class TargetsReader extends GenericReader
{
    // Convenience constants.

    /** The string &quot;matchPattern&quot;. */
    private static final String MATCH_PATTERN       = "matchPattern";

    /** The string &quot;matchThat&quot;. */
    private static final String MATCH_THAT          = "matchThat";

    /** The string &quot;matchTopic&quot;. */
    private static final String MATCH_TOPIC         = "matchTopic";

    /** The string &quot;matchTemplate&quot;. */
    private static final String MATCH_TEMPLATE      = "matchTemplate";

    /** The string &quot;inputText&quot;. */
    private static final String INPUT_TEXT          = "inputText";

    /** The string &quot;inputThat&quot;. */
    private static final String INPUT_THAT          = "inputThat";

    /** The string &quot;inputTopic&quot;. */
    private static final String INPUT_TOPIC         = "inputTopic";

    /** The string &quot;extensionPattern&quot;. */
    private static final String EXTENSION_PATTERN   = "extensionPattern";

    /** The string &quot;extensionThat&quot;. */
    private static final String EXTENSION_THAT      = "extensionThat";

    /** The string &quot;extensionTopic&quot;. */
    private static final String EXTENSION_TOPIC     = "extensionTopic";

    /** The string &quot;extensionTemplate&quot;. */
    private static final String EXTENSION_TEMPLATE  = "extensionTemplate";


    /*
        Parser states.
    */

    /** Parser state: not within any element. */
    private final int S_NONE                        = 1;

    /** Parser state: entered a &lt;targets&gt; element. */
    private final int S_IN_TARGETS                  = 2;

    /** Parser state: entered a &lt;target&gt; element. */
    private final int S_IN_TARGET                   = 3;

    /** Parser state: entered an &lt;input&gt; element. */
    private final int S_IN_INPUT                    = 4;

    /** Parser state: entered a &lt;pattern&gt; element. */
    private final int S_IN_PATTERN                  = 5;

    /** Parser state: entered a &lt;template&gt; element. */
    private final int S_IN_TEMPLATE                 = 6;

    /** Parser state: exited a &lt;template&gt; element. */
    private final int S_OUT_TEMPLATE                = 7;

    /** Parser state: exited a &lt;pattern&gt; element. */
    private final int S_OUT_PATTERN                 = 8;

    /** Parser state: entered a &lt;that&gt; element. */
    private final int S_IN_THAT                     = 9;

    /** Parser state: exited a &lt;that&gt; element. */
    private final int S_OUT_THAT                    = 10;

    /** Parser state: entered a &lt;topic&gt; element. */
    private final int S_IN_TOPIC                    = 11;

    /** Parser state: exited a &lt;template&gt; element. */
    private final int S_OUT_TOPIC                   = 12;

    /** Parser state: exited an &lt;input&gt; element. */
    private final int S_OUT_INPUT                   = 13;

    /** Parser state: entered a &lt;match&gt; element. */
    private final int S_IN_MATCH                    = 14;

    /** Parser state: exited a &lt;match&gt; element. */
    private final int S_OUT_MATCH                   = 15;

    /** Parser state: entered an &lt;extension&gt; element. */
    private final int S_IN_EXTENSION                = 16;

    /** Parser state: exited an &lt;extension&gt; element. */
    private final int S_OUT_EXTENSION               = 17;

    /** Parser state: exited a &lt;target&gt; element. */
    private final int S_OUT_TARGET                  = 18;


    /*
        Parser actions.
    */

    /** Parser action: set input context. */
    private final int SET_INPUT_CONTEXT             = 0;

    /** Parser action: set match context. */
    private final int SET_MATCH_CONTEXT             = 1;

    /** Parser action: set extension context. */
    private final int SET_EXTENSION_CONTEXT         = 2;

    /** Parser action: deliver a category. */
    private final int DELIVER_TARGET                = 3;

    /** Parser action: set done to true. */
    private final int SET_DONE                      = 4;

    /** Parser action: abort unexpectedly. */
    private final int ABORT                         = 5;


    /*
        Instance variables.
    */

    /** The most recently collected match &lt;pattern&gt;&lt;/pattern&gt; contents. */
    public String                 matchPattern       = ASTERISK;

    /** The most recently collected match &lt;that&gt;&lt;/that&gt; contents. */
    public String                 matchThat          = ASTERISK;

    /** The most recently collected match &lt;topic&gt;&lt;/topic&gt; contents. */
    public String                 matchTopic         = ASTERISK;

    /** The most recently collected match &lt;template&gt;&lt;/template&gt; contents. */
    public String                 matchTemplate      = EMPTY_STRING;

    /** The most recently collected input text (&lt;pattern&gt;&lt;/pattern&gt;) contents. */
    public String                 inputText          = ASTERISK;

    /** The most recently collected input &lt;that&gt;&lt;/that&gt; contents. */
    public String                 inputThat          = ASTERISK;

    /** The most recently collected input &lt;topic&gt;&lt;/topic&gt; contents. */
    public String                 inputTopic         = ASTERISK;

    /** The most recently collected extension &lt;pattern&gt;&lt;/pattern&gt; contents. */
    public String                 extensionPattern   = ASTERISK;

    /** The most recently collected extension &lt;that&gt;&lt;/that&gt; contents. */
    public String                 extensionThat      = ASTERISK;

    /** The most recently collected extension &lt;topic&gt;&lt;/topic&gt; contents. */
    public String                 extensionTopic     = ASTERISK;

    /** The most recently collected extension &lt;template&gt;&lt;/template&gt; contents. */
    public String                 extensionTemplate  = EMPTY_STRING;

    /** A reference to some pattern field. */
    public Field                  patternField;

    /** A reference to some that field. */
    public Field                  thatField;

    /** A reference to some topic field. */
    public Field                  topicField;

    /** A reference to some template field. */
    public Field                  templateField;


    public TargetsReader(String fileName, BufferedReader buffReader, TargetsReaderListener targetsListener)
    {
        super(fileName, buffReader, targetsListener);
        super.readerInstance = this;
        state = S_NONE;
    }


    protected void initialize()
    {
        try
        {
            this.patternField = this.getClass().getDeclaredField(INPUT_TEXT);
            this.thatField = this.getClass().getDeclaredField(INPUT_THAT);
            this.topicField = this.getClass().getDeclaredField(INPUT_TOPIC);
            this.templateField = null;
        }
        catch (NoSuchFieldException e)
        {
            throw new DeveloperErrorException("The developer has specified a field that does not exist in TargetsReader.");
        }
        catch (SecurityException e)
        {
            throw new DeveloperErrorException("Security manager prevents TargetsReader from functioning.");
        }
    }


    protected void tryStates() throws TransitionMade
    {
        switch (state)
        {
            case S_NONE :
                transition(Targeting.TARGETS_START, S_IN_TARGETS);
                break;

            case S_IN_TARGETS :
                transition(Targeting.TARGET_START, S_IN_TARGET);
                break;

            case S_IN_TARGET :
                transition(Targeting.INPUT_START, S_IN_INPUT, SET_INPUT_CONTEXT);
                transition(Targeting.MATCH_START, S_IN_MATCH, SET_MATCH_CONTEXT);
                transition(Targeting.EXTENSION_START, S_IN_EXTENSION, SET_EXTENSION_CONTEXT);
                transition(Targeting.TARGET_END, S_OUT_TARGET, DELIVER_TARGET);
                break;

            case S_IN_INPUT :
            case S_IN_MATCH :
            case S_IN_EXTENSION :
                transition(Targeting.PATTERN_START, S_IN_PATTERN);
                break;

            case S_IN_PATTERN :
                transition(Targeting.PATTERN_END, S_OUT_PATTERN, patternField);
                break;

            case S_OUT_PATTERN :
                transition(Targeting.THAT_START, S_IN_THAT);
                break;

            case S_IN_THAT :
                transition(Targeting.THAT_END, S_OUT_THAT, thatField);
                break;

            case S_OUT_THAT :
                transition(Targeting.TOPIC_START, S_IN_TOPIC);
                break;

            case S_IN_TOPIC :
                transition(Targeting.TOPIC_END, S_OUT_TOPIC, topicField);
                break;

            case S_OUT_TOPIC :
                transition(Targeting.INPUT_END, S_IN_TARGET);
                transition(Targeting.TEMPLATE_START, S_IN_TEMPLATE);
                transition(Targeting.EXTENSION_END, S_IN_TARGET);
                break;

            case S_IN_TEMPLATE :
                transition(Targeting.TEMPLATE_END, S_OUT_TEMPLATE, templateField);
                break;

            case S_OUT_TEMPLATE :
                transition(Targeting.MATCH_END, S_IN_TARGET);
                break;

            case S_OUT_TARGET :
                transition(Targeting.TARGET_START, S_IN_TARGET);
                transition(Targeting.TARGETS_END, SET_DONE);
                break;

            default :
                break;
        }
    }


    /**
     *  <p>
     *  If {@link #bufferString} contains
     *  <code>tag</code> at {@link #tagStart},
     *  sets {@link state} to <code>toState</code>,
     *  and performs the action indicated by <code>action</code>.
     *  </p>
     *  <p>
     *  If <code>action</code> is {@link #SET_INPUT_CONTEXT},
     *  sets {@link #patternField}, {@link #thatField} and {@link #topicField}
     *  to the appropriate field references for the input part of the target.
     *  </p>
     *  <p>
     *  If <code>action</code> is {@link #SET_MATCH_CONTEXT},
     *  sets {@link #patternField}, {@link #thatField}, {@link #topicField} and {@link #templateField}
     *  to the appropriate field references for the match part of the target.
     *  </p>
     *  <p>
     *  If <code>action</code> is {@link #SET_EXTENSION_CONTEXT},
     *  sets {@link #patternField}, {@link #thatField} and {@link #topicField}
     *  to the appropriate field references for the extension part of the target.
     *  </p>
     *  <p>
     *  If <code>action</code> is {@link #DELIVER_TARGET}, calls the
     *  {@link Targets.add} method of the <code>targetsListener</code>.
     *  </p>
     *  <p>
     *  If <code>action</code> is {@link #DELIVER_TARGET}, calls the
     *  {@link Targets.add} method of the <code>targetsListener</code>.
     *  </p>
     *  <p>
     *  If <code>action</code> is {@link #SET_DONE}, sets {@link #done} to
     *  <code>true</code>, so that parsing of this file is halted (no message given).
     *  </p>
     *
     *  @param tag          the tag to look for in {@link #buffer}
     *  @param toState      the parser {@link #state} to assign if successful
     *  @param action       one of {{{@link #SET_INPUT_CONTEXT}, {@link #SET_MATCH_CONTEXT},
     *                      {@link #SET_EXTENSION_CONTEXT}, {@link #DELIVER_TARGET}, {@link #SET_DONE}}}.
     *
     *  @throws TransitionMade if the transition is successfully made
     */
    private void transition(String tag, int toState, int action) throws TransitionMade
    {
        if (succeed(tag, toState))
        {
            switch (action)
            {
                case DELIVER_TARGET :
                    // Deliver new target to targetsListener.
                    ((TargetsReaderListener)super.listener).loadTarget(matchPattern,
                                                                       matchThat,
                                                                       matchTopic,
                                                                       matchTemplate,
                                                                       inputText,
                                                                       inputThat,
                                                                       inputTopic,
                                                                       extensionPattern,
                                                                       extensionThat,
                                                                       extensionTopic);

                    // Reset all fields to defaults.
                    matchPattern = matchThat = matchTopic =
                        inputText = inputThat = inputTopic =
                        extensionPattern = extensionThat = extensionTopic = ASTERISK;
                    matchTemplate = extensionTemplate = EMPTY_STRING;
                    searchStart = 0;

                    break;

                case SET_DONE :
                    done = true;
                    break;

                case SET_INPUT_CONTEXT :
                    try
                    {
                        this.patternField = this.getClass().getDeclaredField(INPUT_TEXT);
                        this.thatField = this.getClass().getDeclaredField(INPUT_THAT);
                        this.topicField = this.getClass().getDeclaredField(INPUT_TOPIC);
                        this.templateField = null;
                    }
                    catch (NoSuchFieldException e)
                    {
                        throw new DeveloperErrorException("The developer has specified a field that does not exist in TargetsReader.");
                    }
                    catch (SecurityException e)
                    {
                        throw new DeveloperErrorException("Security manager prevents TargetsReader from functioning.");
                    }
                    break;

                case SET_MATCH_CONTEXT :
                    try
                    {
                        this.patternField = this.getClass().getDeclaredField(MATCH_PATTERN);
                        this.thatField = this.getClass().getDeclaredField(MATCH_THAT);
                        this.topicField = this.getClass().getDeclaredField(MATCH_TOPIC);
                        this.templateField = this.getClass().getDeclaredField(MATCH_TEMPLATE);
                    }
                    catch (NoSuchFieldException e)
                    {
                        throw new DeveloperErrorException("The developer has specified a field that does not exist in TargetsReader.");
                    }
                    catch (SecurityException e)
                    {
                        throw new DeveloperErrorException("Security manager prevents TargetsReader from functioning.");
                    }
                    break;

                case SET_EXTENSION_CONTEXT :
                    try
                    {
                        this.patternField = this.getClass().getDeclaredField(EXTENSION_PATTERN);
                        this.thatField = this.getClass().getDeclaredField(EXTENSION_THAT);
                        this.topicField = this.getClass().getDeclaredField(EXTENSION_TOPIC);
                        this.templateField = this.getClass().getDeclaredField(EXTENSION_TEMPLATE);
                    }
                    catch (NoSuchFieldException e)
                    {
                        throw new DeveloperErrorException("The developer has specified a field that does not exist in TargetsReader.");
                    }
                    catch (SecurityException e)
                    {
                        throw new DeveloperErrorException("Security manager prevents TargetsReader from functioning.");
                    }
                    break;

            }
            throw(super.TRANSITION_MADE);
        }
    }
}
