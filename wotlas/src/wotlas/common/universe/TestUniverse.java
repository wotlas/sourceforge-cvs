/**
 * Programme de test des objets de l'univers
 */

import wotlas.libs.persistence.ObjectDump;
import wotlas.common.universe.*;

class TestUniverse
{
  public static void main(String[] argv)
  {    
    ObjectDump objDump = new ObjectDump();
    
    // objects in the universe
    ServerProcess sp;
    WorldMap wm;
    TownMap tm;
    
    // creation of ServerProcess
    sp = new ServerProcess();
    
    // creation of WorldMap 1
    wm = sp.addWorldMap();
    wm.setFullName("France");
    wm.setShortName("FR");

    // creation of TownMap 1.1
    tm = wm.addTownMap();
    tm.setFullName("Paris");
    tm.setShortName("paris");
    // creation of TownMap 1.2
    tm = wm.addTownMap();
    tm.setFullName("Lyon");
    tm.setShortName("lyon");
    
    // creation of WorldMap 2
    wm = sp.addWorldMap();
    wm.setFullName("Allemagne");
    wm.setShortName("ALL");
    // tms = new TownMap[1];
    // wm.setTownMaps(tms);
    
    //Dump de l'objet
    System.out.println("TestUniverse - Dump de l'objet sp");
    objDump.dumpObjectByInstance(sp);
  }
}
