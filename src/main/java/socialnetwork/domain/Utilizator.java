package socialnetwork.domain;

import java.util.List;
import java.util.Objects;

/**
 * Utilizator Entity, main object to be stored
 */
public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private List<Utilizator> friends;


    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Getters
     * @return firstName/lastName/Friends of an user
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
     * @param firstName, lastName of an user
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * How an user will be showcased
     * @return String
     */
    @Override
    public String toString(){
        return "User: " + this.getLastName() + " " + this.getFirstName();
    }

    /**
     * Equals - HashCode rule: if obj1.equals(obj2)
     * Then obj1.hashCode() == obj2.hashCode()
     */

    @Override
    public boolean equals(Object o){
        if (this==o)
            return true;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName())&&
                getLastName().equals(that.getLastName())&&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getFirstName(), getLastName(), getClass());
    }
}
