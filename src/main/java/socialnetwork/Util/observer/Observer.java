package socialnetwork.Util.observer;

import socialnetwork.Util.events.ObserverEvent;

public interface Observer<E extends ObserverEvent> {
    void update(E e);
}
