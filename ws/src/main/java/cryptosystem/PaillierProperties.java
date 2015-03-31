package cryptosystem;

import java.math.BigInteger;

import protocol.PartyWithPrivateKey;
import protocol.PartyWithoutPrivateKey;
import matrix.BigIntMatrix;

public class PaillierProperties {

    public static final int TWO = 2;
    public static final int INVERSE_ITERATIONS = 128;
    public static final BigInteger MINUS_ONE = new BigInteger("-1");

    // input: E(x), E(y), n; output: E(x + y)
    public static BigInteger doAdd(BigInteger a, BigInteger b, BigInteger n) {
        return a.multiply(b).mod(n.pow(TWO));
    }

    // input: E(x), y, n; output: E(x * y)
    public static BigInteger doMultiplyEncryptedNumberDecryptedNumber(
            BigInteger a, BigInteger b, BigInteger n) {
        return a.modPow(b, n.pow(TWO));
    }

    // input: E(x), E(y), n; output: E(x - y)
    public static BigInteger doSubtract(BigInteger a, BigInteger b, BigInteger n) {
        BigInteger r = PaillierProperties
                .doMultiplyEncryptedNumberDecryptedNumber(b, MINUS_ONE, n);
        return doAdd(a, r, n);
    }

    // input: E(matrix1), E(matrix2), n; output: E(matrix1 + matrix2)
    public static BigIntMatrix doAdd(BigIntMatrix firstMatrix,
            BigIntMatrix secondMatrix, BigInteger n) {
        BigInteger[][] a = firstMatrix.getMatrix();
        BigInteger[][] b = secondMatrix.getMatrix();
        int i = firstMatrix.getRowNumber();
        int j = firstMatrix.getColNumber();
        if (i != secondMatrix.getRowNumber()
                || j != secondMatrix.getColNumber()) {
            return null;
        }

        BigInteger[][] result = new BigInteger[i][j];
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                result[k][l] = doAdd(a[k][l], b[k][l], n);
            }
        }
        return new BigIntMatrix(result);
    }

    // input: E(matrix1), E(matrix2), n; output: E(matrix1 - matrix2)
    public static BigIntMatrix doSubtract(BigIntMatrix firstMatrix,
            BigIntMatrix secondMatrix, BigInteger n) {
        BigInteger[][] a = firstMatrix.getMatrix();
        BigInteger[][] b = secondMatrix.getMatrix();
        int i = firstMatrix.getRowNumber();
        int j = firstMatrix.getColNumber();
        if (i != secondMatrix.getRowNumber()
                || j != secondMatrix.getColNumber()) {
            return null;
        }

        BigInteger[][] result = new BigInteger[i][j];
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                result[k][l] = doSubtract(a[k][l], b[k][l], n);
            }
        }
        return new BigIntMatrix(result);
    }

    // input: E(x), protocol partyOne, partyTwo; output: shares of x/M
    public static BigInteger[] getBigIntProductShares(BigInteger product,
            PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo) {
        BigInteger[] shares = new BigInteger[2];
        BigInteger EP1 = partyOne.outputEP1(partyTwo
                .encryptBeforeSentToPartyOne(product));
        shares[0] = partyOne.outputShare(partyTwo.outputES(EP1, product));
        shares[1] = partyTwo.outputShare();
        return shares;
    }

    // input: E(matrix1), matrix2, n; output: E(matrix1 * matrix2)
    public static BigIntMatrix doMultiplyEnMatrixDeMatrix(
            BigIntMatrix encryptedMatrix, BigIntMatrix deMatrix, BigInteger n) {
        if (encryptedMatrix.getColNumber() != deMatrix.getRowNumber()) {
            return null;
        }
        int rowNumber = encryptedMatrix.getRowNumber();
        int colNumber = deMatrix.getColNumber();
        BigInteger[][] matrix1 = encryptedMatrix.getMatrix();
        BigInteger[][] matrix2 = deMatrix.getMatrix();
        BigInteger[][] product = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                BigInteger r = doMultiplyEncryptedNumberDecryptedNumber(
                        matrix1[i][0], matrix2[0][j], n);
                for (int k = 1; k < encryptedMatrix.getColNumber(); k++) {
                    r = doAdd(
                            r,
                            doMultiplyEncryptedNumberDecryptedNumber(
                            matrix1[i][k], matrix2[k][j], n), n);
                }
                product[i][j] = r;
            }
        }
        return new BigIntMatrix(product);
    }

    // input: matrix1, E(matrix2), n; output: E(matrix1 * matrix2)
    public static BigIntMatrix doMultiplyDeMatrixEnMatrix(
            BigIntMatrix deMatrix, BigIntMatrix encryptedMatrix, BigInteger n) {
        if (deMatrix.getColNumber() != encryptedMatrix.getRowNumber()) {
            return null;
        }
        int rowNumber = deMatrix.getRowNumber();
        int colNumber = encryptedMatrix.getColNumber();
        BigInteger[][] matrix1 = deMatrix.getMatrix();
        BigInteger[][] matrix2 = encryptedMatrix.getMatrix();
        BigInteger[][] product = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                BigInteger r = doMultiplyEncryptedNumberDecryptedNumber(
                        matrix2[0][j], matrix1[i][0], n);
                for (int k = 1; k < deMatrix.getColNumber(); k++) {
                    r = doAdd(
                            r,
                            doMultiplyEncryptedNumberDecryptedNumber(
                            matrix2[k][j], matrix1[i][k], n), n);
                }
                product[i][j] = r;
            }
        }
        return new BigIntMatrix(product);
    }

    public static BigIntMatrix doMultiplyEnMatrixDeNumber(BigIntMatrix matrix,
            BigInteger c, BigInteger n) {
        int rowNumber = matrix.getRowNumber();
        int colNumber = matrix.getColNumber();
        BigInteger[][] matrix1 = matrix.getMatrix();
        BigInteger[][] product = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                product[i][j] = doMultiplyEncryptedNumberDecryptedNumber(
                        matrix1[i][j], c, n);
            }
        }
        return new BigIntMatrix(product);
    }

    public static BigIntMatrix doMultiplyDeMatrixEnNumber(BigIntMatrix matrix,
            BigInteger c, BigInteger n) {
        int rowNumber = matrix.getRowNumber();
        int colNumber = matrix.getColNumber();
        BigInteger[][] matrix1 = matrix.getMatrix();
        BigInteger[][] product = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                product[i][j] = doMultiplyEncryptedNumberDecryptedNumber(
                        c, matrix1[i][j], n);
            }
        }
        return new BigIntMatrix(product);
    }

    public static BigIntMatrix[] getBigIntMatrixProductShares(
            BigIntMatrix product, PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo) {
        int rowNumber = product.getRowNumber();
        int colNumber = product.getColNumber();
        BigInteger[][] productMatrix = product.getMatrix();
        BigInteger[][] matrixShare1 = new BigInteger[rowNumber][colNumber];
        BigInteger[][] matrixShare2 = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                BigInteger[] shares = getBigIntProductShares(
                        productMatrix[i][j], partyOne, partyTwo);
                matrixShare1[i][j] = shares[0];
                matrixShare2[i][j] = shares[1];
            }
        }
        BigIntMatrix[] shares = new BigIntMatrix[2];
        shares[0] = new BigIntMatrix(matrixShare1);
        shares[1] = new BigIntMatrix(matrixShare2);
        return shares;
    }

    // get trace of an encrypted matrix
    public static BigInteger getMatrixTrace(BigIntMatrix matrix, BigInteger n) {
        if (matrix.getColNumber() != matrix.getRowNumber()) {
            return null;
        }
        BigInteger[][] m = matrix.getMatrix();
        BigInteger trace = BigInteger.ZERO;
        int num = matrix.getColNumber();
        for (int i = 0; i < num; i++) {
            trace = doAdd(trace, m[i][i], n);
        }
        return trace;
    }
}
