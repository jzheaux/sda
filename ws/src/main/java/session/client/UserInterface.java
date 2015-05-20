package session.client;

import java.util.ArrayList;
import java.util.List;

import matrix.DoubleMatrix;

import org.springframework.stereotype.Service;

import regression.CacheKeys;
import regression.Regression;
import regression.RegressionType;
import regression.RegressionUtils;
import regression.User;
import regression.twoPartyLinearRegression.TwoPartyLinearRegressionCreatorHandler;
import regression.twoPartyLinearRegression.TwoPartyLinearRegressionJoinerHandler;
import regression.twoPartyLogitRegression.TwoPartyBinomialLogitRegressionCreatorHandler;
import regression.twoPartyLogitRegression.TwoPartyBinomialLogitRegressionJoinerHandler;
import utils.message.client.MessageDispatcher;

@Service
public class UserInterface implements UserInterfaceRemote {

    private static String messageForClient;

    public void addCreator(String ip, int port, boolean connected) {
        User creator = new User();
        creator.setPort(port);
        creator.setIp(ip);
        creator.setConnected(connected);
        Regression.putCacheContent(CacheKeys.CREATOR, creator);
        addDataSourceUser(ip, port, connected);
    }

    public void setCurrentUser(String ip, int port, boolean connected) {
        User creator = new User();
        creator.setPort(port);
        creator.setIp(ip);
        creator.setConnected(connected);
        Regression.putCacheContent(CacheKeys.CURRENT_USER, creator);
    }

    public void addDataSourceUser(String ip, int port, boolean connected) {
        User user = new User();
        user.setPort(port);
        user.setIp(ip);
        user.setConnected(connected);
        synchronized (Regression.cache) {
            List<User> list = (List<User>) Regression.cache.get(CacheKeys.DATA_SOURCE_USER);
            if (list == null) {
                list = new ArrayList<User>();
                Regression.cache.put(CacheKeys.DATA_SOURCE_USER, list);
            }
            list.add(user);
        }

    }

    public List<String> getDataSourceUserDetail() {
        List<User> list = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        List<String> detail = new ArrayList<String>();
        for (User u : list) {
            detail.add(u.getIp() + ":" + u.getPort());
        }
        return detail;
    }

    public void removeDataSourceUser(String ip, int port) {
        synchronized (Regression.cache) {
            List<User> list = (List<User>) Regression.cache.get(CacheKeys.DATA_SOURCE_USER);
            for (User u : list) {
                if (u.getIp().equals(ip) && u.getPort() == port) {
                    list.remove(u);
                    break;
                }
            }
        }
    }

    public boolean checkAllDataSourceUserConnected() {
        return RegressionUtils.checkAllUserConnected();
    }

    public void pingCreator() {
        MessageDispatcher.pingUser((User) Regression.getCacheContent(CacheKeys.CREATOR));
    }

    public String getClinetMessage() {
        return Regression.getClientMessage();
    }

    public void sendDataSourceUserInformation() {
        MessageDispatcher.sendDataSourceUserInformation();
    }

    public void setResponseMatrix(String matrix) {
        Regression.setResponseMatrixDouble(matrix);
    }

    public void setDesignMatrix(String matrix) {
        Regression.setDesignMatrixDouble(matrix);
    }

    public void setResponseMatrix(DoubleMatrix matrix) {
        Regression.putCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE, matrix);
    }

    public void setDesignMatrix(DoubleMatrix matrix) {
    	Regression.putCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE, matrix);
    }
    
    public void startRegression() {
        List<User> userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        User currentUser = (User) Regression.getCacheContent(CacheKeys.CURRENT_USER);
        RegressionType regressionType = (RegressionType) Regression.getCacheContent(CacheKeys.REGRESSION_TYPE);
        if (userList != null && userList.size() == 2) {
            switch (regressionType) {
                case LINEAR_REGRESSION:
                    if (creator.equals(currentUser)) {
                        TwoPartyLinearRegressionCreatorHandler regressionHandler = new TwoPartyLinearRegressionCreatorHandler();
                        regressionHandler.creatorStartRegression();
                    } else {
                        TwoPartyLinearRegressionJoinerHandler regressionHandler = new TwoPartyLinearRegressionJoinerHandler();
                        regressionHandler.joinerStartRegression();
                    }
                    break;
                case BINIOMIAL_LOGIT_REGRESSION:
                    if (creator.equals(currentUser)) {
                        TwoPartyBinomialLogitRegressionCreatorHandler regressionHandler = new TwoPartyBinomialLogitRegressionCreatorHandler();
                        regressionHandler.creatorStartRegression();
                    } else {
                        TwoPartyBinomialLogitRegressionJoinerHandler regressionHandler = new TwoPartyBinomialLogitRegressionJoinerHandler();
                        regressionHandler.joinerStartRegression();
                    }
                    break;
            }

        }
    }

    public boolean checkRegressionEnd() {
        return Regression.regressionEnd;
    }

    public void setRegressionType(String type) {
        Regression.setRegressionType(type);
        MessageDispatcher.sendRegressionType(type);
    }
}
