package socialnetwork.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

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
     * Main intefrace method
     * 
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
            System.out.println("7. Show friends.");
            System.out.println("8.Show friends from a certain month.");
            System.out.println("9.1 Send message.");
            System.out.println("9.2 Reply to message");
            System.out.println("9.3 Print all messages between 2 users.");
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
                    case "7":
                        showFriends();
                        break;
                    case "8":
                        showFriendsFromMonth();
                        break;
                    case "9.1":
                        sendMessage();
                        break;
                    case "9.2":
                        replyToMessage();
                        break;
                    case "9.3":
                        showMessagesBetweenTwoUsers();
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
     * Receives the input from the client and calls the addUser() method from the
     * service
     */
    private void addUser() {
        System.out.println("Insert ID: ");
        Long userID = Long.parseLong(scanner.nextLine());

        System.out.println("Insert last name: ");
        String lastName = scanner.nextLine();

        System.out.println("Insert first name: ");
        String firstName = scanner.nextLine();

        Utilizator user = new Utilizator(firstName, lastName);
        user.setId(userID);
        service.addUser(user);

    }

    /**
     * Receives the input from the client and calls the removeUser() method from the
     * service
     */
    private void removeUser() {
        System.out.println("Insert ID: ");
        Long userID = Long.parseLong(scanner.nextLine());

        System.out.println("Removed - " + service.getUser(userID));
        service.removeUser(userID);
    }

    /**
     * Receives the input from the client and calls the addFriendship() method from
     * the service
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
     * Receives the input from the client and calls the removeFriendship() method
     * from the service
     */
    private void removeFriendship() {
        System.out.println("Insert friendship ID: ");
        Long friendshipID = Long.parseLong(scanner.nextLine());

        System.out.println("Removed - " + service.getFriendship(friendshipID));
        service.removeFriendship(friendshipID);
    }

    /**
     * Calls the nrCommunities() method from the service
     */
    private void communitiesNumber() {
        System.out.println("\nThe number of communities is: " + service.nrCommunities());
    }

    /**
     * Calls the strongestCommunity() method from the service
     */
    private void strongestCommunity() {
        System.out.println("\nThe strongest community is: " + service.strongestCommunity());
    }

    /**
     * Prints all users in the folder
     */
    private void printUsers() {
        System.out.println("Printing users...");
        service.getAllUsers().forEach(System.out::println);
    }

    /**
     * Prints all friendships in the folder
     */
    private void printFriendships() {
        System.out.println("Printing friendships...");
        service.getAllFriendships().forEach(System.out::println);
    }

    /**
     * Receives the input from the client and prints the friends of an user
     */
    private void showFriends() {
        System.out.println("Insert user ID: ");
        Long userID = Long.parseLong(scanner.nextLine());
        System.out.println();
        System.out.println(userID + "'s friends: ");
        service.getFriends(userID).forEach(System.out::println);
    }

    private void showFriendsFromMonth() throws Exception {
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

    private void replyToMessage() {
        System.out.println("Insert user ID:");
        Long creatorId = Long.parseLong(scanner.nextLine());
        System.out.println("Enter reply message ID: ");
        Long replyMessageId = Long.parseLong(scanner.nextLine());
        System.out.println("Message body: ");
        String messageBody = scanner.nextLine();
        service.replyToMessage(replyMessageId, creatorId, messageBody);
    }

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
                        initialMessage.getMessageBody(), initialMessage.getDate(),
                        senderName, receiverName,
                        messageReplyPair.getValue().getMessageBody(), messageReplyPair.getValue().getDate());
            } else {
                Utilizator receiver = initialMessage.getTo().stream()
                        .filter(x -> {
                            return x.getId().equals(idUser1) || x.getId().equals(idUser2);
                        })
                        .findFirst().get();
                String receiverName = receiver.getFirstName() + " " + receiver.getLastName();
                System.out.printf("Message: %s at %s from %s to %s\n",
                        messageReplyPair.getKey().getMessageBody(), messageReplyPair.getKey().getDate(),
                        senderName, receiverName);
            }
        }
    }
}
