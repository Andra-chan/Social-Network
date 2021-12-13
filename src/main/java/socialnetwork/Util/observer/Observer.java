package socialnetwork.Util.observer;

import socialnetwork.Util.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}