package de.Breakcraft.Survival.Utils;

public enum ChunkFlag {

    NON_EXPLOSIVE("Non_Explosive", 1),
    NON_DELOAD("Non_Deload", 2);

    private String name;
    private int id;

    ChunkFlag(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public static ChunkFlag getFlagByID(int id) {
        ChunkFlag flag = null;
        for(ChunkFlag flag2 : ChunkFlag.values()) {
            if(flag2.id == id) {
                flag = flag2;
                break;
            }
        }
        return flag;
    }

}
