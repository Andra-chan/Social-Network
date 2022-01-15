package socialnetwork.Util.events;

public class ChangeObserverEvent implements ObserverEvent {
    private final ChangeObserverEventType type;

    public ChangeObserverEvent(ChangeObserverEventType type) {
        this.type = type;
    }

    public ChangeObserverEventType getType() {
        return type;
    }
}