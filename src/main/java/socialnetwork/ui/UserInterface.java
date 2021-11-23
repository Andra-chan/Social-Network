package socialnetwork.ui;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * User Interface
 * Receives the input of the user and calls for the needed method
 */
public class UserInterface {

    private Service service;

    public UserInterface(Service service) {
        this.service = service;
    }

    /**
     * Main interface method
     * @throws Exception whenever a method catches an exception for the input
     */
    public void run() throws Exception{

        boolean continueRunning = true;

        while(continueRunning){
            System.out.println("\n1.1. Add user.");
            System.out.println("1.2. Remove user.");
            System.out.println("2.1. Add friendship.");
            System.out.println("2.2. Remove friendship.");
            System.out.println("3. Communities number.");
            System.out.println("4. Strongest community.");
            System.out.println("5. Print all users.");
            System.out.println("6. Print all friendships.");
            System.out.println("7. Show friends.");
            System.out.println("8. Show friends from a certain month.");
            System.out.println("9. Friend requests.");
            System.out.println("10. EXIT!");

            String command;
            Scanner scanner = new Scanner(System.in);
            command = scanner.nextLine();

            switch(command) {
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
                case "9":{
                    System.out.println("Who are you? Insert your ID: ");
                    Long userID = Long.parseLong(scanner.nextLine());
                    friendRequests(userID);
                }
                break;
                case "10": {
                    continueRunning = false;
                    break;
                }
                default: {
                    System.out.println(("Invalid input!\n"));
                    break;
                }
            }
        }
    }

    /**
     * Receives the input from the client and calls addUser() method from the service
     */
    private void addUser() {
        Scanner scanner = new Scanner(System.in);

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
     * Receives the input from the client and calls removeUser() method from the service
     */
    private void removeUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert ID: ");
        Long userID = Long.parseLong(scanner.nextLine());

        System.out.println("Removed - " + service.getUser(userID));
        service.removeUser(userID);
    }

    /**
     * Receives the input from the client and calls addFriendship() method from the service
     */
    private void addFriendship() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert friendship ID: ");
        Long friendshipID = Long.parseLong(scanner.nextLine());

        System.out.println("Insert first user ID: ");
        Long firstUserID = Long.parseLong(scanner.nextLine());

        System.out.println("Insert second user ID: ");
        Long secondUserID = Long.parseLong(scanner.nextLine());

        Prietenie prietenie = new Prietenie(firstUserID, secondUserID, LocalDateTime.now());
        prietenie.setId(friendshipID);
        service.addFriendship(prietenie);
    }

    /**
     * Receives the input from the client and calls removeFriendship() method from the service
     */
    private void removeFriendship() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert friendship ID: ");
        Long friendshipID = Long.parseLong(scanner.nextLine());

        System.out.println("Removed - " + service.getFriendship(friendshipID));
        service.removeFriendship(friendshipID);
    }

    /**
     * Calls nrCommunities() method from the service
     */
    private void communitiesNumber() {
        System.out.println("\nThe number of communities is: " + service.nrCommunities());
    }

    /**
     * Calls strongestCommunity() method from the service
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
        Scanner scanner = new Scanner(System.in);
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert user ID: ");
        Long userID = Long.parseLong(scanner.nextLine());
        System.out.println("Insert month: ");
        Integer month = Integer.parseInt(scanner.nextLine());
        if(month < 1 || month > 12) {
            throw new Exception("Month has to be in the 1-12 range!");
        }
        System.out.println();
        System.out.println(userID + "'s friends from month " + month +": ");
        service.getFriends(userID, month).forEach(System.out::println);
    }

    /**
     * This method prints the menu for friend requests and calls service for the selected option
     * @param userID the ID of the user who is logged in
     */
    private void friendRequests(Long userID){
        boolean keepLooping = true;

        while(keepLooping){
            System.out.println("\n1. Send friend request.");
            System.out.println("2. Accept/Refuse friend request.");
            System.out.println("3. Go back to the main menu.");

            String cmd;
            Scanner scanner = new Scanner(System.in);
            cmd = scanner.nextLine();

            switch (cmd){
                case "1": {
                    System.out.println("Insert user ID to send friend request to: ");
                    Long userIDtoSendTo = Long.parseLong(scanner.nextLine());
                    FriendRequest request = new FriendRequest(userID, userIDtoSendTo);
                    service.addFriendRequest(request);
                } break;
                case "2":{
                    if(service.getFriendRequests(userID).isEmpty()){
                        System.out.println("You currently have no friend requests!");
                        break;
                    }
                    else {
                        service.getFriendRequests(userID).forEach(req -> {
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
