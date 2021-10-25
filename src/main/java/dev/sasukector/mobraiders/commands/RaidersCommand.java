package dev.sasukector.mobraiders.commands;

import dev.sasukector.mobraiders.controllers.GameController;
import dev.sasukector.mobraiders.helpers.ServerUtilities;
import dev.sasukector.mobraiders.models.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class RaidersCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            if (args.length > 0) {
                String option = args[0];
                if (validOptions().contains(option)) {
                    switch (option) {
                        case "about" -> {
                            player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);
                            ServerUtilities.sendBroadcastMessage(Component.text("Creado por ")
                                    .append(ServerUtilities.getMiniMessage().parse("<bold><gradient:#1DA1F2:#1D5DF2><click:copy_to_clipboard:https://twitter.com/sasukector><hover:show_text:'<color:#1DA1F2>Twitter</color>: https://twitter.com/sasukector'>Sasukector</hover></click></gradient></bold>")));
                        }
                        case "arena" -> {
                            String subOption = args[1];
                            if (validSubOptions(option).contains(subOption)) {
                                player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);
                                GameController.getInstance().setCurrentArena(GameController.getInstance().getArenas().get(subOption));
                                ServerUtilities.sendBroadcastMessage(
                                        ServerUtilities.getMiniMessage().parse("Se ha cambiado la arena a <bold><color:#0091AD>" +
                                                subOption.toUpperCase() + "</color></bold>")
                                );
                            }
                        }
                        case "team" -> {
                            if (args.length > 1) {
                                String subOption = args[1];
                                if (validSubOptions(option).contains(subOption)) {
                                    switch (subOption) {
                                        case "create" -> {
                                            Team team = GameController.getInstance().getPlayerTeam(player);
                                            if (team == null) {
                                                team = new Team(player);
                                                GameController.getInstance().getTeams().add(team);
                                                ServerUtilities.sendBroadcastMessage(
                                                        ServerUtilities.getMiniMessage().parse("<bold><color:#0091AD>" +
                                                                player.getName() + "</color></bold> ha creado un nuevo equipo")
                                                );
                                            } else {
                                                ServerUtilities.sendServerMessage(player, "§cYa perteneces a un equipo");
                                            }
                                        }
                                        case "join" -> {
                                            String ownerName = args[2];
                                            if (validSubOptions(subOption).contains(ownerName)) {
                                                Player owner = Bukkit.getPlayer(ownerName);
                                                if (owner != null) {
                                                    Team team = GameController.getInstance().getPlayerTeam(owner);
                                                    if (!team.getPlayers().contains(player.getUniqueId())) {
                                                        team.addPlayer(player);
                                                        ServerUtilities.sendBroadcastMessage(
                                                                ServerUtilities.getMiniMessage().parse("<bold><color:#0091AD>" +
                                                                        player.getName() + "</color></bold> se ha unido al equipo de <bold><color:#0091AD>" +
                                                                        owner.getName() + "</color></bold>")
                                                        );
                                                    } else {
                                                        ServerUtilities.sendServerMessage(player, "§cYa perteneces a un equipo");
                                                    }
                                                } else {
                                                    ServerUtilities.sendServerMessage(player, "§cError al obtener al dueño del equipo");
                                                }
                                            } else {
                                                ServerUtilities.sendServerMessage(player, "§cEse usuario no es dueño de un equipo");
                                            }
                                        }
                                        case "leave" -> {
                                            Team team = GameController.getInstance().getPlayerTeam(player);
                                            if (team != null) {
                                                Player owner = team.getOwnerPlayer();
                                                if (owner != null) {
                                                    if (team.getOwner().equals(player.getUniqueId())) {
                                                        ServerUtilities.sendBroadcastMessage(
                                                                ServerUtilities.getMiniMessage().parse("<bold><color:#0091AD>" +
                                                                        player.getName() + "</color></bold> ha abandonado su propio equipo")
                                                        );
                                                    } else {
                                                        ServerUtilities.sendBroadcastMessage(
                                                                ServerUtilities.getMiniMessage().parse("<bold><color:#0091AD>" +
                                                                        player.getName() + "</color></bold> ha abandonado al equipo de <bold><color:#0091AD>" +
                                                                        owner.getName() + "</color></bold>")
                                                        );
                                                    }
                                                    team.removePlayer(player);
                                                } else {
                                                    ServerUtilities.sendServerMessage(player, "§cError al obtener al dueño del equipo");
                                                }
                                            } else {
                                                ServerUtilities.sendServerMessage(player, "§cNo perteneces a un equipo");
                                            }
                                        }
                                    }
                                }
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cSelecciona una opción válida");
                            }
                        }
                        case "start" -> {
                            if (GameController.getInstance().getCurrentStatus() == GameController.Status.LOBBY) {
                                boolean validStart = true;
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (GameController.getInstance().getPlayerTeam(p) == null) {
                                        ServerUtilities.sendServerMessage(player, "§cFaltan jugadores de elegir equipo");
                                        validStart = false;
                                        break;
                                    }
                                }
                                if (GameController.getInstance().getCurrentArena() == null) {
                                    ServerUtilities.sendServerMessage(player, "§cFalta seleccionar una arena");
                                    validStart = false;
                                }
                                if (validStart) {
                                    GameController.getInstance().startGame();
                                }
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cLa partida ya ha empezado");
                            }
                        }
                        case "stop" -> {
                            if (GameController.getInstance().getCurrentStatus() != GameController.Status.LOBBY) {
                                GameController.getInstance().stopGame();
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cNo hay una partida en curso");
                            }
                        }
                    }
                } else {
                    ServerUtilities.sendServerMessage(player, "§cSelecciona una opción válida");
                }
            } else {
                ServerUtilities.sendServerMessage(player, "§cSelecciona una opción");
            }
        } else if (sender instanceof Player player) {
            ServerUtilities.sendServerMessage(player, "§cPermisos insuficientes");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if(sender instanceof Player) {
            if (args.length == 1) {
                String partialItem = args[0];
                StringUtil.copyPartialMatches(partialItem, validOptions(), completions);
            } else if (args.length == 2) {
                String option = args[0];
                String partialItem = args[1];
                StringUtil.copyPartialMatches(partialItem, validSubOptions(option), completions);
            } else if (args.length == 3) {
                String option = args[1];
                String partialItem = args[2];
                StringUtil.copyPartialMatches(partialItem, validSubOptions(option), completions);
            }
        }

        Collections.sort(completions);

        return completions;
    }

    public List<String> validOptions() {
        List<String> valid = new ArrayList<>();
        valid.add("about");
        valid.add("start");
        valid.add("stop");
        valid.add("arena");
        valid.add("team");
        Collections.sort(valid);
        return valid;
    }

    public List<String> validSubOptions(String option) {
        List<String> valid = new ArrayList<>();
        switch (option.toLowerCase()) {
            case "arena" -> valid = Arrays.asList("plains", "taiga");
            case "team" -> valid = Arrays.asList("create", "join", "leave");
            case "join" -> valid = GameController.getInstance().getTeams().stream()
                    .map(team -> Objects.requireNonNull(Bukkit.getPlayer(team.getOwner())).getName().toLowerCase())
                    .collect(Collectors.toList());
        }
        Collections.sort(valid);
        return valid;
    }

}
