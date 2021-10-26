package dev.sasukector.mobraiders.controllers;

import dev.sasukector.mobraiders.models.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;

public class EventsController {

    private static EventsController instance = null;

    public static EventsController getInstance() {
        if (instance == null) {
            instance = new EventsController();
        }
        return instance;
    }

    public void startNewWave(Team team) {
        team.getPlayersList().forEach(p -> {
            p.playSound(p.getLocation(), "minecraft:music.effects.board", 1f, 1f);
            p.showTitle(
                    Title.title(
                            Component.text("Oleada " + team.getCurrentRound(), TextColor.color(0x0091AD)),
                            Component.text("¡Mata todos los enemigos!", TextColor.color(0xB7094C))
                    )
            );
        });
        team.spawnAssault();
    }

}
