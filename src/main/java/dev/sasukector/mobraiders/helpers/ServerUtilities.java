package dev.sasukector.mobraiders.helpers;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerUtilities {

    private final static @Getter MiniMessage miniMessage = MiniMessage.get();
    private static @Getter @Setter Location lobbySpawn = null;
    private static final Random random = new Random();

    // Associate all world names
    public final static @Getter Map<String, String> worldsNames;
    static {
        worldsNames = new HashMap<>();
        worldsNames.put("lobby", "lobby");
        worldsNames.put("plains", "plains");
        worldsNames.put("taiga", "taiga");
        worldsNames.put("overworld", "world");
        worldsNames.put("nether", "world_nether");
        worldsNames.put("end", "world_the_end");
    }

    public static Component getPluginNameColored() {
        return miniMessage.parse("<bold><gradient:#B7094C:#0091AD>MobRaiders</gradient></bold>");
    }

    public static void sendBroadcastMessage(String message) {
        Bukkit.broadcast(getPluginNameColored()
                .append(Component.text(" ▶ ", TextColor.color(0xC0C1C2)))
                .append(Component.text(message, TextColor.color(0xFFFFFF))));
    }

    public static void sendBroadcastMessage(Component message) {
        Bukkit.broadcast(getPluginNameColored()
                .append(Component.text(" ▶ ", TextColor.color(0xC0C1C2)))
                .append(message));
    }

    public static void sendSpacedBroadcastMessage(Component message, int spaces) {
        for (int i = 0; i < spaces; ++i) {
            Bukkit.broadcast(Component.empty());
        }
        Bukkit.broadcast(message);
        for (int i = 0; i < spaces; ++i) {
            Bukkit.broadcast(Component.empty());
        }
    }

    public static void sendSpacedDoubleBroadcastMessage(Component message1, Component message2, int spaces) {
        for (int i = 0; i < spaces; ++i) {
            Bukkit.broadcast(Component.empty());
        }
        Bukkit.broadcast(message1);
        Bukkit.broadcast(message2);
        for (int i = 0; i < spaces; ++i) {
            Bukkit.broadcast(Component.empty());
        }
    }

    public static void sendServerMessage(Player player, String message) {
        player.sendMessage(getPluginNameColored()
                .append(Component.text(" ▶ ", TextColor.color(0xC0C1C2)))
                .append(Component.text(message, TextColor.color(0xFFFFFF))));
    }

    public static void sendServerMessage(Player player, Component message) {
        player.sendMessage(getPluginNameColored()
                .append(Component.text(" ▶ ", TextColor.color(0xC0C1C2)))
                .append(message));
    }

    public static void sendAnnounceMensaje(String message) {
        sendBroadcastMessage(miniMessage.parse("<bold><gradient:#0091AD:#B7094C>" + message + "</gradient></bold>"));
    }

    public static boolean isInWorld(World world, String targetWorld) {
        if (worldsNames.containsKey(targetWorld)) {
            targetWorld = worldsNames.get(targetWorld);
        }
        if (validWorld(targetWorld)) {
            return world.getName().equals(targetWorld);
        }
        return false;
    }

    public static boolean validWorld(String targetWorld) {
        return Bukkit.getWorld(targetWorld) != null;
    }

    public static World getWorld(String worldAlias) {
        if (worldsNames.containsKey(worldAlias)) {
            return Bukkit.getWorld(worldsNames.get(worldAlias));
        }
        return null;
    }

    public static char getCharFromString(String base) {
        int hex = Integer.parseInt(base, 16);
        return (char) hex;
    }

    public static String getUnicodeStringFromCodes(String unformattedCodes) {
        String[] codes = unformattedCodes.split(" ");
        return StringUtils.join(Arrays.stream(codes)
                .map(ServerUtilities::getCharFromString)
                .collect(Collectors.toList()), "");
    }

    private static Location getSafeLocation(Location location, List<Integer> ys) {
        Location newLocation = null;
        for (int y : ys) {
            location.setY(y);
            Block cBlock = location.getBlock();
            Block tBlock = location.add(0, 1, 0).getBlock();
            Block lBlock = location.add(0, -2, 0).getBlock();
            if (cBlock.getType() == Material.WATER && lBlock.getType().isSolid() &&
                    lBlock.getType() != Material.BARRIER) {
                break;
            }
            if (cBlock.getType() == Material.AIR && lBlock.getType() == Material.WATER) {
                break;
            }
            if (cBlock.getType() == Material.AIR && tBlock.getType() == Material.AIR &&
                    lBlock.getType().isSolid() && lBlock.getType() != Material.BARRIER
            ) {
                location.setY(y);
                newLocation = location;
                break;
            }
        }
        return newLocation;
    }

    public static boolean teleportPlayerToSafeOrderedHeight(Player player, Location location) {
        List<Integer> ys = Stream.iterate(2, n -> n + 1).limit(location.getWorld().hasCeiling() ? 100 : 200)
                .sorted(Collections.reverseOrder()).collect(Collectors.toList());
        Location newLocation = getSafeLocation(location, ys);
        if (newLocation != null) {
            player.teleport(newLocation);
        }
        return newLocation != null;
    }

    public static void teleportPlayerToRandomLocationInRadius(Player player, Location center, int radius) {
        boolean validLocation = false;
        int currentAttempts = 3;
        while (!validLocation && currentAttempts-- > 0) {
            Location location = center.clone().add(random.nextInt(radius * 2) - radius, 0,
                    random.nextInt(radius * 2) - radius);
            validLocation = teleportPlayerToSafeOrderedHeight(player, location);
        }
        if (!validLocation || currentAttempts <= 0) {
            sendServerMessage(player, "§cOcurrió un error al teletransportarte a una ubicación segura, será teletransporte aleatorio protegido");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 0));
            center.setY(center.getWorld().hasCeiling() ? 60 : 120);
            player.teleport(center);
        }
    }

    public static void safeTeleportPlayer(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 0));
        teleportPlayerToRandomLocationInRadius(player,
                new Location(ServerUtilities.getWorld("overworld"), 0, 100, 0), 1000);
    }
}
