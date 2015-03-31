package utils.message;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import regression.User;

@XmlRootElement
public class MessageDataSourceUsers extends Message {

    List<User> userList;

    public MessageDataSourceUsers() {
        super();
    }

    @XmlElement(name = "user")
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
