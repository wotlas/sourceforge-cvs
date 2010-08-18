/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wotlas.libs.wizard;

import wotlas.utils.WishGameExtension;

/**
 * Interfaces used by JWizard
 * @author SleepingOwl
 */
public interface WishWizardClassFactory extends WishGameExtension {

    public JWizardStep newWizardStep(JWizardStepParameters params);
}
