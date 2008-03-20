/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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
package wotlas.common.action;

import wotlas.common.action.spell.Spell;
import wotlas.common.character.CharData;
import wotlas.common.screenobject.ScreenObject;

/**
 *
 * @author  Diego
 */
public class CastAction extends UserAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1026246019652545781L;
    static public final int CAST_ADMIN_SUMMON = 0;
    static public final int CAST_ADMIN_CREATE = 1;
    static public final int CAST_ADMIN_SUMMON2 = 2;
    static public final int CAST_ADMIN_SUMMON3 = 3;
    static public final int CAST_ADMIN_SUMMON4 = 4;
    static public final int CAST_ADMIN_PLASMA = 5;
    static public final int CAST_TIME_STOP = 6;
    static public final int CAST_TIME_ANCHOR = 7;
    static public final int CAST_COMEHERE = 8;
    static public final int CAST_LAST_CAST = 9;

    static protected CastAction[] castActions;

    protected int manaCost;
    protected int minimumLevel;
    protected Spell spell;

    public CastAction(int id, String name, String description, byte maskTarget, byte targetRange, int manaCost, int minimumLevel) {
	this();
	init(id, name, description, maskTarget, targetRange, manaCost, minimumLevel, null);
    }

    public CastAction() {
	super();
    }

    protected void init(int id, String name, String description, byte maskTarget, byte targetRange, int manaCost, int minimumLevel,
            Spell spell) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.maskTarget = maskTarget;
        this.targetRange = targetRange;
        this.manaCost = manaCost;
        this.minimumLevel = minimumLevel;
        this.ostileAction = false;
        this.effectRange = UserAction.EFFECT_RANGE_NONE;
        this.maskInform = 0;
        this.spell = spell;
    }

    public Spell getSpell() {
	return this.spell;
    }

    public void setSpell(Spell spell) {
	this.spell = spell;
    }

    @Override
    public boolean CanExecute(ScreenObject user, byte targetType, byte range) {
        // da attivare ma non ora....
        //    if( user.getCharData() == null )
        //            return false;

        // i can cast this ID? : use chardata to check it.....

        // i have mana? : use chardata to check it.....

        // valid target?
        return isValidTarget(targetType, range);
    }

    /* -------------static functions--------------------------------------- */
    static public void InitCastActions(boolean loadByServer) {
        if (CastAction.castActions != null)
            return;
        Spell.loadByServer = loadByServer;

        CastAction.castActions = new CastAction[CastAction.CAST_LAST_CAST];

	CastAction.castActions[CastAction.CAST_ADMIN_SUMMON] = new CastAction(CastAction.CAST_ADMIN_SUMMON, "Summon monster",
		"Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_ADMIN_CREATE] = new CastAction(CastAction.CAST_ADMIN_CREATE, "Create item",
		"Create item anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_ADMIN_SUMMON2] = new CastAction(CastAction.CAST_ADMIN_SUMMON2, "Summon monster dwarf king",
		"Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_ADMIN_SUMMON3] = new CastAction(CastAction.CAST_ADMIN_SUMMON3, "Summon dwarf cleric",
		"Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_ADMIN_SUMMON4] = new CastAction(CastAction.CAST_ADMIN_SUMMON4, "Summon dwarf wizard",
		"Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_ADMIN_PLASMA] = new CastAction(CastAction.CAST_ADMIN_PLASMA, "plasma bolt",
		"Cast plasma bolt to harm target!", new Integer((1 << UserAction.TARGET_TYPE_NPC) + (1 << UserAction.TARGET_TYPE_PLAYER))
			.byteValue(), UserAction.TARGET_RANGE_SAME_MAP, 0, 0); // image , damage

	CastAction.castActions[CastAction.CAST_TIME_STOP] = new CastAction(CastAction.CAST_TIME_STOP, "Time Stop",
		"Stop the world's time!", (byte) (1 << UserAction.TARGET_TYPE_SELF), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_TIME_ANCHOR] = new CastAction(CastAction.CAST_TIME_ANCHOR, "Time Anchor",
		"Psionicist power, prevent the psionicist to be moved or" + "  holded in time.", (byte) (1 << UserAction.TARGET_TYPE_SELF),
		UserAction.TARGET_RANGE_SAME_MAP, 0, 0);

	CastAction.castActions[CastAction.CAST_COMEHERE] = new CastAction(CastAction.CAST_COMEHERE, "Command",
		"Come Here! and it comes.....", (byte) (1 << UserAction.TARGET_TYPE_NPC), UserAction.TARGET_RANGE_SAME_MAP, 0, 0);
    }

    static public CastAction getCastAction(int id) {
        return CastAction.castActions[id];
    }

    /** used by server execute the action
     *
     */
    public void ExecuteToMap(CharData caster, int casterX, int casterY, int targetX, int targetY) {
        this.spell.CastToMap(caster, casterX, casterY, targetX, targetY);
    }

    public void ExecuteToTarget(CharData caster, CharData target) {
        // if action is ostile should set the attack of the 
        // target and set WHO start it : he and his group
        this.spell.CastToTarget(caster, target);
    }

    public void ExecuteToSelf(CharData caster) {
        // if action is ostile should set the attack of the 
        // target and set WHO start it : he and his group
        this.spell.CastToSelf(caster);
    }
}