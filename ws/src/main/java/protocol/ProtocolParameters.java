package protocol;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProtocolParameters {

    private final int M_EXPONENT = 64;
    private final int P_EXPONENT = 128;
    private final String TWO = "2";
    private BigInteger m;
    private BigInteger p;

    public ProtocolParameters() {
        generateM();
        generateP();
    }

    public BigInteger getM() {
        return m;
    }

    public void setM(BigInteger m) {
        this.m = m;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void generateM() {
        generateM(M_EXPONENT);
    }

    public void generateM(int mExponent) {
        setM((new BigInteger(TWO)).pow(mExponent));
    }

    public void generateP() {
        generateP(P_EXPONENT);
    }

    public void generateP(int pExponent) {
        setP((new BigInteger(TWO)).pow(pExponent));
    }
}
