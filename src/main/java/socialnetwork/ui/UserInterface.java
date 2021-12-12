package socialnetwork.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import socialnetwork.domain.Message;

import socialnetwork.domain.FriendRequest;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;
import socialnetwork.Util.Constants;

/**
 * User Interface
 * Receives the input of the user and calls for the needed method
 */
public class UserInterface {

    private Service service;
    private Scanner scanner;

    public UserInterface(Service service) {
        this.service = service;
        scanner = new Scanner(System.in);
    }

    /**
     * Main interface method
     * @throws Exception whenever a method catches an exception for the input
     */
    public void run() throws Exception {

        boolean continueRunning = true;

        while (continueRunning) {
            System.out.println("\n1.1. Add user.");
            System.out.println("1.2. Remove user.");
            System.out.println("2.1. Add friendship.");
            System.out.println("2.2. Remove friendship.");
            System.out.println("3. Communities number.");
            System.out.println("4. Strongest community.");
            System.out.println("5. Print all users.");
            System.out.println("6. Print all friendships.");
            System.out.println("7.1. Show friends.");
            System.out.println("7.2.Show friends from a certain month.");
            System.out.println("8.1 Send message.");
            System.out.println("8.2 Reply to message");
            System.out.println("8.3 Print all messages between 2 users.");
            System.out.println("9. Friend requests.");
            System.out.println("10. EXIT!");

            String command;
            try {
                command = scanner.nextLine();
                switch (command) {
                    case "1.1":
                        addUser();
                        break;
                    case "1.2":
                        removeUser();
                        break;
                    case "2.1":
                        addFriendship();
                        break;
                    case "2.2":
                        removeFriendship();
                        break;
                    case "3":
                        communitiesNumber();
                        break;
                    case "4":
                        strongestCommunity();
                        break;
                    case "5":
                        printUsers();
                        break;
                    case "6":
                        printFriendships();
                        break;
                    case "7.1":
                        showFriends();
                        break;
                    case "7.2":
                        showFriendsFromMonth();
                        break;
                    case "8.1":
                        sendMessage();
                        break;
                    case "8.2":
                        replyToMessage();
                        break;
                    case "8.3":
                        showMessagesBetweenTwoUsers();
                        break;
                    case "9":{
                        System.out.println("Who are you? Insert your ID: ");
                        Long userID = Long.parseLong(scanner.nextLine());
                        if(!service.checkIfUserExists(userID)) {
                            System.out.println("This user does not exist!");
                            break;
                        }
                        friendRequests(userID);
                    }
                    break;
                    case "10": {
                        continueRunning = false;
                        break;
                    }
                    default: {
                        System.out.println(("Invalid input!\n"));
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Receives the input from the client and calls addUser() method from the socialnetwork.service
     */
    private void addUser() {

        System.out.println("Insert last name: ");
        String lastName = scanner.nextLine();

        System.out.println("Insert first name: ");
        String firstName = scanner.nextLine();

        Utilizator user = new Utilizator(firstName, lastName);
        service.addUser(user);

    }

    /**
     * Receives the input from the client and calls removeUser() method from the socialnetwork.service
     */
    private void removeUser() {
        System.out.println("Insert ID: ");
        Long userID = Long.parseLong(scanner.nextLine());

        System.out.println("Removed - " + service.getUser(userID));
        service.removeUser(userID);
    }

    /**
     * Receives the input from the client and calls addFriendship() method from the socialnetwork.service
     */
    private void addFriendship() {
        System.out.println("Insert first user ID: ");
        Long firstUserID = Long.parseLong(scanner.nextLine());

        System.out.println("Insert second user ID: ");
        Long secondUserID = Long.parseLong(scanner.nextLine());

        Prietenie prietenie = new Prietenie(firstUserID, secondUserID, LocalDateTime.now());
        prietenie.setId(0l);
        service.addFriendship(prietenie);
    }

    /**
     * Receives the input from the client and calls removeFriendship() method from the socialnetwork.service
     */
    private void removeFriendship() {
        System.out.println("Insert friendship ID: ");
        Long friendshipID = Long.parseLong(scanner.nextLine());

        System.out.println("Removed - " + service.getFriendship(friendshipID));
        service.removeFriendship(friendshipID);
    }

    /**
     * Calls nrCommunities() method from the socialnetwork.service
     */
    private void communitiesNumber() {
        System.out.println("\nThe number of communities is: " + service.nrCommunities());
    }

    /**
     * Calls strongestCommunity() method from the socialnetwork.service
     */
    private void strongestCommunity() {
        System.out.println("\nThe strongest community is: " + service.strongestCommunity());
    }

    /**
     * Prints all users in the database
     */
    private void printUsers() {
        System.out.println("Printing users...");
        service.getAllUsers().forEach(System.out::println);
    }

    /**
     * Prints all friendships in the database
     */
    private void printFriendships() {
        System.out.println("Printing friendships...");
        service.getAllFriendships().forEach(System.out::println);
    }

    /**
     * Receives the input from the client and prints the friends of a user
     */
    private void showFriends() {
        System.out.println("Insert user ID: ");
        Long userID = Long.parseLong(scanner.nextLine());
        System.out.println();
        System.out.println(userID + "'s friends: ");
        service.getFriends(userID).forEach(System.out::println);
    }

    /**
     * Receives the input from the client and prints the friends of a user from a chosen month
     */
    private void showFriendsFromMonth() throws Exception{
        System.out.println("Insert user ID: ");
        Long userID = Long.parseLong(scanner.nextLine());
        System.out.println("Insert month: ");
        Integer month = Integer.parseInt(scanner.nextLine());
        if (month < 1 || month > 12) {
            throw new Exception("Month has to be in the 1-12 range!");
        }
        System.out.println();
        System.out.println(userID + "'s friends from month " + month + ": ");
        service.getFriends(userID, month).forEach(System.out::println);
    }

    /**
     * Receives input form the client and calls sendMessage from socialnetwork.service.
     */
    private void sendMessage() {
        System.out.println("Insert user ID:");
        Long creatorId = Long.parseLong(scanner.nextLine());
        List<Long> recipients = new ArrayList<>();
        boolean done = false;
        while (!done) {
            System.out.println("Insert recipient id(type 0 to stop):");
            Long id = Long.parseLong(scanner.nextLine());
            if (id == 0) {
                done = true;
                continue;
            }
            recipients.add(id);
        }
        System.out.println("Message body: ");
        String messageBody = scanner.nextLine();
        service.sendMessage(creatorId, recipients, messageBody);
    }

    /**
     * Receives input from the client and calls replyToMessage from socialnetwork.service
     */
    private void replyToMessage() {
        System.out.println("Insert user ID:");
        Long creatorId = Long.parseLong(scanner.nextLine());
        System.out.println("Enter reply message ID: ");
        Long replyMessageId = Long.parseLong(scanner.nextLine());
        System.out.println("Message body: ");
        String messageBody = scanner.nextLine();
        service.replyToMessage(replyMessageId, creatorId, messageBody);
    }

    /**
     * Receive input from the client, calls getAllMessagesBetweenTwoUsers from
     * socialnetwork.service and prints all conversations between 2 users
     */
    private void showMessagesBetweenTwoUsers() {
        System.out.println("First user's ID:");
        Long idUser1 = Long.parseLong(scanner.nextLine());
        System.out.println("Second user's ID:");
        Long idUser2 = Long.parseLong(scanner.nextLine());
        var conversations = service.getAllMessagesBetweenTwoUsers(idUser1, idUser2);

        for (var messageReplyPair : conversations) {
            Message initialMessage = messageReplyPair.getKey();
            String senderName = initialMessage.getFrom().getFirstName() + " " + initialMessage.getFrom().getLastName();
            if (messageReplyPair.getValue() != null) {
                Message replyMessage = messageReplyPair.getValue();
                String receiverName = replyMessage.getFrom().getFirstName() + " "
                        + replyMessage.getFrom().getLastName();
                System.out.printf("Message: %s  at %s from %s to %s; Reply: %s at %s\n",
                        initialMessage.getMessageBody(), initialMessage.getDate().format(Constants.dateTimeFormat),
                        senderName, receiverName,
                        messageReplyPair.getValue().getMessageBody(),
                        messageReplyPair.getValue().getDate().format(Constants.dateTimeFormat));
            } else {
                Utilizator receiver = initialMessage.getTo().stream()
                        .filter(x -> {
                            return x.getId().equals(idUser1) || x.getId().equals(idUser2);
                        })
                        .findFirst().get();
                String receiverName = receiver.getFirstName() + " " + receiver.getLastName();
                System.out.printf("Message: %s at %s from %s to %s\n",
                        messageReplyPair.getKey().getMessageBody(),
                        messageReplyPair.getKey().getDate().format(Constants.dateTimeFormat),
                        senderName, receiverName);
            }
        }
    }
  
    /**
     * This method prints the menu for friend requests and calls socialnetwork.service for the selected option
     * @param userID the ID of the user who is logged in
     */
    private void friendRequests(Long userID){
        boolean keepLooping = true;

        while(keepLooping){
            System.out.println("\n1. Send friend request.");
            System.out.println("2. Accept/Refuse friend request.");
            System.out.println("3. Go back to the main menu.");

            String cmd;
            cmd = scanner.nextLine();

            switch (cmd){
                case "1": {
                    System.out.println("Insert user ID to send friend request to: ");
                    Long userIDtoSendTo = Long.parseLong(scanner.nextLine());
                    if(!service.checkIfUserExists(userIDtoSendTo)) {
                        System.out.println("This user does not exist!");
                        break;
                    }
                    FriendRequest request = new FriendRequest(userID, userIDtoSendTo);
                    var req = service.verifyPendingRequest(request);
                    if(req.isPresent()) {
                        System.out.println("This friend request already exists! Do you want to accept it?");
                        System.out.println("Insert: Y/N");
                        String decision = scanner.nextLine();
                        if(decision.equals("Y"))
                            service.handleFriendRequest(req.get().getId(), "A" );
                    }
                    else
                        service.addFriendRequest(request);
                } break;
                case "2":{
                    if(service.getPendingFriendRequests(userID).isEmpty()){
                        System.out.println("You currently have no friend requests!");
                        break;
                    }
                    else {
                        service.getPendingFriendRequests(userID).forEach(req -> {
                            System.out.println("Friend Request ID: " + req.getId() + ". From: " + this.service.getUser(req.getSender()).getFirstName());
                        });
                        System.out.println("Insert Friend Request ID from the list: ");
                        Long requestID = Long.parseLong(scanner.nextLine());
                        System.out.println("Insert A to accept, R to refuse.");
                        String decision = scanner.nextLine();
                        service.handleFriendRequest(requestID, decision);
                    }
                }break;
                case "3":{
                    keepLooping = false;
                    break;
                }
                default:{
                    System.out.println("Invalid input!\n");
                    break;
                }
            }
        }
    }
}
