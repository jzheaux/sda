package cryptosystem;

import java.math.BigInteger;

public class PaillierPrivateKey {

    private BigInteger n;
    private BigInteger g;
    private BigInteger lambda;

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getLambda() {
        return lambda;
    }

    public void setLambda(BigInteger lambda) {
        this.lambda = lambda;
    }
}
