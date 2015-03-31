package regression;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.DoubleMatrix;

public class Regression {

    private RegressionType regressionType;
    public static Map<String, Object> cache = Collections.synchronizedMap(new HashMap<String, Object>());
    public static Queue<String> clientMessage = new LinkedList<String>();
    public static boolean regressionEnd = false;

    /**
     * Cache contents (Key - valueType) CREATOR - User CURRENT_USER - User
     * DATA_SOURCE_USER - List<User>
     *
     */
    public static Object getCacheContent(String key) {
        Object o = cache.get(key);
        while (o == null) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Regression.class.getName()).log(Level.SEVERE, null, ex);
            }
            o = cache.get(key);
        }
        return o;
    }

    public static void putCacheContent(String key, Object object) {
        cache.put(key, object);
    }

    public static void removeCacheContent(String key) {
        cache.remove(key);
    }

    public RegressionType getRegressionType() {
        return regressionType;
    }

    public void setRegressionType(RegressionType regressionType) {
        this.regressionType = regressionType;
    }

    public static boolean isCurrentUser(User u) {
        User currentUser = (User) getCacheContent(CacheKeys.CURRENT_USER);
        if (u.getIp().equals(currentUser.getIp()) && u.getPort() == currentUser.getPort()) {
            return true;
        }
        return false;
    }

    public static String getClientMessage() {
        String msg = "";
        synchronized (clientMessage) {
            while (!clientMessage.isEmpty()) {
                msg = msg + clientMessage.poll() + "\n";
            }
        }
        return msg;
    }

    public static void addClientMessage(String msg) {
        synchronized (clientMessage) {
            clientMessage.add(msg);
        }
    }

    public static void setDesignMatrixDouble(String matrix) {
        DoubleMatrix doubleMatrix = new DoubleMatrix(matrix);
        putCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE, doubleMatrix);
    }

    public static void setResponseMatrixDouble(String matrix) {
        DoubleMatrix doubleMatrix = new DoubleMatrix(matrix);
        putCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE, doubleMatrix);
    }
    
    public static void setRegressionType(String type){
        if (type.equals("linearRegression")) {
            putCacheContent(CacheKeys.REGRESSION_TYPE, RegressionType.LINEAR_REGRESSION);
        } else if (type.equals("binomialLogisitcRegression")) {
            putCacheContent(CacheKeys.REGRESSION_TYPE, RegressionType.BINIOMIAL_LOGIT_REGRESSION);
        }
    }
}
