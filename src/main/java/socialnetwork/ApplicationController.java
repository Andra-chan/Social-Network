package socialnetwork;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import socialnetwork.domain.FriendDTO;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ApplicationController {

    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    List<String> modelUsernames;
    ObservableList<FriendDTO> modelFriendships = FXCollections.observableArrayList();

    private Service service;

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
    ComboBox<Utilizator> comboBoxUsers;

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
        comboBoxUsers.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    Utilizator currentUser = comboBoxUsers.getSelectionModel().getSelectedItem();
                    addFriendButton.setDisable(newValue.getId().equals(currentUser.getId()));
                    modelFriendships.setAll(getAllFriendRequestsForUser(currentUser.getId()));
                    filterFriendships();
                }
        );

        tableViewFriends.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==null){
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
        });
        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            Utilizator currentUser = comboBoxUsers.getSelectionModel().getSelectedItem();
            addFriendButton.setDisable(newValue == null || newValue.getId().equals(currentUser.getId()));
        });
    }

    public void handleDeleteButtonClick() {
        var friendDTO = tableViewFriends.getSelectionModel().getSelectedItem();
        service.handleFriendRequest(friendDTO.getRequestId(), "R");
        modelFriendships.setAll(getAllFriendRequestsForUser(comboBoxUsers.getSelectionModel().getSelectedItem().getId()));
    }

    public void handleAcceptButtonClick() {
        var friendDTO = tableViewFriends.getSelectionModel().getSelectedItem();
        service.handleFriendRequest(friendDTO.getRequestId(), "A");
        modelFriendships.setAll(getAllFriendRequestsForUser(comboBoxUsers.getSelectionModel().getSelectedItem().getId()));
    }

    public void handleAddFriendButtonClick() {
        Utilizator sender = comboBoxUsers.getSelectionModel().getSelectedItem();
        Utilizator receiver = tableViewUsers.getSelectionModel().getSelectedItem();
        service.addFriendRequest(new FriendRequest(sender.getId(), receiver.getId()));
        modelFriendships.setAll(getAllFriendRequestsForUser(comboBoxUsers.getSelectionModel().getSelectedItem().getId()));
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
        Utilizator currentUser = comboBoxUsers.getSelectionModel().getSelectedItem();
        modelFriendships.setAll(getAllFriendRequestsForUser(currentUser.getId())
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
                        return new FriendDTO(sender.getId(), x.getId(), sender.getFirstName(), sender.getLastName(), x.getStatus(), x.getLocalDateTime());
                    }
                    Utilizator receiver = service.getUser(x.getReceiver());
                    return new FriendDTO(receiver.getId(), x.getId(), receiver.getFirstName(), receiver.getLastName(), x.getStatus(), x.getLocalDateTime());
                })
                .collect(Collectors.toList());
    }

    public void setService(Service service) {
        this.service = service;
        var userList = getAllUsers();
        modelUsers.setAll(userList);

        modelUsernames = userList.stream().map(x -> x.getFirstName() + " " + x.getLastName()).collect(Collectors.toList());

        comboBoxUsers.getItems().setAll(modelUsers);
        comboBoxUsers.setConverter(new StringConverter<>() {
            @Override
            public String toString(Utilizator object) {
                return object.getFirstName() + " " + object.getLastName();
            }

            @Override
            public Utilizator fromString(String string) {
                return null;
            }
        });
        comboBoxUsers.getSelectionModel().selectFirst();
        modelFriendships.setAll(getAllFriendRequestsForUser(comboBoxUsers.getSelectionModel().getSelectedItem().getId()));
    }
}
