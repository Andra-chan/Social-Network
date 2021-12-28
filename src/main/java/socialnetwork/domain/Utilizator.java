package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utilizator Entity, main object to be stored
 */
public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private final List<Utilizator> friends;

    private UserCredentials credentials;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.credentials = new UserCredentials("", "");
        friends = new ArrayList<>();
    }

    public Utilizator(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.credentials = new UserCredentials(email, password);
        friends = new ArrayList<>();
    }

    public String getPassword() {
        return credentials.getPassword();
    }

    public void setPassword(String password) {
        credentials.setPassword(password);
    }

    public String getEmail() {
        return credentials.getEmail();
    }

    /**
     * Getters
     *
     * @return firstName/lastName/Friends of a user
     */

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    /**
     * Setters used to modify an entity
     *
     * @param firstName, lastName of a user
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * How a user will be showcased
     *
     * @return String
     */
    @Override
    public String toString() {
        return "User: " + this.getLastName() + " " + this.getFirstName();
    }

    /**
     * Equals - HashCode rule: if obj1.equals(obj2)
     * Then obj1.hashCode() == obj2.hashCode()
     */

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getClass());
    }
}
