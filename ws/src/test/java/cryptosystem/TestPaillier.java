package cryptosystem;

import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;

public class TestPaillier {

    static String strA = "1234567890";
    static BigInteger A = new BigInteger(strA);
    static String strB = "22345678";
    static BigInteger B = new BigInteger(strB);

    @Test
    public void testEncryption() {
        PaillierPrivateKey key = Paillier.generatePrivateKey();
        BigInteger c = Paillier.encrypt(A, key.getN(), key.getG());
        BigInteger m = Paillier.decrypt(c, key.getLambda(), key.getN(),
                key.getG());
        Assert.assertTrue(!c.equals(m));
        Assert.assertEquals(m, A);
    }

    @Test
    public void testSum() {
        PaillierPrivateKey key = Paillier.generatePrivateKey();
        BigInteger c1 = Paillier.encrypt(A, key.getN(), key.getG());
        BigInteger c2 = Paillier.encrypt(B, key.getN(), key.getG());
        BigInteger cSum = PaillierProperties.doAdd(c1, c2, key.getN());
        BigInteger sum = Paillier.decrypt(cSum, key.getLambda(), key.getN(),
                key.getG());
        Assert.assertEquals(sum, A.add(B));
    }

    @Test
    public void testMultiplication() {
        PaillierPrivateKey key = Paillier.generatePrivateKey();
        BigInteger c1 = Paillier.encrypt(A, key.getN(), key.getG());
        BigInteger cOut = PaillierProperties
                .doMultiplyEncryptedNumberDecryptedNumber(c1, B, key.getN());
        BigInteger out = Paillier.decrypt(cOut, key.getLambda(), key.getN(),
                key.getG());
        Assert.assertEquals(out, A.multiply(B));
    }

    @Test
    public void testSubtraction() {
        PaillierPrivateKey key = Paillier.generatePrivateKey();
        BigInteger c1 = Paillier.encrypt(A, key.getN(), key.getG());
        BigInteger c2 = Paillier.encrypt(B, key.getN(), key.getG());
        BigInteger cSub = PaillierProperties.doSubtract(c1, c2, key.getN());
        BigInteger sub = Paillier.decrypt(cSub, key.getLambda(), key.getN(),
                key.getG());
        Assert.assertEquals(sub, A.subtract(B));
    }
}
