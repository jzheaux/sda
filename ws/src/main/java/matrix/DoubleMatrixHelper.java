package matrix;

import java.util.Random;

public class DoubleMatrixHelper {

    // return a double matrix such that each element x of original matrix become e
    public static DoubleMatrix elementExp(DoubleMatrix doubleMatrix) {
        double[][] matrix = doubleMatrix.getMatrix();
        double[][] result = new double[doubleMatrix.getRowNumber()][doubleMatrix.getColNumber()];

        for (int i = 0; i < doubleMatrix.getRowNumber(); i++) {
            for (int j = 0; j < doubleMatrix.getColNumber(); j++) {
                result[i][j] = Math.exp(matrix[i][j]);
            }
        }
        return new DoubleMatrix(result);
    }

    public static DoubleMatrix invertSign(DoubleMatrix doubleMatrix) {
        double[][] matrix = doubleMatrix.getMatrix();
        double[][] result = new double[doubleMatrix.getRowNumber()][doubleMatrix.getColNumber()];

        for (int i = 0; i < doubleMatrix.getRowNumber(); i++) {
            for (int j = 0; j < doubleMatrix.getColNumber(); j++) {
                result[i][j] = 0 - matrix[i][j];
            }
        }
        return new DoubleMatrix(result);
    }

    public static DoubleMatrix elementInverse(DoubleMatrix doubleMatrix) {
        double[][] matrix = doubleMatrix.getMatrix();
        double[][] result = new double[doubleMatrix.getRowNumber()][doubleMatrix.getColNumber()];

        for (int i = 0; i < doubleMatrix.getRowNumber(); i++) {
            for (int j = 0; j < doubleMatrix.getColNumber(); j++) {
                result[i][j] = 1 / matrix[i][j];
            }
        }
        return new DoubleMatrix(result);
    }

    public static DoubleMatrix getAllOneElementMatrix(int nrow, int ncol) {
        double[][] result = new double[nrow][ncol];
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                result[i][j] = 1;
            }
        }
        return new DoubleMatrix(result);
    }
    
    public static DoubleMatrix getRandomElementMatrix(int nrow, int ncol) {
        Random r = new Random();
        double[][] result = new double[nrow][ncol];
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                result[i][j] = r.nextDouble();
            }
        }
        return new DoubleMatrix(result);
    }
}
