package protocol;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import utils.FloatingPointNumber;

public class TestDivision {

    public static final double x = 54325325.234;
    public static final double y = 652252.6789;
    private static final double ACCURACY = 0.00001;
    public static BigInteger EP1;
    public static BigInteger share1;
    public static BigInteger share2;

    public TestDivision() {
    }

    // test 1/(x*y)
    @Test
    public void testDevision() {
        ProtocolParameters protocol = new ProtocolParameters();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocol.getM(), protocol.getP());
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne.getKey().getPublicKey(),
                protocol.getM(), protocol.getP());

        BigInteger X = FloatingPointNumber.doubleToBigInteger(x,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigInteger Y = FloatingPointNumber.doubleToBigInteger(y,
                protocol.getM(), partyTwo.getPublicKey().getN());
        X = Paillier.encrypt(X, partyTwo.getPublicKey().getN(), partyTwo
                .getPublicKey().getG());
        BigInteger product = PaillierProperties
                .doMultiplyEncryptedNumberDecryptedNumber(X, Y, partyTwo
                .getPublicKey().getN());

        EP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(product));
        share1 = partyOne.outputShare(partyTwo.outputES(EP1, product));
        share2 = partyTwo.outputShare();

        BigInteger result = share1.add(share2);
        double productResult = FloatingPointNumber.bigIntegerToDouble(result,
                protocol.getM(), partyTwo.getPublicKey().getN());
        double productExpected = x * y;
        System.out.println("product of x*y: " + productExpected);
        Assert.assertTrue((productExpected - productResult) / productExpected < ACCURACY);
        Assert.assertTrue((productExpected - productResult) / productExpected > -ACCURACY);

        BigInteger enS1 = Paillier.encrypt(share1, partyTwo.getPublicKey().getN(), partyTwo
                .getPublicKey().getG());
        //Party one send encrypted share1 to party two
        BigInteger enS2 = Paillier.encrypt(share2, partyTwo.getPublicKey().getN(), partyTwo
                .getPublicKey().getG());
        BigInteger out1 = PaillierProperties.doAdd(enS1, enS2, partyTwo.getPublicKey().getN());
        BigInteger test = Paillier.decrypt(out1, partyOne.getPrivateKey().getLambda(), partyOne.getPrivateKey().getN(), partyOne.getPrivateKey().getG());
        System.out.println("x*y = " + FloatingPointNumber.bigIntegerToDouble(test, protocol.getM(), partyTwo.getPublicKey().getN()));
        Random r = new Random();
        double ran = r.nextDouble();
        BigInteger rNumber = FloatingPointNumber.doubleToBigInteger(ran, protocol.getM(), partyTwo.getPublicKey().getN());
        //Party two send encrypted value of (share1+share2)*rNumber to party one
        out1 = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(out1, rNumber, partyTwo.getPublicKey().getN());

        //Party one get 1/((share1+share2)*rNumber)
        out1 = Paillier.decrypt(out1, partyOne.getPrivateKey().getLambda(), partyOne.getPrivateKey().getN(), partyOne.getPrivateKey().getG());
        BigDecimal dM = new BigDecimal(protocol.getM(), MathContext.UNLIMITED);
        BigDecimal dout1 = new BigDecimal(out1, MathContext.UNLIMITED);
        dout1 = dout1.divide(dM);
        out1 = dout1.toBigInteger();
        double newDoubleS1 = FloatingPointNumber.bigIntegerToDouble(out1, protocol.getM(), partyTwo.getPublicKey().getN());
        newDoubleS1 = 1 / newDoubleS1;
        BigInteger newS1 = FloatingPointNumber.doubleToBigInteger(newDoubleS1, protocol.getM(), partyTwo.getPublicKey().getN());
        newS1 = Paillier.encrypt(newS1, partyTwo.getPublicKey().getN(), partyTwo.getPublicKey().getG());

        product = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(newS1, rNumber, partyTwo.getPublicKey().getN());
        EP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(product));
        share1 = partyOne.outputShare(partyTwo.outputES(EP1, product));
        share2 = partyTwo.outputShare();

        result = share1.add(share2);
        productResult = FloatingPointNumber.bigIntegerToDouble(result,
                protocol.getM(), partyTwo.getPublicKey().getN());
        productExpected = 1 / (x * y);
        System.out.println("Expected 1/(x * y); " + productExpected);
        System.out.println("Result of 1/(x * y); " + productResult);
        Assert.assertTrue((productExpected - productResult) / productExpected < ACCURACY);
        Assert.assertTrue((productExpected - productResult) / productExpected > -ACCURACY);


    }
}