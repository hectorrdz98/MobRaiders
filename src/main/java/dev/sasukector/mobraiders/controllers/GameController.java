package dev.sasukector.mobraiders.controllers;

import dev.sasukector.mobraiders.MobRaiders;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import dev.sasukector.mobraiders.models.Arena;
import dev.sasukector.mobraiders.models.Assault;
import dev.sasukector.mobraiders.models.Team;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

    private static GameController instance = null;
    private @Getter Status currentStatus = Status.LOBBY;
    private final @Getter List<Team> teams;
    private final @Getter List<Assault> assaults;
    private final @Getter Map<String, Arena> arenas;
    private @Getter @Setter Arena currentArena = null;
    private final @Getter int totalAssaults = 5;

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
        this.assaults = new ArrayList<>();
        this.arenas = new HashMap<>();
        this.fillArenas();
        this.fillAssaults();
    }

    private void fillArenas() {
        this.arenas.put("plains", new Arena("plains", 0, 100, 0, -53, 67, 135));
        this.arenas.put("taiga", new Arena("taiga", 0, 100, 0, 88, 78, 178));
        this.currentArena = this.arenas.get("taiga");
    }

    private void fillAssaults() {
        Assault assault1 = new Assault(1);
        assault1.addMob(EntityType.PILLAGER);
        assault1.addMob(EntityType.PILLAGER);
        assault1.addMob(EntityType.PILLAGER);
        assault1.addMob(EntityType.PILLAGER);
        assault1.addMob(EntityType.PILLAGER);
        assault1.addMob(EntityType.PILLAGER);
        assault1.addMob(EntityType.VINDICATOR);
        assault1.addMob(EntityType.VINDICATOR);
        this.assaults.add(assault1);

        Assault assault2 = new Assault(2);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.PILLAGER);
        assault2.addMob(EntityType.VINDICATOR);
        assault2.addMob(EntityType.VINDICATOR);
        assault2.addMob(EntityType.VINDICATOR);
        assault2.addMob(EntityType.VINDICATOR);
        assault2.addMob(EntityType.WITCH);
        this.assaults.add(assault2);

        Assault assault3 = new Assault(3);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.PILLAGER);
        assault3.addMob(EntityType.VINDICATOR);
        assault3.addMob(EntityType.VINDICATOR);
        assault3.addMob(EntityType.VINDICATOR);
        assault3.addMob(EntityType.VINDICATOR);
        assault3.addMob(EntityType.VINDICATOR);
        assault3.addMob(EntityType.EVOKER);
        assault3.addMob(EntityType.WITCH);
        assault3.addMob(EntityType.WITCH);
        assault3.addMob(EntityType.RAVAGER);
        this.assaults.add(assault3);

        Assault assault4 = new Assault(4);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.PILLAGER);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.VINDICATOR);
        assault4.addMob(EntityType.EVOKER);
        assault4.addMob(EntityType.WITCH);
        assault4.addMob(EntityType.WITCH);
        assault4.addMob(EntityType.RAVAGER);
        assault4.addMob(EntityType.RAVAGER);
        this.assaults.add(assault4);

        Assault assault5 = new Assault(5);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.PILLAGER);
        assault5.addMob(EntityType.EVOKER);
        assault5.addMob(EntityType.EVOKER);
        assault5.addMob(EntityType.EVOKER);
        assault5.addMob(EntityType.WITCH);
        assault5.addMob(EntityType.WITCH);
        assault5.addMob(EntityType.RAVAGER);
        assault5.addMob(EntityType.RAVAGER);
        this.assaults.add(assault5);
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
                ServerUtilities.teleportEntityToSafeOrderedHeight(player, lobbySpawn);
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
        boolean validStart = true;
        for (Team team : this.teams) {
            team.setCurrentPoints(0);
            team.reloadWorld();
            team.setStarted(false);
            World world = team.getWorld();
            if (world == null) {
                validStart = false;
                break;
            } else {
                world.getWorldBorder().setCenter(new Location(world, 0, 0, 0));
                world.getWorldBorder().setSize(50);
                world.setFullTime(0);
                world.setClearWeatherDuration(20 * 60 * 5);
                world.setDifficulty(org.bukkit.Difficulty.HARD);
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
                world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            }
        }
        if (validStart) {
            this.currentStatus = Status.STARTING;
            this.teleportCountDown(true);
        } else {
            ServerUtilities.sendBroadcastMessage("§cOcurrió un error al reiniciar los mundos");
        }
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
                        teams.forEach(Team::unloadAndDeleteArena);
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

        AtomicInteger remainingTime = new AtomicInteger(120);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    for (Team team : teams) {
                        World world = team.getWorld();
                        if (world != null) {
                            world.getWorldBorder().setSize(2000);
                        }
                        team.teamTimer();
                        team.summonVillagers();
                        team.getCurrentAssault().clear();
                    }
                    ServerUtilities.sendAnnounceMensaje("¡Inicia la partida!");
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.showTitle(Title.title(
                                Component.text("Raid Challenge 1", TextColor.color(0x0091AD)),
                                Component.text("¡Encuentra un outpost y prepárate!", TextColor.color(0xB7094C))
                        ));
                        p.playSound(p.getLocation(), "minecraft:music.effects.start", 1, 1);
                    });
                    currentStatus = Status.PLAYING;
                    cancel();
                } else {
                    if (remainingTime.get() == 105) {
                        ServerUtilities.sendSpacedDoubleBroadcastMessage(
                                ServerUtilities.getMiniMessage().parse("<bold><color:#B7094C>Raid 1</color></bold>"),
                                Component.text("¡Encuentra una pillager outpost, derrota un capitán para obtener el efecto Bad Omen!", TextColor.color(0xFFFFFF))
                        , 1);
                    }
                    if (remainingTime.get() == 90) {
                        ServerUtilities.sendSpacedDoubleBroadcastMessage(
                                ServerUtilities.getMiniMessage().parse("<bold><color:#B7094C>Empezando la raid</color></bold>"),
                                Component.text("¡Entra a tu villa con el efecto Bad Omen para iniciar la raid!", TextColor.color(0xFFFFFF))
                        , 1);
                    }
                    if (remainingTime.get() == 75) {
                        ServerUtilities.sendSpacedDoubleBroadcastMessage(
                                ServerUtilities.getMiniMessage().parse("<bold><color:#B7094C>Derrota tus enemigos</color></bold>"),
                                Component.text("Cuando comience la raid, llegarán varias oleadas de enemigos ¡Derrótalos lo más pronto posible!", TextColor.color(0xFFFFFF))
                        , 1);
                    }
                    if (remainingTime.get() == 50) {
                        ServerUtilities.sendSpacedDoubleBroadcastMessage(
                                ServerUtilities.getMiniMessage().parse("<bold><color:#B7094C>Puntuando</color></bold>"),
                                Component.text("¡Tu equipo ganará más puntos mientras más rápido terminen la raid!", TextColor.color(0xFFFFFF))
                        , 1);
                    }
                    if (remainingTime.get() == 35) {
                        ServerUtilities.sendSpacedDoubleBroadcastMessage(
                                ServerUtilities.getMiniMessage().parse("<bold><color:#B7094C>Puntos extra</color></bold>"),
                                Component.text("Se darán puntos adicionales al primer equipo en activar la raid ¡Sé veloz!", TextColor.color(0xFFFFFF))
                        , 1);
                    }
                    if (remainingTime.get() == 20) {
                        ServerUtilities.sendSpacedDoubleBroadcastMessage(
                                ServerUtilities.getMiniMessage().parse("<bold><color:#B7094C>Tips</color></bold>"),
                                Component.text("Recuerda cuidar tus recursos, preparar tu equipo y mejorar conforme avanza la raid", TextColor.color(0xFFFFFF))
                        , 1);
                    }
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (remainingTime.get() <= 3) {
                            p.showTitle(Title.title(Component.text(remainingTime.get(), TextColor.color(0x0091AD)), Component.empty()));
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
