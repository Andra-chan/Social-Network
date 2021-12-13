package socialnetwork.Util.events;

public class ChangeEvent implements Event {
    private final ChangeEventType type;

    public ChangeEvent(ChangeEventType type) {
        this.type = type;
    }

    public ChangeEventType getType() {
        return type;
    }
}