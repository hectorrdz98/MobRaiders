package dev.sasukector.mobraiders;

import dev.sasukector.mobraiders.commands.RaidersCommand;
import dev.sasukector.mobraiders.events.GameEvents;
import dev.sasukector.mobraiders.events.SpawnEvents;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MobRaiders extends JavaPlugin {

    private static @Getter MobRaiders instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(ChatColor.DARK_PURPLE + "MobRaiders startup!");
        instance = this;

        // Register events
        this.getServer().getPluginManager().registerEvents(new SpawnEvents(), this);
        this.getServer().getPluginManager().registerEvents(new GameEvents(), this);

        // Register commands
        Objects.requireNonNull(MobRaiders.getInstance().getCommand("raiders")).setExecutor(new RaidersCommand());

        // Load lobby
        loadDimensions();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.DARK_PURPLE + "MobRaiders shutdown!");
    }

    private void loadDimensions() {
        List<String> dimensionNames = new ArrayList<>();
        dimensionNames.add("lobby");

        for (String dimensionName : dimensionNames) {
            getLogger().info(ChatColor.DARK_PURPLE + "Loading dimension " + dimensionName);
            World world = Bukkit.getServer().createWorld(new WorldCreator(ServerUtilities.getWorldsNames().get(dimensionName)));
            if (world != null && dimensionName.equals("lobby")) {
                ServerUtilities.setLobbySpawn(new Location(world, 0, 65, 0));
            } else if (world == null) {
                getLogger().info(ChatColor.RED + "La dimensión " + dimensionName + " no ha sido cargada");
            }
        }

    }
}
