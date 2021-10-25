package dev.sasukector.mobraiders.controllers;

import dev.sasukector.mobraiders.helpers.ServerUtilities;
import dev.sasukector.mobraiders.models.Team;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController {

    private static GameController instance = null;
    private final @Getter Status currentStatus = Status.LOBBY;
    private @Getter @Setter String arenaType = "";
    private final @Getter List<Team> teams;

    public enum Status {
        LOBBY, WAITING, PLAYING
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameController() {
        this.teams = new ArrayList<>();
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

}
