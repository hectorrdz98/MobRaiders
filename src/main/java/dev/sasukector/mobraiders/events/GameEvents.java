package dev.sasukector.mobraiders.events;

import dev.sasukector.mobraiders.MobRaiders;
import dev.sasukector.mobraiders.controllers.EventsController;
import dev.sasukector.mobraiders.controllers.GameController;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import dev.sasukector.mobraiders.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class GameEvents implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (GameController.getInstance().getCurrentStatus() == GameController.Status.PLAYING) {
            Team team = GameController.getInstance().getPlayerTeam(player);
            if (team != null) {
                team.addPoints(-1);
            }
            Bukkit.getScheduler().runTaskLater(MobRaiders.getInstance(), () ->
                    ServerUtilities.safeTeleportPlayer(player), 1L);
        } else {
            GameController.getInstance().handlePlayerJoin(player);
        }
    }

    @EventHandler
    public void onRaid(RaidTriggerEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        Team team = GameController.getInstance().getPlayerTeam(player);
        if (team != null && !team.isStarted()) {
            team.setStarted(true);
            Player owner = team.getOwnerPlayer();
            if (owner != null) {
                ServerUtilities.sendBroadcastMessage(ServerUtilities.getMiniMessage().parse("El equipo de <bold><color:#0091AD>" +
                        owner.getName() + "</color></bold> ha iniciado la raid"));
            }
            EventsController.getInstance().startNewWave(team);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getEntity().getKiller();
        if (player != null) {
            Team team = GameController.getInstance().getPlayerTeam(player);
            if (team != null) {
                if (team.getCurrentAssault().contains(entity)) {
                    player.playSound(player.getLocation(), "minecraft:music.effects.item", 1f, 1f);
                    team.getCurrentAssault().remove(entity);
                    if (team.getCurrentAssault().size() <= 0) {
                        Player owner = team.getOwnerPlayer();
                        if (owner != null) {
                            ServerUtilities.sendBroadcastMessage(ServerUtilities.getMiniMessage().parse("El equipo de <bold><color:#0091AD>" +
                                    owner.getName() + "</color></bold> ha completado la raid #" + team.getCurrentRound()));
                            team.prepareForNextAssault();
                        }
                    }
                }
            }
        } else if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                event.setCancelled(true);
            }
        }
    }

}
