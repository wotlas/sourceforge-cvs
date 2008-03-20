/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wotlas.server.action;

import wotlas.common.action.CastAction;
import wotlas.common.action.UserAction;
import wotlas.server.action.spell.Create;
import wotlas.server.action.spell.MoveHere;
import wotlas.server.action.spell.Plasma;
import wotlas.server.action.spell.Summon;
import wotlas.server.action.spell.TimeAnchor;
import wotlas.server.action.spell.TimeStop;

/**
 * 
 * @author Olivier
 */
public class ServerCastAction extends CastAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8721438025214114850L;
    public CastAction clientAction;

    public ServerCastAction(CastAction ca) {
        super();
        init(ca);
    }

    private void init(CastAction ca) {
        this.clientAction = ca;
        switch (ca.getId()) {
            case CastAction.CAST_ADMIN_SUMMON:
                init(CastAction.CAST_ADMIN_SUMMON, "Summon monster", "Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new Summon("dwarf berserk"));
                break;

            case CastAction.CAST_ADMIN_CREATE:
                init(CastAction.CAST_ADMIN_CREATE, "Create item", "Create item anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new Create("oggdef1-name"));
                break;

            case CastAction.CAST_ADMIN_SUMMON2:
                init(CastAction.CAST_ADMIN_SUMMON2, "Summon monster dwarf king", "Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new Summon("dwarf king"));
                break;

            case CastAction.CAST_ADMIN_SUMMON3:
                init(CastAction.CAST_ADMIN_SUMMON3, "Summon dwarf cleric", "Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new Summon("dwarf cleric"));
                break;

            case CastAction.CAST_ADMIN_SUMMON4:
                init(CastAction.CAST_ADMIN_SUMMON4, "Summon dwarf wizard", "Summon any monster anywhere!", (byte) (1 << UserAction.TARGET_TYPE_GROUND), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new Summon("dwarf wizard"));
                break;

            case CastAction.CAST_ADMIN_PLASMA:
                init(CastAction.CAST_ADMIN_PLASMA, "plasma bolt", "Cast plasma bolt to harm target!", new Integer((1 << UserAction.TARGET_TYPE_NPC) + (1 << UserAction.TARGET_TYPE_PLAYER)).byteValue(), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new Plasma(51, 30)); // image , damage
                break;

            case CastAction.CAST_TIME_STOP:
                init(CastAction.CAST_TIME_STOP, "Time Stop", "Stop the world's time!", (byte) (1 << UserAction.TARGET_TYPE_SELF), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new TimeStop());
                break;

            case CastAction.CAST_TIME_ANCHOR:
                init(CastAction.CAST_TIME_ANCHOR, "Time Anchor", "Psionicist power, prevent the psionicist to be moved or" + "  holded in time.", (byte) (1 << UserAction.TARGET_TYPE_SELF), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new TimeAnchor());
                break;

            case CastAction.CAST_COMEHERE:
                init(CastAction.CAST_COMEHERE, "Command", "Come Here! and it comes.....", (byte) (1 << UserAction.TARGET_TYPE_NPC), UserAction.TARGET_RANGE_SAME_MAP, 0, 0, new MoveHere());
                break;
        }
    }

}
