package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.App;
import socialnetwork.Util.events.ChangeEvent;
import socialnetwork.Util.events.ChangeEventType;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.FriendDTO;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ApplicationController implements Observer<ChangeEvent> {

    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<FriendDTO> modelFriendships = FXCollections.observableArrayList();
    @FXML
    TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator, String> tableColumnLastName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFrFirstName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFrLastName;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFrStatus;
    @FXML
    TableColumn<FriendDTO, String> tableColumnFrDate;
    @FXML
    Button deleteButton;
    @FXML
    Button addFriendButton;
    @FXML
    Button acceptButton;
    @FXML
    TableView<Utilizator> tableViewUsers;
    @FXML
    TableView<FriendDTO> tableViewFriends;
    @FXML
    TextField textFieldUserName;
    @FXML
    TextField textFieldFriendName;
    @FXML
    Label currentUserLabel;
    @FXML
    Button logoutButton;
    private Service service;
    private Long userId;

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        tableColumnFrFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnFrLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnFrStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnFrDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        textFieldUserName.textProperty().addListener(o -> filterUsers());
        textFieldFriendName.textProperty().addListener(o -> filterFriendships());

        tableViewUsers.setItems(modelUsers);
        tableViewFriends.setItems(modelFriendships);

        tableViewFriends.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                deleteButton.setDisable(true);
                acceptButton.setDisable(true);
                return;
            }
            if (newValue.getStatus().equals("ACCEPTED")) {
                deleteButton.setDisable(false);
                acceptButton.setDisable(true);
            }
            if (newValue.getStatus().equals("PENDING")) {
                deleteButton.setDisable(false);
                acceptButton.setDisable(false);
            }
            if (newValue.getStatus().equals("OUTGOING")) {
                deleteButton.setDisable(false);
                acceptButton.setDisable(true);
            }
        });
        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            addFriendButton.setDisable(newValue == null || newValue.getId().equals(userId));
        });
    }

    public void handleDeleteButtonClick() {
        var friendDTO = tableViewFriends.getSelectionModel().getSelectedItem();
        if (friendDTO != null) {
            if (friendDTO.getStatus().equals("ACCEPTED") || friendDTO.getStatus().equals("PENDING")) {
                service.handleFriendRequest(friendDTO.getRequestId(), "R");
            } else if (friendDTO.getStatus().equals("OUTGOING")) {
                service.stopFriendRequest(friendDTO.getRequestId());
            }
        }
    }

    public void handleAcceptButtonClick() {
        var friendDTO = tableViewFriends.getSelectionModel().getSelectedItem();
        service.handleFriendRequest(friendDTO.getRequestId(), "A");
    }

    public void handleAddFriendButtonClick() {
        Utilizator receiver = tableViewUsers.getSelectionModel().getSelectedItem();
        service.addFriendRequest(new FriendRequest(userId, receiver.getId()));
    }

    public void handleLogoutButtonClick() {
        App.changeSceneToLogin(service);
    }

    public void filterUsers() {
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(textFieldUserName.getText());
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(textFieldUserName.getText());
        modelUsers.setAll(getAllUsers()
                .stream().filter(firstNameFilter.or(lastNameFilter)).collect(Collectors.toList()));
    }

    public void filterFriendships() {
        Predicate<FriendDTO> firstNameFilter = u -> u.getFirstName().startsWith(textFieldFriendName.getText());
        Predicate<FriendDTO> lastNameFilter = u -> u.getLastName().startsWith(textFieldFriendName.getText());
        modelFriendships.setAll(getAllFriendRequestsForUser(userId)
                .stream().filter(firstNameFilter.or(lastNameFilter)).collect(Collectors.toList()));
    }

    private List<Utilizator> getAllUsers() {
        return StreamSupport.stream(service.getAllUsers().spliterator(), false).toList();
    }

    private List<FriendDTO> getAllFriendRequestsForUser(Long userId) {
        return service.getFriendRequests(userId)
                .stream()
                .map(x -> {
                    if (x.getReceiver().equals(userId)) {
                        Utilizator sender = service.getUser(x.getSender());
                        return new FriendDTO(sender.getId(), x.getId(), sender.getFirstName(), sender.getLastName(),
                                x.getStatus(), x.getLocalDateTime());
                    }
                    Utilizator receiver = service.getUser(x.getReceiver());
                    return new FriendDTO(receiver.getId(), x.getId(), receiver.getFirstName(), receiver.getLastName(),
                            x.getStatus(), x.getLocalDateTime());
                })
                .collect(Collectors.toList());
    }

    private void updateUserModel() {
        modelUsers.setAll(getAllUsers());
    }

    private void updateFriendshipModel() {
        modelFriendships.setAll(getAllFriendRequestsForUser(userId));
    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;

        service.addObserver(this);
        updateUserModel();

        updateFriendshipModel();

        Utilizator currentUser = service.getUser(userId);
        currentUserLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
    }

    @Override
    public void update(ChangeEvent event) {
        if (event.getType().equals(ChangeEventType.MESSAGE)) {
            return;
        }
        if (event.getType().equals(ChangeEventType.FRIEND_REQUEST) ||
                event.getType().equals(ChangeEventType.FRIENDSHIP)) {
            updateFriendshipModel();
            return;
        }
        if (event.getType().equals(ChangeEventType.USER)) {
            updateUserModel();
            return;
        }
    }
}
