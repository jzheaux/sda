package regression.twoPartyLinearRegression;

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

public class TwoPartyLinearRegressionJoinerHandler {

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

    public TwoPartyLinearRegressionJoinerHandler() {
        this.userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        this.currentUser = (User) Regression.getCacheContent(CacheKeys.CURRENT_USER);
        this.creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        this.designMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE);
        this.responseMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE);
    }

    public void joinerStartRegression() {
        setupParameters();
        DoubleMatrix doubleMatrixTran = designMatrixDouble.getTransposedMatrix();
        DoubleMatrix dmProduct = doubleMatrixTran.multiply(designMatrixDouble);
        BigIntMatrix bigMatrix = designMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        Regression.putCacheContent(CacheKeys.BIG_INT_DESIGN_MATRIX, bigMatrix);
        BigIntMatrix bigMatrix2 = responseMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        Regression.putCacheContent(CacheKeys.BIG_INT_RESPONSE_MATRIX, bigMatrix2);
        
        BigIntMatrix part2 = dmProduct.toBigIntMatrix(protocolM, publicKeyN);
        Regression.putCacheContent(CacheKeys.SHARE_PART_TWO, part2);   
        DoubleMatrix dyProduct = doubleMatrixTran.multiply(responseMatrixDouble);
        BigIntMatrix part2y = dyProduct.toBigIntMatrix(protocolM, publicKeyN);
        Regression.putCacheContent(CacheKeys.SHARE_PART_TWO_Y, part2y);
        
        
        while (!(boolean) Regression.getCacheContent(CacheKeys.XTX_DONE)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(TwoPartyLinearRegressionJoinerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        part2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.SHARE_PART_TWO);
        BigInteger trace2 = part2.getTrace().mod(publicKeyN);
        Regression.putCacheContent(CacheKeys.PARTY_TWO_TRACE, trace2);

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
        Regression.putCacheContent(CacheKeys.XTX_DONE, false);
        Regression.putCacheContent(CacheKeys.XTY_DONE, false);
    }
}
