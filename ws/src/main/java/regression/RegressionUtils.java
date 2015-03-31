package regression;

import java.util.List;

public class RegressionUtils {

    public static boolean checkAllUserConnected() {
        synchronized (Regression.cache) {
            List<User> list = (List<User>) Regression.cache.get(CacheKeys.DATA_SOURCE_USER);
            if (list == null || list.isEmpty()) {
                return false;
            }
            for (User u : list) {
                if (!u.isConnected()) {
                    return false;
                }
            }
        }
        return true;
    }
}
