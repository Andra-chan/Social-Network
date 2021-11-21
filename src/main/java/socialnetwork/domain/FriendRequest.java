package socialnetwork.domain;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Long>{
    private Long sender;
    private Long receiver;
    private String status;
    private LocalDateTime localDateTime;

    public FriendRequest(){}

    public FriendRequest(Long sender, Long receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Long getSender() {
        return sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public String getStatus(){
        return status;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setStatus(String status){
        this.status = status;
    }


}
