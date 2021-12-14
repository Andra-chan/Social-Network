package socialnetwork.Util.observer;

import socialnetwork.Util.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);

    void notifyObservers(E t);
}
