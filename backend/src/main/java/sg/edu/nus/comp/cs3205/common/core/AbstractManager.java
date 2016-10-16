package sg.edu.nus.comp.cs3205.common.core;

import sg.edu.nus.comp.cs3205.common.events.BaseEvent;
import sg.edu.nus.comp.cs3205.common.events.EventsCenter;

public abstract class AbstractManager {

    private EventsCenter eventsCenter;

    public AbstractManager() {
        this.eventsCenter = EventsCenter.getInstance();
        eventsCenter.registerHandler(this);
    }

    protected void raise(BaseEvent event) {
        eventsCenter.post(event);
    }

}
