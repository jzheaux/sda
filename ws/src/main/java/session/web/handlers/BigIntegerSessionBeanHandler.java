package session.web.handlers;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import cryptosystem.PaillierPublicKey;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;
import matrix.BigIntMatrix;
import protocol.PartyWithPrivateKey;
import protocol.PartyWithoutPrivateKey;
import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;
import regression.User;
import regression.ProtocolInformation;
import utils.FloatingPointNumber;
import utils.message.MessageBigInteger;
import utils.message.client.MessageDispatcher;

public class BigIntegerSessionBeanHandler implements Runnable {

    private MessageBigInteger message;

    public BigIntegerSessionBeanHandler(MessageBigInteger message) {
        this.message = message;
    }

    @Override
    public void run() {
        handleBigInteger(message);
    }

    public void handleBigInteger(MessageBigInteger message) {
        String protocolInformation = message.getProtocolInformation();
        if (protocolInformation.equals(ProtocolInformation.DO_COMPUTE_INVERSE)) {
            protocolComputeInverse(message);
        } else if (protocolInformation.equals(ProtocolInformation.DO_ADD_SHARES)) {
            doAddIntegerShares(message);
        } else if(protocolInformation.equals(ProtocolInformation.DO_MATRIX_INVERSION_INITIALIZE_MATRIX)){
            doMatrixInversionInitializeMatrix(message);
        } else if(protocolInformation.equals(ProtocolInformation.DO_GENERAL_MATRIX_INVERSION_INITIALIZE_MATRIX)){
            doGeneralMatrixInversionInitializeMatrix(message);
        }
    }
    
    private void doGeneralMatrixInversionInitializeMatrix(MessageBigInteger message){
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        BigInteger publicKeyN = ((PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator))).getN();
        String integerId = message.getIntegerId();
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(message.getOppShareCacheKey());
        BigIntMatrix x1p2Product = PaillierProperties.doMultiplyDeMatrixEnNumber(part2, message.getBigInt(), publicKeyN);
        Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId, x1p2Product);   
    }
    
    private void doMatrixInversionInitializeMatrix(MessageBigInteger message){
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        BigInteger publicKeyN = ((PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator))).getN();
        String integerId = message.getIntegerId();
        BigIntMatrix part2 = (BigIntMatrix) Regression.getCacheContent(CacheKeys.SHARE_PART_TWO);
        BigIntMatrix x1p2Product = PaillierProperties.doMultiplyDeMatrixEnNumber(part2, message.getBigInt(), publicKeyN);
        Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId, x1p2Product);   
    }

    private void doAddIntegerShares(MessageBigInteger message) {
        BigInteger share1 = message.getBigInt();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        ProtocolParameters parameters = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        String oppShareKey = message.getOppShareCacheKey();
        BigInteger share2 = (BigInteger) Regression.getCacheContent(oppShareKey);
        BigInteger result = share1.add(share2);
        double productResult = FloatingPointNumber.bigIntegerToDouble(result, parameters.getM(), publicKeyN);
        System.out.println("Result of 1/(x * y); " + productResult);
    }

    private void protocolComputeInverse(MessageBigInteger message) {
        User messageSender = message.getCurrentUser();
        BigInteger share1 = message.getBigInt();
        String integerId = message.getIntegerId();
        int stepNumber = message.getProtocolStepNumber();
        User creator = (User) Regression.getCacheContent(CacheKeys.CREATOR);
        PaillierPublicKey publicKey = (PaillierPublicKey) Regression.getCacheContent(CacheKeys.keyForPublicKey(creator));
        BigInteger publicKeyN = publicKey.getN();
        BigInteger publicKeyG = publicKey.getG();
        ProtocolParameters parameters = (ProtocolParameters) Regression.getCacheContent(CacheKeys.PROTOCOL_PARAMETERS);
        if (stepNumber == 1) {
            String oppShareKey = message.getOppShareCacheKey();
            BigInteger share2 = (BigInteger) Regression.getCacheContent(oppShareKey);
            share2 = Paillier.encrypt(share2, publicKeyN, publicKeyG);
            BigInteger out1 = PaillierProperties.doAdd(share1, share2, publicKeyN);
            Random r = new Random();
            double ran = r.nextDouble();
            BigInteger rNumber = FloatingPointNumber.doubleToBigInteger(ran, parameters.getM(), publicKeyN);
            //Party two send encrypted value of (share1+share2)*rNumber to party one
            out1 = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(out1, rNumber, publicKeyN);
            Regression.removeCacheContent(oppShareKey);
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId, rNumber);
            MessageDispatcher.sendBigInteger(out1, messageSender, ProtocolInformation.DO_COMPUTE_INVERSE, integerId, 2, null, null);
        } else if (stepNumber == 2) {
            PartyWithPrivateKey partyOne = (PartyWithPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_ONE);
            share1 = Paillier.decrypt(share1, partyOne.getPrivateKey().getLambda(), publicKeyN, publicKeyG);
            BigDecimal dout1 = new BigDecimal(share1, MathContext.UNLIMITED);
            dout1 = dout1.divide(new BigDecimal(parameters.getM(), MathContext.UNLIMITED));
            share1 = dout1.toBigInteger();
            double newDoubleS1 = FloatingPointNumber.bigIntegerToDouble(share1, parameters.getM(), publicKeyN);
            newDoubleS1 = 1 / newDoubleS1;
            BigInteger newS1 = FloatingPointNumber.doubleToBigInteger(newDoubleS1, parameters.getM(), publicKeyN);
            newS1 = Paillier.encrypt(newS1, publicKeyN, publicKeyG);
            MessageDispatcher.sendBigInteger(newS1, messageSender, ProtocolInformation.DO_COMPUTE_INVERSE, integerId, 3, null, null);
        } else if (stepNumber == 3) {
            BigInteger rNumber = (BigInteger) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId);
            BigInteger product = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(share1, rNumber, publicKeyN);
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO);
            partyTwo = partyTwo.getNewCopy();
            BigInteger output = partyTwo.encryptBeforeSentToPartyOne(product);
            Regression.putCacheContent(CacheKeys.PARTY_TWO + integerId, partyTwo);
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId, product);
            MessageDispatcher.sendBigInteger(output, messageSender, ProtocolInformation.DO_COMPUTE_INVERSE, integerId, 4, null, null);
        } else if (stepNumber == 4) {
            PartyWithPrivateKey partyOne = (PartyWithPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_ONE);
            partyOne = partyOne.getNewCopy();
            BigInteger output = partyOne.outputEP1(share1);
            Regression.putCacheContent(CacheKeys.PARTY_ONE + integerId, partyOne);
            MessageDispatcher.sendBigInteger(output, messageSender, ProtocolInformation.DO_COMPUTE_INVERSE, integerId, 5, null, null);
        } else if (stepNumber == 5) {
            PartyWithoutPrivateKey partyTwo = (PartyWithoutPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_TWO + integerId);
            BigInteger product = (BigInteger) Regression.getCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId);
            BigInteger output = partyTwo.outputES(share1, product);
            Regression.putCacheContent(CacheKeys.PARTY_TWO_SHARE + integerId, partyTwo.outputShare());
            Regression.removeCacheContent(CacheKeys.PARTY_TWO + integerId);
            MessageDispatcher.sendBigInteger(output, messageSender, ProtocolInformation.DO_COMPUTE_INVERSE, integerId, 6, null, null);
        } else if (stepNumber == 6) {
            PartyWithPrivateKey partyOne = (PartyWithPrivateKey) Regression.getCacheContent(CacheKeys.PARTY_ONE + integerId);
            share1 = partyOne.outputShare(share1);
            Regression.removeCacheContent(CacheKeys.PARTY_ONE + integerId);
            Regression.putCacheContent(CacheKeys.PARTY_ONE_SHARE + integerId, share1);
        }
    }
}
