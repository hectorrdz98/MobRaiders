package dev.sasukector.mobraiders.models;

import dev.sasukector.mobraiders.controllers.GameController;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team {

    private @Getter @Setter UUID owner;
    private final @Getter List<UUID> players;
    private @Getter @Setter int currentRound;
    private @Getter int currentPoints;
    private @Getter @Setter Assault currentAssault;
    private @Getter @Setter World world = null;

    public Team(Player player) {
        this.players = new ArrayList<>();
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
        if (arena != null) {
            // Temp
            World world = ServerUtilities.getWorld(arena.getName());
            if (world != null) {
                int[] spawn = arena.getSpawn();
                return new Location(world, spawn[0], spawn[1], spawn[2]);
            }
            return null;
        }
        return null;
    }

    public void reloadWorld() {

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
