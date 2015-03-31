package regression;

import matrix.DoubleMatrix;
import org.junit.Test;
import utils.Reader;

public class TestLogitRegression {

    public final String ADDRESS1 = ".\\test\\testFiles\\logitRegression\\testMatrixD";
    public final String ADDRESS2 = ".\\test\\testFiles\\logitRegression\\testMatrixR";
    public final String COMMA = ",";
    public final double SMALL_NUMBER = 0.00000001;

    @Test
    public void testTwoPartyRegression() {
        Reader reader = new Reader();
        DoubleMatrix x = new DoubleMatrix(reader.readMatrix(
                ADDRESS1, COMMA));
        DoubleMatrix y = new DoubleMatrix(reader.readMatrix(
                ADDRESS2, COMMA));

        DoubleMatrix beta = new DoubleMatrix(new double[x.getColNumber()][1]);
        DoubleMatrix pi;
        DoubleMatrix w = new DoubleMatrix(new double[x.getRowNumber()][x.getRowNumber()]);


        for (int k = 0; k < 4; k++) {
            pi = x.multiply(beta);
            double[][] tmp1 = pi.getMatrix();
            for (int i = 0; i < pi.getRowNumber(); i++) {
                tmp1[i][0] = Math.exp(tmp1[i][0]) / (1 + Math.exp(tmp1[i][0]));
            }
            pi = new DoubleMatrix(tmp1);

            double[][] tmp2 = w.getMatrix();
            for (int i = 0; i < w.getColNumber(); i++) {
                tmp2[i][i] = tmp1[i][0] * (1 - tmp1[i][0]);
            }
            w = new DoubleMatrix(tmp2);


            DoubleMatrix xtwx = x.getTransposedMatrix().multiply(w).multiply(x);
            DoubleMatrix mu = y.add(pi.multiply(-1));
            DoubleMatrix part2 = x.getTransposedMatrix().multiply(mu);

            double ic = 1 / xtwx.getTrace();
            DoubleMatrix m = xtwx.multiply(ic);
            DoubleMatrix x2 = DoubleMatrix.getIdentityMatrix(xtwx.getRowNumber());
            x2 = x2.multiply(ic);

            for (int i = 0; i < 30; i++) {
                x2 = fx(x2, m);
                m = fm(m);
            }

            beta = beta.add(x2.multiply(part2));
        }
        System.out.println(beta);
    }

    public DoubleMatrix fx(DoubleMatrix x, DoubleMatrix m) {
        DoubleMatrix result = x.multiply(m);
        result = x.multiply(2).add(result.multiply(-1));
        return result;
    }

    public DoubleMatrix fm(DoubleMatrix m) {
        DoubleMatrix result = m.multiply(m);
        result = m.multiply(2).add(result.multiply(-1));
        return result;
    }
}
