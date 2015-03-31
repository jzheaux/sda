package regression;

import java.util.ArrayList;
import java.util.List;
import matrix.DoubleMatrix;
import org.junit.Test;
import regression.twoPartyLogitRegression.TwoPartyBinomialLogitRegressionJoinerHandler;
import utils.Reader;

public class TestJoiner {

    public final String ADDRESS1 = ".\\test\\testFiles\\logitRegression\\testMatrixD1";
    public final String ADDRESS2 = ".\\test\\testFiles\\logitRegression\\testMatrixR1";
    public final String ADDRESS3 = ".\\test\\testFiles\\logitRegression\\testMatrixD2";
    public final String ADDRESS4 = ".\\test\\testFiles\\logitRegression\\testMatrixR2";
    public final String COMMA = ",";

    public TestJoiner() {
    }

    @Test
    public void testCreator() {
        Reader reader = new Reader();
        List<User> user = new ArrayList<User>();
        User u1 = new User();
        u1.setConnected(true);
        u1.setIp("192.168.1.100");
        u1.setPort(8080);
        User u2 = new User();
        u2.setConnected(true);
        u2.setIp("192.168.1.102");
        u2.setPort(8080);
        user.add(u1);
        user.add(u2);
        Regression.putCacheContent(CacheKeys.DATA_SOURCE_USER, user);
        Regression.putCacheContent(CacheKeys.CURRENT_USER, u2);
        Regression.putCacheContent(CacheKeys.CREATOR, u1);
        DoubleMatrix doubleX1 = new DoubleMatrix(reader.readMatrix(ADDRESS1, COMMA));
        DoubleMatrix doubleY1 = new DoubleMatrix(reader.readMatrix(ADDRESS2, COMMA));
        DoubleMatrix doubleX2 = new DoubleMatrix(reader.readMatrix(ADDRESS3, COMMA));
        DoubleMatrix doubleY2 = new DoubleMatrix(reader.readMatrix(ADDRESS4, COMMA));
        Regression.putCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE, doubleX2);
        Regression.putCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE, doubleY2);
        TwoPartyBinomialLogitRegressionJoinerHandler h = new TwoPartyBinomialLogitRegressionJoinerHandler();
        h.joinerStartRegression();
    }
}