package utils.message;

import regression.CacheKeys;
import regression.Regression;
import regression.User;
import utils.RandomIDGenerator;

public class Message {

    User currentUser;
    String id;

    public Message() {
        setId(RandomIDGenerator.generateID());
        id = getId();
        currentUser = (regression.User) Regression.getCacheContent(CacheKeys.CURRENT_USER);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}