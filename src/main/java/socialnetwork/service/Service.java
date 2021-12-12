package socialnetwork.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.swing.text.Utilities;

import socialnetwork.domain.Friend;

import socialnetwork.domain.Message;
import socialnetwork.domain.FriendRequest;
import socialnetwork.service.ServiceException;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

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

    private Repository<Long, Message> messageRepository;
    private Repository<Long, FriendRequest> friendRequestRepository;

    public Service(Repository<Long, Utilizator> userRepository, Repository<Long, Prietenie> friendshipRepository,
            Repository<Long, FriendRequest> friendRequestRepository, Repository<Long, Message> messageRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Adds a user via save() method from the repository
     * 
     * @param user entity to be stored
     * @return
     */
    public Utilizator addUser(Utilizator user) {
        return this.userRepository.save(user);
    }

    /**
     *
     * @return all users in the repository
     */
    public Iterable<Utilizator> getAllUsers() {
        return this.userRepository.findAll();
    }

    /**
     *
     * @return all friendships in the repository
     */
    public Iterable<Prietenie> getAllFriendships() {
        return this.friendshipRepository.findAll();
    }

    /**
     * Removes a user via delete() method from the repository
     * 
     * @return the entity that was removed or not
     */
    public Utilizator removeUser(Long userID) {
        Utilizator toBeReturned = userRepository.delete(userID);
        List<Prietenie> allFriendships = new ArrayList<>();
        this.getAllFriendships().forEach(allFriendships::add);
        allFriendships.forEach(fr -> {
            if (fr.getFirstUser().equals(userID) || fr.getSecondUser().equals(userID))
                friendshipRepository.delete(fr.getId());
        });
        return toBeReturned;
    }

    /**
     *
     * @param userID ID of the user
     * @return the user with the given ID
     */
    public Utilizator getUser(Long userID) {
        return userRepository.findOne(userID);
    }

    /**
     * Adds a friendship via save() method from the repository
     * 
     * @param prietenie entity to be stored
     * @return the stored entity
     */
    public Prietenie addFriendship(Prietenie prietenie) {
        return this.friendshipRepository.save(prietenie);
    }

    /**
     *
     * @return a friendship from the repository
     */
    public Prietenie getFriendship(Long friendshipID) {
        return friendshipRepository.findOne(friendshipID);
    }

    public Prietenie removeFriendship(Utilizator user, Utilizator friend) {
        for (Prietenie pr : this.getAllFriendships()) {
            if ((pr.getFirstUser().equals(user.getId())) || (pr.getSecondUser().equals(user.getId()))) {
                return this.friendshipRepository.delete(pr.getId());
            }
        }
        return null;
    }

    /**
     * Removes a friendship via delete() method from the repository
     * 
     * @param id of the entity to be deleted
     * @return the deleted entity
     */
    public Prietenie removeFriendship(Long id) {
        return this.friendshipRepository.delete(id);
    }

    /**
     * Method which determines the maximum number of communities in the network
     * 
     * @return the maximum number of communities
     */
    public Integer nrCommunities() {
        int nrCommunities = 0;

        List<Utilizator> allUsers = new ArrayList<>();
        List<Utilizator> visitedUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);

        for (Utilizator user : allUsers) {
            if (!visitedUsers.contains(user)) {
                nrCommunities++;
                communitiesDeterminant(visitedUsers, user);
            }
        }
        return nrCommunities;
    }

    /**
     * Method which determines the strongest community in the network
     * 
     * @return a list with the strongest community
     */
    public List<Utilizator> strongestCommunity() {
        int strongestCommunity = 0;
        List<Utilizator> strongestList = new ArrayList<>();

        List<Utilizator> allUsers = new ArrayList<>();
        List<Utilizator> visitedUsers = new ArrayList<>();
        userRepository.findAll().forEach(allUsers::add);

        for (Utilizator user : allUsers) {
            if (!visitedUsers.contains(user)) {
                List<Utilizator> currentUsers = communitiesDeterminant(visitedUsers, user);

                if (currentUsers.size() > strongestCommunity) {
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
     * Refactored method to determine the strongest community and the number of
     * communities
     * 
     * @param visitedUsers users that where checked if they are part of the
     *                     community
     * @param user         to be checked if they are part of the community
     * @return current users in the community
     */
    private List<Utilizator> communitiesDeterminant(List<Utilizator> visitedUsers, Utilizator user) {
        List<Utilizator> currentUsers = new ArrayList<>();
        currentUsers.add(user);
        for (int i = 0; i < currentUsers.size(); i++) {
            int finalIndex = i;
            friendshipRepository.findAll().forEach(fr -> {
                Utilizator firstUser = userRepository.findOne(fr.getFirstUser());
                Utilizator secondUser = userRepository.findOne(fr.getSecondUser());
                if (currentUsers.get(finalIndex) == firstUser || currentUsers.get(finalIndex) == secondUser) {
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

        return friendships.stream().filter(fr -> fr.getSecondUser().equals(userID) || fr.getFirstUser().equals(userID))
                .map(fr -> {
                    if (fr.getFirstUser().equals(userID)) {
                        Utilizator user = userRepository.findOne(fr.getSecondUser());
                        return new Friend(user.getFirstName(), user.getLastName(), fr.getDate());
                    } else {
                        Utilizator user = userRepository.findOne(fr.getFirstUser());
                        return new Friend(user.getFirstName(), user.getLastName(), fr.getDate());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * @param userID the ID of a user
     * @param month  the month which corresponds to the friend request
     * @return a list of Friends of the given user in a certain month
     */
    public List<Friend> getFriends(Long userID, Integer month) {
        return this.getFriends(userID).stream()
                .filter(fr -> fr.getDateTime().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    /**
     * Sends a message from a user to a list of users.
     *
     * @param creatorId   the user's id(message sender)
     * @param to          a list of id's representing the message recipients.
     * @param messageBody the message's body.
     *
     * @return null if the message wasn't sent, and the message itself
     *         otherwise.
     * @throws RepositoryException if the user with the id creatorId doesn't exist,
     *                             or if any of the users in the recipients list
     *                             don't exist
     */
    public Message sendMessage(Long creatorId, List<Long> to, String messageBody) {
        Message message = new Message(null, null, messageBody, LocalDateTime.now(), null);
        var creator = userRepository.findOne(creatorId);
        message.setFrom(creator);

        List<Utilizator> toUsers = new ArrayList<>();
        for (var userId : to) {
            toUsers.add(userRepository.findOne(userId));
        }
        message.setTo(toUsers);
        return messageRepository.save(message);
    }

    /**
     * Replies to a message
     *
     * @param messageId      the message's id that you're trying to reply to
     * @param replyCreatorId the user's id that created the reply
     * @param messageBody    the reply's body
     *
     * @return null if the message wasn't sent(the message you want to reply to
     *         doesn't exist, or it wasn't sent to replyCreatorId), the message
     *         otherwise
     */
    public Message replyToMessage(Long messageId, Long replyCreatorId, String messageBody) {
        Message message = new Message(null, null, messageBody, LocalDateTime.now(), null);
        var reply = messageRepository.findOne(messageId);
        var replyCreator = userRepository.findOne(replyCreatorId);

        // if the message the user is trying to reply to hasn't been sent to
        // him (replyCreatorId is not in the recipients list of the message).
        boolean isValidReply = reply.getTo().stream()
                .anyMatch(x -> x.getId().equals(replyCreatorId));
        if (!isValidReply) {
            throw new ServiceException("The user doesn't have the message you're trying to reply to.");
        }
        isValidReply = !StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .anyMatch(x -> x.getFrom().getId().equals(replyCreatorId)
                        && x.getReply() != null
                        && x.getReply().getId().equals(messageId));
        if (!isValidReply) {
            throw new ServiceException("Cannot reply to the same message twice");
        }

        List<Utilizator> to = new ArrayList<>();
        var messageRecipient = userRepository.findOne(reply.getFrom().getId());
        to.add(messageRecipient);
        message.setFrom(replyCreator);
        message.setTo(to);
        message.setReply(reply);

        return messageRepository.save(message);
    }

    public void replyAll(Long userID, String messageBody) {
        Message message = new Message(null, null, messageBody, LocalDateTime.now(), null);
        Utilizator replyCreator = userRepository.findOne(userID);

        Predicate<Message> isValidReply = m -> m.getTo().stream()
                .anyMatch(x -> x.getId().equals(userID));

        Predicate<Message> checkReplyLimit = m -> !StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .anyMatch(x -> x.getFrom().getId().equals(userID)
                        && x.getReply() != null
                        && x.getReply().getId().equals(m.getId()));

        var messages = StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .filter(isValidReply.and(checkReplyLimit)).collect(Collectors.toList());
        for (var currentMessage : messages) { 
            List<Utilizator> to = new ArrayList<>();
            var messageRecipient = userRepository.findOne(currentMessage.getFrom().getId());
            to.add(messageRecipient);
            message.setFrom(replyCreator);
            message.setTo(to);
            message.setReply(currentMessage);
            messageRepository.save(message);
        }
    }

    /**
     * Get a list of all messages between two users.
     *
     * @param userId1 the first user's id
     * @param userId2 the second user's id
     *
     * @return a list of entries of all messages between two users(in chronological
     *         order). Entry.key is a
     *         message, Entry.value is the reply(if it exists)
     */
    public List<Map.Entry<Message, Message>> getAllMessagesBetweenTwoUsers(Long userId1, Long userId2) {
        var allMessages = messageRepository.findAll();
        Map<Long, Message> messages = StreamSupport.stream(allMessages.spliterator(), false)
                .filter(x -> {
                    // creator must be one of the provided users
                    if (x.getFrom().getId().equals(userId1) || x.getFrom().getId().equals(userId2)) {
                        return true;
                    }
                    return false;
                })
                .filter(x -> {
                    // message must have one of the users in the recipients list
                    boolean from1to2 = x.getTo().stream().anyMatch(y -> y.getId().equals(userId1));
                    boolean from2to1 = x.getTo().stream().anyMatch(y -> y.getId().equals(userId2));
                    return from1to2 || from2to1;
                })
                .collect(Collectors.toMap(x -> x.getId(), x -> x));

        HashMap<Message, Message> messageReply = getMessageReplyPairs(messages);

        var conversations = messageReply.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey,
                        (a, b) -> a.getDate().compareTo(b.getDate())))
                .collect(Collectors.toList());
        return conversations;
    }

    /**
     * Makes a map of messages where the key is a message and the value is a message
     * that replies to the key message
     *
     * @param messages a map of messages, key is the message id, value is the
     *                 message itself
     * @return HashMap<Message, Message> where key is a message, and value is the
     *         message that replies to the key(null otherwise)
     */
    private HashMap<Message, Message> getMessageReplyPairs(Map<Long, Message> messages) {
        HashMap<Message, Message> messageReply = new HashMap<>();
        for (var messageEntry : messages.entrySet()) {
            Predicate<Map.Entry<Long, Message>> hasParent = x -> {
                if (x.getValue().getReply() != null) {
                    return x.getValue().getReply().getId().equals(messageEntry.getValue().getId());
                }
                return false;
            };
            var replyMessage = messages.entrySet().stream().filter(hasParent).findAny();
            if (replyMessage.isPresent()) {
                messageReply.put(messageEntry.getValue(), replyMessage.get().getValue());
            } else {
                messageReply.put(messageEntry.getValue(), null);
            }
        }
        return messageReply;
    }

    /**
     * Adds a friend request using the save() from the repository
     * 
     * @param request to be saved
     * @return the saved friend request
     */
    public FriendRequest addFriendRequest(FriendRequest request) {
        request.setStatus("PENDING");
        request.setLocalDateTime(LocalDateTime.now());
        return this.friendRequestRepository.save(request);
    }

    public Optional<FriendRequest> verifyPendingRequest(FriendRequest request) {
        return StreamSupport.stream(this.friendRequestRepository.findAll().spliterator(), false).filter(req -> {
            if (req.getSender() == request.getReceiver() && req.getReceiver() == request.getSender()
                    && req.getStatus().equals("PENDING"))
                return true;
            return false;
        }).findFirst();
    }

    public boolean checkIfUserExists(Long userID) {
        try {
            userRepository.findOne(userID);
        } catch (RepositoryException e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param userID id of a user
     * @return returns a list of friend requests of a user
     */
    public List<FriendRequest> getFriendRequests(Long userID) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        this.friendRequestRepository.findAll().forEach(req -> {
            if (req.getReceiver().equals(userID) && req.getStatus().equals("PENDING"))
                friendRequests.add(req);
        });
        return friendRequests;
    }

    /**
     * handles a friend requests:
     * - if accepted, the friendship will be stored and the friend request will be
     * updated with the status ACCEPTED
     * - if denied, the friend request will be updated with the status REJECTED
     * 
     * @param requestID the ID of a request
     * @param decision  whether a user accepts or rejects a friend request
     */
    public void handleFriendRequest(Long requestID, String decision) {

        FriendRequest fr = friendRequestRepository.findOne(requestID);
        if (decision.equals("A")) {
            Prietenie prietenie = new Prietenie(fr.getSender(), fr.getReceiver(), LocalDateTime.now());

            AtomicLong friendshipID = new AtomicLong(1L);
            friendshipRepository.findAll().forEach((friendship -> friendshipID.getAndIncrement()));
            prietenie.setId(friendshipID.get());

            friendshipRepository.save(prietenie);
            fr.setStatus("ACCEPTED");
            fr.setLocalDateTime(LocalDateTime.now());
            friendRequestRepository.update(fr);
        } else {
            fr.setStatus("REJECTED");
            fr.setLocalDateTime(LocalDateTime.now());
            friendRequestRepository.delete(requestID);
        }
    }
}
