package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Utilizator from;
    private List<Utilizator> to;
    private String messageBody;
    private LocalDateTime date;
    Message reply;

    public Message(Utilizator from, List<Utilizator> to, String message_body, LocalDateTime date, Message reply) {
        this.from = from;
        this.to = to;
        this.messageBody = message_body;
        this.date = date;
        this.reply = reply;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String message_body) {
        this.messageBody = message_body;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        String str = "Id: " + getId() + "; From: " + from + "; To: ";
        for (var recipient : to) {
            str += recipient.getFirstName() + " " + recipient.getLastName() + ", ";
        }
        str += "; Message: " + messageBody + "; Date: " + date;
        if (reply != null) {
            str += " ReplyTo: " + reply.getId();
        }
        return str;
    }
}
