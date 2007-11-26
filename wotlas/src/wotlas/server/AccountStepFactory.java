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

package wotlas.server;

import java.io.File;
import java.util.Hashtable;
import wotlas.common.ResourceManager;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.utils.Debug;

/** A factory that stores and builds the JWizardStepParameters objects. It is used by
 *  the AccountBuilder.<br>
 *
 *  This factory is thread safe.
 *
 * @author Aldiss
 */

public class AccountStepFactory {

    /*------------------------------------------------------------------------------------*/

    /** Suffix of the wizard files.
     */
    public static final String WIZARD_SUFFIX = ".wiz";

    /** Name of the first step to be started by the AccountBuilder. This step must always
     *  exist.
     */
    public static final String FIRST_STEP = "index.wiz";

    /*------------------------------------------------------------------------------------*/

    /** Our JWizardStep Buffer where we store static JWizard steps.
     *  The hashtable's key is the file name that contains the persistent version
     *  of the JWizardStepParameters.
     */
    private Hashtable staticStepParameters;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. We load the wizard steps.
     *
     * @param rManager our resource manager
     */
    public AccountStepFactory(ResourceManager rManager) {
        this.staticStepParameters = new Hashtable(20);

        String accountWizardHome = rManager.getWizardStepsDir();

        // We load all the step parameters in our hashtable
        String list[] = rManager.listFiles(accountWizardHome, AccountStepFactory.WIZARD_SUFFIX);

        int nbSteps = 0;

        for (int i = 0; i < list.length; i++) {

            JWizardStepParameters parameters = JWizardStepParameters.loadFromStream(rManager.getFileStream(list[i]));

            if (parameters == null) {
                Debug.signal(Debug.ERROR, this, "Failed to load wizard step " + list[i]);
                continue;
            }

            String name = list[i];
            int index = list[i].lastIndexOf(File.separator);

            if (index < 0) {
                index = list[i].lastIndexOf("/");

                if (index > 0)
                    name = name.substring(index + 1, name.length());
            } else
                name = name.substring(index + File.separator.length(), name.length());

            this.staticStepParameters.put(name, parameters);
            nbSteps++;
        }

        Debug.signal(Debug.NOTICE, null, "Loaded " + nbSteps + " account wizard steps...");
    }

    /*------------------------------------------------------------------------------------*/

    /** To get a JWizardStepParameters object from the file name of its persistent state.
     *
     *  @param stepFileName file name
     *  @return the wanted JWizardStepParameters instance, null if we failed to retrieve it.
     */
    public JWizardStepParameters getStep(String stepFileName) {

        // Class already in our buffer ?
        if (this.staticStepParameters.containsKey(stepFileName))
            return (JWizardStepParameters) this.staticStepParameters.get(stepFileName);

        return null; // not found
    }

    /*------------------------------------------------------------------------------------*/

}
