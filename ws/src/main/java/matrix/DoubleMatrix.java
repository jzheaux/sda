package matrix;

import java.math.BigInteger;
import java.util.Random;
import javax.xml.bind.annotation.XmlRootElement;

import utils.FloatingPointNumber;

@XmlRootElement
public class DoubleMatrix extends AbstractMatrix {

    private double[][] matrix;

    public DoubleMatrix(double[][] matrix) {
        setMatrix(matrix);
    }

    public DoubleMatrix() {
    }

    public DoubleMatrix(String matrix) {
        setMatrix(ParseMatrixString(matrix));
    }

    public DoubleMatrix getNewCopy() {
        return new DoubleMatrix(matrix);
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
        rowNumber = matrix.length;
        colNumber = matrix[0].length;
    }

    public BigIntMatrix toBigIntMatrix(BigInteger m, BigInteger n) {
        return new BigIntMatrix(toBigInteger(m, n));
    }

    public BigInteger[][] toBigInteger(BigInteger m, BigInteger n) {
        BigInteger[][] a = new BigInteger[rowNumber][colNumber];
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                a[k][l] = FloatingPointNumber.doubleToBigInteger(matrix[k][l],
                        m, n);
            }
        }
        return a;
    }

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

    public DoubleMatrix getTransposedMatrix() {
        double[][] tMatrix = new double[colNumber][rowNumber];
        for (int k = 0; k < rowNumber; k++) {
            for (int l = 0; l < colNumber; l++) {
                tMatrix[l][k] = matrix[k][l];
            }
        }
        return new DoubleMatrix(tMatrix);
    }

    public DoubleMatrix multiply(DoubleMatrix doubleMatrix) {
        if (getColNumber() != doubleMatrix.getRowNumber()) {
            return null;
        }
        int rowNumber = getRowNumber();
        int colNumber = doubleMatrix.getColNumber();
        double[][] matrix1 = getMatrix();
        double[][] matrix2 = doubleMatrix.getMatrix();
        double[][] result = new double[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                double r = 0;
                for (int k = 0; k < getColNumber(); k++) {
                    r += matrix1[i][k] * matrix2[k][j];
                }
                result[i][j] = r;
            }
        }
        return new DoubleMatrix(result);
    }

    public DoubleMatrix multiply(double num) {
        double[][] matrix1 = getMatrix();
        int rowNumber = getRowNumber();
        int colNumber = getColNumber();
        double[][] result = new double[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = matrix1[i][j] * num;
            }
        }
        return new DoubleMatrix(result);
    }

    public static DoubleMatrix getIdentityMatrix(int nrow) {
        double[][] result = new double[nrow][nrow];
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < nrow; j++) {
                result[i][j] = 0;
            }
        }
        for (int i = 0; i < nrow; i++) {
            result[i][i] = 1;
        }
        return new DoubleMatrix(result);
    }

    public DoubleMatrix add(DoubleMatrix doubleMatrix) {
        if (getColNumber() != doubleMatrix.getColNumber()
                || getRowNumber() != doubleMatrix.getRowNumber()) {
            return null;
        }
        int rowNumber = getRowNumber();
        int colNumber = getColNumber();
        double[][] matrix1 = getMatrix();
        double[][] matrix2 = doubleMatrix.getMatrix();
        double[][] result = new double[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return new DoubleMatrix(result);
    }

    public DoubleMatrix subtract(DoubleMatrix doubleMatrix) {
        if (getColNumber() != doubleMatrix.getColNumber()
                || getRowNumber() != doubleMatrix.getRowNumber()) {
            return null;
        }
        int rowNumber = getRowNumber();
        int colNumber = getColNumber();
        double[][] matrix1 = getMatrix();
        double[][] matrix2 = doubleMatrix.getMatrix();
        double[][] result = new double[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        return new DoubleMatrix(result);
    }

    public boolean equalWithinError(DoubleMatrix doubleMatrix, double error) {
        DoubleMatrix difference = this.subtract(doubleMatrix);
        int rowNumber = getRowNumber();
        int colNumber = getColNumber();
        double[][] matrix = difference.getMatrix();
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                if (matrix[i][j] < 0) {
                    matrix[i][j] = -matrix[i][j];
                }
                if (matrix[i][j] > error) {
                    return false;
                }
            }
        }
        return true;
    }

    public double getTrace() {
        if (getColNumber() != getRowNumber()) {
            System.exit(0);
        }
        double[][] m = getMatrix();
        double trace = 0;
        int num = getColNumber();
        for (int i = 0; i < num; i++) {
            trace += m[i][i];
        }
        return trace;
    }

    public DoubleMatrix squreRoot() {
        int rowNumber = getRowNumber();
        int colNumber = getColNumber();
        double[][] matrix = getMatrix();
        double[][] result = new double[rowNumber][colNumber];
        for (int i = 0; i < rowNumber; i++) {
            for (int j = 0; j < colNumber; j++) {
                result[i][j] = Math.sqrt(matrix[i][j]);
            }
        }
        return new DoubleMatrix(result);
    }

    public double[][] ParseMatrixString(String matrix) {
        String[] row = matrix.split("\n");
        String[] col = row[0].split(",");
        double[][] result = new double[row.length][col.length];
        for (int i = 0; i < row.length; i++) {
            String[] str = row[i].split(",");
            for (int j = 0; j < col.length; j++) {
                result[i][j] = Double.valueOf(str[j]);
            }
        }
        return result;
    }

    public static DoubleMatrix generateRandomMatrix(int nrow, int ncol) {
        if (nrow < 0 || ncol < 0) {
            return null;
        }
        double[][] result = new double[nrow][ncol];
        Random r = new Random();
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                while (result[i][j] < 0.001) {
                    result[i][j] = r.nextInt(30) * r.nextDouble();
                }
                if (r.nextBoolean()) {
                    result[i][j] = 0 - result[i][j];
                }
            }
        }
        return new DoubleMatrix(result);
    }
}
