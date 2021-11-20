package socialnetwork.domain;

import java.time.LocalDateTime;

/**
 * Friend class
 */
public class Friend {
    public String firstName;
    public String lastName;
    public LocalDateTime dateTime;


    public Friend(String firstName, String lastName, LocalDateTime dateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
    }

    /**
     * Getters
     * @return firstName/lastName/date of a friend
     */

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getDateTime() {return dateTime;}

    /**
     * Setters which modify a friend
     * @param firstName/lastName/date to be modified
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}

    @Override
    public String toString() {
        return lastName + " " + firstName + " |";
    }
}
