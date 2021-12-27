package socialnetwork.controller;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import socialnetwork.HelloApplication;
import socialnetwork.Util.message.MessageType;
import socialnetwork.domain.MessageDTO;
import socialnetwork.service.Service;

public class ChatboxController {

    @FXML
    GridPane chatGridPane;
    @FXML
    Button sendButton;
    @FXML
    TextField chatTextField;

    @FXML
    Button refreshButton;

    @FXML
    ScrollPane chatScrollPane;

    private Service service;
    Long sender;
    Long receiver;

    @FXML
    public void initialize() {
        chatScrollPane.vvalueProperty().bind(chatGridPane.heightProperty());
    }

    private List<MessageDTO> getAllMessages() {
        return service.getMessageListBetweenTwoUsers(sender, receiver)
                .stream()
                .map(x -> new MessageDTO(
                        x.getId(),
                        x.getReply() != null ? x.getReply().getId() : null,
                        x.getMessageBody(),
                        x.getFrom().getId().equals(sender) ? MessageType.SEND : MessageType.RECEIVE,
                        x.getFrom().getFirstName() + " " + x.getFrom().getLastName()))
                .collect(Collectors.toList());
    }

    private void updateModel() {
        var messages = getAllMessages();
        for (var message : messages) {
            addMessageToChat(message);
        }
    }

    private MessageDTO getLastMessage() {
        var messages = getAllMessages();
        if (messages.size() == 0) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    private void addMessageToChat(MessageDTO message) {
        if (message == null) {
            return;
        }
        final Text agent = message.getType().equals(MessageType.SEND) ? new Text("You") : new Text(message.getFrom());
        Text recordMessage = new Text(message.getMessageBody());
        final var messageFlow = new TextFlow(recordMessage);

        if (message.getType().equals(MessageType.SEND)) {
            recordMessage.getStyleClass().add("sentMessageContent");
            messageFlow.getStyleClass().addAll("sentMessage", "message");
        } else {
            recordMessage.getStyleClass().add("receivedMessageContent");
            messageFlow.getStyleClass().addAll("receivedMessage", "message");
        }
        messageFlow.setLineSpacing(2);

        final var columnIndex = message.getType().equals(MessageType.SEND) ? 1 : 0;
        final var columnSpan = 2;

        final var rowIndex = chatGridPane.getRowCount();
        final var rowSpan = 1;

        VBox vBox = getVBox(agent, messageFlow);
        chatGridPane.add(vBox, columnIndex, rowIndex, columnSpan, rowSpan);
    }

    private VBox getVBox(Node... nodes) {
        final var vbox = new VBox(nodes);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    public void onSendButtonPress() {
        String messageText = chatTextField.getText();
        if (!messageText.isBlank()) {
            ArrayList<Long> to = new ArrayList<>();
            to.add(receiver);
            var message = service.sendMessage(sender, to, messageText);
            if (message == null) {
                addMessageToChat(getLastMessage());
            }
            chatTextField.clear();
        }
    }

    public void setService(Service service, Long userId, Long receiver) {
        this.service = service;
        this.sender = userId;
        this.receiver = receiver;
        updateModel();
    }
}
