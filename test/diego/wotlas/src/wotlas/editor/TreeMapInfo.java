package wotlas.editor;

import wotlas.common.universe.*;

public class TreeMapInfo {
        
    public String mapName;
    public int Id;

    public TreeMapInfo(String mapName, int Id) {
        this.mapName = mapName;
        this.Id = Id;
    }

    public String toString() {
        return mapName;
    }
}


