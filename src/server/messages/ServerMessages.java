package server.messages;


/**
 * This class is final - can't extend other class or be extended.
 * The variables are final and static because:
 * a) they won't change (final);
 * b) they could be accessed through a class without creating objects (static).
 * @author Lina Balciunate
 */
public final class ServerMessages {

    public static final String SERVER_STARTED = "Server started on port: %s";

    public static final String DEFAULT_NAME = "Player-";
    public static final String CLIENT_ENTERED_CHAT = " entered the chat.";

    public static final String CLIENT_DISCONNECTED = " left the game.";

    public static final String CLIENT_ERROR = "Something went wrong with this player's connection. Error: ";
    public static final String CLIENT_ALREADY_EXISTS = "A player with this username already exists. Please choose another one.";
}
