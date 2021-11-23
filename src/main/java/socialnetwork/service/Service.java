package socialnetwork.service;

import socialnetwork.domain.Friend;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service class that implements all methods
 */
public class Service {
    private Repository<Long, Utilizator> userRepository;
    private Repository<Long, Prietenie> friendshipRepository;
    private Repository<Long, FriendRequest> friendRequestRepository;

    public Service(Repository<Long, Utilizator> userRepository, Repository<Long, Prietenie> friendshipRepository,
                   Repository<Long, FriendRequest> friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    /**
     * Adds a user via save() method from the repository
     * @param user entity to be stored
     * @return
     */
    public Utilizator addUser(Utilizator user){
        return this.userRepository.save(user);
    }

    /**
     *
     * @return all users in the repository
     */
    public  Iterable<Utilizator> getAllUsers(){
        return this.userRepository.findAll();
    }

    /**
     *
     * @return all friendships in the repository
     */
    public Iterable<Prietenie> getAllFriendships(){
        return this.friendshipRepository.findAll();
    }

    /**
     * Removes a user via delete() method from the repository
     * @return the entity that was removed or not
     */
    public Utilizator removeUser(Long userID){
        Utilizator toBeReturned = userRepository.delete(userID);
        List<Prietenie> allFriendships = new ArrayList<>();
        this.getAllFriendships().forEach(allFriendships::add);
        allFriendships.forEach(fr->{
            if(fr.getFirstUser().equals(userID) || fr.getSecondUser().equals(userID))
                friendshipRepository.delete(fr.getId());
        });
        return toBeReturned;
    }

    /**
     *
     * @param userID ID of the user
     * @return the user with the given ID
     */
    public Utilizator getUser(Long userID){
        return userRepository.findOne(userID);
    }

    /**
     * Adds a friendship via save() method from the repository
     * @param prietenie entity to be stored
     * @return the stored entity
     */
    public Prietenie addFriendship(Prietenie prietenie){
        return this.friendshipRepository.save(prietenie);
    }

    /**
     *
     * @return a friendship from the repository
     */
    public Prietenie getFriendship(Long friendshipID){
        return friendshipRepository.findOne(friendshipID);
    }

    public Prietenie removeFriendship(Utilizator user, Utilizator friend){
        for(Prietenie pr:this.getAllFriendships()){
            if((pr.getFirstUser().equals(user.getId())) || (pr.getSecondUser().equals(user.getId()))){
                return this.friendshipRepository.delete(pr.getId());
            }
        }
        return null;
    }

    /**
     * Removes a friendship via delete() method from the repository
     * @param id of the entity to be deleted
     * @return the deleted entity
     */
    public Prietenie removeFriendship(Long id){
        return this.friendshipRepository.delete(id);
    }

    /**
     * Method which determines the maximum number of communities in the network
     * @return the maximum number of communities
     */
    public Integer nrCommunities() {
        int nrCommunities = 0;

        List<Utilizator> allUsers = new ArrayList<>();
        List<Utilizator> visitedUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);

        for(Utilizator user: allUsers){
            if(!visitedUsers.contains(user)){
                nrCommunities++;
                communitiesDeterminant(visitedUsers, user);
            }
        }
        return nrCommunities;
     }

    /**
     * Method which determines the strongest community in the network
     * @return a list with the strongest community
     */
    public List<Utilizator> strongestCommunity() {
        int strongestCommunity = 0;
        List<Utilizator> strongestList = new ArrayList<>();

        List<Utilizator> allUsers = new ArrayList<>();
        List<Utilizator> visitedUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);

        for(Utilizator user: allUsers){
            if (!visitedUsers.contains(user)){
                List<Utilizator> currentUsers = communitiesDeterminant(visitedUsers, user);

                if(currentUsers.size() > strongestCommunity){
                    strongestList.clear();
                    strongestCommunity = currentUsers.size();
                    System.out.println(currentUsers);
                    strongestList.addAll(currentUsers);
                }
            }
        }
        return strongestList;
    }

    /**
     * Refactored method to determine the strongest community and the number of communities
     * @param visitedUsers users that where checked if they are part of the community
     * @param user to be checked if they are part of the community
     * @return current users in the community
     */
    private List<Utilizator> communitiesDeterminant(List<Utilizator> visitedUsers, Utilizator user) {
        List<Utilizator> currentUsers = new ArrayList<>();
        currentUsers.add(user);
        for(int i = 0; i < currentUsers.size(); i++){
            int finalIndex = i;
            friendshipRepository.findAll().forEach(fr->{
                Utilizator firstUser = userRepository.findOne(fr.getFirstUser());
                Utilizator secondUser = userRepository.findOne(fr.getSecondUser());
                if (currentUsers.get(finalIndex) == firstUser || currentUsers.get(finalIndex) == secondUser){
                    if (!visitedUsers.contains(firstUser))
                        visitedUsers.add(firstUser);
                    if (!visitedUsers.contains(secondUser))
                        visitedUsers.add(secondUser);
                    if (!currentUsers.contains(firstUser))
                        currentUsers.add(firstUser);
                    if (!currentUsers.contains(secondUser))
                        currentUsers.add(secondUser);
                }
            });
        }
        return currentUsers;
    }

    /**
     *
     * @param userID the ID of o user
     * @return a list of Friends of the given user
     */
    public List<Friend> getFriends(Long userID) {
        List<Prietenie> friendships = new ArrayList<>();
        friendshipRepository.findAll().forEach(friendships::add);

        return friendships.stream().filter(fr ->
                fr.getSecondUser().equals(userID) || fr.getFirstUser().equals(userID))
                .map(fr -> {
                    if(fr.getFirstUser().equals(userID)){
                        Utilizator user = userRepository.findOne(fr.getSecondUser());
                        return new Friend(user.getFirstName(), user.getLastName(), fr.getDate());
                    }
                    else{
                        Utilizator user = userRepository.findOne(fr.getFirstUser());
                        return new Friend(user.getFirstName(), user.getLastName(), fr.getDate());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     *
     * @param userID the ID of o user
     * @param month the month which corresponds to the friend request
     * @return a list of Friends of the given user in a certain month
     */
    public List<Friend> getFriends(Long userID, Integer month){
        return this.getFriends(userID).stream()
                .filter(fr-> fr.getDateTime().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    /**
     * Adds a friend request using the save() from the repository
     * @param request to be saved
     * @return the saved friend request
     */
    public FriendRequest addFriendRequest(FriendRequest request) {
        request.setStatus("PENDING");
        request.setLocalDateTime(LocalDateTime.now());
        return this.friendRequestRepository.save(request);
    }

    /**
     *
     * @param userID id of a user
     * @return returns a list of friend requests of a user
     */
    public List<FriendRequest> getFriendRequests(Long userID) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        this.friendRequestRepository.findAll().forEach(req -> {
            if(req.getReceiver().equals(userID) && req.getStatus().equals("PENDING")) friendRequests.add(req);
        });
        return friendRequests;
    }

    /**
     * handles a friend requests:
     *  - if accepted, the friendship will be stored and the friend request will be updated with the status ACCEPTED
     *  - if denied, the friend request will be updated with the status REJECTED
     * @param requestID the ID of a request
     * @param decision whether a user accepts or rejects a friend request
     */
    public void handleFriendRequest(Long requestID, String decision) {

        FriendRequest fr = friendRequestRepository.findOne(requestID);
        if(decision.equals("A")){
            Prietenie prietenie = new Prietenie(fr.getSender(), fr.getReceiver(), LocalDateTime.now());

            AtomicLong friendshipID = new AtomicLong(1L);
            friendshipRepository.findAll().forEach((friendship -> friendshipID.getAndIncrement()));
            prietenie.setId(friendshipID.get());

            System.out.println(prietenie.getId());
            friendshipRepository.save(prietenie);
            fr.setStatus("ACCEPTED");
            fr.setLocalDateTime(LocalDateTime.now());
            friendRequestRepository.update(fr);
        }
        else {
            fr.setStatus("REJECTED");
            fr.setLocalDateTime(LocalDateTime.now());
            friendRequestRepository.delete(requestID);
        }
    }
}
