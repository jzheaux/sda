package regression;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import java.math.BigInteger;
import org.junit.Test;
import protocol.PartyWithPrivateKey;
import protocol.PartyWithoutPrivateKey;
import protocol.ProtocolParameters;
import utils.FloatingPointNumber;

public class TestSharing {

    public TestSharing() {
    }

    @Test
    public void testSharing() {
        ProtocolParameters protocol = new ProtocolParameters();
        BigInteger protocolM = protocol.getM();
        BigInteger protocolP = protocol.getP();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocolM,
                protocolP);
        // party two only has the public key
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne
                .getKey().getPublicKey(), protocolM, protocolP);
        BigInteger N = partyTwo.getPublicKey().getN();
        BigInteger G = partyTwo.getPublicKey().getG();
        BigInteger newN = Paillier.generatePrivateKey().getN();
        int i = 1;
        while (newN.compareTo(N) != -1) {
            newN = Paillier.generatePrivateKey().getN();
            System.out.println("test: ==============  " + i);
            i++;
        }

        BigInteger a = FloatingPointNumber.doubleToBigInteger(-123, protocolM, newN);
        BigInteger b = FloatingPointNumber.doubleToBigInteger(-2, protocolM, newN);
        System.out.println("a: " + a + "\nb: " + b);

        a = Paillier.encrypt(a, N, G);
        b = Paillier.encrypt(b, N, G);
        BigInteger sum = PaillierProperties.doAdd(a, b, N);

        a = Paillier.decrypt(a, partyOne.getPrivateKey().getLambda(), N, G);
        b = Paillier.decrypt(b, partyOne.getPrivateKey().getLambda(), N, G);
        sum = Paillier.decrypt(sum, partyOne.getPrivateKey().getLambda(), N, G);
        sum = sum.mod(newN);

        double a1 = FloatingPointNumber.bigIntegerToDouble(a, protocolM, newN);
        double b1 = FloatingPointNumber.bigIntegerToDouble(b, protocolM, newN);
        double sum1 = FloatingPointNumber.bigIntegerToDouble(sum, protocolM, newN);
        System.out.println("a: " + a1 + "\nb: " + b1 + "\nsum: " + sum1);
    }

    @Test
    public void testSharing2() {
        ProtocolParameters protocol = new ProtocolParameters();
        BigInteger protocolM = protocol.getM();
        BigInteger protocolP = protocol.getP();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocolM,
                protocolP);
        // party two only has the public key
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne
                .getKey().getPublicKey(), protocolM, protocolP);
        BigInteger N = partyTwo.getPublicKey().getN();
        BigInteger G = partyTwo.getPublicKey().getG();
        BigInteger newN = Paillier.generatePrivateKey().getN();
        int i = 1;
        while (newN.compareTo(N) != -1) {
            newN = Paillier.generatePrivateKey().getN();
            System.out.println("test: ==============  " + i);
            i++;
        }

        BigInteger a = FloatingPointNumber.doubleToBigInteger(-123, protocolM, N);
        BigInteger b = FloatingPointNumber.doubleToBigInteger(-2, protocolM, N);
        System.out.println("a: " + a + "\nb: " + b);

        a = Paillier.encrypt(a, N, G);
        b = Paillier.encrypt(b, N, G);
        BigInteger sum = PaillierProperties.doAdd(a, b, N);
        a = Paillier.decrypt(a, partyOne.getPrivateKey().getLambda(), N, G);
        b = Paillier.decrypt(b, partyOne.getPrivateKey().getLambda(), N, G);
        sum = Paillier.decrypt(sum, partyOne.getPrivateKey().getLambda(), N, G);

        double a1 = FloatingPointNumber.bigIntegerToDouble(a, protocolM, N);
        double b1 = FloatingPointNumber.bigIntegerToDouble(b, protocolM, N);
        double sum1 = FloatingPointNumber.bigIntegerToDouble(sum, protocolM, N);
        System.out.println("a: " + a1 + "\nb: " + b1 + "\nsum: " + sum1);
    }
}