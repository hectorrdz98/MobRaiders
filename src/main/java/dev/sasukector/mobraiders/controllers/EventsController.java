package dev.sasukector.mobraiders.controllers;

public class EventsController {

    private static EventsController instance = null;

    public static EventsController getInstance() {
        if (instance == null) {
            instance = new EventsController();
        }
        return instance;
    }

    public EventsController() {

    }

}
