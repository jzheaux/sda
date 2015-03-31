package regression;

public class CacheKeys {

    public static final String CREATOR = "creator";
    public static final String REGRESSION_TYPE = "regressionType";
    public static final String CURRENT_USER = "currentUser";
    public static final String DATA_SOURCE_USER = "dataSourceUser";
    public static final String DESIGN_MATRIX_DOUBLE = "designMatrixDouble";
    public static final String RESPONSE_MATRIX_DOUBLE = "responseMatrixDouble";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PROTOCOL_PARAMETERS = "protocolParameters";
    public static final String PARTY_TWO = "partyTwo";
    public static final String PARTY_ONE = "partyOne";
    public static final String MATRIX_PRODUCT = "matrixProduct";
    public static final String BIG_INT_DESIGN_MATRIX = "bigIntDesignMatrix";
    public static final String BIG_INT_DESIGN_MATRIX_TRANSPOSED = "bigIntDesignMatrixTransposed";
    public static final String BIG_INT_RESPONSE_MATRIX = "bigIntResponseMatrix";
    public static final String PARTY_ONE_SHARE = "partyOneShare_";
    public static final String PARTY_TWO_SHARE = "partyTwoShare_";
    public static final String SHARE_PART_TWO = "sharePartTwo"; //get sum of all of shares of party two
    public static final String SHARE_PART_TWO_Y = "sharePartTwoY";
    public static final String XTX_DONE = "xtxDone";
    public static final String XTY_DONE = "xtyDone";
    public static final String PARTY_TWO_TRACE = "partyTwoTrace";
    public static final String PARTY_ONE_TRACE = "partyOneTrace";
    public static final String RANDOM = "random";

    public static String keyForPublicKey(User user) {
        return PUBLIC_KEY + user.getIp() + ";" + user.getPort();
    }
}
