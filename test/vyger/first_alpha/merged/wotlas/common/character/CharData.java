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

package wotlas.common.character;

import wotlas.common.screenobject.ScreenObject;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.persistence.BackupReady;
import wotlas.utils.MaskTools;

/** Interface for attributes / skill
 *
 * @author Diego
 *
 *
 * this should be used by npc +/-
 * this means every stat[][] should be some like:
 * stat[2][attributes]
 * sta[0] = npc_Def
 * stat[1] = npc 
 *
 *
 *
 *
 *
 * Let's make some example on how data should be used:
 *
 * . 0 should usually means data is not set (es for skills)
 * . how can use mask for attributes?
 * if an attributes it's masked as false, it cant be manipulated
 * and/or showed, this means zombie could not have int, and it
 * cant be manipulated (for example), and it means
 * WotCharacter can add new stat attributes, without adding 'em to RLikeChar
 *
 */

public abstract class CharData implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /* index for array of data [x][2] */
    final static public int IDX_MAX = 0;
    final static public int IDX_ACTUAL = 1;
    final static public int IDX_SIZE = 2;

    /* list of all the classes */
    /* wheel of time classes */
    final static public short CLASSES_WOT_AES_SEDAI = 1;
    final static public short CLASSES_WOT_WARDER = 2;
    final static public short CLASSES_WOT_CHILDREN_OF_THE_LIGHT = 3;
    final static public short CLASSES_WOT_ASHAMAN = 4;
    final static public short CLASSES_WOT_DARK_ONE = 5;
    final static public short CLASSES_WOT_WOLF_BROTHER = 6;
    final static public short CLASSES_WOT_AIEL_WARRIOR = 7;
    /* rogue like race */
    final static public short CLASSES_RL_HUMAN = 8;
    final static public short CLASSES_RL_HALFELF = 9;
    final static public short CLASSES_RL_ELF = 10;
    final static public short CLASSES_RL_HALFLING = 11;
    final static public short CLASSES_RL_DWARF = 12;
    final static public short CLASSES_RL_ORC = 13;
    final static public short CLASSES_RL_HALFORC = 14;
    final static public short CLASSES_RL_GNOME = 15;
    final static public short CLASSES_RL_DROW = 16;
    final static public short CLASSES_RL_GOBLIN = 17;
    /* rogue like class */
    final static public short CLASSES_RL_MONK = 18;
    final static public short CLASSES_RL_PSIONIST = 19;
    final static public short CLASSES_RL_WIZARD = 20;
    final static public short CLASSES_RL_WARRIOR = 21;
    final static public short CLASSES_RL_CLERIC = 22;
    final static public short CLASSES_RL_BARD = 23;
    final static public short CLASSES_RL_THIEF = 24;
    final static public short CLASSES_RL_RANGER = 25;
    /* used to check arrays */
    final static public short CLASSES_LAST_CLASS = 26;

    final static public String[] CLASSES_NAMES = { "NULL", "Wot:Aes Sedai", "Wot:Warder", "Wot:Children of the Light", "Wot:Ashaman", "Wot:Dark One", "Wot:Wolf Brother", "Wot:Aiel Warrior", "Rl: Human", "Rl: Half-Elf", "Rl: Elf", "Rl Halfling: Dwarf", "Rl: Orc", "Rl: Half-Orc", "Rl: Gnome", "Rl:Drow", "Rl: Goblin", "Rl: Monk", "Rl: Psionist", "Rl: Wizard", "Rl: Warrior", "Rl: Cleric", "Rl: Bard", "Rl: Thief", "Rl: Ranger" };

    /* spells and items, will point to this constant to maniupulate data flags*/
    final static public int FLAG_INVISIBLE = 0;
    final static public int FLAG_PARALIZED = 1;
    final static public int FLAG_TIME_ANCHOR = 2;
    final static public int FLAG_LAST_FLAG = 3;

    final static public String[] FLAG_NAMES = { "Invisible", "Paralized" };

    /* spells and items, will point to this constant to maniupulate data known*/
    final static public int KNOW_READ = 0;
    final static public int KNOW_WRITE = 1;
    final static public int KNOW_LAST_KNOWN = 2;

    /* spells and items, will point to this constant to maniupulate data skill*/
    final static public int SKILL_PICKLOCK = 0;
    final static public int SKILL_CRAFT_ARMOR = 1;
    final static public int SKILL_CRAFT_WEAPON = 2;
    final static public int SKILL_LAST_SKILL = 3;

    /* spells and items, will point to this constant to maniupulate data attr.*/
    final static public int ATTR_EXP = 0; // managed with special proc.
    final static public int ATTR_GOLD = 1; // managed with special proc.
    final static public int ATTR_CLASSES = 2; // managed with special proc.
    final static public int ATTR_LEVELS = 3; // managed with special proc.
    final static public int ATTR_HP = 4;
    final static public int ATTR_STR = 5;
    final static public int ATTR_INT = 6;
    final static public int ATTR_WIS = 7;
    final static public int ATTR_CON = 8;
    final static public int ATTR_DEX = 9;
    final static public int ATTR_CHA = 10;
    final static public int ATTR_MANA = 11;
    final static public int ATTR_GEMS1 = 12;
    final static public int ATTR_GEMS2 = 13;
    final static public int ATTR_GEMS3 = 14;
    final static public int ATTR_MOVEMENT = 15;
    final static public int ATTR_AGE = 16;
    final static public int ATTR_CHAR_WEIGHT = 17;
    final static public int ATTR_CHAR_HEIGHT = 18;
    final static public int ATTR_AC = 19;
    final static public int ATTR_THAC0 = 20;
    final static public int ATTR_HITROLL = 21; // special proc?
    final static public int ATTR_DAMROLL = 22; // special proc?
    final static public int ATTR_SAVINGMIND = 23;
    final static public int ATTR_SAVINGREFLEX = 24;
    final static public int ATTR_SAVINGCONST = 25;
    final static public int ATTR_HUNGER = 26;
    final static public int ATTR_THIRSTY = 27;
    final static public int ATTR_LAST_ATTR = 28;

    final static public String[] ATTR_NAMES = { "DONT USE", "DONT USE", "DONT USE", "DONT USE", "Hp", "Str", "Int", "Wis", "Con", "Dex", "Cha", "Mana", "DONT USE", "DONT USE", "DONT USE", "Mov", "Age", "Weight", "Height", "Ac", "Thac0", "Hit", "Dam", "Sav. Mind", "Sav. Reflex", "Sav. Const", "Hunger", "Thirsty" };

    /** character location
    */
    private WotlasLocation location;

    // public int[] maskCharAttributes;
    /**
    *   list of attribues of a playe/mob with [2] data
    *   [0]=max [1]=actual
    */
    private short[][] charAttributes;

    /** this hold the levels reached by a character :
     * levels[firstindex][secondindex] 
     * firstindex : 2 value, IDX_ACTUAL, ID_MAX  they arent the same if someone drains level from you
     * secondindex : 1 value for all the wot character, 'cause they have only a class
     *             up to 3 value for all the rougelike characters, that can have up to 3 classes
     */
    private byte[][] levels;

    /** hold the gold of a player
     *
     */
    private long gold;

    /**
     * hold the exp of a player : IDX_MAX,IDX_ACTUAL points to actual exp or maximum exp
     * the actual != max if someone drains exp from you
     */
    private long[] exp;

    /** this mask is done to hold information about classes and races
     * this player is.
     * any bit set, means the players is of that class/race
     *
     */
    private int[] maskCharClasses;

    /** this mask hold information about what skill a player CAN have
     * this means the system should check this mask BEFORE add a skill
     * it should be used by a TRAIN function before add the skill
     *
     */
    private int[] maskCharSkills;
    /**
    *   list of skills/knowledge with an int value:
    *   u have it with value of x>0 or u have not it, and it's 0.
    */
    private byte[] charSkills;

    /** this mask hold information about what KNOWLEDGE a player CAN have
     * this means the system should check this mask BEFORE add a KNOWLEDGE
     * it should be used by a TRAIN function before add the KNOWLEDGE
     *
     */
    private int[] maskCharKnownledge;
    /**
    *   list of skills/knowledge with a boolean value:
    *   u have it or u have not it.
    */
    private boolean[] charKnownledge;

    /**
    *    it's a list of status flags, like <invisible?>:
    *    if the bit is ste, the char is invisible
    *    managed like a maskXXXX
    */
    private int[] maskCharFlags;

    /** Player's primary key (usually the client account name)
    */
    private String primaryKey;

    transient private ScreenObject screenObject;
    transient private long delay = 0;
    transient private boolean attack = false;

    /* ------------------ VARIABLE FINISHED ---------------------------- */

    protected void InitCharData() {
        this.maskCharClasses = new int[1];
        // maskCharAttributes = new int[2];
        this.charAttributes = new short[CharData.IDX_SIZE][CharData.ATTR_LAST_ATTR];
        this.levels = new byte[CharData.IDX_SIZE][1];
        this.gold = 0;
        this.exp = new long[CharData.IDX_SIZE];
        this.maskCharSkills = new int[CharData.IDX_SIZE];
        this.charSkills = new byte[CharData.SKILL_LAST_SKILL];
        this.maskCharKnownledge = new int[CharData.IDX_SIZE];
        this.charKnownledge = new boolean[CharData.KNOW_LAST_KNOWN];
        this.maskCharFlags = new int[CharData.IDX_SIZE];
    }

    /**
    *   hold object in the backpack every mob/char have
    */
    //    public container BackPackInventory;
    /**
    *   hold object in the body every mob/char have
    */
    //     public container BodyInventory;
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns an image for this character/npc/mob, depending on the Map type.
    *
    *  @return image identifier of this character.
    */
    public abstract ImageIdentifier getImage();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */

    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeObject(this.primaryKey);
        objectOutput.writeObject(this.maskCharClasses);
        // objectOutput.writeObject( maskCharAttributes );
        objectOutput.writeObject(this.charAttributes);
        objectOutput.writeObject(this.levels);
        objectOutput.writeLong(this.gold);
        objectOutput.writeObject(this.exp);
        objectOutput.writeObject(this.maskCharSkills);
        objectOutput.writeObject(this.charSkills);
        objectOutput.writeObject(this.maskCharKnownledge);
        objectOutput.writeObject(this.charKnownledge);
        objectOutput.writeObject(this.maskCharFlags);
        objectOutput.writeObject(this.location);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.primaryKey = (String) objectInput.readObject();
            this.maskCharClasses = (int[]) objectInput.readObject();
            // maskCharAttributes = ( int[] ) objectInput.readObject();
            this.charAttributes = (short[][]) objectInput.readObject();
            this.levels = (byte[][]) objectInput.readObject();
            this.gold = objectInput.readLong();
            this.exp = (long[]) objectInput.readObject();
            this.maskCharSkills = (int[]) objectInput.readObject();
            this.charSkills = (byte[]) objectInput.readObject();
            this.maskCharKnownledge = (int[]) objectInput.readObject();
            this.charKnownledge = (boolean[]) objectInput.readObject();
            this.maskCharFlags = (int[]) objectInput.readObject();
            this.location = (WotlasLocation) objectInput.readObject();

            this.delay = 0;
            this.attack = true;
        } else {
            // to do.... when new version
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    public void writeObject(java.io.ObjectOutputStream objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeObject(this.primaryKey);
        objectOutput.writeObject(this.maskCharClasses);
        // objectOutput.writeObject( maskCharAttributes );
        objectOutput.writeObject(this.charAttributes);
        objectOutput.writeObject(this.levels);
        objectOutput.writeLong(this.gold);
        objectOutput.writeObject(this.exp);
        objectOutput.writeObject(this.maskCharSkills);
        objectOutput.writeObject(this.charSkills);
        objectOutput.writeObject(this.maskCharKnownledge);
        objectOutput.writeObject(this.charKnownledge);
        objectOutput.writeObject(this.maskCharFlags);
        objectOutput.writeObject(this.location);
    }

    public void readObject(java.io.ObjectInputStream objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.primaryKey = (String) objectInput.readObject();
            this.maskCharClasses = (int[]) objectInput.readObject();
            // maskCharAttributes = ( int[] ) objectInput.readObject();
            this.charAttributes = (short[][]) objectInput.readObject();
            this.levels = (byte[][]) objectInput.readObject();
            this.gold = objectInput.readLong();
            this.exp = (long[]) objectInput.readObject();
            this.maskCharSkills = (int[]) objectInput.readObject();
            this.charSkills = (byte[]) objectInput.readObject();
            this.maskCharKnownledge = (int[]) objectInput.readObject();
            this.charKnownledge = (boolean[]) objectInput.readObject();
            this.maskCharFlags = (int[]) objectInput.readObject();
            this.location = (WotlasLocation) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    /*  -------------- SHOW DATA (ATTRIBUTES) SECTION ------------------------------- */

    public String getCharAttrWihDescr(int index) {
        return CharData.ATTR_NAMES[index] + " " + this.charAttributes[CharData.IDX_ACTUAL][index];
    }

    /*  -------------- ATTRIBUTES SECTION ------------------------------- */

    public void setCharAttr(int index, int value) {
        if (index >= CharData.ATTR_LAST_ATTR) {
            System.out.println("Impossible to set this ATTR: it doez not exist!");
            return;
        }
        this.charAttributes[CharData.IDX_ACTUAL][index] = (short) value;
        this.charAttributes[CharData.IDX_MAX][index] = (short) value;
    }

    public void addCharAttr(int index, int value) {
        if (index >= CharData.ATTR_LAST_ATTR) {
            System.out.println("Impossible to set this ATTR: it doez not exist!");
            return;
        }
        this.charAttributes[CharData.IDX_ACTUAL][index] += (short) value;
        this.charAttributes[CharData.IDX_MAX][index] += (short) value;
    }

    public short getCharAttrActual(int index) {
        if (index >= CharData.ATTR_LAST_ATTR) {
            System.out.println("Impossible to set this ATTR: it doez not exist!");
            return -1;
        }
        return this.charAttributes[CharData.IDX_ACTUAL][index];
    }

    public short getCharAttrMax(int index) {
        if (index >= CharData.ATTR_LAST_ATTR) {
            System.out.println("Impossible to set this ATTR: it doez not exist!");
            return -1;
        }
        return this.charAttributes[CharData.IDX_MAX][index];
    }

    public void setCharAttrActualAdd(int index, int value) {
        if (index >= CharData.ATTR_LAST_ATTR) {
            System.out.println("Impossible to set this ATTR: it doez not exist!");
            return;
        }
        this.charAttributes[CharData.IDX_ACTUAL][index] += (short) value;
    }

    public void setCharAttrMaxAdd(int index, int value) {
        if (index >= CharData.ATTR_LAST_ATTR) {
            System.out.println("Impossible to set this ATTR: it doez not exist!");
            return;
        }
        this.charAttributes[CharData.IDX_MAX][index] += (short) value;
    }

    /*  -------------- CLASS SECTION ------------------------------- */

    public void setCharClass(int index) {
        if (index >= CharData.CLASSES_LAST_CLASS) {
            System.out.println("Impossible to set this class : it doez not exist!");
            return;
        }
        this.maskCharClasses = MaskTools.set(this.maskCharClasses, index);
    }

    /*  -------------- EXP SECTION ------------------------------- */

    public long getExpActual() {
        return this.exp[CharData.IDX_ACTUAL];
    }

    public void addExpActual(int value) {
        this.exp[CharData.IDX_ACTUAL] += value;
    }

    public void setExpActual(int value) {
        this.exp[CharData.IDX_ACTUAL] = value;
    }

    public long getExpMax() {
        return this.exp[CharData.IDX_MAX];
    }

    public void addExpMax(int value) {
        this.exp[CharData.IDX_MAX] += value;
    }

    public void setExpMax(int value) {
        this.exp[CharData.IDX_MAX] = value;
    }

    public long getExp() {
        return this.exp[CharData.IDX_ACTUAL];
    }

    public void addExp(int value) {
        this.exp[CharData.IDX_ACTUAL] += value;
        this.exp[CharData.IDX_MAX] += value;
    }

    public void setExp(int value) {
        this.exp[CharData.IDX_ACTUAL] = value;
        this.exp[CharData.IDX_MAX] = value;
    }

    /*  -------------- GOLD SECTION ------------------------------- */

    public long getGold() {
        return this.gold;
    }

    public void addGold(int value) {
        this.gold += value;
    }

    public void setGold(int value) {
        this.gold = value;
    }

    /*  -------------- LEVEL SECTION ------------------------------- */

    public void setLevel(int value) {
        this.levels[CharData.IDX_MAX][0] = (byte) value;
        this.levels[CharData.IDX_ACTUAL][0] = (byte) value;
    }

    public byte getLevel() {
        return this.levels[CharData.IDX_ACTUAL][0];
    }

    public void setLevel(int value, int index) {
        this.levels[CharData.IDX_MAX][index] = (byte) value;
        this.levels[CharData.IDX_ACTUAL][index] = (byte) value;
    }

    public byte getLevel(int index) {
        return this.levels[CharData.IDX_ACTUAL][index];
    }

    /** probably used only with RLIKE classes, it's used
     *  when a player is a human with dualclass warrior/wizard for example
     *  or it's a elf multiclass warrior/wizard/cleric for example
     *
     *  however there should be a limit : u cant have a player with 7 classes.
     *  The actual limit is 3 classes per character.
     *  So if adding the program see there is 3 classes it returns and index
     *  set to -1 to let you know there area already 3 classes.
     *
     * it return the index of the level of the added class
     */
    public byte addNewClassLevel(int startingLevel) {
        byte newIndex = (byte) this.levels[CharData.IDX_ACTUAL].length;
        if (newIndex >= 3) {
            System.out.println("Can't add more then 3 classes");
            return -1;
        }
        byte[][] newLevels = new byte[CharData.IDX_MAX][newIndex + 1];
        for (int i = 0; i < newIndex; i++) {
            newLevels[CharData.IDX_ACTUAL][i] = this.levels[CharData.IDX_ACTUAL][i];
            newLevels[CharData.IDX_MAX][i] = this.levels[CharData.IDX_MAX][i];
        }
        newLevels[CharData.IDX_ACTUAL][newIndex] = (byte) startingLevel;
        newLevels[CharData.IDX_MAX][newIndex] = (byte) startingLevel;
        this.levels = newLevels;
        return newIndex;
    }

    /*  -------------- CHAR FLAGS SECTION ------------------------------- */

    /** check if a flag is set : for example invisible or something like this.
     *
     */
    public boolean isFlagSet(int index) {
        if (index >= CharData.FLAG_LAST_FLAG) {
            System.out.println("Impossible to set this FLAG: it doez not exist!");
            return false;
        }
        return MaskTools.isSet(this.maskCharFlags, index);
    }

    /** used to set a flag: flag cant be set if it's not enable.
     * let's make an example a devil can't be blessed.
     */
    public void setFlag(int index) {
        if (index >= CharData.FLAG_LAST_FLAG) {
            System.out.println("Impossible to set this FLAG: it doez not exist!");
            return;
        }
        MaskTools.set(this.maskCharFlags, index);
    }

    protected void clone(CharData o) {
        this.gold = o.gold;
        this.maskCharClasses = o.maskCharClasses.clone();
        this.charAttributes = o.charAttributes.clone();
        this.levels = o.levels.clone();
        this.exp = o.exp.clone();
        this.maskCharSkills = o.maskCharSkills.clone();
        this.charSkills = o.charSkills.clone();
        this.maskCharKnownledge = o.maskCharKnownledge.clone();
        this.charKnownledge = o.charKnownledge.clone();
        this.maskCharFlags = o.maskCharFlags.clone();
        this.location = null;
    }

    /** To get the character location.
    *
    *  @return character WotlasLocation
    */
    public WotlasLocation getLocation() {
        return this.location;
    }

    /** To set the character location.
    *
    *  @param new character WotlasLocation
    */
    public void setLocation(WotlasLocation myLocation) {
        this.location = myLocation;
    }

    /** To get the player primary Key ( account name )
    *
    *  @return player primary key
    */
    public String getPrimaryKey() {
        return this.primaryKey;
    }

    /** To set the player's primary key.
    *
    *  @param primary key
    */
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    /** To get the screenObject
    *
    *  @return screenObject
    */
    public ScreenObject getScreenObject() {
        return this.screenObject;
    }

    /** To set the char screenObject
    *
    *  @param screenObject
    */
    public void setScreenObject(ScreenObject screenObject) {
        this.screenObject = screenObject;
    }
}