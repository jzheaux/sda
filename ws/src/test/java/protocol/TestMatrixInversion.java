package protocol;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import java.math.BigInteger;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import org.junit.Test;
import utils.FloatingPointNumber;
import utils.Reader;

public class TestMatrixInversion {

    public final String ADDRESS = ".\\test\\testFiles\\testMatrix3";
    public final String COMMA = ",";
    public final double x = 0.00000001;

    @Test
    public void testMatrix() {
        ProtocolParameters protocol = new ProtocolParameters();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocol.getM(), protocol.getP());
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne.getKey().getPublicKey(),
                protocol.getM(), protocol.getP());

        Reader reader = new Reader();
        DoubleMatrix doubleMatrix = new DoubleMatrix(reader.readMatrix(ADDRESS,
                COMMA));
        System.out.println("read double matrix:");
        System.out.println(doubleMatrix);

        System.out.println("transpose double matrix:");
        System.out.println(doubleMatrix.getTransposedMatrix());

        System.out.println("multiply double matrix:");
        System.out.println(doubleMatrix.getTransposedMatrix().multiply(
                doubleMatrix));

        System.out.println("convert double matrix to biginteger matrix:");
        BigIntMatrix bigMatrix = doubleMatrix.toBigIntMatrix(protocol.getM(),
                partyTwo.getPublicKey().getN());
        System.out.println(bigMatrix);

        System.out.println("encrypt matrix:");
        BigIntMatrix enMatrix = Paillier.encrypt(bigMatrix, partyTwo
                .getPublicKey().getN(), partyTwo.getPublicKey().getG());
        System.out.println(enMatrix);

        System.out.println("add two encrypted matrix:");
        BigIntMatrix add = PaillierProperties.doAdd(enMatrix, enMatrix,
                partyTwo.getPublicKey().getN());
        System.out.println(add);

        System.out.println("decrypt matrix add:");
        add = Paillier.decrypt(add, partyOne.getPrivateKey().getLambda(),
                partyTwo.getPublicKey().getN(), partyTwo.getPublicKey().getG());
        System.out.println(new DoubleMatrix(add.toDouble(protocol.getM(),
                partyTwo.getPublicKey().getN())));

        System.out.println("multiply encrypted matrix and decrypted:");
        BigIntMatrix product = PaillierProperties.doMultiplyEnMatrixDeMatrix(
                enMatrix.getTransposedMatrix(), bigMatrix, partyTwo
                .getPublicKey().getN());
        System.out.println(product);

        System.out.println("decrypt matrix multiply:");
        BigIntMatrix[] productShares = PaillierProperties
                .getBigIntMatrixProductShares(product, partyOne, partyTwo);
        product = productShares[0].addDecryptedMatrix(productShares[1]);
        System.out.println(new DoubleMatrix(product.toDouble(protocol.getM(),
                partyTwo.getPublicKey().getN())));
    }

    @Test
    public void testInverse() {
        ProtocolParameters protocol = new ProtocolParameters();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocol.getM(), protocol.getP());
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne.getKey().getPublicKey(),
                protocol.getM(), protocol.getP());

        Reader reader = new Reader();
        DoubleMatrix doubleMatrix = new DoubleMatrix(reader.readMatrix(ADDRESS,
                COMMA));
        System.out.println("read double matrix:");
        System.out.println(doubleMatrix);

        System.out.println("convert double matrix to biginteger matrix:");
        BigIntMatrix bigMatrix = doubleMatrix.toBigIntMatrix(protocol.getM(),
                partyTwo.getPublicKey().getN());
        System.out.println(bigMatrix);

        System.out.println("encrypt matrix:");
        BigIntMatrix enMatrix = Paillier.encrypt(bigMatrix, partyTwo
                .getPublicKey().getN(), partyTwo.getPublicKey().getG());
        System.out.println(enMatrix);

        System.out.println("multiply encrypted matrix and decrypted:");
        BigIntMatrix product = PaillierProperties.doMultiplyEnMatrixDeMatrix(
                enMatrix.getTransposedMatrix(), bigMatrix, partyTwo
                .getPublicKey().getN());
        System.out.println(product);

        System.out.println("decrypt matrix multiply:");
        BigIntMatrix[] productShares = PaillierProperties
                .getBigIntMatrixProductShares(product, partyOne, partyTwo);
        product = productShares[0].addDecryptedMatrix(productShares[1]);
        product = (new DoubleMatrix(product.toDouble(protocol.getM(), partyTwo
                .getPublicKey().getN()))).toBigIntMatrix(protocol.getM(),
                partyTwo.getPublicKey().getN());
        System.out.println(new DoubleMatrix(product.toDouble(protocol.getM(),
                partyTwo.getPublicKey().getN())));
        BigIntMatrix a = Paillier.encrypt(product, partyTwo.getPublicKey()
                .getN(), partyTwo.getPublicKey().getG());

        BigInteger c = product.getTrace();
        double dc = FloatingPointNumber.bigIntegerToDouble(c, protocol.getM(),
                partyTwo.getPublicKey().getN());
        c = FloatingPointNumber.doubleToBigInteger(1 / dc, protocol.getM(),
                partyTwo.getPublicKey().getN());

        BigIntMatrix m = getProduct(a, c, partyOne, partyTwo, protocol);
        BigIntMatrix x = DoubleMatrix
                .getIdentityMatrix(a.getColNumber())
                .toBigIntMatrix(protocol.getM(), partyTwo.getPublicKey().getN());
        x = Paillier.encrypt(x, partyTwo.getPublicKey().getN(), partyTwo
                .getPublicKey().getG());
        x = getProduct(x, c, partyOne, partyTwo, protocol);

        for (int i = 0; i < 30; i++) {
            x = fx(x, m, partyOne, partyTwo, protocol);
            m = fm(m, partyOne, partyTwo, protocol);
        }

        m = Paillier.decrypt(m, partyOne.getPrivateKey().getLambda(), partyTwo
                .getPublicKey().getN(), partyTwo.getPublicKey().getG());
        System.out.println(new DoubleMatrix(m.toDouble(protocol.getM(),
                partyTwo.getPublicKey().getN())));

        BigIntMatrix f = this.getProduct(x, a, partyOne, partyTwo, protocol);
        f = Paillier.decrypt(f, partyOne.getPrivateKey().getLambda(), partyTwo
                .getPublicKey().getN(), partyTwo.getPublicKey().getG());
        System.out.println(new DoubleMatrix(f.toDouble(protocol.getM(),
                partyTwo.getPublicKey().getN())));
    }

    public BigIntMatrix getProduct(BigIntMatrix matrix, BigInteger num,
            PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo, ProtocolParameters protocol) {
        BigIntMatrix product = PaillierProperties.doMultiplyEnMatrixDeNumber(
                matrix, num, partyTwo.getPublicKey().getN());
        BigIntMatrix[] productShares = PaillierProperties
                .getBigIntMatrixProductShares(product, partyOne, partyTwo);
        product = productShares[0].addDecryptedMatrix(productShares[1]);
        product = (new DoubleMatrix(product.toDouble(protocol.getM(), partyTwo
                .getPublicKey().getN()))).toBigIntMatrix(protocol.getM(),
                partyTwo.getPublicKey().getN());
        product = Paillier.encrypt(product, partyTwo.getPublicKey().getN(),
                partyTwo.getPublicKey().getG());
        return product;
    }

    public BigIntMatrix getProduct(BigIntMatrix matrix1, BigIntMatrix matrix2,
            PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo, ProtocolParameters protocol) {
        matrix2 = Paillier.decrypt(matrix2, partyOne.getPrivateKey()
                .getLambda(), partyTwo.getPublicKey().getN(), partyTwo
                .getPublicKey().getG());
        BigIntMatrix product = PaillierProperties.doMultiplyEnMatrixDeMatrix(
                matrix1, matrix2, partyTwo.getPublicKey().getN());
        BigIntMatrix[] productShares = PaillierProperties
                .getBigIntMatrixProductShares(product, partyOne, partyTwo);
        product = productShares[0].addDecryptedMatrix(productShares[1]);
        product = (new DoubleMatrix(product.toDouble(protocol.getM(), partyTwo
                .getPublicKey().getN()))).toBigIntMatrix(protocol.getM(),
                partyTwo.getPublicKey().getN());
        product = Paillier.encrypt(product, partyTwo.getPublicKey().getN(),
                partyTwo.getPublicKey().getG());
        return product;
    }

    public BigIntMatrix fx(BigIntMatrix x, BigIntMatrix m, PartyWithPrivateKey partyOne,
            PartyWithoutPrivateKey partyTwo, ProtocolParameters protocol) {
        BigIntMatrix result = this.getProduct(x, m, partyOne, partyTwo,
                protocol);
        BigInteger two = FloatingPointNumber.doubleToBigInteger(2,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigInteger nOne = FloatingPointNumber.doubleToBigInteger(-1,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigIntMatrix item1 = this.getProduct(x, two, partyOne, partyTwo,
                protocol);
        BigIntMatrix item2 = this.getProduct(result, nOne, partyOne, partyTwo,
                protocol);
        result = PaillierProperties.doAdd(item1, item2, partyTwo.getPublicKey()
                .getN());
        return result;
    }

    public BigIntMatrix fm(BigIntMatrix m, PartyWithPrivateKey partyOne,
            PartyWithoutPrivateKey partyTwo, ProtocolParameters protocol) {
        BigIntMatrix result = this.getProduct(m, m, partyOne, partyTwo,
                protocol);
        BigInteger two = FloatingPointNumber.doubleToBigInteger(2,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigInteger nOne = FloatingPointNumber.doubleToBigInteger(-1,
                protocol.getM(), partyTwo.getPublicKey().getN());
        BigIntMatrix item1 = this.getProduct(m, two, partyOne, partyTwo,
                protocol);
        BigIntMatrix item2 = this.getProduct(result, nOne, partyOne, partyTwo,
                protocol);
        result = PaillierProperties.doAdd(item1, item2, partyTwo.getPublicKey()
                .getN());
        return result;
    }
}