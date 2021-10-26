package dev.sasukector.mobraiders.models;

import lombok.Getter;
import org.bukkit.entity.EntityType;
import java.util.ArrayList;
import java.util.List;

public class Assault {

    private final @Getter int id;
    private final @Getter List<EntityType> mobs;

    public Assault(int id) {
        this.id = id;
        mobs = new ArrayList<>();
    }

    public void addMob(EntityType entity) {
        this.mobs.add(entity);
    }

}
