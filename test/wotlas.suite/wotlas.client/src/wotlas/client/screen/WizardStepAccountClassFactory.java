/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wotlas.client.screen;

import java.util.logging.Level;
import java.util.logging.Logger;
import wotlas.libs.wizard.JWizardStep;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.libs.wizard.WishWizardClassFactory;

/**
 *
 *@author SleepingOwl
 */
public class WizardStepAccountClassFactory implements WishWizardClassFactory {

    /*------------------------------------------------------------------------------------*/
    public JWizardStep newWizardStep(JWizardStepParameters params) {
        try {
            Class cl = Class.forName(params.getStepClass());
            JWizardStep step = (JWizardStep) cl.newInstance();
            return step;
        } catch (InstantiationException ex) {
            Logger.getLogger(JAccountCreationWizard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JAccountCreationWizard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JAccountCreationWizard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
