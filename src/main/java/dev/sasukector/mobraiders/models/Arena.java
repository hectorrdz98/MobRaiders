package dev.sasukector.mobraiders.models;

import lombok.Getter;

public class Arena {

    private final @Getter int[] spawn;
    private final @Getter String name;

    public Arena(String name, int x, int y, int z) {
        this.spawn = new int[]{x, y, z};
        this.name = name;
    }

}
