package utils;

import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;

public class TestFloatingPointNumber {

    private static final double A = -123456789012.0123456789;
    private static final double B = 34568903212.0123456789;
    private static final double C = 2;
    private static final double D = 9;
    private static final double ACCURACY = 0.000000000000001;
    public static BigInteger N = new BigInteger(
            "211111111111111111111111111111111111111111111111111111111");
    public static BigInteger M = new BigInteger(
            "111111111111111111111111111111111111111111111");

    @Test
    public void testNegative() {
        BigInteger a = FloatingPointNumber.doubleToBigInteger(A, M, N);
        System.out.println(a);
        double d = FloatingPointNumber.bigIntegerToDouble(a, M, N);
        System.out.println(d);
        System.out.println((d - A) / A);
        Assert.assertTrue((d - A) / A < ACCURACY);
        Assert.assertTrue((d - A) / A > -ACCURACY);
    }

    @Test
    public void testPositive() {
        BigInteger b = FloatingPointNumber.doubleToBigInteger(B, M, N);
        System.out.println(b);
        double d = FloatingPointNumber.bigIntegerToDouble(b, M, N);
        System.out.println(d);
        System.out.println((d - B) / B);
        Assert.assertTrue((d - B) / B < ACCURACY);
        Assert.assertTrue((d - B) / B > -ACCURACY);
    }

    @Test
    public void testSum() {
        BigInteger a = FloatingPointNumber.doubleToBigInteger(A, M, N);
        BigInteger b = FloatingPointNumber.doubleToBigInteger(B, M, N);
        BigInteger sum = a.add(b);
        double s = FloatingPointNumber.bigIntegerToDouble(sum, M, N);
        System.out.println("sum:");
        System.out.println(s);
        System.out.println(A + B);
        Assert.assertTrue(s == (A + B));
    }

    @Test
    public void testProduct() {
        BigInteger c = FloatingPointNumber.doubleToBigInteger(C, M, N);
        BigInteger d = FloatingPointNumber.doubleToBigInteger(D, M, N);
        BigInteger product = c.multiply(d).multiply(d).divide(M).divide(M);
        double p = FloatingPointNumber.bigIntegerToDouble(product, M, N);
        System.out.println("product:");
        System.out.println(p);
        Assert.assertTrue(p == (C * D * D));
    }
}
