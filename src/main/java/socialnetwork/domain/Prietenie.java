package socialnetwork.domain;

import java.time.LocalDateTime;

/**
 * Friendship between users which contains
 * - ID of the first user
 * - ID of the second user
 * - the time the friendship was made
 */
public class Prietenie extends Entity<Long>{

    LocalDateTime date;
    Long firstUser;
    Long secondUser;

    public Prietenie(Long firstUser, Long secondUser, LocalDateTime date) {
        this.date = date;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    /**
     * Getters
     * @return the first user from the friendship
     */
    public Long getFirstUser() {
        return firstUser;
    }

    /**
     *
     * @return the second user from the friendship
     */
    public Long getSecondUser() {
        return secondUser;
    }

    /**
     * Setters which modifies the users of a friendship
     * @param firstUser/secondUser
     */

    public void setFirstUser(Long firstUser) {
        this.firstUser = firstUser;
    }

    public void setSecondUser(Long secondUser) {
        this.secondUser = secondUser;
    }

    /**
     * How a friendship will be showcased
     * @return String
     */
    @Override
    public String toString() {
        return "Friendship: " + this.firstUser + " and " + this.secondUser + " at " + this.date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
