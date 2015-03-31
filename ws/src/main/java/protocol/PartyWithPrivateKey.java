package protocol;

import cryptosystem.Paillier;
import java.math.BigInteger;
import cryptosystem.PaillierKey;
import cryptosystem.PaillierPrivateKey;
import matrix.BigIntMatrix;

public class PartyWithPrivateKey {

    private PaillierKey key;
    private PaillierPrivateKey privateKey;
    private BigInteger p;
    private BigInteger m;
    private BigInteger p1;
    private BigInteger[][] p1Matrix;

    public PartyWithPrivateKey(BigInteger m, BigInteger p) {
        this.generateNewKey();
        this.setM(m);
        this.setP(p);
    }
    
    public PartyWithPrivateKey(){     
    }
    
    public PartyWithPrivateKey getNewCopy(){
        PartyWithPrivateKey partyWithPrivateKey = new PartyWithPrivateKey();
        partyWithPrivateKey.setKey(key);
        partyWithPrivateKey.setPrivateKey(privateKey);
        partyWithPrivateKey.setM(m);
        partyWithPrivateKey.setP(p);
        partyWithPrivateKey.setP1(p1);
        partyWithPrivateKey.setP1Matrix(p1Matrix);
        return partyWithPrivateKey;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getM() {
        return m;
    }

    public void setM(BigInteger m) {
        this.m = m;
    }

    public PaillierPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PaillierPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public BigInteger getP1() {
        return p1;
    }

    public void setP1(BigInteger p1) {
        this.p1 = p1;
    }

    public BigInteger[][] getP1Matrix() {
        return p1Matrix;
    }

    public void setP1Matrix(BigInteger[][] p1Matrix) {
        this.p1Matrix = p1Matrix;
    }

    public PaillierKey getKey() {
        return key;
    }

    public void setKey(PaillierKey key) {
        this.key = key;
    }

    public void generateNewKey() {
        setKey(Paillier.generateKey());
        setPrivateKey(key.getPrivateKey());
    }

    //******************************************************************
    // Two party product protocol start
    public BigInteger outputEP1(BigInteger s) {
        s = Paillier.decrypt(s, getPrivateKey().getLambda(), getPrivateKey()
                .getN(), getPrivateKey().getG());
        p1 = s.subtract(getPrivateKey().getN()).mod(getP());
        return Paillier.encrypt(p1, getPrivateKey().getN(), getPrivateKey()
                .getG());
    }

    public BigIntMatrix outputEP1(BigIntMatrix matrix) {
        int rowNumber = matrix.getRowNumber();
        int colNumber = matrix.getColNumber();
        BigInteger[][] m = matrix.getMatrix();
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        p1Matrix = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                m[i][j] = Paillier.decrypt(m[i][j], getPrivateKey().getLambda(), getPrivateKey()
                        .getN(), getPrivateKey().getG());
                p1Matrix[i][j] = m[i][j].subtract(getPrivateKey().getN()).mod(getP());
                result[i][j] = Paillier.encrypt(p1Matrix[i][j], getPrivateKey().getN(), getPrivateKey()
                        .getG());
            }
        }
        return (new BigIntMatrix(result));
    }

    public BigInteger outputShare(BigInteger eS) {
        return p1
                .divide(getM())
                .subtract(
                Paillier.decrypt(eS, privateKey.getLambda(),
                privateKey.getN(), privateKey.getG()))
                .mod(getPrivateKey().getN());
    }

    public BigIntMatrix outputShare(BigIntMatrix eS) {
        int rowNumber = p1Matrix.length;
        int colNumber = p1Matrix[0].length;
        BigInteger[][] eSMatrix = eS.getMatrix();
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = p1Matrix[i][j]
                        .divide(getM())
                        .subtract(
                        Paillier.decrypt(eSMatrix[i][j], privateKey.getLambda(),
                        privateKey.getN(), privateKey.getG()))
                        .mod(getPrivateKey().getN());
            }
        }
        return (new BigIntMatrix(result));
    }
    //******************************************************************
    // Two party product protocol end
}
