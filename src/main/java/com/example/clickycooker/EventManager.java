package com.example.clickycooker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EventManager{
    private static final ArrayList<Event> eventList = createEventList();
    private final GameManager gm;

    public EventManager(GameManager gm) {
        this.gm = gm;
    }

    private static ArrayList<Event> createEventList() {
        ArrayList<Event> result = new ArrayList<>();
        List<Event> nothings = Collections.nCopies(99, new Nothing());
        List<Event> gemRains = Collections.nCopies(1, new GemRain());

        result.addAll(nothings);
        result.addAll(gemRains);
        return result;
    }

    public void start() {
        Random random = new Random();

        Event event = eventList.get(random.nextInt(eventList.size()-1));
        event.start(gm);
    }
}
