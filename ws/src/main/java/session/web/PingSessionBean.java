package session.web;

import java.util.List;

import org.springframework.stereotype.Service;

import regression.CacheKeys;
import regression.Regression;
import regression.User;

@Service
public class PingSessionBean {

    public void handlePing(User user) {
        String ip = user.getIp();
        int port = user.getPort();
        boolean connected = false;
        synchronized (Regression.cache) {
            List<User> list = (List<User>) Regression.cache.get(CacheKeys.DATA_SOURCE_USER);
            for (User u : list) {
                if (u.getIp().equals(ip) && u.getPort() == port) {
                    u.setConnected(true);
                    connected = true;
                }
            }
        }
        if (connected) {
            Regression.addClientMessage("User: " + ip + ":" + port + " is connected.");
        }
    }
}
