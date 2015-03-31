package regression.twoPartyLinearRegression;

import regression.ProtocolInformation;
import regression.TwoPartyRegressionHelper;
import cryptosystem.Paillier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import protocol.PartyWithPrivateKey;
import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;
import regression.User;
import utils.RandomIDGenerator;
import utils.message.GeneralInstructionMessage;
import utils.message.MessageBigIntMatrix;
import utils.message.client.MessageDispatcher;

public class TwoPartyLinearRegressionCreatorHandler {
    
    List<User> userList;
    User currentUser;
    User creator;
    DoubleMatrix designMatrixDouble;
    DoubleMatrix responseMatrixDouble;
    PartyWithPrivateKey partyOne;
    BigInteger publicKeyN;
    BigInteger publicKeyG;
    BigInteger protocolM;
    BigInteger protocolP;
    
    public TwoPartyLinearRegressionCreatorHandler() {
        this.userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        this.currentUser = (User) Regression.getCacheContent(CacheKeys.CURRENT_USER);
        this.creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        this.designMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.DESIGN_MATRIX_DOUBLE);
        this.responseMatrixDouble = (DoubleMatrix) Regression.getCacheContent(CacheKeys.RESPONSE_MATRIX_DOUBLE);
    }
    
    public void creatorStartRegression() {
        setupParameters();
        DoubleMatrix doubleMatrixTran = designMatrixDouble.getTransposedMatrix();
        DoubleMatrix dmProduct = doubleMatrixTran.multiply(designMatrixDouble);
        BigIntMatrix bigMatrix = designMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        BigIntMatrix enMatrix = Paillier.encrypt(bigMatrix, publicKeyN, publicKeyG);
        BigIntMatrix bigMatrix2 = responseMatrixDouble.toBigIntMatrix(protocolM, publicKeyN);
        BigIntMatrix enMatrix2 = Paillier.encrypt(bigMatrix2, publicKeyN, publicKeyG);
        //party one send encrypted design matrix to party two
        String matrixId1 = RandomIDGenerator.generateID();
        //share id will be CacheKeys.PARTY_ONE_SHARE + matrixId1
        MessageDispatcher.sendBigIntMatrix(enMatrix, userList.get(1), ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId1, 1, CacheKeys.BIG_INT_DESIGN_MATRIX);
        String matrixId2 = RandomIDGenerator.generateID();
        //share id will be CacheKeys.PARTY_ONE_SHARE + matrixId2
        MessageDispatcher.sendBigIntMatrix(enMatrix, userList.get(1), ProtocolInformation.DO_MULTIPLY_DEMATRIX_ENMATRIX, matrixId2, 1, CacheKeys.BIG_INT_DESIGN_MATRIX);
        GeneralInstructionMessage giMessage = new GeneralInstructionMessage();
        giMessage.setProtocolInformation(ProtocolInformation.DO_XTX_SHARE_SUM);
        List<String> idList = new ArrayList<String>();
        idList.add(CacheKeys.PARTY_TWO_SHARE + matrixId1);
        idList.add(CacheKeys.PARTY_TWO_SHARE + matrixId2);
        giMessage.setMatrixIdList(idList);
        MessageDispatcher.sendGeneralInstruction(giMessage, userList.get(1));
        BigIntMatrix part1 = dmProduct.toBigIntMatrix(protocolM, publicKeyN);
        part1 = part1.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + matrixId1)).addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + matrixId2)).mod(publicKeyN);
//        MessageDispatcher.sendBigIntMatrix(part1, userList.get(1), ProtocolInformation.DO_ADD_SHARES, null, 0);
        
        String matrixId3 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigIntMatrix(enMatrix, userList.get(1), ProtocolInformation.DO_MULTIPLY_ENMATRIX_DEMATRIX, matrixId3, 1, CacheKeys.BIG_INT_RESPONSE_MATRIX);
        String matrixId4 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigIntMatrix(enMatrix2, userList.get(1), ProtocolInformation.DO_MULTIPLY_DEMATRIX_ENMATRIX, matrixId4, 1, CacheKeys.BIG_INT_DESIGN_MATRIX);
        GeneralInstructionMessage giMessage2 = new GeneralInstructionMessage();
        giMessage2.setProtocolInformation(ProtocolInformation.DO_XTY_SHARE_SUM);
        List<String> idList2 = new ArrayList<String>();
        idList2.add(CacheKeys.PARTY_TWO_SHARE + matrixId3);
        idList2.add(CacheKeys.PARTY_TWO_SHARE + matrixId4);
        giMessage2.setMatrixIdList(idList2);
        MessageDispatcher.sendGeneralInstruction(giMessage2, userList.get(1));
        DoubleMatrix dyProduct = doubleMatrixTran.multiply(responseMatrixDouble);
        BigIntMatrix part1y = dyProduct.toBigIntMatrix(protocolM, publicKeyN);
        part1y = part1y.addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + matrixId3)).addDecryptedMatrix((BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + matrixId4)).mod(publicKeyN);
//        MessageDispatcher.sendBigIntMatrix(part1y, userList.get(1), ProtocolInformation.DO_ADD_SHARES_Y, null, 0);
        
        BigInteger trace1 = part1.getTrace().mod(publicKeyN);
        trace1 = Paillier.encrypt(trace1, publicKeyN, publicKeyG);
        String traceId1 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigInteger(trace1, userList.get(1), ProtocolInformation.DO_COMPUTE_INVERSE, traceId1, 1, null, CacheKeys.PARTY_TWO_TRACE);
        trace1 = (BigInteger) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + traceId1);
        MessageDispatcher.sendBigInteger(trace1, userList.get(1), ProtocolInformation.DO_ADD_SHARES, traceId1, 1, null, CacheKeys.PARTY_TWO_SHARE + traceId1);
        //start matrix inversion

        Object[] result = matrixInversion(part1, trace1, traceId1);
        BigIntMatrix matrixInversionResultShare1 = (BigIntMatrix) result[0];
        String matrixInversionResultShare2Id = (String) result[1];
        //SHARES OF  (XTX)^-1:  matrixInversionResultShare1;  CacheKeys.PARTY_TWO_SHARE + matrixInversionResultShare2Id;
        //SHARES OF XTY: part1y, CacheKeys.SHARE_PART_TWO_Y
        String finalResultShare = RandomIDGenerator.generateID();
        // result will be stored in CacheKeys.PARTY_TWO_SHARE+resultId1
        TwoPartyRegressionHelper.computeTwoPartyShareProduct(matrixInversionResultShare1, part1y, publicKeyN, publicKeyG, CacheKeys.PARTY_TWO_SHARE + matrixInversionResultShare2Id, CacheKeys.SHARE_PART_TWO_Y, finalResultShare, userList.get(1));
        BigIntMatrix matrixXShare3 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + finalResultShare);
        String finalResult = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigIntMatrix(matrixXShare3, userList.get(1), ProtocolInformation.DO_SHARES_SUM, finalResult, 0, finalResultShare);
        Regression.addClientMessage("Linear regression estimated coefficients:\n" + (DoubleMatrix) Regression.getCacheContent(CacheKeys.PARTY_ONE_SHARE + finalResult) + "\n");
        GeneralInstructionMessage giMessage3 = new GeneralInstructionMessage();
        giMessage3.setProtocolInformation(ProtocolInformation.DO_SHOW_LINEAR_REGRESSION_FINAL_RESULT);
        giMessage3.setResultId(finalResult);
        MessageDispatcher.sendGeneralInstruction(giMessage3, userList.get(1));
        Regression.regressionEnd = true;
    }
    
    private Object[] matrixInversion(BigIntMatrix part1, BigInteger trace1, String traceId1) {
        BigIntMatrix enPart1 = Paillier.encrypt(part1, publicKeyN, publicKeyG);
        BigInteger enX1 = Paillier.encrypt(trace1, publicKeyN, publicKeyG);
        BigIntMatrix x1p1Product = part1.multiply(trace1);
        x1p1Product = Paillier.encrypt(x1p1Product, publicKeyN, publicKeyG);
        String integerId1 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigInteger(enX1, userList.get(1), ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX, integerId1, 1, null, null);
        String matrixId3 = RandomIDGenerator.generateID();
        MessageDispatcher.sendBigIntMatrix(enPart1, userList.get(1), ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX, matrixId3, 1, CacheKeys.PARTY_TWO_SHARE + traceId1);
        GeneralInstructionMessage giMessage2 = new GeneralInstructionMessage();
        giMessage2.setProtocolInformation(ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX);
        List<String> matrixIdList = new ArrayList<String>();
        matrixIdList.add(CacheKeys.SHARE_PART_TWO);
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
