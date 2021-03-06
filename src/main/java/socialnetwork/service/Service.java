package socialnetwork.service;

import javafx.util.Pair;
import org.springframework.security.crypto.bcrypt.BCrypt;
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.events.ChangeObserverEventType;
import socialnetwork.Util.observer.Observable;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.paging.Pageable;
import socialnetwork.service.paging.Paginator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static socialnetwork.Util.Constants.BCryptNumberOfRounds;
import static socialnetwork.Util.Constants.eventNotificationTimeMinutes;

/**
 * Service class that implements all methods
 */
public class Service implements Observable<ChangeObserverEvent> {
    private final Repository<Long, Utilizator> userRepository;
    private final Repository<Long, Prietenie> friendshipRepository;
    private final Repository<String, UserCredentials> userCredentialsRepository;

    private final Repository<Long, Message> messageRepository;
    private final Repository<Long, FriendRequest> friendRequestRepository;
    private final Repository<Long, Event> eventRepository;
    private final Repository<Pair<Long, Long>, EventAttendance> eventAttendanceRepository;
    private final List<Observer<ChangeObserverEvent>> observers = new ArrayList<>();

    public Service(Repository<Long, Utilizator> userRepository,
                   Repository<String, UserCredentials> userCredentialsRepository,
                   Repository<Long, Prietenie> friendshipRepository,
                   Repository<Long, FriendRequest> friendRequestRepository, Repository<Long, Message> messageRepository, Repository<Long, Event> eventRepository, Repository<Pair<Long, Long>, EventAttendance> eventAttendanceRepository) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.messageRepository = messageRepository;
        this.eventRepository = eventRepository;
        this.eventAttendanceRepository = eventAttendanceRepository;
    }

    /**
     * Adds a user via save() method from the userRepsitory.
     *
     * @param user entity to be stored
     * @return the user if it already exists, null otherwise
     * @throws ValidationException if the user in invalid
     * @throws RepositoryException if the email address already exists or if the
     *                             user is null.
     */
    public Utilizator addUser(Utilizator user) {
        String plainTextPassword = user.getPassword();
        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCryptNumberOfRounds));
        user.setPassword(hashedPassword);

        return this.userRepository.save(user);
    }

    /**
     * Returns the user id for the provided user credentials.
     *
     * @return the user id if the email, and password match an existing user.
     * @throws RepositoryException if the email does't exist
     */
    public Long login(UserCredentials credentials) {
        try {
            UserCredentials storedCredentials = userCredentialsRepository.findOne(credentials.getEmail());
            if (!BCrypt.checkpw(credentials.getPassword(), storedCredentials.getPassword())) {
                return null;
            }
            return storedCredentials.getUserId();
        } catch (RepositoryException ex) {
            return null;
        }
    }

    public Event addEvent(Event event) {
        var ret = eventRepository.save(event);
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.EVENT));
        return ret;
    }

    public List<Event> getEvents() {
        LocalDateTime now = LocalDateTime.now();
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .filter(x -> x.getDate().isAfter(now))
                .sorted(Comparator.comparing(Event::getDate))
                .collect(Collectors.toList());
    }

    public List<Event> getEventsPaged(Pageable pageable) {
        Paginator<Event> paginator = new Paginator<>(pageable, getEvents());
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    public void setNotificationsForEvent(Long userId, Long eventId, boolean wantNotifications) {
        EventAttendance attendance = new EventAttendance(null, null);
        attendance.setId(new Pair<Long, Long>(eventId, userId));
        attendance.setNotifications(wantNotifications);
        eventAttendanceRepository.update(attendance);
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.EVENT_ATTENDANCE));
    }

    public EventAttendance addAttendance(Long eventId, Long userId) {
        EventAttendance attendance = new EventAttendance(null, null);
        attendance.setId(new Pair<Long, Long>(eventId, userId));
        attendance.setNotifications(true);
        var retVal = eventAttendanceRepository.save(attendance);
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.EVENT_ATTENDANCE));
        return retVal;
    }

    public List<EventAttendance> getUpcomingEventsForUser(Long userId) {
        var now = LocalDateTime.now();
        return getEventsForUser(userId)
                .stream()
                .filter(x ->
                        ((x.getEvent().getDate().isAfter(now)) &&
                                (x.getEvent().getDate().isBefore(now.plusMinutes(eventNotificationTimeMinutes)))))
                .sorted(Comparator.comparing(EventAttendance::getDate))
                .collect(Collectors.toList());
    }

    public List<EventAttendance> getUpcomingEventsForUserPaged(Long userId, Pageable pageable) {
        Paginator<EventAttendance> paginator = new Paginator<>(pageable, getUpcomingEventsForUser(userId));
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    public void cancelAttendance(Long eventId, Long userId) {
        EventAttendance attendance = new EventAttendance(null, null);
        attendance.setId(new Pair<Long, Long>(eventId, userId));
        eventAttendanceRepository.delete(attendance.getId());
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.EVENT_ATTENDANCE));
    }

    public List<EventAttendance> getEventsForUser(Long userId) {
        return StreamSupport.stream(eventAttendanceRepository.findAll().spliterator(), false)
                .filter(x -> x.getId().getValue().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Friend> getNewFriendsFromTimePeriod(Long userId, LocalDateTime from, LocalDateTime to) {
        var friendships = friendshipRepository.findAll();
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(x -> x.getFirstUser().equals(userId) || x.getSecondUser().equals(userId))
                .filter(x -> x.getDate().isAfter(from) && x.getDate().isBefore(to))
                .map(fr -> {
                    Utilizator user;
                    if (fr.getFirstUser().equals(userId)) {
                        user = userRepository.findOne(fr.getSecondUser());
                    } else {
                        user = userRepository.findOne(fr.getFirstUser());
                    }
                    return new Friend(user.getFirstName(), user.getLastName(), fr.getDate(), user.getId(), user.getImagePath());
                })
                .sorted(Comparator.comparing(Friend::getFirstName))
                .toList();
    }

    public List<Friend> getNewFriendsFromTimePeriodPaged(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        var friends = getNewFriendsFromTimePeriod(userId, from, to);
        Paginator<Friend> paginator = new Paginator<>(pageable, friends);
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    public List<Message> getReceivedMessagesFromTimePeriod(Long userId, LocalDateTime from, LocalDateTime to) {
        var messages = messageRepository.findAll();
        return StreamSupport.stream(messages.spliterator(), false)
                .filter(x -> x.getTo().get(0).getId().equals(userId))
                .filter(x -> x.getDate().isAfter(from) && x.getDate().isBefore(to))
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    public List<Message> getReceivedMessagesFromTimePeriodPaged(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        var messages = getReceivedMessagesFromTimePeriod(userId, from, to);
        Paginator<Message> paginator = new Paginator<>(pageable, messages);
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    public List<Message> getReceivedMesasgesFromUserFromTimePeriod(Long userid1, Long userid2, LocalDateTime from, LocalDateTime to) {
        return getReceivedMessagesFromTimePeriod(userid1, from, to).stream()
                .filter(x -> x.getFrom().getId().equals(userid2))
                .toList();
    }

    public List<Message> getReceivedMesasgesFromUserFromTimePeriodPaged(Long userId1, Long userId2, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        var messages = getReceivedMesasgesFromUserFromTimePeriod(userId1, userId2, from, to);
        Paginator<Message> paginator = new Paginator<>(pageable, messages);
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    public List<Event> getCommonEvents(Long userId1, Long userId2) {
        var allAttendances = eventAttendanceRepository.findAll();
        Set<Long> firstUserEventIds = new HashSet<>();
        StreamSupport.stream(allAttendances.spliterator(), false)
                .filter(x -> x.getId().getValue().equals(userId1))
                .map(x -> x.getId().getKey())
                .forEach(firstUserEventIds::add);
        var now = LocalDateTime.now();
        return StreamSupport.stream(allAttendances.spliterator(), false)
                .filter(x -> x.getId().getValue().equals(userId2))
                .filter(x -> firstUserEventIds.contains(x.getId().getKey()))
                .filter(x -> x.getEvent().getDate().isAfter(now))
                .map(x -> x.getEvent())
                .collect(Collectors.toList());
    }

    public List<Event> getCommonEventsPaged(Long userId1, Long userId2, Pageable pageable) {
        Paginator<Event> paginator = new Paginator<>(pageable, getCommonEvents(userId1, userId2));
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    /**
     * Get the list of users directed to the logged in user
     *
     * @return a list of users with friend requests directed to the logged in user
     */
    public List<Utilizator> getUsersListWithFriendRequests(Long userId) {
        return getPendingFriendRequests(userId).stream().map(x -> getUser(x.getSender()))
                .collect(Collectors.toList());
    }

    public List<Utilizator> getUsersWithPendingRequestsFilteredAndPaged(Long userId, String first_name_filter, String last_name_filter, Pageable pageable) {
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(first_name_filter);
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(last_name_filter);
        var filteredUsers = getUsersListWithFriendRequests(userId).stream().filter(firstNameFilter.or(lastNameFilter)).toList();
        Paginator<Utilizator> paginator = new Paginator<>(pageable, filteredUsers);
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    public List<Utilizator> getUsersListWithFriendRequestsFilteredAndPaged(Long userId, String first_name_filter, String last_name_filter, Pageable pageable) {
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(first_name_filter);
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(last_name_filter);
        var filteredUsers = getUsersListWithFriendRequests(userId).stream().filter(firstNameFilter.or(lastNameFilter)).toList();
        Paginator<Utilizator> paginator = new Paginator<>(pageable, filteredUsers);
        return paginator.paginate().getContent().collect(Collectors.toList());
    }


    public List<Friend> getCommonFriends(Long userId1, Long userId2) {
        var friendships = friendshipRepository.findAll();
        Set<Long> firstUserFriends = getFriendsSet(userId1, friendships);

        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(fr -> (fr.getSecondUser().equals(userId2) && firstUserFriends.contains(fr.getFirstUser()))
                        || (fr.getFirstUser().equals(userId2) && firstUserFriends.contains(fr.getSecondUser())))
                .map(fr -> {
                            Utilizator user;
                            if (fr.getFirstUser().equals(userId2)) {
                                user = userRepository.findOne(fr.getSecondUser());
                            } else {
                                user = userRepository.findOne(fr.getFirstUser());
                            }
                            return new Friend(user.getFirstName(), user.getLastName(), fr.getDate(), user.getId(), user.getImagePath());
                        }
                ).collect(Collectors.toList());
    }

    public List<Friend> getCommonFriendsPaged(Long userId1, Long userId2, Pageable pageable) {
        Paginator<Friend> paginator = new Paginator<>(pageable, getCommonFriends(userId1, userId2));
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    private HashSet<Long> getFriendsSet(Long userId, Iterable<Prietenie> friendships) {
        HashSet<Long> userFriends = new HashSet<>();
        StreamSupport.stream(friendships.spliterator(), false)
                .filter(fr -> fr.getSecondUser().equals(userId) || fr.getFirstUser().equals(userId))
                .map(fr -> {
                    Long friendId;
                    if (fr.getFirstUser().equals(userId)) {
                        friendId = fr.getSecondUser();
                    } else {
                        friendId = fr.getFirstUser();
                    }
                    return friendId;
                }).forEach(userFriends::add);
        return userFriends;
    }


    /**
     * Get all users that are not friends with a certain user
     *
     * @param userId a users' id
     * @return a list of all users that are not friends with the users with id userId
     */
    public List<Utilizator> getAllUsersThatAreNotFriends(Long userId) {
        var friendships = friendshipRepository.findAll();
        Set<Long> firstUserFriends = getFriendsSet(userId, friendships);

        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(x -> (!x.getId().equals(userId)) && (!firstUserFriends.contains(x.getId())))
                .collect(Collectors.toList());
    }

    public List<Utilizator> getAllUsersThatAreNotFriendsFilteredAndPaged(Long userId, String first_name_filter, String last_name_filter, Pageable pageable) {
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(first_name_filter);
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(last_name_filter);
        var users = getAllUsersThatAreNotFriends(userId).stream().filter(firstNameFilter.or(lastNameFilter)).toList();
        Paginator<Utilizator> paginator = new Paginator<>(pageable, users);
        return paginator.paginate().getContent().collect(Collectors.toList());
    }

    /**
     * @return all users in the socialnetwork.repository
     */
    public Iterable<Utilizator> getAllUsers() {
        return this.userRepository.findAll();
    }

    /**
     * @return all friendships in the socialnetwork.repository
     */
    public Iterable<Prietenie> getAllFriendships() {
        return this.friendshipRepository.findAll();
    }

    /**
     * Removes a user via delete() method from the socialnetwork.repository
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
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.USER));
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIENDSHIP));
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.MESSAGE));
        return toBeReturned;
    }

    /**
     * @param userID ID of the user
     * @return the user with the given ID
     */
    public Utilizator getUser(Long userID) {
        return userRepository.findOne(userID);
    }

    public Utilizator updateUser(Utilizator user) {
        return userRepository.update(user);
    }

    /**
     * Adds a friendship via save() method from the socialnetwork.repository
     *
     * @param prietenie entity to be stored
     * @return the stored entity
     */
    public Prietenie addFriendship(Prietenie prietenie) {
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIENDSHIP));
        return friendshipRepository.save(prietenie);
    }

    /**
     * @return a friendship from the socialnetwork.repository
     */
    public Prietenie getFriendship(Long friendshipID) {
        return friendshipRepository.findOne(friendshipID);
    }

    /**
     * Removes a friendship via delete() method from the socialnetwork.repository
     *
     * @param id of the entity to be deleted
     * @return the deleted entity
     */
    public Prietenie removeFriendship(Long id) {
        var friendship = friendshipRepository.findOne(id);
        friendRequestRepository.delete(getFriendRequest(friendship.getFirstUser(),
                friendship.getSecondUser()).get().getId());

        var rez = this.friendshipRepository.delete(id);
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIENDSHIP));
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIEND_REQUEST));
        return rez;
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
     * @param userID the ID of o user
     * @return a list of Friends of the given user
     */
    public List<Friend> getFriends(Long userID) {
        var friendships = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false).toList();

        return friendships.stream().filter(fr -> fr.getSecondUser().equals(userID) || fr.getFirstUser().equals(userID))
                .map(fr -> {
                    Utilizator user;
                    if (fr.getFirstUser().equals(userID)) {
                        user = userRepository.findOne(fr.getSecondUser());
                    } else {
                        user = userRepository.findOne(fr.getFirstUser());
                    }
                    return new Friend(user.getFirstName(), user.getLastName(), fr.getDate(), user.getId(), user.getImagePath());
                })
                .collect(Collectors.toList());
    }

    public List<Friend> getFriendsFilteredPaged(Long userId, String first_name_filter, String last_name_filter, Pageable pageable) {
        Predicate<Friend> firstNameFilter = u -> u.getFirstName().startsWith(first_name_filter);
        Predicate<Friend> lastNameFilter = u -> u.getLastName().startsWith(last_name_filter);
        var friends = getFriends(userId).stream()
                .filter(firstNameFilter.or(lastNameFilter))
                .sorted(Comparator.comparing(Friend::getFirstName))
                .toList();
        Paginator<Friend> paginator = new Paginator<>(pageable, friends);
        return paginator.paginate().getContent().collect(Collectors.toList());
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
     * @return null if the message wasn't sent, and the message itself
     * otherwise.
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
        var sentMessage = messageRepository.save(message);
        if (sentMessage == null) {
            notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.MESSAGE));
        }
        return sentMessage;
    }

    /**
     * Replies to a message
     *
     * @param messageId      the message's id that you're trying to reply to
     * @param replyCreatorId the user's id that created the reply
     * @param messageBody    the reply's body
     * @return null if the message wasn't sent(the message you want to reply to,
     * doesn't exist, or it wasn't sent to replyCreatorId), the message
     * otherwise
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
        isValidReply = StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .noneMatch(x -> x.getFrom().getId().equals(replyCreatorId)
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

        var replyResult = messageRepository.save(message);
        if (replyResult == null) {
            notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.MESSAGE));
        }
        return replyResult;
    }

    /**
     * reply to all messages sent to a user.
     *
     * @param userID      the user's id
     * @param messageBody reply message body
     */
    public void replyAll(Long userID, String messageBody) {
        Message message = new Message(null, null, messageBody, LocalDateTime.now(), null);
        Utilizator replyCreator = userRepository.findOne(userID);

        var users = getAllUsers();
        for (var user : users) {
            if (!user.getId().equals(userID)) {
                var allMessages = getAllMessagesBetweenTwoUsers(userID, user.getId());
                if (!allMessages.isEmpty()) {
                    for (int i = allMessages.size() - 1; i >= 0; i--) {
                        if (allMessages.get(i).getKey().getFrom().getId().equals(userID)) {
                            continue;
                        }
                        if (allMessages.get(i).getValue() != null) {
                            break;
                        }
                        List<Utilizator> to = new ArrayList<>();
                        var messageRecipient = user;
                        to.add(messageRecipient);
                        message.setFrom(replyCreator);
                        message.setTo(to);
                        message.setReply(allMessages.get(i).getKey());
                        messageRepository.save(message);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Get a list of all messages between two users.
     *
     * @param userId1 the first user's id
     * @param userId2 the second user's id
     * @return a list of entries of all messages between two users(in chronological
     * order). Entry.key is a
     * message, Entry.value is the reply(if it exists)
     */
    public List<Map.Entry<Message, Message>> getAllMessagesBetweenTwoUsers(Long userId1, Long userId2) {
        var allMessages = messageRepository.findAll();
        Map<Long, Message> messages = StreamSupport.stream(allMessages.spliterator(), false)
                .filter(x -> {
                    // creator must be one of the provided users
                    return x.getFrom().getId().equals(userId1) || x.getFrom().getId().equals(userId2);
                })
                .filter(x -> {
                    // message must have one of the users in the recipients list
                    boolean from1to2 = x.getTo().stream().anyMatch(y -> y.getId().equals(userId1));
                    boolean from2to1 = x.getTo().stream().anyMatch(y -> y.getId().equals(userId2));
                    return from1to2 || from2to1;
                })
                .collect(Collectors.toMap(Entity::getId, x -> x));

        HashMap<Message, Message> messageReply = getMessageReplyPairs(messages);

        var conversations = messageReply.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Message::getDate)))
                .collect(Collectors.toList());
        return conversations;
    }

    /**
     * Get a list with all the mssages between two users, sorted in chronological order
     *
     * @param userId1 the first ussers' id
     * @param userId2 the second users' id
     * @return list with the messages between the users with id userId1 and userId2
     */
    public List<Message> getMessageListBetweenTwoUsers(Long userId1, Long userId2) {
        var allMessages = messageRepository.findAll();
        List<Message> messages = StreamSupport.stream(allMessages.spliterator(), false)
                .filter(x -> {
                    // creator must be one of the provided users
                    return x.getFrom().getId().equals(userId1) || x.getFrom().getId().equals(userId2);
                })
                .filter(x -> {
                    // message must have one of the users in the recipients list
                    boolean from1to2 = x.getTo().stream().anyMatch(y -> y.getId().equals(userId1));
                    boolean from2to1 = x.getTo().stream().anyMatch(y -> y.getId().equals(userId2));
                    return from1to2 || from2to1;
                })
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
        return messages;
    }

    /**
     * Makes a map of messages where the key is a message and the value is a message
     * that replies to the key message
     *
     * @param messages a map of messages, key is the message id, value is the
     *                 message itself
     * @return HashMap<Message, Message> where key is a message, and value is the
     * message that replies to the key(null otherwise)
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
     * Adds a friend request using the save() from the socialnetwork.repository
     *
     * @param request to be saved
     * @return the saved friend request
     */
    public FriendRequest addFriendRequest(FriendRequest request) {
        request.setStatus("PENDING");
        request.setLocalDateTime(LocalDateTime.now());
        if (verifyPendingRequest(request).isPresent()) {
            return null;
        }
        if (areFriends(request.getSender(), request.getReceiver())) {
            return null;
        }
        var requestResult = friendRequestRepository.save(request);
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIEND_REQUEST));
        return requestResult;
    }

    public boolean areFriends(Long userId1, Long userId2) {
        var friendList = getFriends(userId1);
        return friendList.stream().anyMatch(x -> x.getId().equals(userId2));
    }


    /**
     * Finds and returns a pending friend request
     *
     * @param request: the request you're searching for
     * @return optional of request if the request exists, empty optional otherwise.
     */
    public Optional<FriendRequest> verifyPendingRequest(FriendRequest request) {
        return StreamSupport.stream(this.friendRequestRepository.findAll().spliterator(), false)
                .filter(req -> req.getSender().equals(request.getReceiver())
                        && req.getReceiver().equals(request.getSender())
                        && req.getStatus().equals("PENDING"))
                .findFirst();
    }

    /**
     * @param userID: a user's id
     * @return true of the user exists, false otherwise
     */
    public boolean checkIfUserExists(Long userID) {
        try {
            userRepository.findOne(userID);
        } catch (RepositoryException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a pending friend request from userId2 to userId1 if it exists.
     *
     * @param userId1 first user's id
     * @param userId2 second user's id
     * @return pending friend erquest from userId2 to userId1 or empty optional if no pending requests exists,
     */
    public Optional<FriendRequest> getPendingFriendRequest(Long userId1, Long userId2) {
        return getPendingFriendRequests(userId1).stream()
                .filter(x -> x.getSender().equals(userId2))
                .filter(x -> x.getStatus().equals("PENDING"))
                .findAny();
    }

    /**
     * @param userID id of a user
     * @return returns a list of friend requests of a user
     */
    public List<FriendRequest> getPendingFriendRequests(Long userID) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        this.friendRequestRepository.findAll().forEach(req -> {
            if (req.getReceiver().equals(userID) && req.getStatus().equals("PENDING"))
                friendRequests.add(req);
        });
        return friendRequests;
    }

    /**
     * Returns all accepted or pending friend requests for a user.
     *
     * @param userID: id of a user
     * @return a list of accepted and pending friend requests for a user
     */
    public List<FriendRequest> getFriendRequests(Long userID) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        this.friendRequestRepository.findAll().forEach(req -> {
            if ((req.getReceiver().equals(userID) || req.getSender().equals(userID))
                    && req.getStatus().equals("ACCEPTED")) {
                friendRequests.add(req);
            } else if (req.getReceiver().equals(userID) && req.getStatus().equals("PENDING")) {
                friendRequests.add(req);
            } else if (req.getSender().equals(userID) && req.getStatus().equals("PENDING")) {
                req.setStatus("OUTGOING");
                friendRequests.add(req);
            }

        });
        return friendRequests;
    }

    /**
     * Deletes the friend request with id requestId
     *
     * @param requestID the friend requests id.
     */
    public void stopFriendRequest(Long requestID) {
        FriendRequest fr = friendRequestRepository.findOne(requestID);
        if (fr != null) {
            friendRequestRepository.delete(requestID);
        }
        notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIEND_REQUEST));
    }

    /**
     * Returns a friendship(if any) between two users
     *
     * @param userId1 first user's id
     * @param userId2 second user's id
     * @return returns a friendship between the two users or an empty optional if there isn't one.
     */
    public Optional<Prietenie> getFriendship(Long userId1, Long userId2) {
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(x -> (x.getFirstUser().equals(userId1) && x.getSecondUser().equals(userId2)) ||
                        (x.getFirstUser().equals(userId2) && x.getSecondUser().equals(userId1)))
                .findAny();
    }

    /**
     * Returns a friend request(if any) between two users
     *
     * @param userId1 first user's id
     * @param userId2 second user's id
     * @return returns a friend request between the two users or an empty optional if there isn't one.
     */
    public Optional<FriendRequest> getFriendRequest(Long userId1, Long userId2) {
        return StreamSupport.stream(friendRequestRepository.findAll().spliterator(), false)
                .filter(x -> (x.getSender().equals(userId1) && x.getReceiver().equals(userId2)) ||
                        (x.getSender().equals(userId2) && x.getReceiver().equals(userId1)))
                .findAny();
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
            notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIENDSHIP));
            notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIEND_REQUEST));

        } else {
            fr.setStatus("REJECTED");
            fr.setLocalDateTime(LocalDateTime.now());
            friendRequestRepository.update(fr);
            notifyObservers(new ChangeObserverEvent(ChangeObserverEventType.FRIEND_REQUEST));
        }
    }

    /**
     * Add 'e' to the list of observers
     *
     * @param e : an observer
     */
    @Override
    public void addObserver(Observer<ChangeObserverEvent> e) {
        observers.add(e);
    }

    /**
     * Notify all the observers with the provided event
     *
     * @param t : an event
     */
    @Override
    public void notifyObservers(ChangeObserverEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }

    public void clearObservers() {
        observers.clear();
    }
}
