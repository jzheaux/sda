package protocol;

import matrix.DoubleMatrix;
import org.junit.Test;
import utils.Reader;

public class TestDoubleMatrixInversion {

    public final String ADDRESS = ".\\test\\testFiles\\testMatrix3";
    public final String COMMA = ",";
    public static final double x = 12345325325.23453254;
    public static final double y = 24532520.23432453221;

    @Test
    public void testMatrix() {
        Reader reader = new Reader();
        DoubleMatrix doubleMatrix = new DoubleMatrix(reader.readMatrix(ADDRESS,
                COMMA));
        System.out.println("read double matrix:");
        System.out.println(doubleMatrix);

        System.out.println("multiply double matrix:");
        DoubleMatrix a = doubleMatrix.getTransposedMatrix().multiply(
                doubleMatrix);
        System.out.println(a);

        double ic = 1 / a.getTrace();
        DoubleMatrix m = a.multiply(ic);
        System.out.println(m);
        DoubleMatrix x = DoubleMatrix.getIdentityMatrix(a.getRowNumber());
        x = x.multiply(ic);
        System.out.println(x);

        for (int i = 0; i < 30; i++) {
            x = fx(x, m);
            m = fm(m);
        }

        System.out.println("==================================");
        System.out.println(m);
        System.out.println(x);
        System.out.println(x.multiply(a));
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
