package matrix;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlRootElement;

import utils.FloatingPointNumber;

@XmlRootElement
public class BigIntMatrix extends AbstractMatrix {

    private BigInteger[][] matrix;

    public BigIntMatrix() {
    }

    public BigIntMatrix(BigInteger[][] matrix) {
        setMatrix(matrix);
    }

    public BigIntMatrix(int rowNumber, int colNumber) {
        BigInteger[][] m = new BigInteger[rowNumber][colNumber];
        setMatrix(m);
    }

    public BigInteger[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(BigInteger[][] matrix) {
        this.matrix = matrix;
        rowNumber = matrix.length;
        colNumber = matrix[0].length;
    }

    public double[][] toDouble(BigInteger m, BigInteger n) {
        double[][] a = new double[rowNumber][colNumber];
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                a[k][l] = FloatingPointNumber.bigIntegerToDouble(matrix[k][l],
                        m, n);
            }
        }
        return a;
    }

    public DoubleMatrix toDoubleMatrix(BigInteger m, BigInteger n) {
        return new DoubleMatrix(toDouble(m, n));
    }

    @Override
    public String toString() {
        String str = "";
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                str = str + matrix[k][l] + ", ";
            }
            str = str + "\n";
        }
        return str;
    }

    public BigIntMatrix getTransposedMatrix() {
        BigInteger[][] tMatrix = new BigInteger[colNumber][rowNumber];
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                tMatrix[l][k] = matrix[k][l];
            }
        }
        return new BigIntMatrix(tMatrix);
    }

    // add a matrix that is not encrypted
    public BigIntMatrix addDecryptedMatrix(BigIntMatrix m) {
        if (m.getRowNumber() != rowNumber || m.getColNumber() != colNumber) {
            return null;
        }
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        BigInteger[][] mArray = m.getMatrix();
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                result[k][l] = matrix[k][l].add(mArray[k][l]);
            }
        }
        return new BigIntMatrix(result);
    }

    // subtract a matrix that is not encrypted
    public BigIntMatrix subtractDecryptedMatrix(BigIntMatrix m) {
        if (m.getRowNumber() != rowNumber || m.getColNumber() != colNumber) {
            return null;
        }
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        BigInteger[][] mArray = m.getMatrix();
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                result[k][l] = matrix[k][l].subtract(mArray[k][l]);
            }
        }
        return new BigIntMatrix(result);
    }

    public BigIntMatrix mod(BigInteger n) {
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                result[k][l] = matrix[k][l].mod(n);
            }
        }
        return new BigIntMatrix(result);

    }

    public BigIntMatrix getIdentityMatrix(int n) {
        BigInteger[][] iMatrix = new BigInteger[n][n];
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < n; k++) {
                iMatrix[j][k] = BigInteger.ZERO;
            }
        }
        for (int j = 0; j < n; j++) {
            iMatrix[j][j] = BigInteger.ONE;
        }
        return new BigIntMatrix(iMatrix);
    }

    public BigIntMatrix multiply(BigIntMatrix bigIntMatrix) {
        if (getColNumber() != bigIntMatrix.getRowNumber()) {
            return null;
        }
        int rowNumber = getRowNumber();
        int colNumber = bigIntMatrix.getColNumber();
        BigInteger[][] matrix1 = getMatrix();
        BigInteger[][] matrix2 = bigIntMatrix.getMatrix();
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                BigInteger r = BigInteger.ZERO;
                for (int k = 0; k < getColNumber(); k++) {
                    r = r.add(matrix1[i][k].multiply(matrix2[k][j]));
                }
                result[i][j] = r;
            }
        }
        return new BigIntMatrix(result);
    }

    public BigIntMatrix multiply(BigInteger bigInt) {
        BigInteger[][] result = new BigInteger[rowNumber][colNumber];
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                result[k][l] = matrix[k][l].multiply(bigInt);
            }
        }
        return new BigIntMatrix(result);
    }

    public BigInteger getTrace() {
        if (getColNumber() != getRowNumber()) {
            return null;
        }
        BigInteger[][] m = getMatrix();
        BigInteger trace = m[0][0];
        int num = getColNumber();
        for (int i = 1; i < num; i++) {
            trace = trace.add(m[i][i]);
        }
        return trace;
    }

    public BigIntMatrix getDiagonalElements() {
        if (getColNumber() != getRowNumber()) {
            return null;
        }
        BigInteger[][] m = getMatrix();
        int num = getColNumber();
        BigInteger[][] diagonal = new BigInteger[num][1];
        for (int i = 0; i < num; i++) {
            diagonal[i][0] = m[i][i];
        }
        return new BigIntMatrix(diagonal);
    }
}
