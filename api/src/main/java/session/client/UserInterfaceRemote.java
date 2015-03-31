package session.client;

import java.util.List;

public interface UserInterfaceRemote {

    public void addCreator(String ip, int port, boolean connected);

    public void setCurrentUser(String ip, int port, boolean connected);

    public void addDataSourceUser(String ip, int port, boolean connected);

    public List<String> getDataSourceUserDetail();

    public void removeDataSourceUser(String ip, int port);

    public boolean checkAllDataSourceUserConnected();

    public void pingCreator();

    public String getClinetMessage();

    public void sendDataSourceUserInformation();

    public void setResponseMatrix(String matrix);
    
    public void setDesignMatrix(String matrix);
    
    public void startRegression();
    
    public boolean checkRegressionEnd();
    
    public void setRegressionType(String type);
}
