package socialnetwork.domain;

import javafx.util.Pair;

public class EventAttendance extends Entity<Pair<Long, Long>>{
    private Utilizator user;
    private Event event;

    private boolean notifications;

    public boolean hasNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public EventAttendance(Utilizator user, Event event) {
        this.user = user;
        this.event = event;
    }

    public Utilizator getUser() {
        return user;
    }

    public void setUser(Utilizator user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
