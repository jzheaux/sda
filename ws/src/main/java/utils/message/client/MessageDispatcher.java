package utils.message.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import matrix.BigIntMatrix;
import matrix.DoubleMatrix;

import org.glassfish.jersey.client.ClientResponse;

import protocol.ProtocolParameters;
import regression.CacheKeys;
import regression.Regression;
import regression.User;
import utils.message.GeneralInstructionMessage;
import utils.message.MessageBigIntMatrix;
import utils.message.MessageBigInteger;
import utils.message.MessageDataSourceUsers;
import utils.message.MessageDoubleMatrix;
import utils.message.MessagePing;
import utils.message.MessageProtocolParameters;
import utils.message.MessagePublicKey;
import utils.message.MessageRegressionType;
import cryptosystem.PaillierPublicKey;

public class MessageDispatcher {

    public static int pingUser(User user) {
        MessagePing message = new MessagePing();
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(user, UrlGenerator.PING));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static void sendDataSourceUserInformation() {
        List<User> userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        MessageDataSourceUsers message = new MessageDataSourceUsers();
        message.setUserList(userList);
        for (User u : userList) {
            if (!Regression.isCurrentUser(u)) {
                Client c = ClientBuilder.newClient();
                WebTarget r = c.target(UrlGenerator.generateUrl(u, UrlGenerator.DATA_SOURCE_USER_INFORMATION));
                Response response = r.request().put(Entity.xml(message));//ClientResponse.class, message);
            }
        }
    }

    public static int sendPublicKey(PaillierPublicKey publicKey, User toUser) {
        MessagePublicKey message = new MessagePublicKey();
        message.setPublicKey(publicKey);
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.PUBLIC_KEY));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static void sendRegressionType(String regressionType) {
        MessageRegressionType message = new MessageRegressionType();
        message.setRegressionType(regressionType);
        List<User> userList = (List<User>) Regression.getCacheContent(CacheKeys.DATA_SOURCE_USER);
        for (User u : userList) {
            if (!Regression.isCurrentUser(u)) {
                Client c = ClientBuilder.newClient();
                WebTarget r = c.target(UrlGenerator.generateUrl(u, UrlGenerator.REGRESSION_TYPE));
                Response response = r.request().post(Entity.xml(message));
                response.getStatus();
            }
        }

    }

    public static int sendProtocolParameters(ProtocolParameters parameters, User toUser) {
        MessageProtocolParameters message = new MessageProtocolParameters();
        message.setParameters(parameters);
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.PROTOCOL_PARAMETERS));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static int sendBigIntMatrix(BigIntMatrix bigIntMatrix, User toUser, String protocolInformation, String matrixId, int stepNumber) {
        List<BigIntMatrix> list = new ArrayList<BigIntMatrix>();
        list.add(bigIntMatrix);
        return MessageDispatcher.sendBigIntMatrix(list, toUser, protocolInformation, matrixId, stepNumber, null);
    }

    public static int sendBigIntMatrix(BigIntMatrix bigIntMatrix, User toUser, String protocolInformation, String matrixId, int stepNumber, String oppShareId) {
        List<BigIntMatrix> list = new ArrayList<BigIntMatrix>();
        list.add(bigIntMatrix);
        return MessageDispatcher.sendBigIntMatrix(list, toUser, protocolInformation, matrixId, stepNumber, oppShareId);
    }

    public static int sendBigIntMatrix(List<BigIntMatrix> bigIntMatrixList, User toUser, String protocolInformation, String matrixId, int stepNumber, String oppShareId) {
        MessageBigIntMatrix message = new MessageBigIntMatrix();
        message.setBigIntMatrixList(bigIntMatrixList);
        message.setProtocolInformation(protocolInformation);
        message.setMatrixId(matrixId);
        message.setProtocolStepNumber(stepNumber);
        message.setOppShareId(oppShareId);
        return sendBigIntMatrix(message, toUser);
    }

    public static int sendDoubleMatrix(DoubleMatrix doubleMatrix, User toUser, String protocolInformation, String resultId) {
        MessageDoubleMatrix message = new MessageDoubleMatrix();
        message.setDoubleMatrix(doubleMatrix);
        message.setProtocolInformation(protocolInformation);
        message.setResultId(resultId);
        return sendDoubleMatrix(message, toUser);
    }

    public static int sendDoubleMatrix(MessageDoubleMatrix message, User toUser) {
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.DOUBLE_MATRIX));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static int sendBigIntMatrix(MessageBigIntMatrix message, User toUser) {
        System.out.println("Sending BigIntMatrix, matrix id:" + message.getMatrixId() + ", protocol: " + message.getProtocolInformation() + " , to " + toUser.toString());
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.BIG_INT_MATRIX));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static int sendGeneralInstruction(GeneralInstructionMessage message, User toUser) {
        System.out.println("Sending General Instruction, protocol: " + message.getProtocolInformation() + " , to " + toUser.toString());
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.GENERAL_INSTRUCTION));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static int sendBigInteger(BigInteger bigInt, User toUser, String protocolInformation, String integerId, int stepNumber, String resultCacheKey, String oppShareCasheKey) {
        MessageBigInteger message = new MessageBigInteger();
        message.setBigInt(bigInt);
        message.setProtocolInformation(protocolInformation);
        message.setIntegerId(integerId);
        message.setProtocolStepNumber(stepNumber);
        message.setOppShareCacheKey(oppShareCasheKey);
        message.setResultCasheKey(resultCacheKey);
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.BIG_INTEGER));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }

    public static int sendBigInteger(BigInteger bigInt, User toUser, String protocolInformation, String integerId, int stepNumber, String resultCacheKey, String oppShareCasheKey, String additionalInfo) {
        MessageBigInteger message = new MessageBigInteger();
        message.setBigInt(bigInt);
        message.setProtocolInformation(protocolInformation);
        message.setIntegerId(integerId);
        message.setProtocolStepNumber(stepNumber);
        message.setOppShareCacheKey(oppShareCasheKey);
        message.setResultCasheKey(resultCacheKey);
        message.setAdditionalInfo(additionalInfo);
        Client c = ClientBuilder.newClient();
        WebTarget r = c.target(UrlGenerator.generateUrl(toUser, UrlGenerator.BIG_INTEGER));
        Response response = r.request().post(Entity.xml(message));
        return response.getStatus();
    }
}
