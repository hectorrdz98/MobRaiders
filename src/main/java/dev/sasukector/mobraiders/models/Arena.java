package dev.sasukector.mobraiders.models;

import lombok.Getter;

public class Arena {

    private final @Getter int[] spawn;
    private final @Getter int[] outpost;
    private final @Getter String name;

    public Arena(String name, int x, int y, int z, int ox, int oy, int oz) {
        this.spawn = new int[]{x, y, z};
        this.outpost = new int[]{ox, oy, oz};
        this.name = name;
    }

}
