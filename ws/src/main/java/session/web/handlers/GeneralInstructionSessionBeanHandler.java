package session.web.handlers;

import cryptosystem.Paillier;
import cryptosystem.PaillierPublicKey;
import java.math.BigInteger;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;
import regression.User;
import regression.ProtocolInformation;
import utils.message.GeneralInstructionMessage;
import utils.message.client.MessageDispatcher;

public class GeneralInstructionSessionBeanHandler implements Runnable {

    private GeneralInstructionMessage message;

    public GeneralInstructionSessionBeanHandler(GeneralInstructionMessage message) {
        this.message = message;
    }

    @Override
    public void run() {
        handleGeneralInstruction(message);
    }

    public void handleGeneralInstruction(GeneralInstructionMessage message) {
        if (message.getProtocolInformation().equals(ProtocolInformation.DO_XTX_SHARE_SUM)) {
            doXTXShareSum(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_XTY_SHARE_SUM)) {
            doXTYShareSum(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX)) {
            doMatrixInversionInitializeMatrix(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_COMPUTE_MATRIX_X_SHARE_TWO)) {
            doComputeMatrixXShareTwo(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_TWO_PARTY_SHARE_PRODUCT_UPDATE_SHARE)) {
            doTwoPartyShareProductUpdateShare(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_INITIATE_BETA_VALUE)) {
            doInitiateBetaValue(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.GET_SHARE)) {
            getShare(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_COMPUTE_W_DIAGONAL)) {
            doComputeWDiagonal(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_ADD)) {
            doAdd(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_SET_CACHE)) {
            doSetCache(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_UPDATE_BETA)) {
            doUpdateBeta(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_COMPUTE_PART_TWO)) {
            doComputePartTwo(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_SHOW_LINEAR_REGRESSION_FINAL_RESULT)) {
            doShowLinearRegressionFinalResult(message);
        } else if (message.getProtocolInformation().equals(ProtocolInformation.DO_SHOW_BINOMIAL_LOGIT_REGRESSION_FINAL_RESULT)) {
            doShowBinomialLogitRegressionFinalResult(message);
        }
    }

    private void doShowBinomialLogitRegressionFinalResult(GeneralInstructionMessage message) {
        DoubleMatrix matrix = (DoubleMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId());
        Regression.addClientMessage("Binomial logistic regression estimated coefficients:\n" + matrix + "\n");
        Regression.regressionEnd = true;
    }
    
    private void doShowLinearRegressionFinalResult(GeneralInstructionMessage message) {
        DoubleMatrix matrix = (DoubleMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId());
        Regression.addClientMessage("Linear regression estimated coefficients:\n" + matrix + "\n");
        Regression.regressionEnd = true;
    }

    private void doComputePartTwo(GeneralInstructionMessage message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        Regression.putCacheContent(CacheKeys.SHARE_PART_TWO + message.getResultId(), Regression.getCacheContent(message.getMatrixIdList().get(0)));
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0));
        BigInteger trace2 = part2.getTrace().mod(publicKeyN);
        Regression.putCacheContent(CacheKeys.PARTY_TWO_TRACE + message.getResultId(), trace2);
    }

    private void doUpdateBeta(GeneralInstructionMessage message) {
        BigIntMatrix beta = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0));
        beta = beta.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(1)));
        Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId(), beta);
    }

    private void doSetCache(GeneralInstructionMessage message) {
        Regression.putCacheContent(message.getMatrixIdList().get(0), Regression.getCacheContent(message.getMatrixIdList().get(1)));
    }

    private void doAdd(GeneralInstructionMessage message) {
        BigIntMatrix a = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0));
        BigIntMatrix b = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(1));
        Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId(), a.addDecryptedMatrix(b));
    }

    private void doComputeWDiagonal(GeneralInstructionMessage message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        BigIntMatrix x2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.BIG_INT_DESIGN_MATRIX);
        BigIntMatrix wDiagonal = ((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getMatrixIdList().get(0))).subtractDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getMatrixIdList().get(1)));
        DoubleMatrix dW2 = new DoubleMatrix(new double[x2.getRowNumber()][x2.getRowNumber()]);
        BigIntMatrix w2 = dW2.toBigIntMatrix(pp.getM(), publicKeyN);
        BigInteger[][] wtmp2 = w2.getMatrix();
        for (int i = 0; i < w2.getColNumber(); i++) {
            wtmp2[i][i] = wDiagonal.getMatrix()[i][0];
        }
        w2 = new BigIntMatrix(wtmp2);
        Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId(), w2);

    }

    private void getShare(GeneralInstructionMessage message) {
        int stepNumber = message.getStepNumber();
        if (stepNumber == 1) {
            GeneralInstructionMessage giMessage1 = new GeneralInstructionMessage();
            giMessage1.setProtocolInformation(ProtocolInformation.GET_SHARE);
            giMessage1.setResultId(message.getResultId());
            giMessage1.setStepNumber(2);
            giMessage1.setMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId()));
            MessageDispatcher.sendGeneralInstruction(giMessage1, message.getCurrentUser());
        } else if (stepNumber == 2) {
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + message.getResultId(), message.getMatrix());
        }
    }

    private void doInitiateBetaValue(GeneralInstructionMessage message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        ProtocolParameters pp = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        BigIntMatrix x2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.BIG_INT_DESIGN_MATRIX);
        BigIntMatrix beta2 = new DoubleMatrix(new double[x2.getColNumber()][1]).toBigIntMatrix(pp.getM(), publicKeyN);
        Regression.putCacheContent(message.getResultId(), beta2);
    }

    private void doTwoPartyShareProductUpdateShare(GeneralInstructionMessage message) {
        BigIntMatrix share = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0));
        BigIntMatrix matrix = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(1));
        share = share.addDecryptedMatrix(share).subtractDecryptedMatrix(matrix);
        Regression.putCacheContent(message.getResultId(), share);
        Regression.removeCacheContent(message.getMatrixIdList().get(0));
    }

    private void doComputeMatrixXShareTwo(GeneralInstructionMessage message) {
        BigIntMatrix matrixMShare2 = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0));
        BigInteger x2 = (BigInteger) Regression.getCacheContent(message.getIntegerIdList().get(0));
        BigIntMatrix matrixXShare2 = matrixMShare2.getIdentityMatrix(matrixMShare2.getColNumber()).multiply(x2);
        Regression.putCacheContent(message.getResultId(), matrixXShare2);
    }

    private void doMatrixInversionInitializeMatrix(GeneralInstructionMessage message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigInteger publicKeyG = publicKey.getG();
        int stepNumber = message.getStepNumber();
        if (stepNumber == 1) {
            BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0));
            BigInteger x2 = (BigInteger) Regression.getCacheContent(message.getIntegerIdList().get(0));
            BigIntMatrix x2p2Product = part2.multiply(x2);
            x2p2Product = Paillier.encrypt(x2p2Product, publicKeyN, publicKeyG);
            Regression.putCacheContent(message.getResultId(), x2p2Product);
        }
    }

    private void doXTXShareSum(GeneralInstructionMessage message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.SHARE_PART_TWO);
        part2 = part2.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0)))
                .addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(1)))
                .mod(publicKeyN);
        Regression.putCacheContent(CacheKeys.SHARE_PART_TWO, part2);
        Regression.putCacheContent(CacheKeys.XTX_DONE, true);
    }

    private void doXTYShareSum(GeneralInstructionMessage message) {
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.SHARE_PART_TWO_Y);
        part2 = part2.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(0)))
                .addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(message.getMatrixIdList().get(1)))
                .mod(publicKeyN);
        Regression.putCacheContent(CacheKeys.SHARE_PART_TWO_Y, part2);
        Regression.putCacheContent(CacheKeys.XTY_DONE, true);
    }
}
