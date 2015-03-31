package regression;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import java.math.BigInteger;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import org.junit.Test;
import protocol.PartyWithPrivateKey;
import protocol.PartyWithoutPrivateKey;
import protocol.ProtocolParameters;
import utils.FloatingPointNumber;
import utils.Reader;

public class TestTwoPartyRegression {

    public final String ADDRESS1 = ".\\test\\testFiles\\testMatrix1";
    public final String ADDRESS2 = ".\\test\\testFiles\\testMatrix2";
    public final String ADDRESS3 = ".\\test\\testFiles\\testMatrixY1";
    public final String ADDRESS4 = ".\\test\\testFiles\\testMatrixY2";
    public final String COMMA = ",";
    public final double SMALL_NUMBER = 0.00000001;

    @Test
    public void testTwoPartyRegression() {
        Reader reader = new Reader();
        DoubleMatrix doubleMatrix1 = new DoubleMatrix(reader.readMatrix(
                ADDRESS1, COMMA));
        DoubleMatrix doubleMatrix2 = new DoubleMatrix(reader.readMatrix(
                ADDRESS2, COMMA));

        DoubleMatrix doubleMatrix1Tran = doubleMatrix1.getTransposedMatrix();
        DoubleMatrix doubleMatrix2Tran = doubleMatrix2.getTransposedMatrix();
        // ******get x^t*x locally
        DoubleMatrix dmProduct1 = doubleMatrix1Tran.multiply(doubleMatrix1);
        DoubleMatrix dmProduct2 = doubleMatrix2Tran.multiply(doubleMatrix2);

        ProtocolParameters protocol = new ProtocolParameters();
        BigInteger protocolM = protocol.getM();
        BigInteger protocolP = protocol.getP();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocolM,
                protocolP);
        // party two only has the public key
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne
                .getKey().getPublicKey(), protocolM, protocolP);

        PartyWithPrivateKey partyOne1 = new PartyWithPrivateKey(protocolM,
                protocolP);
        partyOne1.setKey(partyOne.getKey());
        partyOne1.setPrivateKey(partyOne.getPrivateKey());
        // party two only has the public key
        PartyWithoutPrivateKey partyTwo1 = new PartyWithoutPrivateKey(partyOne1
                .getKey().getPublicKey(), protocolM, protocolP);

        BigInteger N = partyTwo.getPublicKey().getN();
        BigInteger G = partyTwo.getPublicKey().getG();

        BigIntMatrix bigMatrix1Tran = doubleMatrix1Tran.toBigIntMatrix(protocolM, N);
        BigIntMatrix bigMatrix2Tran = doubleMatrix2Tran.toBigIntMatrix(protocolM, N);

        // party one encrypts the matrix and send enMatrix1 to party two
        BigIntMatrix bigMatrix1 = doubleMatrix1.toBigIntMatrix(protocolM, N);

        // party two get enMatrix1 and calculates the products
        BigIntMatrix bigMatrix2 = doubleMatrix2.toBigIntMatrix(protocolM, N);

        XYShareCalculator xyC = new XYShareCalculator(N, G, protocolM, bigMatrix1, bigMatrix2, partyOne1, partyTwo1);
        Thread xycThread = new Thread(xyC);
        xycThread.start();
        BigIntMatrix enMatrix1 = Paillier.encrypt(bigMatrix1, N, G);
        BigIntMatrix product1 = PaillierProperties.doMultiplyEnMatrixDeMatrix(
                enMatrix1.getTransposedMatrix(), bigMatrix2, N);
        BigIntMatrix product2 = PaillierProperties.doMultiplyDeMatrixEnMatrix(
                bigMatrix2.getTransposedMatrix(), enMatrix1, N);

        // get the shares of product
        // ******party one get share1in1 and share1in2
        // ******party two get share2in1 and share2in2
        BigIntMatrix EP1 = partyOne.outputEP1(partyTwo
                .encryptBeforeSentToPartyOne(product1));
        BigIntMatrix share1in1 = partyOne.outputShare(partyTwo.outputES(EP1,
                product1));
        BigIntMatrix share2in1 = partyTwo.outputMatrixShare();

        BigIntMatrix EP2 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(product2));
        BigIntMatrix share1in2 = partyOne.outputShare(partyTwo.outputES(EP2,
                product2));
        BigIntMatrix share2in2 = partyTwo.outputMatrixShare();

        // party one add all of his shares and get part1
        BigIntMatrix part1 = dmProduct1.toBigIntMatrix(protocolM,
                N);
        part1 = part1.addDecryptedMatrix(share1in1)
                .addDecryptedMatrix(share1in2)
                .mod(N);

        // party two get part2, (product matrix = part1 + part2)
        BigIntMatrix part2 = dmProduct2.toBigIntMatrix(protocolM,
                N);
        part2 = part2.addDecryptedMatrix(share2in1)
                .addDecryptedMatrix(share2in2)
                .mod(N);

        System.out.println("X^T*X\n" + part1.addDecryptedMatrix(part2).toDoubleMatrix(
                protocolM, N));

        // party one and party two get the trace of their shares, party one send
        // the encrypted trace1 to party two
        BigInteger trace1 = part1.getTrace()
                .mod(N);
        BigInteger trace2 = part2.getTrace()
                .mod(N);
        BigInteger smallNum = FloatingPointNumber.doubleToBigInteger(
                SMALL_NUMBER, protocolM, N);
        System.out.println("trace sum 1:   "
                + FloatingPointNumber.bigIntegerToDouble(trace1.add(trace2)
                .mod(N), protocolM,
                N));

        BigInteger enTrace1 = Paillier.encrypt(trace1, N, G);
        BigInteger enTrace2 = Paillier.encrypt(trace2, N, G);
        BigInteger enSum = PaillierProperties.doAdd(enTrace1, enTrace2, N);
        BigInteger enP = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(enSum, smallNum, N);
        BigInteger enPEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(enP));
        trace1 = partyOne.outputShare(partyTwo.outputES(enPEP1, enP));
        trace2 = partyTwo.outputShare();
        System.out.println("trace sum 2:   "
                + FloatingPointNumber.bigIntegerToDouble(trace1.add(trace2).mod(N), protocolM, N));

        BigInteger x1 = FloatingPointNumber.doubleToBigInteger(0.5, protocolM, N);
        BigInteger x2 = FloatingPointNumber.doubleToBigInteger(0.5, protocolM, N);
        x1 = x1.multiply(smallNum).divide(protocolM);
        x2 = x2.multiply(smallNum).divide(protocolM);

        //get the shares of the inverse of the trace
        for (int i = 0; i < 20; i++) {
            BigInteger encryptedTrace1 = Paillier.encrypt(trace1, N, G);
            BigInteger traceProduct = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(encryptedTrace1, trace2, N);
            BigInteger trace1sq = trace1.multiply(trace1).mod(N);
            BigInteger trace2sq = trace2.multiply(trace2).mod(N);
            BigInteger encryptedTrace1sq = Paillier.encrypt(trace1sq, N, G);
            BigInteger encryptedTrace2sq = Paillier.encrypt(trace2sq, N, G);

            BigInteger enProduct = PaillierProperties.doAdd(encryptedTrace1sq, encryptedTrace2sq, N);
            enProduct = PaillierProperties.doAdd(enProduct, traceProduct, N);
            enProduct = PaillierProperties.doAdd(enProduct, traceProduct, N);
            BigInteger tEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(enProduct));
            BigInteger tProductShare1 = partyOne.outputShare(partyTwo.outputES(tEP1, enProduct));
            BigInteger tProductShare2 = partyTwo.outputShare();

            BigInteger encryptedX1 = Paillier.encrypt(x1, N, G);
            BigInteger xt1Product = PaillierProperties
                    .doMultiplyEncryptedNumberDecryptedNumber(encryptedX1, trace2, N);
            BigInteger xt2Product = PaillierProperties
                    .doMultiplyEncryptedNumberDecryptedNumber(encryptedTrace1, x2, N);
            BigInteger p1 = Paillier.encrypt(x1.multiply(trace1), N, G);
            BigInteger p2 = Paillier.encrypt(x2.multiply(trace2), N, G);
            BigInteger enXProduct = PaillierProperties.doAdd(xt1Product, xt2Product, N);
            enXProduct = PaillierProperties.doAdd(enXProduct, p1, N);
            enXProduct = PaillierProperties.doAdd(enXProduct, p2, N);
            BigInteger xEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(enXProduct));
            BigInteger xProductShare1 = partyOne.outputShare(partyTwo.outputES(xEP1, enXProduct));
            BigInteger xProductShare2 = partyTwo.outputShare();

            x1 = x1.add(x1).subtract(xProductShare1).mod(N);
            x2 = x2.add(x2).subtract(xProductShare2).mod(N);

            trace1 = trace1.add(trace1).subtract(tProductShare1).mod(N);
            trace2 = trace2.add(trace2).subtract(tProductShare2).mod(N);
        }

        System.out.println(FloatingPointNumber.bigIntegerToDouble(
                trace1.add(trace2).mod(N),
                protocolM, N));
        System.out.println(FloatingPointNumber.bigIntegerToDouble(x1.add(x2)
                .mod(N), protocolM, partyTwo
                .getPublicKey().getN()));

        //Initialize shares of matrix M0 and shares of matrix X0
        BigIntMatrix enPart1 = Paillier.encrypt(part1, N, G);
        BigInteger enX1 = Paillier.encrypt(x1, N, G);

        BigIntMatrix x1p1Product = part1.multiply(x1);
        x1p1Product = Paillier.encrypt(x1p1Product, N, G);

        BigIntMatrix x2p2Product = part2.multiply(x2);
        x2p2Product = Paillier.encrypt(x2p2Product, N, G);
        BigIntMatrix x2p1Product = PaillierProperties.doMultiplyEnMatrixDeNumber(enPart1, x2, N);
        BigIntMatrix x1p2Product = PaillierProperties.doMultiplyDeMatrixEnNumber(part2, enX1, N);

        BigIntMatrix sumProduct = PaillierProperties.doAdd(x1p1Product, x2p2Product, N);
        sumProduct = PaillierProperties.doAdd(sumProduct, x2p1Product, N);
        sumProduct = PaillierProperties.doAdd(sumProduct, x1p2Product, N);

        BigIntMatrix spEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(sumProduct));
        BigIntMatrix matrixMShare1 = partyOne.outputShare(partyTwo.outputES(spEP1, sumProduct));
        BigIntMatrix matrixMShare2 = partyTwo.outputMatrixShare();
        System.out.println("MATRIX M0: \n" + new DoubleMatrix(matrixMShare1.addDecryptedMatrix(matrixMShare2).mod(N).toDouble(protocolM,
                N)));

        BigIntMatrix matrixXShare1 = matrixMShare1.getIdentityMatrix(matrixMShare1.getColNumber()).multiply(x1);
        BigIntMatrix matrixXShare2 = matrixMShare2.getIdentityMatrix(matrixMShare2.getColNumber()).multiply(x2);
        System.out.println("MATRIX X0: \n" + new DoubleMatrix(matrixXShare1.addDecryptedMatrix(matrixXShare2).mod(N).toDouble(protocolM,
                N)));

        for (int i = 0; i < 30; i++) {
            BigIntMatrix[] mspShares = getProductShares(matrixMShare1, matrixMShare1, matrixMShare2, matrixMShare2, N, G, partyOne, partyTwo);
            BigIntMatrix mspShare1 = mspShares[0];
            BigIntMatrix mspShare2 = mspShares[1];

            BigIntMatrix[] xspShares = getProductShares(matrixXShare1, matrixMShare1, matrixXShare2, matrixMShare2, N, G, partyOne, partyTwo);
            BigIntMatrix xspShare1 = xspShares[0];
            BigIntMatrix xspShare2 = xspShares[1];

            matrixXShare1 = matrixXShare1.addDecryptedMatrix(matrixXShare1).subtractDecryptedMatrix(xspShare1);
            matrixXShare2 = matrixXShare2.addDecryptedMatrix(matrixXShare2).subtractDecryptedMatrix(xspShare2);

            matrixMShare1 = matrixMShare1.addDecryptedMatrix(matrixMShare1).subtractDecryptedMatrix(mspShare1);
            matrixMShare2 = matrixMShare2.addDecryptedMatrix(matrixMShare2).subtractDecryptedMatrix(mspShare2);
        }

        System.out.println("MATRIX M: \n" + new DoubleMatrix(matrixMShare1.addDecryptedMatrix(matrixMShare2).mod(N).toDouble(protocolM,
                N)));

        //matrixXShare1.addDecryptedMatrix(matrixXShare2) is the inversion of X^T*X
        System.out.println("INVERSTION OF MATRIX X^T*X: \n" + new DoubleMatrix(matrixXShare1.addDecryptedMatrix(matrixXShare2).mod(N).toDouble(protocolM,
                N)));

        try {
            xycThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BigIntMatrix xyProductShare1 = xyC.getXyProductShare1();
        BigIntMatrix xyProductShare2 = xyC.getXyProductShare2();

        //==============================================================================
        //CALCULATE FINAL RESULT
        BigIntMatrix[] productShares = getProductShares(matrixXShare1, xyProductShare1, matrixXShare2, xyProductShare2, N, G, partyOne, partyTwo);
        BigIntMatrix fProductShare1 = productShares[0];
        BigIntMatrix fProductShare2 = productShares[1];

        System.out.println("REGRESSION RESULT: \n" + new DoubleMatrix(fProductShare1.addDecryptedMatrix(fProductShare2).mod(N).toDouble(protocolM,
                N)));
        //================================================================================
        //CALCULATE COVARIANCE

        BigIntMatrix[] xyxShares = getProductShares(matrixXShare1, bigMatrix1Tran, matrixXShare2, bigMatrix2Tran, N, G, partyOne, partyTwo);
        BigIntMatrix xyxShare1 = xyxShares[0];
        BigIntMatrix xyxShare2 = xyxShares[1];

        BigIntMatrix[] hShares = getProductShares(bigMatrix1, xyxShare1, bigMatrix2, xyxShare2, N, G, partyOne, partyTwo);
        BigIntMatrix hShare1 = hShares[0];
        BigIntMatrix hShare2 = hShares[1];

        BigIntMatrix[] hyShares = getProductShares(hShare1, xyC.getY1(), hShare2, xyC.getY2(), N, G, partyOne, partyTwo);
        BigIntMatrix hyShare1 = hyShares[0];
        BigIntMatrix hyShare2 = hyShares[1];

        BigIntMatrix eShare1 = hShare1.getIdentityMatrix(hShare1.getColNumber()).multiply(xyC.getY1()).subtractDecryptedMatrix(hyShare1);
        BigIntMatrix eShare2 = hShare2.getIdentityMatrix(hShare2.getColNumber()).multiply(xyC.getY2()).subtractDecryptedMatrix(hyShare2);
        System.out.println("E: \n" + new DoubleMatrix(eShare1.addDecryptedMatrix(eShare2).mod(N).toDouble(protocolM, N)));


        BigIntMatrix[] eteShares = getProductShares(eShare1.getTransposedMatrix(), eShare1, eShare2.getTransposedMatrix(), eShare2, N, G, partyOne, partyTwo);
        BigIntMatrix eteShare1 = eteShares[0];
        BigIntMatrix eteShare2 = eteShares[1];

        double noiseVariance = new DoubleMatrix(eteShare1.addDecryptedMatrix(eteShare2).mod(N).toDouble(protocolM, N)).getMatrix()[0][0] * 1 / (doubleMatrix1.getRowNumber() - doubleMatrix1.getColNumber() - 1);
        System.out.println("Noise variance: \n" + noiseVariance + "\n");

        System.out.println("Standard error: \n" + new DoubleMatrix(matrixXShare1.getDiagonalElements().addDecryptedMatrix(matrixXShare2.getDiagonalElements()).mod(N).toDouble(protocolM, N)).multiply(noiseVariance).squreRoot());

    }

    // return shares of X*Y, XShare1 + XShare2 = X, YShare1 + YShare2 = Y
    public BigIntMatrix[] getProductShares(BigIntMatrix XShare1, BigIntMatrix YShare1, BigIntMatrix XShare2, BigIntMatrix YShare2, BigInteger N, BigInteger G, PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo) {
        BigIntMatrix f1 = XShare1.multiply(YShare1);
        f1 = Paillier.encrypt(f1, N, G);
        BigIntMatrix f2 = XShare2.multiply(YShare2);
        f2 = Paillier.encrypt(f2, N, G);
        BigIntMatrix enXShare1 = Paillier.encrypt(XShare1, N, G);
        BigIntMatrix enYShare1 = Paillier.encrypt(YShare1, N, G);
        
        BigIntMatrix f3 = PaillierProperties.doMultiplyEnMatrixDeMatrix(enXShare1, YShare2, N);
        BigIntMatrix f4 = PaillierProperties.doMultiplyDeMatrixEnMatrix(XShare2, enYShare1, N);
        BigIntMatrix fProduct = PaillierProperties.doAdd(f1, f2, N);
        fProduct = PaillierProperties.doAdd(fProduct, f3, N);
        fProduct = PaillierProperties.doAdd(fProduct, f4, N);
        BigIntMatrix fEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(fProduct));
        BigIntMatrix[] productShare = new BigIntMatrix[2];
        productShare[0] = partyOne.outputShare(partyTwo.outputES(fEP1, fProduct));
        productShare[1] = partyTwo.outputMatrixShare();
        return productShare;

    }

    private class XYShareCalculator implements Runnable {

        private BigInteger N;
        private BigInteger G;
        private BigInteger protocolM;
        private BigIntMatrix bigMatrix1;
        private BigIntMatrix bigMatrix2;
        private BigIntMatrix xyPShare1;
        private BigIntMatrix xyPShare2;
        private PartyWithPrivateKey partyOne;
        private PartyWithoutPrivateKey partyTwo;
        private BigIntMatrix Y1;
        private BigIntMatrix Y2;

        public XYShareCalculator(BigInteger N, BigInteger G, BigInteger protocolM, BigIntMatrix bigMatrix1, BigIntMatrix bigMatrix2, PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo) {
            this.N = N;
            this.G = G;
            this.protocolM = protocolM;
            this.bigMatrix1 = bigMatrix1;
            this.bigMatrix2 = bigMatrix2;
            this.partyOne = partyOne;
            this.partyTwo = partyTwo;
        }

        @Override
        public void run() {
            //================================================================
            // CALCULATE THE SHARES OF X^T*y
            Reader reader = new Reader();
            BigIntMatrix xt1 = bigMatrix1.getTransposedMatrix();
            BigIntMatrix xt2 = bigMatrix2.getTransposedMatrix();
            DoubleMatrix doubleY1 = new DoubleMatrix(reader.readMatrix(
                    ADDRESS3, COMMA));
            DoubleMatrix doubleY2 = new DoubleMatrix(reader.readMatrix(
                    ADDRESS4, COMMA));
            Y1 = doubleY1.toBigIntMatrix(protocolM, N);
            BigIntMatrix xyShare1 = xt1.multiply(Y1);
            xyShare1 = Paillier.encrypt(xyShare1, N, G);
            Y2 = doubleY2.toBigIntMatrix(protocolM, N);
            BigIntMatrix xyShare2 = xt2.multiply(Y2);
            xyShare2 = Paillier.encrypt(xyShare2, N, G);

            BigIntMatrix enXt1 = Paillier.encrypt(xt1, N, G);
            BigIntMatrix bigY1 = doubleY1.toBigIntMatrix(protocolM, N);
            BigIntMatrix enY1 = Paillier.encrypt(bigY1, N, G);
            BigIntMatrix bigY2 = doubleY2.toBigIntMatrix(protocolM, N);
            BigIntMatrix xyShare3 = PaillierProperties.doMultiplyEnMatrixDeMatrix(enXt1, bigY2, N);
            BigIntMatrix xyShare4 = PaillierProperties.doMultiplyDeMatrixEnMatrix(xt2, enY1, N);
            BigIntMatrix xyProduct = PaillierProperties.doAdd(xyShare1, xyShare2, N);
            xyProduct = PaillierProperties.doAdd(xyProduct, xyShare3, N);
            xyProduct = PaillierProperties.doAdd(xyProduct, xyShare4, N);
            BigIntMatrix xyEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(xyProduct));
            this.xyPShare1 = partyOne.outputShare(partyTwo.outputES(xyEP1, xyProduct));
            this.xyPShare2 = partyTwo.outputMatrixShare();

            System.out.println("X^T*y: \n" + new DoubleMatrix(xyPShare1.addDecryptedMatrix(xyPShare2).mod(N).toDouble(protocolM,
                    N)));

        }

        public BigIntMatrix getXyProductShare1() {
            return xyPShare1;
        }

        public BigIntMatrix getXyProductShare2() {
            return xyPShare2;
        }

        public BigIntMatrix getY1() {
            return Y1;
        }

        public void setY1(BigIntMatrix y1) {
            Y1 = y1;
        }

        public BigIntMatrix getY2() {
            return Y2;
        }

        public void setY2(BigIntMatrix y2) {
            Y2 = y2;
        }
    }
}