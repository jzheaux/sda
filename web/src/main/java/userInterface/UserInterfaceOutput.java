package userInterface;

public class UserInterfaceOutput {

    public static final String HEADER = "====================SDA=======================";
    public static final String INSTRUCTION = "1. Create a new regression\n"
            + "2. Join a regression\n" + "0. Exit\n" + "Enter choice: ";
    public static final String ENTER_YOUR_PORT = "\nPlease enter your message listener port number:";
    public static final String INVALID_PORT = "Invalid port number, please enter the port number again";
    public static final String INVALID_IP = "Invalid IP, please enter the IP address again";
    public static final String ARE_YOU_DATA_SOURCE = "Are you one of the data providers? y/n: ";
    public static final String CREATE_REGRESSION_INSTRUCTION = "\nData provider information:\n"
            + "1. Add data provider\n"
            + "2. Remove data provider\n"
            + "3. List users added\n"
            + "4. Start regression\n"
            + "0. Exit\n"
            + "Enter choice: ";
    public static final String ADD_USER = "\nAdd new data provider";
    public static final String USER_IP = "User IP: ";
    public static final String USER_PORT_NUMBER = "User port number: ";
    public static final String USER_ADDED = "New data provider successfully added.";
    public static final String USER_LIST = "\nList of users:";
    public static final String NO_USER = "No user is added.";
    public static final String REMOVE_USER = "Please enter your choice to remove user: ";
    public static final String USER_REMOVED = "The user selected is successfully removed.";
    public static final String INVALID_CHOICE = "Invalide choice.";
    public static final String WAITE = "Waiting for data providers to connect ...";
    public static final String LISTENER_STATRED = "Started message listenter successfully.";
    public static final String ENTER_CREATOR_INFORMATION = "Please enter creator information";
    public static final String CREATOR_IP = "Creator IP address: ";
    public static final String CREATOR_PORT_NUMBER = "Creator port number: ";
}
