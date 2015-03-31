package utils.message.client;

import regression.User;

public class UrlGenerator {

    private static final String HTTP = "http://";
    public static final String PING = "/sda-ws/rest/ping";
    public static final String DATA_SOURCE_USER_INFORMATION = "/sda-ws/rest/dataSourceUserInformation";
    public static final String PUBLIC_KEY = "/sda-ws/rest/publicKey";
    public static final String PROTOCOL_PARAMETERS = "/sda-ws/rest/protocolParameters";
    public static final String BIG_INT_MATRIX = "/sda-ws/rest/bigIntMatrix";
    public static final String BIG_INTEGER = "/sda-ws/rest/bigInteger";
    public static final String DOUBLE_MATRIX = "/sda-ws/rest/doubleMatrix";
    public static final String GENERAL_INSTRUCTION = "/sda-ws/rest/generalInstruction";
    public static final String REGRESSION_TYPE = "/sda-ws/rest/regressionType";

    public static String generateUrl(User user, String url) {
        return HTTP + user.getIp() + ":" + user.getPort() + url;
    }
}
