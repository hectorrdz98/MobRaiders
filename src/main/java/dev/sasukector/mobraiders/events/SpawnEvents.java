package dev.sasukector.mobraiders.events;

import dev.sasukector.mobraiders.controllers.BoardController;
import dev.sasukector.mobraiders.controllers.GameController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;

public class SpawnEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(
                Component.text("+ ", TextColor.color(0x84E3A4))
                        .append(Component.text(player.getName(), TextColor.color(0x84E3A4)))
        );
        GameController.getInstance().handlePlayerJoin(player);
        BoardController.getInstance().newPlayerBoard(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BoardController.getInstance().removePlayerBoard(player);
        event.quitMessage(
                Component.text("- ", TextColor.color(0xE38486))
                        .append(Component.text(player.getName(), TextColor.color(0xE38486)))
        );
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockChestInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof InventoryHolder) {
                if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDroppedItem(PlayerDropItemEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemEaten(PlayerItemConsumeEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickedUpItems(PlayerAttemptPickupItemEvent event) {
        if (GameController.getInstance().getCurrentStatus() != GameController.Status.PLAYING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

}
