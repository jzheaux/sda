package utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

// f: Z -> R, M is a big integer
// f(a)= M^-1 * a, if a <= n/2; f(a) = M^-1 * (a - n), if a> n/2
public class FloatingPointNumber {

    public static double bigIntegerToDouble(BigInteger a, BigInteger m,
            BigInteger n) {
        return bigIntegerToBigDecimal(a, m, n).doubleValue();
    }

    public static BigDecimal bigIntegerToBigDecimal(BigInteger a, BigInteger m,
            BigInteger n) {
        BigDecimal dM = new BigDecimal(m, MathContext.UNLIMITED);
        BigDecimal dA = new BigDecimal(a, MathContext.UNLIMITED);
        BigDecimal dN = new BigDecimal(n, MathContext.UNLIMITED);
        BigDecimal two = new BigDecimal(2, MathContext.UNLIMITED);

        if (dA.compareTo(dN.divide(two)) == 1) {
            return BigDecimal.ONE.divide(dM, 100, RoundingMode.CEILING)
                    .multiply(dA.subtract(dN), MathContext.UNLIMITED);
        } else {
            return BigDecimal.ONE.divide(dM, 100, RoundingMode.CEILING)
                    .multiply(dA, MathContext.UNLIMITED);
        }
    }

    public static BigInteger doubleToBigInteger(double a, BigInteger m,
            BigInteger n) {
        BigDecimal dA = new BigDecimal(a, MathContext.UNLIMITED);
        if (a > 0) {
            return dA.multiply(new BigDecimal(m, MathContext.UNLIMITED))
                    .toBigInteger();
        } else {
            return dA.multiply(new BigDecimal(m, MathContext.UNLIMITED))
                    .add(new BigDecimal(n)).toBigInteger();
        }
    }

    public static BigInteger bigDecimalToBigInteger(BigDecimal dA, BigInteger m,
            BigInteger n) {
        if (dA.compareTo(BigDecimal.ZERO) == 1) {
            return dA.multiply(new BigDecimal(m, MathContext.UNLIMITED))
                    .toBigInteger();
        } else {
            return dA.multiply(new BigDecimal(m, MathContext.UNLIMITED))
                    .add(new BigDecimal(n)).toBigInteger();
        }
    }
}
