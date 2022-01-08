package socialnetwork.domain;

public class UserCredentials extends Entity<String> {
    private String password;
    private Long userId;

    public UserCredentials(String email, String password) {
        this.setId(email);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.getId();
    }

    public void setEmail(String email) {
        setId(email);
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long id) {
        this.userId = id;
    }
}
