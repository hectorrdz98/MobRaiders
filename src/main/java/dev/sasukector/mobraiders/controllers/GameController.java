package dev.sasukector.mobraiders.controllers;

import dev.sasukector.mobraiders.MobRaiders;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import dev.sasukector.mobraiders.models.Arena;
import dev.sasukector.mobraiders.models.Team;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

    private static GameController instance = null;
    private @Getter Status currentStatus = Status.LOBBY;
    private final @Getter List<Team> teams;
    private final @Getter Map<String, Arena> arenas;
    private @Getter @Setter Arena currentArena = null;

    public enum Status {
        LOBBY, STARTING, PLAYING
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameController() {
        this.teams = new ArrayList<>();
        this.arenas = new HashMap<>();
        this.fillArenas();
    }

    private void fillArenas() {
        this.arenas.put("plains", new Arena("plains", 0, 100, 0));
        this.arenas.put("taiga", new Arena("taiga", 0, 100, 0));
        this.currentArena = this.arenas.get("taiga");
    }

    public void handlePlayerJoin(Player player) {
        if (this.currentStatus == Status.LOBBY) {
            restartPlayer(player);
            teleportPlayerToLobby(player);
        }
    }

    public void teleportPlayerToLobby(Player player) {
        Location lobbySpawn = ServerUtilities.getLobbySpawn();
        if (lobbySpawn != null) {
            player.teleport(lobbySpawn);
        } else {
            World lobby = ServerUtilities.getWorld("lobby");
            if (lobby != null) {
                lobbySpawn = new Location(lobby, 0, 100, 0);
                ServerUtilities.teleportPlayerToSafeOrderedHeight(player, lobbySpawn);
            } else {
                player.kick(Component.text("El lobby no ha sido cargado", TextColor.color(0xD9ED92)));
            }
        }
    }

    public void restartPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setArrowsInBody(0);
        player.setFireTicks(0);
        player.setVisualFire(false);
        player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
        player.getInventory().clear();
        player.updateInventory();
    }

    public Team getPlayerTeam(Player player) {
        Optional<Team> playerTeam = this.teams.stream().filter(team -> team.getPlayers().contains(player.getUniqueId())).findAny();
        return playerTeam.orElse(null);
    }

    public void startGame() {
        ServerUtilities.sendAnnounceMensaje("Preparando el juego");
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.playSound(p.getLocation(), "minecraft:block.note_block.bell", 1, 1);
            p.setStatistic(Statistic.DEATHS, 0);
        });
        for (Team team : this.teams) {
            team.reloadWorld();
        }
        this.currentStatus = Status.STARTING;
        this.teleportCountDown(true);
    }

    public void stopGame() {
        this.currentStatus = Status.LOBBY;
        ServerUtilities.sendAnnounceMensaje("El juego ha terminado");
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.playSound(p.getLocation(), "minecraft:block.note_block.bell", 1, 1);
            p.setStatistic(Statistic.DEATHS, 0);
        });
        teleportCountDown(false);
    }

    public void teleportCountDown(boolean overworld) {
        AtomicInteger remainingTime = new AtomicInteger(15);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    if (overworld) {
                        teleportToOverworld();
                    } else {
                        Bukkit.getOnlinePlayers().forEach(p -> handlePlayerJoin(p));
                    }
                    cancel();
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (remainingTime.get() <= 3) {
                            p.playSound(p.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
                        }
                        p.sendActionBar(
                                Component.text("Teletransporte en: " + remainingTime.get() + "s", TextColor.color(0x0091AD))
                        );
                    });
                    remainingTime.addAndGet(-1);
                }
            }
        }.runTaskTimer(MobRaiders.getInstance(), 0L, 20L);
    }

    public void teleportToOverworld() {
        for (Team team : this.teams) {
            team.teleportToWorld();
        }

        AtomicInteger remainingTime = new AtomicInteger(30);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    ServerUtilities.sendAnnounceMensaje("¡Inicia la partida!");
                    Bukkit.getOnlinePlayers().forEach(p ->
                            p.playSound(p.getLocation(), "minecraft:music.effects.start", 1, 1));
                    currentStatus = Status.PLAYING;
                    cancel();
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (remainingTime.get() <= 3) {
                            p.playSound(p.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
                        }
                        p.sendActionBar(
                                Component.text("La partida inicia en: " + remainingTime.get() + "s", TextColor.color(0x0091AD))
                        );
                    });
                    remainingTime.addAndGet(-1);
                }
            }
        }.runTaskTimer(MobRaiders.getInstance(), 0L, 20L);
    }

}
