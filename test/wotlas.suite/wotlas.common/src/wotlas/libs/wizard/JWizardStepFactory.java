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
package wotlas.libs.wizard;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import wotlas.libs.persistence.PropertiesConverter;
import wotlas.utils.Debug;
import wotlas.utils.Tools;
import wotlas.utils.WotlasGameDefinition;

/** A factory that builds, initializes and stores JWizardStep objects. It is used by
 *  the JWizard class.<br>
 *
 *  Note that this factory is NOT thread safe. Only ONE JWizard should use it at the
 *  time.
 *
 * @author Aldiss
 */
public class JWizardStepFactory {

    /*------------------------------------------------------------------------------------*/
    /** Our JWizardStep Buffer where we store static JWizard steps.
     *  The hashtable's key is the JWizardStep java class 'stringified'.
     */
    private Hashtable<String, JWizardStep> staticSteps;

    private WotlasGameDefinition gameDefinition;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     */
    public JWizardStepFactory(WotlasGameDefinition wgd) {
        this.staticSteps = new Hashtable<String, JWizardStep>(10);
        this.gameDefinition = wgd;
    }

    /*------------------------------------------------------------------------------------*/
    /** To clear the factory's buffer.
     */
    public void clear() {
        this.staticSteps.clear();
    }

    /*------------------------------------------------------------------------------------*/
    /** To get an instance of a static JWizardStep. This is a simple & fast method to get a
     *  JWizardStep that only contains well-defined data fields. If you need to get
     *  a JWizardStep that contains dynamic data use the other getJWizardStep method.<br>
     *
     *  IMPORTANT : because we don't use any JWizardStepParameters class here, it's your
     *  own job to set all the parameters : title, properties, isLastStep, etc... and
     *  it's mandatory ! The JWizardStep returned could contain previous unwanted data.<br>
     *
     *  ADVICE : only use this method to build simple wizards. Prefer the other available
     *  getJWizardStep() method.
     *
     *  @param stepClass JWizardStep class to get an instance from.
     *  @return the wanted JWizardStep instance, null if we failed to retrieve/build it.
     */
    public JWizardStep getJWizardStep(String stepClass) {

        // Class already in our buffer ?
        if (this.staticSteps.containsKey(stepClass)) {
            return this.staticSteps.get(stepClass);
        }

        // We create, store and return a new instance
        try {
            JWizardStepParameters params = new JWizardStepParameters(stepClass, " ");
            JWizardStep step = null;

            // Lookup : find classFactory impl.
            try {
                Object[] classesFactories = Tools.getImplementorsOf(WishWizardClassFactory.class, getGameDefinition());
                for (int i = 0; i < classesFactories.length && step == null; i++) {
                    WishWizardClassFactory classFactory = (WishWizardClassFactory) classesFactories[i];
                    step = classFactory.newWizardStep(params);
                }
            } catch (ClassNotFoundException cnfe) {
                // FIXME Problem.
                cnfe.printStackTrace();
            }
            if (step == null) {
                // Class.forname can't do the work : netbeans classloader hinders it.
                Class myStepClass = Class.forName(stepClass);
                step = (JWizardStep) myStepClass.newInstance();
            }

            step.init(params);
            this.staticSteps.put(stepClass, step);
            return step;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @return the gameDefinition
     */
    public WotlasGameDefinition getGameDefinition() {
        return this.gameDefinition;
    }

    /*------------------------------------------------------------------------------------*/
    /** To get an instance of a JWizardStep according to its parameters. The returned
     *  JWizardStep is fully initialized.
     *
     *  @param parameters contains all the needed parameters to initialize/build the wanted
     *         instance.
     *  @return the wanted JWizardStep instance, null if we failed to retrieve/build it.
     */
    public JWizardStep getJWizardStep(JWizardStepParameters parameters) {

        // Class already in our buffer ?
        if (!parameters.getIsDynamic() && this.staticSteps.containsKey(parameters.getStepClass())) {
            JWizardStep step = this.staticSteps.get(parameters.getStepClass());

            try {
                step.init(parameters);
                return step;
            } catch (Exception ex) {
                // FIXME 
                ex.printStackTrace();
                return null;
            }
        }

        // We create, eventually store and return a new instance
        try {
            JWizardStep step = null;

            // Lookup : find classFactory impl.
            try {
                Object[] classesFactories = Tools.getImplementorsOf(WishWizardClassFactory.class, getGameDefinition());
                for (int i = 0; i < classesFactories.length && step == null; i++) {
                    WishWizardClassFactory classFactory = (WishWizardClassFactory) classesFactories[i];
                    step = classFactory.newWizardStep(parameters);
                }
            } catch (ClassNotFoundException cnfe) {
                // FIXME Problem.
                cnfe.printStackTrace();
            }
            if (step == null) {
                // Class.forname can't do the work : netbeans classloader hinders it.
                Class myStepClass = Class.forName(parameters.getStepClass());
                step = (JWizardStep) myStepClass.newInstance();
            }

            step.init(parameters);

            if (!parameters.getIsDynamic()) {
                this.staticSteps.put(parameters.getStepClass(), step);
            }

            return step;
        } catch (Exception ex) {
            // FIXME 
            ex.printStackTrace();
            return null;
        }
    }

    /*------------------------------------------------------------------------------------*/
    /** To get an instance of a JWizardStep according to its parameters. The parameters
     *  are taken from a file which is given as parameter. We load the file using the
     *  JWizardStepParameters.loadFromFile() method. We then try to create the JWizardStep
     *  using these parameters. The returned JWizardStep is fully initialized.
     *
     *  @param parametersFile the JWizardStepParameters file to load.
     *  @return the wanted JWizardStep instance, null if we failed to retrieve/build it.
     */
    public JWizardStep getJWizardStepFromFile(String parametersFile) {

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(parametersFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JWizardStepParameters parameters = JWizardStepFactory.loadJWizardStepParameters(fis, getGameDefinition());

        if (parameters == null) {
            return null;
        } // load failed

        return getJWizardStep(parameters);
    }

    /*------------------------------------------------------------------------------------*/

    /** To load JWizardStepParameters from a file. This method uses the Wotlas
     *  Persistent Library and the Wotlas Debug utility.
     *
     * @param path full path to the file containing the JWizardStepParameters.
     * @return null if the file could not be loaded.
     */
    public static final JWizardStepParameters loadJWizardStepParameters(InputStream istream, WotlasGameDefinition wgd) {
        if (istream == null)
            return null;

        try {
            return (JWizardStepParameters) PropertiesConverter.load(istream, wgd);
        } catch (Exception pe) {
            Debug.signal(Debug.ERROR, null, "Failed to load file: " + pe.getMessage());
            return null;
        }
    }

    /*------------------------------------------------------------------------------------*/

}
