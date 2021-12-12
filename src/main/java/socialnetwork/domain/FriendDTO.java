package socialnetwork.domain;

import socialnetwork.Util.Constants;

import java.time.LocalDateTime;

public class FriendDTO {
    private Long friendId;
    private Long requestId;
    private String firstName;
    private String lastName;
    private String status;
    private LocalDateTime date;

    public FriendDTO(Long friendId,Long requestId, String firstName, String lastName, String status, LocalDateTime date) {
        this.friendId = friendId;
        this.requestId = requestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date.format(Constants.dateTimeFormat);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
