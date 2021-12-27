package socialnetwork.domain;

import socialnetwork.Util.message.MessageType;

public class MessageDTO {
    private String messageBody;
    private MessageType type;
    private String from;

	public MessageDTO(String messageBody, MessageType type, String from) {
        this.messageBody = messageBody;
        this.type = type;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

	public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

}
