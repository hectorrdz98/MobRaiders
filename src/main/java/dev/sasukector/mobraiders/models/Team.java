package dev.sasukector.mobraiders.models;

import dev.sasukector.mobraiders.MobRaiders;
import dev.sasukector.mobraiders.controllers.EventsController;
import dev.sasukector.mobraiders.controllers.GameController;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Team {

    private final @Getter UUID uuid;
    private @Getter @Setter UUID owner;
    private final @Getter List<UUID> players;
    private @Getter @Setter int currentRound;
    private @Getter @Setter int currentPoints;
    private final @Getter List<Entity> currentAssault;
    private @Getter @Setter World world = null;
    private @Getter @Setter boolean started;
    private @Getter @Setter int taskID = -1;

    public Team(Player player) {
        this.players = new ArrayList<>();
        this.uuid = UUID.randomUUID();
        this.currentRound = 1;
        this.currentPoints = 0;
        this.currentAssault = new ArrayList<>();
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
        if (this.taskID != -1) {
            Bukkit.getScheduler().cancelTask(this.taskID);
        }
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

    public void spawnAssault() {
        this.currentAssault.clear();
        Optional<Assault> preAssault = GameController.getInstance().getAssaults().stream()
                .filter(a -> a.getId() == this.currentRound).findAny();
        if (preAssault.isPresent()) {
            Assault assault = preAssault.get();
            for (EntityType entityType : assault.getMobs()) {
                Entity entity = this.world.spawnEntity(new Location(this.world, 0, 100, 0), entityType);
                entity.setPersistent(true);
                entity.setGlowing(true);
                ServerUtilities.teleportEntityToRandomLocationInRadius(entity, new Location(this.world, 0, 100, 0), 20);
                this.currentAssault.add(entity);
            }
        }
    }

    public void spawnRemainingAssault() {
        List<Entity> assault = new ArrayList<>(this.currentAssault);
        this.currentAssault.clear();
        for (Entity entity : assault) {
            Entity newEntity = this.world.spawnEntity(new Location(this.world, 0, 100, 0), entity.getType());
            newEntity.setPersistent(true);
            newEntity.setGlowing(true);
            ServerUtilities.teleportEntityToRandomLocationInRadius(newEntity, new Location(this.world, 0, 100, 0), 10);
            this.currentAssault.add(newEntity);
            entity.remove();
        }
    }

    public void teamTimer() {
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MobRaiders.getInstance(), () -> {
            Arena arena = GameController.getInstance().getCurrentArena();
            if (this.world != null && arena != null) {
                if (this.getCurrentAssault().size() > 0) {
                    this.getPlayersList().forEach(p -> p.sendActionBar(
                            Component.text("Enemigos restantes: " + this.getCurrentAssault().size(), TextColor.color(0xB7094C))
                    ));
                }
                int[] outpost = arena.getOutpost();
                long totalPillagers = this.world.getEntities().stream().filter(e -> e.getType() == EntityType.PILLAGER).count();
                if (totalPillagers < 15) {
                    Pillager pillager = (Pillager) this.world.spawnEntity(new Location(this.world, 0, 100, 0), EntityType.PILLAGER);
                    ServerUtilities.teleportEntityToRandomLocationInRadius(
                            pillager,
                            new Location(this.world, outpost[0], outpost[1], outpost[2]),
                            20
                    );
                }
            }
        }, 0L, 20L);
    }

    public void summonVillagers() {
        for (int i = 0; i < 3; ++i) {
            Location spawn = getSpawn();
            if (this.world != null && spawn != null) {
                Bukkit.getScheduler().runTaskLater(MobRaiders.getInstance(), () -> {
                    Villager villager = (Villager) this.world.spawnEntity(spawn, EntityType.VILLAGER);
                    villager.setPersistent(true);
                    ServerUtilities.teleportEntityToRandomLocationInRadius(villager, spawn, 5);
                }, 10L);
            }
        }
    }

    public void prepareForNextAssault() {
        if (++this.currentRound > GameController.getInstance().getTotalAssaults()) {
            Player owner = this.getOwnerPlayer();
            if (owner != null) {
                ServerUtilities.sendAnnounceMensaje("El equipo de " + owner.getName() + " ha completado todas las oleadas");
                GameController.getInstance().stopGame();
            }
        } else {
            switch ((int) GameController.getInstance().getTeams().stream()
                    .filter(t -> t.getCurrentRound() >= currentRound && t.getUuid() != this.uuid).count()) {
                case 0 -> this.addPoints(3);
                case 1 -> this.addPoints(2);
                default -> this.addPoints(1);
            }
            AtomicInteger remainingTime = new AtomicInteger(20);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (remainingTime.get() <= 0) {
                        startNewWave();
                        cancel();
                    } else {
                        getPlayersList().forEach(p -> {
                            if (remainingTime.get() <= 3) {
                                p.playSound(p.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
                            }
                            p.sendActionBar(
                                    Component.text("Siguiente asalto en: " + remainingTime.get() + "s", TextColor.color(0x0091AD))
                            );
                        });
                        remainingTime.addAndGet(-1);
                    }
                }
            }.runTaskTimer(MobRaiders.getInstance(), 0L, 20L);
        }
    }

    public void startNewWave() {
        EventsController.getInstance().startNewWave(this);
    }
}
