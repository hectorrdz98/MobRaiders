package dev.sasukector.mobraiders.controllers;

import dev.sasukector.mobraiders.MobRaiders;
import dev.sasukector.mobraiders.helpers.FastBoard;
import dev.sasukector.mobraiders.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;

public class BoardController {

    private static BoardController instance = null;
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public static BoardController getInstance() {
        if (instance == null) {
            instance = new BoardController();
        }
        return instance;
    }

    public BoardController() {
        Bukkit.getScheduler().runTaskTimer(MobRaiders.getInstance(), this::updateBoards, 0L, 20L);
    }

    public void newPlayerBoard(Player player) {
        FastBoard board = new FastBoard(player);
        this.boards.put(player.getUniqueId(), board);
    }

    public void removePlayerBoard(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void updateBoards() {
        boards.forEach((uuid, board) -> {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;

            board.updateTitle("§5§lMob§d§lRaiders");
            Team team = GameController.getInstance().getPlayerTeam(player);

            GameController.Status status = GameController.getInstance().getCurrentStatus();
            List<String> lines = new ArrayList<>();
            switch (status) {
                case LOBBY -> {
                    lines.add("");
                    lines.add("Jugador: §6" + player.getName());
                    lines.add("En línea: §6" + Bukkit.getOnlinePlayers().size());
                    lines.add("");
                    lines.add("§cEsperando...");
                }
                case STARTING, PLAYING -> {
                    lines.add("");
                    lines.add("Jugador: §6" + player.getName());
                    lines.add("Muertes: §6" + player.getStatistic(Statistic.DEATHS));
                }
            }
            if (team != null) {
                Player owner = team.getOwnerPlayer();
                lines.add("");
                lines.add("Líder: §d" + (owner != null ? owner.getName() : "Nadie" ));
                lines.add("Puntos: §d" + team.getCurrentPoints());
                lines.add("Compañeros: §d" + team.getPlayers().size());
            }
            lines.add("");
            lines.add("§7§oBy Sasukector");
            board.updateLines(lines);
        });
    }

}
