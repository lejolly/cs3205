package sg.edu.nus.comp.cs3205.common.logs;

import sg.edu.nus.comp.cs3205.common.events.BaseEvent;

public class LogsCenter {

    /**
     * Decorates the given string to create a log message suitable for logging event handling methods.
     */
    public static String getEventHandlingLogMessage(BaseEvent e, String message) {
        return "---[Event handled][" + e + "]" + message;
    }

    /**
     * @see #getEventHandlingLogMessage(BaseEvent, String)
     */
    public static String getEventHandlingLogMessage(BaseEvent e) {
        return getEventHandlingLogMessage(e, "");
    }

}
