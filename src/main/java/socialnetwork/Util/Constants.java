package socialnetwork.Util;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter eventDateTime = DateTimeFormatter.ofPattern("MMM dd @ HH:mm");
    public static final int BCryptNumberOfRounds = 13;
    public static final int eventNotificationTimeMinutes = 60;
}
