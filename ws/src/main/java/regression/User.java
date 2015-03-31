package regression;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {

    private String ip;
    private int port;
    private boolean connected = false;

    public User() {
    }

    public User(String str) {
        String[] strArray = str.split(",");
        for (String s : strArray) {
            if (s.contains("ip=")) {
                this.setIp(s.split("=")[1]);
            } else if (s.contains("port=")) {
                this.setPort(Integer.valueOf(s.split("=")[1]));
            } else if (s.contains("connected=")) {
                this.setConnected(Boolean.valueOf(s.split("=")[1]));
            }
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ip=" + ip + ", port=" + port + ", connected=" + connected;
    }

    @Override
    public boolean equals(Object user) {
        if (user instanceof User) {
            User u = (User) user;
            if (u.getIp().equals(ip) && u.getPort() == port) {
                return true;
            }
        }
        return false;
    }
}
