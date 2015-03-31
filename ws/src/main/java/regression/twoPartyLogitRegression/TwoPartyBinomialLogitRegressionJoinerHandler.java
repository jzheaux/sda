package regression.twoPartyLogitRegression;

import cryptosystem.PaillierPublicKey;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import protocol.PartyWithoutPrivateKey;
import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;
import regression.User;
import regression.twoPartyLinearRegression.TwoPartyLinearRegressionJoinerHandler;

public class TwoPartyBinomialLogitRegressionJoinerHandler {
     List<User> userList;
    User currentUser;
    User creator;
    DoubleMatrix designMatrixDouble;
    DoubleMatrix responseMatrixDouble;
    PartyWithoutPrivateKey partyTwo;
    BigInteger publicKeyN;
    BigInteger publicKeyG;
    BigInteger protocolM;
    BigInteger protocolP;

    public TwoPartyBinomialLogitRegressionJoinerHandler() {
        this.userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        this.currentUser = (User) Regression.getCacheContent(CacheKeys.CURRENT_USER);
        this.creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        this.designMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE);
        this.responseMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE);
    }

    public void joinerStartRegression() {
        setupParameters();
        BigIntMatrix x2 = designMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        Regression.putCacheContent(CacheKeys.BIG_INT_DESIGN_MATRIX, x2);
        Regression.putCacheContent(CacheKeys.BIG_INT_DESIGN_MATRIX_TRANSPOSED, x2.getTransposedMatrix());
        BigIntMatrix y2 = responseMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        Regression.putCacheContent(CacheKeys.BIG_INT_RESPONSE_MATRIX, y2);
        
    }
    
    private void setupParameters() {
        while (Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS) == null || Regression.getCacheContent(CacheKeys.keyForPublicKey(creator)) == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(TwoPartyLinearRegressionJoinerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ProtocolParameters parameters = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        protocolM = parameters.getM();
        protocolP = parameters.getP();
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        publicKeyN = publicKey.getN();
        publicKeyG = publicKey.getG();
        partyTwo = new PartyWithoutPrivateKey(publicKey, protocolM, protocolP);
        Regression.putCacheContent(CacheKeys.PARTY_TWO, partyTwo);
    }
}
