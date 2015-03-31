package regression.twoPartyLogitRegression;

import cryptosystem.Paillier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import matrix.DoubleMatrixHelper;
import protocol.PartyWithPrivateKey;
import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.ProtocolInformation;
import regression.Regression;
import regression.TwoPartyRegressionHelper;
import regression.User;
import utils.RandomIDGenerator;
import utils.message.GeneralInstructionMessage;
import utils.message.MessageBigIntMatrix;
import utils.message.client.MessageDispatcher;

public class TwoPartyBinomialLogitRegressionCreatorHandler {

    private static final double ACCEPTABLE_ERROR = 0.00001;
    private List<User> userList;
    private User currentUser;
    private User creator;
    private DoubleMatrix designMatrixDouble;
    private DoubleMatrix responseMatrixDouble;
    private PartyWithPrivateKey partyOne;
    private BigInteger publicKeyN;
    private BigInteger publicKeyG;
    private BigInteger protocolM;
    private BigInteger protocolP;

    public TwoPartyBinomialLogitRegressionCreatorHandler() {
        this.userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        this.currentUser = (User) Regression.getCacheContent(CacheKeys.CURRENT_USER);
        this.creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        this.designMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE);
        this.responseMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE);
    }

    public void creatorStartRegression() {
        setupParameters();

        BigIntMatrix x1 = designMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        BigIntMatrix y1 = responseMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        BigIntMatrix beta1 = new DoubleMatrix(new double[x1.getColNumber()][1]).toBigIntMatrix(protocolM, publicKeyN);
        String betaCacheId = RandomIDGenerator.generateID();
        Regression.putCacheContent(CacheKeys.PARTY_ONE_SHARE + betaCacheId, beta1);
        GeneralInstructionMessage giMessage = new GeneralInstructionMessage();
        giMessage.setResultId(CacheKeys.PARTY_TWO_SHARE + betaCacheId);
        giMessage.setProtocolInformation(ProtocolInformation.DO_INITIATE_BETA_VALUE);
        MessageDispatcher.sendGeneralInstruction(giMessage, userList.get(1));

        int stepNumber = 0;
        DoubleMatrix previousBeta = null;
        for (int k = 0; k < 15; k++) {
            stepNumber++;
            String resultId1 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareProduct(x1, (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + betaCacheId), publicKeyN, publicKeyG, CacheKeys.BIG_INT_DESIGN_MATRIX, CacheKeys.PARTY_TWO_SHARE + betaCacheId, resultId1, userList.get(1));
            BigIntMatrix enPi1 = Paillier.encrypt((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId1), publicKeyN, publicKeyG);

            String resultId2 = RandomIDGenerator.generateID();
            MessageDispatcher.sendBigIntMatrix(enPi1, userList.get(1), ProtocolInformation.DO_E_EXP, resultId2, 1, resultId1);
            // cache key for e1 will be CacheKeys.PARTY_ONE_SHARE + resultId2

            String resultId3 = RandomIDGenerator.generateID();
            BigIntMatrix enE1 = Paillier.encrypt((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId2), publicKeyN, publicKeyG);
            MessageDispatcher.sendBigIntMatrix(enE1, userList.get(1), ProtocolInformation.DO_ONE_OVER_E_PLUS_ONE, resultId3, 1, resultId2);

            GeneralInstructionMessage giMessage1 = new GeneralInstructionMessage();
            giMessage1.setProtocolInformation(ProtocolInformation.GET_SHARE);
            giMessage1.setResultId(resultId3);
            giMessage1.setStepNumber(1);
            MessageDispatcher.sendGeneralInstruction(giMessage1, userList.get(1));

            BigIntMatrix tmpS1 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId3);
            BigIntMatrix tmpS2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + resultId3);

            BigIntMatrix product = tmpS1.addDecryptedMatrix(tmpS2);
            product = DoubleMatrixHelper.elementInverse(product.toDoubleMatrix(protocolM, publicKeyN)).toBigIntMatrix(protocolM, publicKeyN);
            product = Paillier.encrypt(product, publicKeyN, publicKeyG);
            String resultId4 = RandomIDGenerator.generateID();
            MessageDispatcher.sendBigIntMatrix(product, userList.get(1), ProtocolInformation.DO_ONE_OVER_E_PLUS_ONE, resultId4, 2, resultId3);


            String resultId5 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareVectorElementProduct((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId2), (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId4), publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + resultId2, CacheKeys.PARTY_TWO_SHARE + resultId4, resultId5, userList.get(1));
            // resultid of pi1 will be  resultId5

            String resultId6 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareVectorElementProduct((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId5), (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId5), publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + resultId5, CacheKeys.PARTY_TWO_SHARE + resultId5, resultId6, userList.get(1));

            BigIntMatrix wDiagonal = ((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId5)).subtractDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId6));
            String resultId7 = RandomIDGenerator.generateID();
            GeneralInstructionMessage giMessage2 = new GeneralInstructionMessage();
            giMessage2.setProtocolInformation(ProtocolInformation.DO_COMPUTE_W_DIAGONAL);
            giMessage2.setResultId(resultId7);
            giMessage2.setStepNumber(1);
            List<String> matrixList2 = new ArrayList<String>();
            matrixList2.add(resultId5);
            matrixList2.add(resultId6);
            giMessage2.setMatrixIdList(matrixList2);
            MessageDispatcher.sendGeneralInstruction(giMessage2, userList.get(1));


            DoubleMatrix dW1 = new DoubleMatrix(new double[x1.getRowNumber()][x1.getRowNumber()]);
            BigIntMatrix w1 = dW1.toBigIntMatrix(protocolM, publicKeyN);
            BigInteger[][] wtmp1 = w1.getMatrix();
            for (int i = 0; i < w1.getColNumber(); i++) {
                wtmp1[i][i] = wDiagonal.getMatrix()[i][0];
            }
            w1 = new BigIntMatrix(wtmp1);

            String resultId8 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareProduct(x1.getTransposedMatrix(), w1, publicKeyN, publicKeyG, CacheKeys.BIG_INT_DESIGN_MATRIX_TRANSPOSED, CacheKeys.PARTY_TWO_SHARE + resultId7, resultId8, userList.get(1));
            String resultId9 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareProduct((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId8), x1, publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + resultId8, CacheKeys.BIG_INT_DESIGN_MATRIX, resultId9, userList.get(1));
            //resultid of xtwx share will be resultId9

            enPi1 = Paillier.encrypt((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId5), publicKeyN, publicKeyG);
            // party 2 get encrypted pi1 and computes the value of - pi
            String resultId10 = RandomIDGenerator.generateID();
            MessageDispatcher.sendBigIntMatrix(enPi1, userList.get(1), ProtocolInformation.DO_INVERT_SIGN, resultId10, 1, resultId5);
            // resultid of pi1 will be resultId10

            String resultId11 = RandomIDGenerator.generateID();
            GeneralInstructionMessage giMessage3 = new GeneralInstructionMessage();
            giMessage3.setProtocolInformation(ProtocolInformation.DO_ADD);
            giMessage3.setResultId(resultId11);
            giMessage3.setStepNumber(1);
            List<String> matrixList3 = new ArrayList<String>();
            matrixList3.add(CacheKeys.BIG_INT_RESPONSE_MATRIX);
            matrixList3.add(CacheKeys.PARTY_TWO_SHARE + resultId10);
            giMessage3.setMatrixIdList(matrixList3);
            MessageDispatcher.sendGeneralInstruction(giMessage3, userList.get(1));

            String resultId12 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareProduct(x1.getTransposedMatrix(), y1.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId10)), publicKeyN, publicKeyG, CacheKeys.BIG_INT_DESIGN_MATRIX_TRANSPOSED, CacheKeys.PARTY_TWO_SHARE + resultId11, resultId12, userList.get(1));
            //resultid of part2 will be resultId12

            String partTwoId = RandomIDGenerator.generateID();
            GeneralInstructionMessage giMessage4 = new GeneralInstructionMessage();
            giMessage4.setProtocolInformation(ProtocolInformation.DO_COMPUTE_PART_TWO);
            giMessage4.setResultId(partTwoId);
            List<String> matrixList4 = new ArrayList<String>();
            matrixList4.add(CacheKeys.PARTY_TWO_SHARE + resultId9);
            giMessage4.setMatrixIdList(matrixList4);
            MessageDispatcher.sendGeneralInstruction(giMessage4, userList.get(1));


            BigIntMatrix part1 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId9);
            BigInteger trace1 = part1.getTrace().mod(publicKeyN);
            trace1 = Paillier.encrypt(trace1, publicKeyN, publicKeyG);
            String traceId1 = RandomIDGenerator.generateID();
            MessageDispatcher.sendBigInteger(trace1, userList.get(1), ProtocolInformation.DO_COMPUTE_INVERSE, traceId1, 1, null, CacheKeys.PARTY_TWO_TRACE + partTwoId);
            trace1 = (BigInteger) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + traceId1);
            //MessageDispatcher.sendBigInteger(trace1, userList.get(1), ProtocolInformation.DO_ADD_SHARES, traceId1, 1, null, CacheKeys.PARTY_TWO_SHARE + traceId1);
            //start matrix inversion

            Object[] result = matrixInversion(part1, trace1, traceId1, partTwoId);
            BigIntMatrix matrixInversionResultShare1 = (BigIntMatrix) result[0];
            String matrixInversionResultShare2Id = (String) result[1];
            //SHARES OF  (XTWX)^-1:  matrixInversionResultShare1;  CacheKeys.PARTY_TWO_SHARE + matrixInversionResultShare2Id;

            String resultId13 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareProduct(matrixInversionResultShare1, (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId12), publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + matrixInversionResultShare2Id, CacheKeys.PARTY_TWO_SHARE + resultId12, resultId13, userList.get(1));
            //resultid of part2 will be resultId12
            BigIntMatrix beta = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + betaCacheId);
            beta = beta.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId13));
            String betaOldId = betaCacheId;
            betaCacheId = RandomIDGenerator.generateID();
            Regression.putCacheContent(CacheKeys.PARTY_ONE_SHARE + betaCacheId, beta);

            GeneralInstructionMessage giMessage5 = new GeneralInstructionMessage();
            giMessage5.setProtocolInformation(ProtocolInformation.DO_UPDATE_BETA);
            giMessage5.setResultId(betaCacheId);
            List<String> matrixList5 = new ArrayList<String>();
            matrixList5.add(CacheKeys.PARTY_TWO_SHARE + betaOldId);
            matrixList5.add(CacheKeys.PARTY_TWO_SHARE + resultId13);
            giMessage5.setMatrixIdList(matrixList5);
            MessageDispatcher.sendGeneralInstruction(giMessage5, userList.get(1));

            if (stepNumber > 1) {
                String finalResult = RandomIDGenerator.generateID();
                MessageDispatcher.sendBigIntMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + betaCacheId), userList.get(1), ProtocolInformation.DO_SHARES_SUM, finalResult, 0, betaCacheId);
                DoubleMatrix newBeta = (DoubleMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + finalResult);
                if (previousBeta != null && previousBeta.equalWithinError(newBeta, ACCEPTABLE_ERROR)) {
                    Regression.addClientMessage("Binomial logistic regression estimated coefficients:\n" + (DoubleMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + finalResult) + "\n");
                    GeneralInstructionMessage giMessageFinalResult = new GeneralInstructionMessage();
                    giMessageFinalResult.setProtocolInformation(ProtocolInformation.DO_SHOW_BINOMIAL_LOGIT_REGRESSION_FINAL_RESULT);
                    giMessageFinalResult.setResultId(finalResult);
                    MessageDispatcher.sendGeneralInstruction(giMessageFinalResult, userList.get(1));
                    Regression.regressionEnd = true;
                    break;
                }
                previousBeta = newBeta;
            }
            if (stepNumber > 10) {
                stepNumber = 0;
                //roll back, initialize beta with random value
                DoubleMatrix random = DoubleMatrixHelper.getRandomElementMatrix(previousBeta.getRowNumber(), previousBeta.getColNumber());
                BigIntMatrix bigIntRandom = random.toBigIntMatrix(protocolM, publicKeyN);
                Regression.putCacheContent(CacheKeys.PARTY_ONE_SHARE + betaCacheId, bigIntRandom);
                MessageDispatcher.sendBigIntMatrix(bigIntRandom, userList.get(1), ProtocolInformation.DO_SET_CACHE, CacheKeys.PARTY_TWO_SHARE + betaCacheId, 0);
            }
        }

    }

    private Object[] matrixInversion(BigIntMatrix part1, BigInteger trace1, String traceId1, String partTwoId) {
        BigIntMatrix enPart1 = Paillier.encrypt(part1, publicKeyN, publicKeyG);
        BigInteger enX1 = Paillier.encrypt(trace1, publicKeyN, publicKeyG);
        BigIntMatrix x1p1Product = part1.multiply(trace1);
        x1p1Product = Paillier.encrypt(x1p1Product, publicKeyN, publicKeyG);
        String integerId1 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigInteger(enX1, userList.get(1), ProtocolInformation.DO_GENERAL_MATRIX_INVERSION_INITIALIZE_MATRIX, integerId1, 1, null, CacheKeys.SHARE_PART_TWO + partTwoId);
        String matrixId3 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigIntMatrix(enPart1, userList.get(1), ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX, matrixId3, 1, CacheKeys.PARTY_TWO_SHARE + traceId1);
        GeneralInstructionMessage giMessage2 = new GeneralInstructionMessage();
        giMessage2.setProtocolInformation(ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX);
        List<String> matrixIdList = new ArrayList<String>();
        matrixIdList.add(CacheKeys.SHARE_PART_TWO + partTwoId);
        giMessage2.setMatrixIdList(matrixIdList);
        List<String> integerIdList = new ArrayList<String>();
        integerIdList.add(CacheKeys.PARTY_TWO_SHARE + traceId1);
        giMessage2.setIntegerIdList(integerIdList);
        String x2p2ProductId = RandomIDGenerator.generateID();
        giMessage2.setResultId(x2p2ProductId);
        giMessage2.setStepNumber(1);
        MessageDispatcher.sendGeneralInstruction(giMessage2, userList.get(1));

        String matrixInvesionResultId = RandomIDGenerator.generateID();
        MessageBigIntMatrix biMessage3 = new MessageBigIntMatrix();
        biMessage3.setMatrixId(matrixInvesionResultId);
        List<String> idList = new ArrayList<String>();
        idList.add(x2p2ProductId);
        idList.add(CacheKeys.PARTY_TWO_SHARE + matrixId3);
        idList.add(CacheKeys.PARTY_TWO_SHARE + integerId1);
        biMessage3.setAdditionalInformation(idList);
        biMessage3.setProtocolInformation(ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX);
        biMessage3.setProtocolStepNumber(2);
        List<BigIntMatrix> mList = new ArrayList<BigIntMatrix>();
        mList.add(x1p1Product);
        biMessage3.setBigIntMatrixList(mList);
        MessageDispatcher.sendBigIntMatrix(biMessage3, userList.get(1));
        // party one result id will be CacheKeys.PARTY_ONE_SHARE + matrixInvesionResultId

        BigIntMatrix matrixMShare1 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + matrixInvesionResultId);
        BigIntMatrix matrixXShare1 = matrixMShare1.getIdentityMatrix(matrixMShare1.getColNumber()).multiply(trace1);
        String matrixXShare2Id = RandomIDGenerator.generateID();
        GeneralInstructionMessage giMessage = new GeneralInstructionMessage();
        giMessage.setResultId(CacheKeys.PARTY_TWO_SHARE + matrixXShare2Id);
        List<String> matrixIdlist = new ArrayList<String>();
        matrixIdlist.add(CacheKeys.PARTY_TWO_SHARE + matrixInvesionResultId);
        giMessage.setMatrixIdList(matrixIdlist);
        List<String> integerIdList2 = new ArrayList<String>();
        integerIdList2.add(CacheKeys.PARTY_TWO_SHARE + traceId1);
        giMessage.setIntegerIdList(integerIdList2);
        giMessage.setProtocolInformation(ProtocolInformation.DO_COMPUTE_MATRIX_X_SHARE_TWO);
        MessageDispatcher.sendGeneralInstruction(giMessage, userList.get(1));

        String newMId = matrixInvesionResultId;
        String oldMId;
        String newXId = matrixXShare2Id;
        String oldXId;
        for (int i = 0; i < 30; i++) {
            String resultId1 = RandomIDGenerator.generateID();
            // result will be stored in CacheKeys.PARTY_TWO_SHARE+resultId1
            TwoPartyRegressionHelper.computeTwoPartyShareProduct(matrixMShare1, matrixMShare1, publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + newMId, CacheKeys.PARTY_TWO_SHARE + newMId, resultId1, userList.get(1));
            String resultId2 = RandomIDGenerator.generateID();
            TwoPartyRegressionHelper.computeTwoPartyShareProduct(matrixXShare1, matrixMShare1, publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + newXId, CacheKeys.PARTY_TWO_SHARE + newMId, resultId2, userList.get(1));
            oldMId = newMId;
            oldXId = newXId;
            newMId = RandomIDGenerator.generateID();
            newXId = RandomIDGenerator.generateID();
            updateShareProductShares(CacheKeys.PARTY_TWO_SHARE + oldMId, CacheKeys.PARTY_TWO_SHARE + resultId1, CacheKeys.PARTY_TWO_SHARE + newMId);
            updateShareProductShares(CacheKeys.PARTY_TWO_SHARE + oldXId, CacheKeys.PARTY_TWO_SHARE + resultId2, CacheKeys.PARTY_TWO_SHARE + newXId);

            BigIntMatrix mspShare1 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId1);
            matrixMShare1 = matrixMShare1.addDecryptedMatrix(matrixMShare1).subtractDecryptedMatrix(mspShare1);
            BigIntMatrix xspShare1 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + resultId2);
            matrixXShare1 = matrixXShare1.addDecryptedMatrix(matrixXShare1).subtractDecryptedMatrix(xspShare1);
        }
        //the result of matrix inversion will be CacheKeys.PARTY_TWO_SHARE + newXId, and matrixXShare1

        Object[] result = new Object[2];
        result[0] = matrixXShare1;
        result[1] = newXId;
        return result;
    }

    private void updateShareProductShares(String key1, String key2, String resultId) {
        GeneralInstructionMessage giMessage3 = new GeneralInstructionMessage();
        giMessage3.setResultId(resultId);
        List<String> matrixIdlist3 = new ArrayList<String>();
        matrixIdlist3.add(key1);
        matrixIdlist3.add(key2);
        giMessage3.setMatrixIdList(matrixIdlist3);
        giMessage3.setProtocolInformation(ProtocolInformation.DO_TWO_PARTY_SHARE_PRODUCT_UPDATE_SHARE);
        MessageDispatcher.sendGeneralInstruction(giMessage3, userList.get(1));
    }

    private void setupParameters() {
        ProtocolParameters parameters = new ProtocolParameters();
        protocolM = parameters.getM();
        protocolP = parameters.getP();
        partyOne = new PartyWithPrivateKey(protocolM, protocolP);
        publicKeyN = partyOne.getKey().getPublicKey().getN();
        publicKeyG = partyOne.getKey().getPublicKey().getG();
        MessageDispatcher.sendProtocolParameters(parameters, userList.get(1));
        MessageDispatcher.sendPublicKey(partyOne.getKey().getPublicKey(), userList.get(1));
        Regression.putCacheContent(CacheKeys.PARTY_ONE, partyOne);
        Regression.putCacheContent(CacheKeys.keyForPublicKey(creator), partyOne.getKey().getPublicKey());
        Regression.putCacheContent(CacheKeys.PROTOCOL_PARAMETERS, parameters);
    }
}
