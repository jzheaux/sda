package regression;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import matrix.BigIntMatrix;
import utils.message.MessageBigIntMatrix;
import utils.message.client.MessageDispatcher;

public class TwoPartyRegressionHelper {

    public static void computeTwoPartyShareProduct(BigIntMatrix XShare1, BigIntMatrix YShare1, BigInteger publicKeyN, BigInteger publicKeyG, String oppShareId1, String oppShareId2, String resultId, User toUser) {
        BigIntMatrix f1 = XShare1.multiply(YShare1);
        f1 = Paillier.encrypt(f1, publicKeyN, publicKeyG);
        BigIntMatrix enXShare1 = Paillier.encrypt(XShare1, publicKeyN, publicKeyG);
        BigIntMatrix enYShare1 = Paillier.encrypt(YShare1, publicKeyN, publicKeyG);
        MessageBigIntMatrix message = new MessageBigIntMatrix();
        List<BigIntMatrix> matrixList = new ArrayList<BigIntMatrix>();
        matrixList.add(enXShare1);
        matrixList.add(enYShare1);
        matrixList.add(f1);
        message.setBigIntMatrixList(matrixList);
        List<String> idList = new ArrayList<String>();
        idList.add(oppShareId1);
        idList.add(oppShareId2);
        message.setAdditionalInformation(idList);
        message.setResultId(resultId);
        message.setProtocolInformation(ProtocolInformation.DO_TWO_PARTY_SHARE_PRODUCT);
        message.setProtocolStepNumber(1);
        MessageDispatcher.sendBigIntMatrix(message, toUser);
    }
    
    public static void computeTwoPartyShareVectorElementProduct(BigIntMatrix XShare1, BigIntMatrix YShare1, BigInteger publicKeyN, BigInteger publicKeyG, String oppShareId1, String oppShareId2, String resultId, User toUser) {
        BigIntMatrix f1 = multiplyVectorElement(XShare1, YShare1);
        f1 = Paillier.encrypt(f1, publicKeyN, publicKeyG);
        BigIntMatrix enXShare1 = Paillier.encrypt(XShare1, publicKeyN, publicKeyG);
        BigIntMatrix enYShare1 = Paillier.encrypt(YShare1, publicKeyN, publicKeyG);
        MessageBigIntMatrix message = new MessageBigIntMatrix();
        List<BigIntMatrix> matrixList = new ArrayList<BigIntMatrix>();
        matrixList.add(enXShare1);
        matrixList.add(enYShare1);
        matrixList.add(f1);
        message.setBigIntMatrixList(matrixList);
        List<String> idList = new ArrayList<String>();
        idList.add(oppShareId1);
        idList.add(oppShareId2);
        message.setAdditionalInformation(idList);
        message.setResultId(resultId);
        message.setProtocolInformation(ProtocolInformation.DO_TWO_PARTY_SHARE_VECTOR_ELEMENT_PRODUCT);
        message.setProtocolStepNumber(1);
        MessageDispatcher.sendBigIntMatrix(message, toUser);
    }

    
    public static BigIntMatrix multiplyVectorElementEnMatrixDeMatrix(BigIntMatrix x, BigIntMatrix y, BigInteger N) {
        BigInteger[][] result = new BigInteger[x.getRowNumber()][x.getColNumber()];
        for (int i = 0; i < x.getRowNumber(); i++) {
            result[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(x.getMatrix()[i][0], y.getMatrix()[i][0], N);
        }
        return new BigIntMatrix(result);
    }
    
    public static BigIntMatrix multiplyVectorElement(BigIntMatrix x, BigIntMatrix y) {
        BigInteger[][] result = new BigInteger[x.getRowNumber()][y.getColNumber()];
        for (int i = 0; i < x.getRowNumber(); i++) {
            result[i][0] = x.getMatrix()[i][0].multiply(y.getMatrix()[i][0]);
        }
        return new BigIntMatrix(result);
    }
}
