package cryptosystem;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PaillierPublicKey {

    private BigInteger n;
    private BigInteger g;

    public PaillierPublicKey(BigInteger n, BigInteger g) {
        this.setN(n);
        this.setG(g);
    }

    public PaillierPublicKey() {
    }

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
}
