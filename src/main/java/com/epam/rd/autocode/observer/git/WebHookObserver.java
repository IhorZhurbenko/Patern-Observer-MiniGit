package com.epam.rd.autocode.observer.git;

import java.util.ArrayList;
import java.util.List;

public class WebHookObserver implements WebHook {
    List<Event> eventList;
    private String branchName;
    private Event.Type type;

    public WebHookObserver(String branchName, Event.Type type) {
        this.branchName = branchName;
        this.type = type;
        eventList = new ArrayList<>();
    }

    @Override
    public String branch() {
        return branchName;
    }

    @Override
    public Event.Type type() {
        return type;
    }

    @Override
    public List<Event> caughtEvents() {
        return eventList;
    }

    @Override
    public void onEvent(Event event) {
        eventList.add(event);
    }
}
