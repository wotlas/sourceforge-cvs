/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wotlas.common;

import java.util.Random;

/**
 * 
 * @author Olivier
 */
public class DiceRollerManager {
    /* - - - - - - - - ROLL DICE SECTION - - - - - - - - - - - - - - - - - */

    static private Random Dice;
    static private boolean needInit = true;

    static public final short roll(int dices, int diceSize) {
	short value = 0;
	for (int i = 0; i < dices; i++) {
	    value += new Double(1 + DiceRollerManager.Dice.nextDouble() * diceSize).shortValue();
	    ;
	}
	return value;
    }

    static public void initRoll() {
	if (!DiceRollerManager.needInit) {
	    return;
	}
	DiceRollerManager.Dice = new Random(System.currentTimeMillis());
	DiceRollerManager.needInit = false;
    }
}
