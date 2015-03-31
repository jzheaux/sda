package protocol;

import java.math.BigInteger;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import utils.FloatingPointNumber;

import org.junit.Assert;
import org.junit.Test;

public class TestTwoPartyProductProtocol {

    public static final double x = 12345325325.23453254;
    public static final double y = 24532520.23432453221;
    private static final double ACCURACY = 0.000000000000001;
    public static BigInteger EP1;
    public static BigInteger share1;
    public static BigInteger share2;

    @Test
    public void testProduct() {
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

        System.out.println("Expected output:");
        System.out.println(productExpected);
        System.out.println("Actual output:");
        System.out.println(productResult);
        Assert.assertTrue((productExpected - productResult) / productExpected < ACCURACY);
        Assert.assertTrue((productExpected - productResult) / productExpected > -ACCURACY);

    }

    @Test
    public void testNumberInverse() {
        ProtocolParameters protocol = new ProtocolParameters();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocol.getM(),
                protocol.getP());
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne
                .getKey().getPublicKey(), protocol.getM(), protocol.getP());

        BigInteger smallNum = FloatingPointNumber.doubleToBigInteger(0.000001,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigInteger trace1 = FloatingPointNumber.doubleToBigInteger(8122,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigInteger trace2 = FloatingPointNumber.doubleToBigInteger(21453,
                protocol.getM(), partyTwo.getPublicKey().getN());
        trace1 = trace1.multiply(smallNum).divide(protocol.getM());
        trace2 = trace2.multiply(smallNum).divide(protocol.getM());
        BigInteger x1 = FloatingPointNumber.doubleToBigInteger(0.5,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigInteger x2 = FloatingPointNumber.doubleToBigInteger(0.5,
                protocol.getM(), partyTwo.getPublicKey().getN());
        x1 = x1.multiply(smallNum).divide(protocol.getM());
        x2 = x2.multiply(smallNum).divide(protocol.getM());

        for (int i = 0; i < 30; i++) {
            BigInteger encryptedTrace1 = Paillier.encrypt(trace1, partyTwo
                    .getPublicKey().getN(), partyTwo.getPublicKey().getG());
            BigInteger traceProduct = PaillierProperties
                    .doMultiplyEncryptedNumberDecryptedNumber(encryptedTrace1, trace2,
                    partyTwo.getPublicKey().getN());
            BigInteger trace1sq = trace1.multiply(trace1).mod(partyTwo.getPublicKey().getN());
            BigInteger trace2sq = trace2.multiply(trace2).mod(partyTwo.getPublicKey().getN());
            BigInteger encryptedTrace1sq = Paillier.encrypt(trace1sq, partyTwo
                    .getPublicKey().getN(), partyTwo.getPublicKey().getG());
            BigInteger encryptedTrace2sq = Paillier.encrypt(trace2sq, partyTwo
                    .getPublicKey().getN(), partyTwo.getPublicKey().getG());

            BigInteger enProduct = PaillierProperties.doAdd(encryptedTrace1sq, encryptedTrace2sq, partyTwo.getPublicKey().getN());
            enProduct = PaillierProperties.doAdd(enProduct, traceProduct, partyTwo.getPublicKey().getN());
            enProduct = PaillierProperties.doAdd(enProduct, traceProduct, partyTwo.getPublicKey().getN());
            BigInteger tEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(enProduct));
            BigInteger tProductShare1 = partyOne.outputShare(partyTwo.outputES(tEP1,
                    enProduct));
            BigInteger tProductShare2 = partyTwo.outputShare();

            BigInteger encryptedX1 = Paillier.encrypt(x1, partyTwo
                    .getPublicKey().getN(), partyTwo.getPublicKey().getG());
            BigInteger xt1Product = PaillierProperties
                    .doMultiplyEncryptedNumberDecryptedNumber(encryptedX1, trace2,
                    partyTwo.getPublicKey().getN());
            BigInteger xt2Product = PaillierProperties
                    .doMultiplyEncryptedNumberDecryptedNumber(encryptedTrace1, x2,
                    partyTwo.getPublicKey().getN());
            BigInteger p1 = Paillier.encrypt(x1.multiply(trace1), partyTwo
                    .getPublicKey().getN(), partyTwo.getPublicKey().getG());
            BigInteger p2 = Paillier.encrypt(x2.multiply(trace2), partyTwo
                    .getPublicKey().getN(), partyTwo.getPublicKey().getG());
            BigInteger enXProduct = PaillierProperties.doAdd(xt1Product, xt2Product, partyTwo.getPublicKey().getN());
            enXProduct = PaillierProperties.doAdd(enXProduct, p1, partyTwo.getPublicKey().getN());
            enXProduct = PaillierProperties.doAdd(enXProduct, p2, partyTwo.getPublicKey().getN());
            BigInteger xEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(enXProduct));
            BigInteger xProductShare1 = partyOne.outputShare(partyTwo.outputES(xEP1,
                    enXProduct));
            BigInteger xProductShare2 = partyTwo.outputShare();

            x1 = x1.add(x1).subtract(xProductShare1).mod(partyTwo.getPublicKey().getN());
            x2 = x2.add(x2).subtract(xProductShare2).mod(partyTwo.getPublicKey().getN());

            trace1 = trace1.add(trace1).subtract(tProductShare1).mod(partyTwo.getPublicKey().getN());
            trace2 = trace2.add(trace2).subtract(tProductShare2).mod(partyTwo.getPublicKey().getN());
        }
        System.out.println(FloatingPointNumber.bigIntegerToDouble(trace1.add(trace2).mod(partyTwo.getPublicKey().getN()), protocol.getM(), partyTwo.getPublicKey().getN()));
        System.out.println(FloatingPointNumber.bigIntegerToDouble(x1.add(x2).mod(partyTwo.getPublicKey().getN()), protocol.getM(), partyTwo.getPublicKey().getN()));



    }

    @Test
    public void testDoubleInverse() {
        double c = 0.000000000001;
        double m = 3 * c;
        double x = c;
        for (int i = 0; i < 3000; i++) {
            x = fx(x, m);
            m = fm(m);
        }
        System.out.println(x);
        System.out.println(m);
    }

    public double fx(double x, double m) {
        return (2 * x - x * m);
    }

    public double fm(double m) {
        return (2 * m - m * m);
    }
}
