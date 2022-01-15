package socialnetwork.domain;

import socialnetwork.Util.message.MessageType;

public class MessageDTO {
    private Long messageId;
    private Long replyMessageId;
    private String messageBody;
    private MessageType type;
    private String from;

    public MessageDTO(Long messageId, Long replyMessageId, String messageBody, MessageType type, String from) {
        this.messageId = messageId;
        this.replyMessageId = replyMessageId;
        this.messageBody = messageBody;
        this.type = type;
        this.from = from;
    }

    public Boolean isReply() {
        return replyMessageId != null;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(Long replyMessageId) {
        this.replyMessageId = replyMessageId;
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