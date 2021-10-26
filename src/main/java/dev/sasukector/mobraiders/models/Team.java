package dev.sasukector.mobraiders.models;

import dev.sasukector.mobraiders.MobRaiders;
import dev.sasukector.mobraiders.controllers.GameController;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import lombok.Getter;
import lombok.Setter;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team {

    private final @Getter UUID uuid;
    private @Getter @Setter UUID owner;
    private final @Getter List<UUID> players;
    private @Getter @Setter int currentRound;
    private @Getter int currentPoints;
    private @Getter @Setter Assault currentAssault;
    private @Getter @Setter World world = null;

    public Team(Player player) {
        this.players = new ArrayList<>();
        this.uuid = UUID.randomUUID();
        this.currentRound = 0;
        this.currentPoints = 0;
        this.currentAssault = null;
        this.owner = player.getUniqueId();
        this.addPlayer(player);
    }

    public void addPlayer(Player player) {
        if (!this.players.contains(player.getUniqueId())) {
            this.players.add(player.getUniqueId());
        }
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
        if (player.getUniqueId().equals(this.owner)) {
            if (players.size() > 0) {
                Player newOwner = Bukkit.getPlayer(players.get(0));
                if (newOwner != null) {
                    ServerUtilities.sendBroadcastMessage(
                            ServerUtilities.getMiniMessage().parse("Ahora el líder del equipo de <bold><color:#0091AD>" +
                                    player.getName() + "</color></bold> es <bold><color:#0091AD>" +
                                    newOwner.getName() + "</color></bold>")
                    );
                    this.owner = players.get(0);
                } else {
                    ServerUtilities.sendServerMessage(player, "§cError al obtener al nuevo dueño del equipo");
                }
            } else {
                ServerUtilities.sendBroadcastMessage(
                        ServerUtilities.getMiniMessage().parse("Se ha eliminado el equipo de <bold><color:#0091AD>" +
                                player.getName() + "</color></bold>")
                );
                GameController.getInstance().getTeams().remove(this);
                this.owner = null;
                if (this.world != null) {
                    this.unloadAndDeleteArena();
                }
            }
        }
    }

    public Player getOwnerPlayer() {
        if (this.owner != null) {
            return Bukkit.getPlayer(this.owner);
        }
        return null;
    }

    public List<Player> getPlayersList() {
        return this.players.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public void addPoints(int amount) {
        this.currentPoints += amount;
    }

    public Location getSpawn() {
        Arena arena = GameController.getInstance().getCurrentArena();
        if (arena != null && this.world != null) {
            int[] spawn = arena.getSpawn();
            return new Location(this.world, spawn[0], spawn[1], spawn[2]);
        }
        return null;
    }

    public void reloadWorld() {
        Arena arena = GameController.getInstance().getCurrentArena();
        if (arena != null) {
            if (this.createNewArena(arena.getName())) {
                if (this.unZipNewArena()) {
                    this.world = Bukkit.getServer().createWorld(new WorldCreator("arena_" + this.uuid));
                }
            }
        } else {
            ServerUtilities.sendBroadcastMessage("§cNo hay arena seleccionada");
        }
    }

    public void unloadAndDeleteArena() {
        String worldPath = this.world.getWorldFolder().getPath();
        Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Unloaded world for arena_" + this.uuid);
        Bukkit.getServer().unloadWorld("arena_" + this.uuid, false);
        Bukkit.getScheduler().runTaskAsynchronously(MobRaiders.getInstance(), () -> {
            File dir = new File(worldPath);
            try {
                Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Deleted directory for arena_" + this.uuid);
                FileUtils.deleteDirectory(dir);
            } catch (Exception e) {
                Bukkit.getLogger().info(ChatColor.RED + "Error while deleteArena(): " + e);
                e.printStackTrace();
            }
        });
    }

    public boolean createNewArena(String originalMap) {
        boolean correct = false;
        try {
            // Create new world directory
            File arenaDir = new File("arena_" + this.uuid);
            if (arenaDir.exists()) {
                if (this.world != null) {
                    this.unloadAndDeleteArena();
                }
            }
            if (arenaDir.mkdir()) {
                Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Created directory for new arena arena_" + this.uuid);
                // Copy zip file to lobby directory
                ServerUtilities.copyFile("maps/" + originalMap + ".zip", arenaDir + "/map.zip");
                Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Success coping files to new arena directory");
                correct = true;
            }
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while createNewArena(): " + e);
            e.printStackTrace();
        }

        return correct;
    }

    public boolean unZipNewArena() {
        boolean correct = false;
        String zipFilePath = "arena_" + this.uuid + "/map.zip";
        try {
            // Unzip world
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll("arena_" + this.uuid);
            Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Success unzipping new arena " + "arena_" + this.uuid);
            // Delete zip file
            File originalWorldZip = new File(zipFilePath);
            if (originalWorldZip.delete()) {
                Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Success deleting new arena " + "arena_" + this.uuid + " zip file");
            } else {
                Bukkit.getLogger().info(ChatColor.RED + "Failed to delete new arena " + "arena_" + this.uuid + " zip file");
            }
            correct = true;
        } catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Error while unZipNewArena(): " + e);
            e.printStackTrace();
        }
        return correct;
    }

    public void teleportToWorld() {
        Location spawn = this.getSpawn();
        if (spawn != null) {
            this.getPlayersList().forEach(player -> player.teleport(spawn));
        } else {
            ServerUtilities.sendBroadcastMessage("§cError al tele transportar al equipo de " + this.getOwnerPlayer().getName());
        }
    }
}
