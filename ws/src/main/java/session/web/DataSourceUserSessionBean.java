package session.web;

import java.util.List;

import org.springframework.stereotype.Service;

import regression.CacheKeys;
import regression.Regression;
import regression.User;

@Service
public class DataSourceUserSessionBean {

    public void handleDataSourceUserInformation(List<User> userList) {
        Regression.putCacheContent(CacheKeys.DATA_SOURCE_USER, userList);
        String output = "";
        for (User u : userList) {
            output = output + u.toString() + "\n";
        }
        Regression.addClientMessage("Data source user information updated: \n" + output);
    }
}
