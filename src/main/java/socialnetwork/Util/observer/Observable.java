package socialnetwork.Util.observer;

import socialnetwork.Util.events.ObserverEvent;

public interface Observable<E extends ObserverEvent> {
    void addObserver(Observer<E> e);

    void notifyObservers(E t);
}
