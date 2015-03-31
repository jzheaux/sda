package protocol;

import java.math.BigInteger;
import java.util.Random;
import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import cryptosystem.PaillierPublicKey;
import matrix.BigIntMatrix;

public class PartyWithoutPrivateKey {

    private BigInteger r;
    private BigInteger p;
    private BigInteger m;
    private PaillierPublicKey publicKey;
    private BigInteger p2;
    private BigInteger r2;
    private BigInteger[][] p2Matrix;
    private BigInteger[][] r2Matrix;

    public PartyWithoutPrivateKey(PaillierPublicKey publicKey, BigInteger m, BigInteger p) {
        setPublicKey(publicKey);
        setP(p);
        setM(m);
        generateR();
    }
    
    public PartyWithoutPrivateKey(){
    }
    
    public PartyWithoutPrivateKey getNewCopy(){
        PartyWithoutPrivateKey partyWithoutPrivateKey = new PartyWithoutPrivateKey();
        partyWithoutPrivateKey.setM(m);
        partyWithoutPrivateKey.setP(p);
        partyWithoutPrivateKey.setR(r);
        partyWithoutPrivateKey.setP2(p2);
        partyWithoutPrivateKey.setP2Matrix(p2Matrix);
        partyWithoutPrivateKey.setR2(r2);
        partyWithoutPrivateKey.setR2Matrix(r2Matrix);
        partyWithoutPrivateKey.setPublicKey(publicKey);
        return partyWithoutPrivateKey;
    }

    public BigInteger getR() {
        return r;
    }

    public PaillierPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PaillierPublicKey publicKey) {
        this.publicKey = publicKey;
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

    public void setR(BigInteger r) {
        this.r = r;
    }

    public BigInteger getP2() {
        return p2;
    }

    public void setP2(BigInteger p2) {
        this.p2 = p2;
    }

    public BigInteger getR2() {
        return r2;
    }

    public void setR2(BigInteger r2) {
        this.r2 = r2;
    }

    public BigInteger[][] getP2Matrix() {
        return p2Matrix;
    }

    public void setP2Matrix(BigInteger[][] p2Matrix) {
        this.p2Matrix = p2Matrix;
    }

    public BigInteger[][] getR2Matrix() {
        return r2Matrix;
    }

    public void setR2Matrix(BigInteger[][] r2Matrix) {
        this.r2Matrix = r2Matrix;
    }
    

    // need to set publicKey and set p before running this method
    private void generateR() {
        if (p != null) {
            do {
                r = new BigInteger(publicKey.getN().bitLength(), new Random());
                r = r.mod(publicKey.getN());
            } while (r.compareTo(p) < 0);
        } else {
            System.exit(0);
        }
    }
    //******************************************************************
    // Two party product protocol start

    // output of this method -> input of PartyOne.outputEP1(BigInteger s);
    public BigInteger encryptBeforeSentToPartyOne(BigInteger product) {
        BigInteger r = Paillier.encrypt(getR(), getPublicKey().getN(),
                getPublicKey().getG());
        return PaillierProperties.doSubtract(product, r, getPublicKey().getN());
    }

    public BigIntMatrix encryptBeforeSentToPartyOne(BigIntMatrix product) {
        int rowNumber = product.getRowNumber();
        int colNumber = product.getColNumber();
        BigInteger[][] productMatrix = product.getMatrix();
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = encryptBeforeSentToPartyOne(productMatrix[i][j]);
            }
        }
        return (new BigIntMatrix(result));
    }

    // c is the same value as the input of public BigInteger
    // encryptBeforeSentToPartyOne(BigInteger product);
    public BigInteger outputES(BigInteger EP1, BigInteger product) {
        p2 = getR().mod(getP());
        BigInteger EP2 = Paillier.encrypt(p2, getPublicKey().getN(),
                getPublicKey().getG());
        BigInteger result = PaillierProperties.doAdd(EP1, EP2, getPublicKey()
                .getN());
        result = PaillierProperties
                .doSubtract(result, product, getPublicKey().getN());
        result = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(
                result, getM().modInverse(getPublicKey().getN()),
                getPublicKey().getN());
        r2 = new BigInteger(publicKey.getN().bitLength(), new Random());
        r2 = r2.mod(publicKey.getN());
        BigInteger ER2 = Paillier.encrypt(r2, getPublicKey().getN(),
                getPublicKey().getG());
        return PaillierProperties
                .doSubtract(result, ER2, getPublicKey().getN());
    }

    public BigIntMatrix outputES(BigIntMatrix EP1, BigIntMatrix product) {
        int rowNumber = EP1.getRowNumber();
        int colNumber = EP1.getColNumber();
        BigInteger[][] EP1Matrix = EP1.getMatrix();
        BigInteger[][] productMatrix = product.getMatrix();
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        p2Matrix = new BigInteger[rowNumber][colNumber];
        r2Matrix = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                p2Matrix[i][j] = getR().mod(getP());
                BigInteger EP2 = Paillier.encrypt(p2Matrix[i][j], getPublicKey().getN(),
                        getPublicKey().getG());
                result[i][j] = PaillierProperties.doAdd(EP1Matrix[i][j], EP2, getPublicKey()
                        .getN());
                result[i][j] = PaillierProperties
                        .doSubtract(result[i][j], productMatrix[i][j], getPublicKey().getN());
                result[i][j] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(
                        result[i][j], getM().modInverse(getPublicKey().getN()),
                        getPublicKey().getN());
                r2Matrix[i][j] = new BigInteger(publicKey.getN().bitLength(), new Random());
                r2Matrix[i][j] = r2Matrix[i][j].mod(publicKey.getN());
                BigInteger ER2 = Paillier.encrypt(r2Matrix[i][j], getPublicKey().getN(),
                        getPublicKey().getG());
                result[i][j] = PaillierProperties
                        .doSubtract(result[i][j], ER2, getPublicKey().getN());
            }
        }
        return (new BigIntMatrix(result));
    }

    // need to run public BigInteger outputES(BigInteger EP1, BigInteger product);
    // before running this method
    public BigInteger outputShare() {
        return p2.divide(getM()).subtract(r2).mod(getPublicKey().getN());
    }

    public BigIntMatrix outputMatrixShare() {
        int rowNumber = p2Matrix.length;
        int colNumber = p2Matrix[0].length;
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = p2Matrix[i][j].divide(getM()).subtract(r2Matrix[i][j]).mod(getPublicKey().getN());
            }
        }
        return (new BigIntMatrix(result));
    }
    //******************************************************************
    // Two party product protocol end
}
