package socialnetwork.domain;

import java.time.LocalDateTime;

/**
 * Friend class
 */
public class Friend implements ImageHolder {
    private String firstName;
    private String lastName;
    private LocalDateTime dateTime;
    private String profileImagePath;
    private final Long id;


    public Friend(String firstName, String lastName, LocalDateTime dateTime, Long id, String profileImagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
        this.id = id;
        this.profileImagePath = profileImagePath;
    }

    public String getImagePath() {
        return profileImagePath;
    }

    public void setImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    /**
     * Getters
     *
     * @return firstName/lastName/date of a friend
     */

    public String getFirstName() {
        return firstName;
    }

    /**
     * Setters which modify a friend
     *
     * @param firstName/lastName/date to be modified
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName + " |";
    }

    public Long getId() {
        return id;
    }
}
