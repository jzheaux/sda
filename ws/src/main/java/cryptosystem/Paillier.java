package cryptosystem;

import java.math.BigInteger;
import java.util.Random;
import matrix.BigIntMatrix;

public class Paillier {

    private static final int CERTAINTY = 70;
    private static final int KEY_LENGTH = 512;

    public static PaillierKey generateKey() {
        return generateKey(KEY_LENGTH);
    }

    public static PaillierKey generateKey(int bitLength) {
        PaillierPrivateKey privateKey = generatePrivateKey(bitLength);
        PaillierPublicKey publicKey = new PaillierPublicKey(privateKey.getN(),
                privateKey.getG());
        return (new PaillierKey(privateKey, publicKey));
    }

    public static PaillierPrivateKey generatePrivateKey() {
        return generatePrivateKey(Paillier.KEY_LENGTH);
    }

    // bitLength: length of key
    public static PaillierPrivateKey generatePrivateKey(int bitLength) {
        PaillierPrivateKey key = new PaillierPrivateKey();
        BigInteger p = generatePrimeNumber(bitLength / 2, Paillier.CERTAINTY);
        BigInteger q = generatePrimeNumber(bitLength / 2, Paillier.CERTAINTY);
        BigInteger n = p.multiply(q);
        key.setN(n);
        BigInteger lambda = leastCommonMultiple(p.subtract(BigInteger.ONE),
                q.subtract(BigInteger.ONE));
        key.setLambda(lambda);
        BigInteger gcd;
        BigInteger g;
        do {
            g = new BigInteger(bitLength * 2, new Random()).mod(n.pow(2));
            gcd = n.gcd(Paillier.functionL(g.modPow(lambda, n.pow(2)), n));
        } while (gcd.compareTo(BigInteger.ONE) != 0);
        key.setG(g);

        return key;
    }

    // m is the message to be encrypted
    // (n, g) is the public (encryption) key
    public static BigInteger encrypt(BigInteger m, BigInteger n, BigInteger g) {
        return encrypt(m, n, g, new BigInteger(Paillier.KEY_LENGTH,
                new Random()));
    }

    public static BigInteger encrypt(BigInteger m, BigInteger n, BigInteger g,
            BigInteger r) {
        BigInteger nsquare = n.pow(2);
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }

    // c is the message to be decrypted
    // (lambda, n, g) is the private (decryption) key
    public static BigInteger decrypt(BigInteger c, BigInteger lambda,
            BigInteger n, BigInteger g) {
        BigInteger nsquare = n.pow(2);
        BigInteger l1 = functionL(c.modPow(lambda, nsquare), n);
        BigInteger l2 = functionL(g.modPow(lambda, nsquare), n).modInverse(n);
        return l1.multiply(l2).mod(n);
    }

    public static BigInteger generatePrimeNumber(int bitLength, int certainty) {
        return new BigInteger(bitLength, certainty, new Random());
    }

    public static BigInteger leastCommonMultiple(BigInteger a, BigInteger b) {
        return a.multiply(b).divide(a.gcd(b));
    }

    public static BigInteger functionL(BigInteger u, BigInteger n) {
        return u.subtract(BigInteger.ONE).divide(n);
    }

    public static BigIntMatrix encrypt(BigIntMatrix matrix, BigInteger n,
            BigInteger g) {
        int i = matrix.getRowNumber();
        int j = matrix.getColNumber();
        BigInteger[][] oMatrix = matrix.getMatrix();
        BigInteger[][] enMatrix = new BigInteger[i][j];
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                enMatrix[k][l] = encrypt(oMatrix[k][l], n, g);
            }
        }
        return new BigIntMatrix(enMatrix);
    }

    public static BigIntMatrix decrypt(BigIntMatrix matrix, BigInteger lambda,
            BigInteger n, BigInteger g) {
        int i = matrix.getRowNumber();
        int j = matrix.getColNumber();
        BigInteger[][] oMatrix = matrix.getMatrix();
        BigInteger[][] deMatrix = new BigInteger[i][j];
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                deMatrix[k][l] = decrypt(oMatrix[k][l], lambda, n, g);
            }
        }
        return new BigIntMatrix(deMatrix);
    }
}
