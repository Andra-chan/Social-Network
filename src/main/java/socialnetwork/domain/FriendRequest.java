package socialnetwork.domain;

import java.time.LocalDateTime;

/**
 * FriendRequest class
 */
public class FriendRequest extends Entity<Long> {
    private Long sender;
    private Long receiver;
    private String status;
    private LocalDateTime localDateTime;

    public FriendRequest() {
    }

    public FriendRequest(Long sender, Long receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Getters
     *
     * @return sender/receiver/ldt/status of a friendRequest
     */
    public Long getSender() {
        return sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Setters
     *
     * @param sender/receiver/ldt/status to be modified
     */
    public void setSender(Long sender) {
        this.sender = sender;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
