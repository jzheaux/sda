package session.web.handlers;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import cryptosystem.PaillierPublicKey;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import matrix.DoubleMatrixHelper;
import protocol.PartyWithPrivateKey;
import protocol.PartyWithoutPrivateKey;
import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;
import regression.User;
import regression.ProtocolInformation;
import regression.TwoPartyRegressionHelper;
import session.web.BigIntMatrixSessionBean;
import utils.FloatingPointNumber;
import utils.message.MessageBigIntMatrix;
import utils.message.client.MessageDispatcher;

public class BigIntMatrixSessionBeanHandler implements Runnable {

    private MessageBigIntMatrix message;

    public BigIntMatrixSessionBeanHandler(MessageBigIntMatrix message) {
        this.message = message;
    }

    @Override
    public void run() {
        handleBigIntMatrix(message);
    }

    public void handleBigIntMatrix(MessageBigIntMatrix message) {
        String protocolInformation = message.getProtocolInformation();
        if (protocolInformation.equals(ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX)) {
            protocolMultiplyEnMatrixDeMatrix(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_MULTIPLY_DEMATRIX_ENMATRIX)) {
            protocolMultiplyDeMatrixEnMatrix(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_ADD_SHARES)) {
            doAddShares(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_ADD_SHARES_Y)) {
            doAddSharesY(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX)) {
            doMatrixInversionInitializeMatrix(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_TWO_PARTY_SHARE_PRODUCT)) {
            doTwoPartyShareProduct(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_TWO_PARTY_SHARE_VECTOR_ELEMENT_PRODUCT)) {
            doTwoPartyShareVectorElementProduct(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_SHARES_SUM)) {
            doSharesSum(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_E_EXP)) {
            doEExp(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_ONE_OVER_E_PLUS_ONE)) {
            doOneOverEPlusOne(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_INVERT_SIGN)) {
            doInvertSign(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_SET_CACHE)) {
            doSetCache(message);
        }
    }
    
    private void doSetCache(MessageBigIntMatrix message) {
        Regression.putCacheContent(message.getMatrixId(), message.getBigIntMatrixList().get(0));
    }

    private void doInvertSign(MessageBigIntMatrix message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigInteger publicKeyG = publicKey.getG();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        int stepNumber = message.getProtocolStepNumber();
        String matrixId = message.getMatrixId();
        if (stepNumber == 1) {
            BigIntMatrix enPi2 = Paillier.encrypt((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getOppShareId()), publicKeyN, publicKeyG);
            BigIntMatrix tmp4 = PaillierProperties.doAdd(message.getBigIntMatrixList().get(0), enPi2, publicKeyN);
            BigInteger minusOne = FloatingPointNumber.doubleToBigInteger(-1, pp.getM(), publicKeyN);
            BigInteger[][] tmp5 = new BigInteger[tmp4.getRowNumber()][tmp4.getColNumber()];
            for (int i = 0; i < tmp4.getRowNumber(); i++) {
                tmp5[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(tmp4.getMatrix()[i][0], minusOne, publicKeyN);
            }
            tmp4 = new BigIntMatrix(tmp5);
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(tmp4);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, tmp4);
            MessageDispatcher.sendBigIntMatrix(output, message.getCurrentUser(), ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 2);
        }
    }

    private void doOneOverEPlusOne(MessageBigIntMatrix message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigInteger publicKeyG = publicKey.getG();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        int stepNumber = message.getProtocolStepNumber();
        String oppShareId = message.getOppShareId();
        String matrixId = message.getMatrixId();
        if (stepNumber == 1) {
            BigIntMatrix enE2 = Paillier.encrypt((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + oppShareId), publicKeyN, publicKeyG);
            BigIntMatrix allOneMatrix = DoubleMatrixHelper.getAllOneElementMatrix(enE2.getRowNumber(), enE2.getColNumber()).toBigIntMatrix(pp.getM(), publicKeyN);
            BigIntMatrix enAllOne = Paillier.encrypt(allOneMatrix, publicKeyN, publicKeyG);
            BigIntMatrix sum = PaillierProperties.doAdd(message.getBigIntMatrixList().get(0), enE2, publicKeyN);
            sum = PaillierProperties.doAdd(enAllOne, sum, publicKeyN);
            DoubleMatrix doubleR = DoubleMatrix.generateRandomMatrix(enE2.getRowNumber(), enE2.getColNumber());
            BigIntMatrix r = doubleR.toBigIntMatrix(pp.getM(), publicKeyN);
            Regression.putCacheContent(CacheKeys.RANDOM + matrixId, r);
            BigIntMatrix product = TwoPartyRegressionHelper.multiplyVectorElementEnMatrixDeMatrix(sum, r, publicKeyN);
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(product);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, product);
            MessageDispatcher.sendBigIntMatrix(output, message.getCurrentUser(), ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 2);
        } else if (stepNumber == 2) {
            BigIntMatrix product = message.getBigIntMatrixList().get(0);
            BigIntMatrix r = (BigIntMatrix) Regression.getCacheContent(CacheKeys.RANDOM + oppShareId);
            product = TwoPartyRegressionHelper.multiplyVectorElementEnMatrixDeMatrix(product, r, publicKeyN);
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(product);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, product);
            MessageDispatcher.sendBigIntMatrix(output, message.getCurrentUser(), ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 2);
        }
    }

    private void doEExp(MessageBigIntMatrix message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigInteger publicKeyG = publicKey.getG();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        int stepNumber = message.getProtocolStepNumber();
        String oppShareId = message.getOppShareId();
        String matrixId = message.getMatrixId();
        if (stepNumber == 1) {
            BigIntMatrix enPi1 = message.getBigIntMatrixList().get(0);
            BigIntMatrix enPi2 = Paillier.encrypt((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + oppShareId), publicKeyN, publicKeyG);
            DoubleMatrix doubleP2 = DoubleMatrix.generateRandomMatrix(enPi1.getRowNumber(), enPi1.getColNumber());
            BigIntMatrix p2 = doubleP2.toBigIntMatrix(pp.getM(), publicKeyN);
            BigIntMatrix enRandom = Paillier.encrypt(p2, publicKeyN, publicKeyG);
            BigIntMatrix piSum = PaillierProperties.doAdd(enPi1, enPi2, publicKeyN);
            piSum = PaillierProperties.doAdd(piSum, enRandom, publicKeyN);
            p2 = DoubleMatrixHelper.elementExp(DoubleMatrixHelper.invertSign(doubleP2)).toBigIntMatrix(pp.getM(), publicKeyN);
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + matrixId, p2);
            MessageDispatcher.sendBigIntMatrix(piSum, message.getCurrentUser(), ProtocolInformation.DO_E_EXP, matrixId, 2);
        } else if (stepNumber == 2) {
            PartyWithPrivateKey partyOne = (PartyWithPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_ONE);
            BigIntMatrix p1 = Paillier.decrypt(message.getBigIntMatrixList().get(0), partyOne.getPrivateKey().getLambda(), publicKeyN, publicKeyG);
            DoubleMatrix doubleP1 = p1.toDoubleMatrix(pp.getM(), publicKeyN);
            doubleP1 = DoubleMatrixHelper.elementExp(doubleP1);
            p1 = doubleP1.toBigIntMatrix(pp.getM(), publicKeyN);
            BigIntMatrix enP1 = Paillier.encrypt(p1, publicKeyN, publicKeyG);
            MessageDispatcher.sendBigIntMatrix(enP1, message.getCurrentUser(), ProtocolInformation.DO_E_EXP, matrixId, 3);
        } else if (stepNumber == 3) {
            BigIntMatrix enP1 = message.getBigIntMatrixList().get(0);
            BigIntMatrix p2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + matrixId);
            BigIntMatrix product1 = TwoPartyRegressionHelper.multiplyVectorElementEnMatrixDeMatrix(enP1, p2, publicKeyN);
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(product1);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, product1);
            MessageDispatcher.sendBigIntMatrix(output, message.getCurrentUser(), ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 2);
        }

    }

    private void doSharesSum(MessageBigIntMatrix message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        BigIntMatrix share = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getOppShareId());
        BigIntMatrix share2 = message.getBigIntMatrixList().get(0);
        DoubleMatrix result = share.addDecryptedMatrix(share2).mod(publicKeyN).toDoubleMatrix(pp.getM(), publicKeyN);
        Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getMatrixId(), result);
        MessageDispatcher.sendDoubleMatrix(result, message.getCurrentUser(), ProtocolInformation.DO_SET_CACHE, CacheKeys.PARTY_ONE_SHARE + message.getMatrixId());
    }

    private void doTwoPartyShareVectorElementProduct(MessageBigIntMatrix message) {
        BigIntMatrix enXShare1 = message.getBigIntMatrixList().get(0);
        BigIntMatrix enYShare1 = message.getBigIntMatrixList().get(1);
        BigIntMatrix f1 = message.getBigIntMatrixList().get(2);
        String resultId = message.getResultId();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigIntMatrix XShare2 = (BigIntMatrix) Regression.getCacheContent(message.getAdditionalInformation().get(0));
        BigIntMatrix YShare2 = (BigIntMatrix) Regression.getCacheContent(message.getAdditionalInformation().get(1));

        BigIntMatrix f3 = TwoPartyRegressionHelper.multiplyVectorElementEnMatrixDeMatrix(enXShare1, YShare2, publicKeyN);
        BigIntMatrix f4 = TwoPartyRegressionHelper.multiplyVectorElementEnMatrixDeMatrix(enYShare1, XShare2, publicKeyN);
        BigIntMatrix f2 = TwoPartyRegressionHelper.multiplyVectorElement(XShare2, YShare2);
        f2 = Paillier.encrypt(f2, publicKeyN, publicKey.getG());
        BigIntMatrix fProduct = PaillierProperties.doAdd(f1, f2, publicKeyN);
        fProduct = PaillierProperties.doAdd(fProduct, f3, publicKeyN);
        fProduct = PaillierProperties.doAdd(fProduct, f4, publicKeyN);

        PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
        partyTwo = partyTwo.getNewCopy();
        BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(fProduct);
        Regression.putCacheContent(CacheKeys.PARTY_TWO + resultId, partyTwo);
        Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + resultId, fProduct);
        MessageBigIntMatrix newMessage = new MessageBigIntMatrix();
        newMessage.setMatrixId(resultId);
        newMessage.setProtocolStepNumber(2);
        newMessage.setProtocolInformation(ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX);
        List<BigIntMatrix> matrixList = new ArrayList<BigIntMatrix>();
        matrixList.add(output);
        newMessage.setBigIntMatrixList(matrixList);
        MessageDispatcher.sendBigIntMatrix(newMessage, message.getCurrentUser());
    }

    private void doTwoPartyShareProduct(MessageBigIntMatrix message) {
        BigIntMatrix enXShare1 = message.getBigIntMatrixList().get(0);
        BigIntMatrix enYShare1 = message.getBigIntMatrixList().get(1);
        BigIntMatrix f1 = message.getBigIntMatrixList().get(2);
        String resultId = message.getResultId();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigIntMatrix XShare2 = (BigIntMatrix) Regression.getCacheContent(message.getAdditionalInformation().get(0));
        BigIntMatrix YShare2 = (BigIntMatrix) Regression.getCacheContent(message.getAdditionalInformation().get(1));

        BigIntMatrix f3 = PaillierProperties.doMultiplyEnMatrixDeMatrix(enXShare1, YShare2, publicKeyN);
        BigIntMatrix f4 = PaillierProperties.doMultiplyDeMatrixEnMatrix(XShare2, enYShare1, publicKeyN);
        BigIntMatrix f2 = XShare2.multiply(YShare2);
        f2 = Paillier.encrypt(f2, publicKeyN, publicKey.getG());
        BigIntMatrix fProduct = PaillierProperties.doAdd(f1, f2, publicKeyN);
        fProduct = PaillierProperties.doAdd(fProduct, f3, publicKeyN);
        fProduct = PaillierProperties.doAdd(fProduct, f4, publicKeyN);

        PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
        partyTwo = partyTwo.getNewCopy();
        BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(fProduct);
        Regression.putCacheContent(CacheKeys.PARTY_TWO + resultId, partyTwo);
        Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + resultId, fProduct);
        MessageBigIntMatrix newMessage = new MessageBigIntMatrix();
        newMessage.setMatrixId(resultId);
        newMessage.setProtocolStepNumber(2);
        newMessage.setProtocolInformation(ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX);
        List<BigIntMatrix> matrixList = new ArrayList<BigIntMatrix>();
        matrixList.add(output);
        newMessage.setBigIntMatrixList(matrixList);
        MessageDispatcher.sendBigIntMatrix(newMessage, message.getCurrentUser());
    }

    private void doMatrixInversionInitializeMatrix(MessageBigIntMatrix message) {
        User messageSender = message.getCurrentUser();
        BigIntMatrix matrix1 = message.getBigIntMatrixList().get(0);
        String matrixId = message.getMatrixId();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        String oppShareId = message.getOppShareId();
        int stepNumber = message.getProtocolStepNumber();
        if (stepNumber == 1) {
            BigInteger x2 = (BigInteger) Regression.getCacheContent(oppShareId);
            BigIntMatrix product = PaillierProperties.doMultiplyEnMatrixDeNumber(matrix1, x2, publicKeyN);
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + matrixId, product);
        } else if (stepNumber == 2) {
            List<String> matrixIdList = message.getAdditionalInformation();
            BigIntMatrix sumProduct = PaillierProperties.doAdd(matrix1, (BigIntMatrix) Regression.getCacheContent(matrixIdList.get(0)), publicKeyN);
            sumProduct = PaillierProperties.doAdd(sumProduct, (BigIntMatrix) Regression.getCacheContent(matrixIdList.get(1)), publicKeyN);
            sumProduct = PaillierProperties.doAdd(sumProduct, (BigIntMatrix) Regression.getCacheContent(matrixIdList.get(2)), publicKeyN);
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(sumProduct);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, sumProduct);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            MessageBigIntMatrix newMessage = new MessageBigIntMatrix();
            newMessage.setMatrixId(matrixId);
            newMessage.setProtocolStepNumber(2);
            newMessage.setProtocolInformation(ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX);
            List<BigIntMatrix> matrixList = new ArrayList<BigIntMatrix>();
            matrixList.add(output);
            newMessage.setBigIntMatrixList(matrixList);
            MessageDispatcher.sendBigIntMatrix(newMessage, messageSender);
        }

    }

    private void doAddShares(MessageBigIntMatrix message) {
        BigIntMatrix matrix = message.getBigIntMatrixList().get(0);
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.SHARE_PART_TWO);
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        while (!(boolean) Regression.getCacheContent(CacheKeys.XTX_DONE)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(BigIntMatrixSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("X^T*X\n" + matrix.addDecryptedMatrix(part2).toDoubleMatrix(
                pp.getM(), publicKeyN));
    }

    private void doAddSharesY(MessageBigIntMatrix message) {
        BigIntMatrix matrix = message.getBigIntMatrixList().get(0);
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.SHARE_PART_TWO_Y);
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        while (!(boolean) Regression.getCacheContent(CacheKeys.XTY_DONE)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(BigIntMatrixSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("X^T*Y\n" + matrix.addDecryptedMatrix(part2).toDoubleMatrix(
                pp.getM(), publicKeyN));
    }

    private void protocolMultiplyDeMatrixEnMatrix(MessageBigIntMatrix message) {
        User messageSender = message.getCurrentUser();
        BigIntMatrix matrix = message.getBigIntMatrixList().get(0);
        String matrixId = message.getMatrixId();
        int stepNumber = message.getProtocolStepNumber();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        if (stepNumber == 1) {
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix designMatrix = (BigIntMatrix) Regression.getCacheContent(message.getOppShareId());
            BigIntMatrix product1 = PaillierProperties.doMultiplyDeMatrixEnMatrix(designMatrix.getTransposedMatrix(), matrix, publicKeyN);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, product1);
            //The following steps are same with protocolMultiplyEnMatrixDeMatrix, so change protocolInformation here
            MessageDispatcher.sendBigIntMatrix(partyTwo.encryptBeforeSentToPartyOne(product1), messageSender, ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 2);
        }
    }

    private void protocolMultiplyEnMatrixDeMatrix(MessageBigIntMatrix message) {
        User messageSender = message.getCurrentUser();
        BigIntMatrix matrix = message.getBigIntMatrixList().get(0);
        String matrixId = message.getMatrixId();
        int stepNumber = message.getProtocolStepNumber();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        if (stepNumber == 1) {
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigIntMatrix designMatrix = (BigIntMatrix) Regression.getCacheContent(message.getOppShareId());
            BigIntMatrix product1 = PaillierProperties.doMultiplyEnMatrixDeMatrix(matrix.getTransposedMatrix(), designMatrix, publicKeyN);
            BigIntMatrix output = partyTwo.encryptBeforeSentToPartyOne(product1);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + matrixId, partyTwo);
            Regression.putCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId, product1);
            MessageDispatcher.sendBigIntMatrix(output, messageSender, ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 2);
        } else if (stepNumber == 2) {
            PartyWithPrivateKey partyOne = (PartyWithPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_ONE);
            partyOne = partyOne.getNewCopy();
            BigIntMatrix output = partyOne.outputEP1(matrix);
            Regression.putCacheContent(CacheKeys.PARTY_ONE + matrixId, partyOne);
            MessageDispatcher.sendBigIntMatrix(output, messageSender, ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 3);
        } else if (stepNumber == 3) {
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO + matrixId);
            BigIntMatrix product1 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId);
            BigIntMatrix output = partyTwo.outputES(matrix, product1);
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + matrixId, partyTwo.outputMatrixShare());
            Regression.removeCacheContent(CacheKeys.PARTY_TWO + matrixId);
            Regression.removeCacheContent(CacheKeys.MATRIX_PRODUCT + matrixId);
            MessageDispatcher.sendBigIntMatrix(output, messageSender, ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId, 4);
        } else if (stepNumber == 4) {
            PartyWithPrivateKey partyOne = (PartyWithPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_ONE + matrixId);
            Regression.putCacheContent(CacheKeys.PARTY_ONE_SHARE + matrixId, partyOne.outputShare(matrix));
            Regression.removeCacheContent(CacheKeys.PARTY_ONE + matrixId);
        }
    }
}
