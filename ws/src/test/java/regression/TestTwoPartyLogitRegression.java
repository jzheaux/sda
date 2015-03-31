package regression;

import cryptosystem.Paillier;
import cryptosystem.PaillierProperties;
import java.math.BigInteger;
import matrix.BigIntMatrix;
import matrix.DoubleMatrix;
import matrix.DoubleMatrixHelper;
import org.junit.Test;
import protocol.PartyWithPrivateKey;
import protocol.PartyWithoutPrivateKey;
import protocol.ProtocolParameters;
import utils.FloatingPointNumber;
import utils.Reader;

public class TestTwoPartyLogitRegression {

    public final String ADDRESS1 = ".\\test\\testFiles\\logitRegression\\testMatrixD1";
    public final String ADDRESS2 = ".\\test\\testFiles\\logitRegression\\testMatrixR1";
    public final String ADDRESS3 = ".\\test\\testFiles\\logitRegression\\testMatrixD2";
    public final String ADDRESS4 = ".\\test\\testFiles\\logitRegression\\testMatrixR2";
    public final String COMMA = ",";
    public final double SMALL_NUMBER = 0.00000001;

    public TestTwoPartyLogitRegression() {
    }

    @Test
    public void testLogitRegression() {
        Reader reader = new Reader();
        DoubleMatrix doubleX1 = new DoubleMatrix(reader.readMatrix(ADDRESS1, COMMA));
        DoubleMatrix doubleY1 = new DoubleMatrix(reader.readMatrix(ADDRESS2, COMMA));
        DoubleMatrix doubleX2 = new DoubleMatrix(reader.readMatrix(ADDRESS3, COMMA));
        DoubleMatrix doubleY2 = new DoubleMatrix(reader.readMatrix(ADDRESS4, COMMA));
        DoubleMatrix doubleBeta1 = new DoubleMatrix(new double[doubleX1.getColNumber()][1]);
        DoubleMatrix doubleBeta2 = new DoubleMatrix(new double[doubleX2.getColNumber()][1]);
        DoubleMatrix doubleW1 = new DoubleMatrix(new double[doubleX1.getRowNumber()][doubleX1.getRowNumber()]);
        DoubleMatrix doubleW2 = new DoubleMatrix(new double[doubleX2.getRowNumber()][doubleX2.getRowNumber()]);

        ProtocolParameters protocol = new ProtocolParameters();
        BigInteger protocolM = protocol.getM();
        BigInteger protocolP = protocol.getP();
        PartyWithPrivateKey partyOne = new PartyWithPrivateKey(protocolM, protocolP);
        // party two only has the public key
        PartyWithoutPrivateKey partyTwo = new PartyWithoutPrivateKey(partyOne.getKey().getPublicKey(), protocolM, protocolP);
        BigInteger N = partyTwo.getPublicKey().getN();
        BigInteger G = partyTwo.getPublicKey().getG();

        BigIntMatrix x1 = doubleX1.toBigIntMatrix(protocolM, N);
        BigIntMatrix x2 = doubleX2.toBigIntMatrix(protocolM, N);
        BigIntMatrix y1 = doubleY1.toBigIntMatrix(protocolM, N);
        BigIntMatrix y2 = doubleY2.toBigIntMatrix(protocolM, N);
        BigIntMatrix beta1 = doubleBeta1.toBigIntMatrix(protocolM, N);
        BigIntMatrix beta2 = doubleBeta2.toBigIntMatrix(protocolM, N);

        for (int k = 0; k < 3; k++) {
            //step1: compute the value of pi
            BigIntMatrix[] piResult = getProductShares(x1, beta1, x2, beta2, N, G, partyOne, partyTwo);
            BigIntMatrix enPi1 = Paillier.encrypt(piResult[0], N, G);

            //party 1 send encrypted pi1 to party two
            BigIntMatrix enPi2 = Paillier.encrypt(piResult[1], N, G);
            DoubleMatrix doubleP2 = DoubleMatrix.generateRandomMatrix(enPi1.getRowNumber(), enPi1.getColNumber());
            BigIntMatrix p2 = doubleP2.toBigIntMatrix(protocolM, N);
            BigIntMatrix enRandom = Paillier.encrypt(p2, N, G);
            BigIntMatrix piSum = PaillierProperties.doAdd(enPi1, enPi2, N);
            piSum = PaillierProperties.doAdd(piSum, enRandom, N);
            p2 = DoubleMatrixHelper.elementExp(DoubleMatrixHelper.invertSign(doubleP2)).toBigIntMatrix(protocolM, N);

            //party 1 decrypt encrypted piSum
            BigIntMatrix p1 = Paillier.decrypt(piSum, partyOne.getPrivateKey().getLambda(), N, G);
            DoubleMatrix doubleP1 = p1.toDoubleMatrix(protocolM, N);
            doubleP1 = DoubleMatrixHelper.elementExp(doubleP1);
            p1 = doubleP1.toBigIntMatrix(protocolM, N);
            BigIntMatrix enP1 = Paillier.encrypt(p1, N, G);

            //party2 get product
            BigInteger[][] tmp = new BigInteger[enP1.getRowNumber()][enP1.getColNumber()];
            for (int i = 0; i < enP1.getRowNumber(); i++) {
                tmp[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(enP1.getMatrix()[i][0], p2.getMatrix()[i][0], N);
            }
            BigIntMatrix product1 = new BigIntMatrix(tmp);

            BigIntMatrix EP1 = partyOne.outputEP1(partyTwo
                    .encryptBeforeSentToPartyOne(product1));
            BigIntMatrix e1 = partyOne.outputShare(partyTwo.outputES(EP1, product1));
            BigIntMatrix e2 = partyTwo.outputMatrixShare();
            
            BigIntMatrix enE1 = Paillier.encrypt(e1, N, G);
            //party 2 get encrypted e1
            BigIntMatrix enE2 = Paillier.encrypt(e2, N, G);
            BigIntMatrix allOneMatrix = DoubleMatrixHelper.getAllOneElementMatrix(enE1.getRowNumber(), enE2.getColNumber()).toBigIntMatrix(protocolM, N);
            BigIntMatrix enAllOne = Paillier.encrypt(allOneMatrix, N, G);
            BigIntMatrix sum = PaillierProperties.doAdd(enE1, enE2, N);
            sum = PaillierProperties.doAdd(enAllOne, sum, N);
            DoubleMatrix doubleR = DoubleMatrix.generateRandomMatrix(enPi1.getRowNumber(), enPi2.getColNumber());

            BigIntMatrix r = doubleR.toBigIntMatrix(protocolM, N);

            BigInteger[][] tmp1 = new BigInteger[r.getRowNumber()][r.getColNumber()];
            for (int i = 0; i < r.getRowNumber(); i++) {
                tmp1[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(sum.getMatrix()[i][0], r.getMatrix()[i][0], N);
            }
            BigIntMatrix product = new BigIntMatrix(tmp1);

            EP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(product));
            BigIntMatrix tmpS1 = partyOne.outputShare(partyTwo.outputES(EP1, product));
            BigIntMatrix tmpS2 = partyTwo.outputMatrixShare();

            product = tmpS1.addDecryptedMatrix(tmpS2);
            product = DoubleMatrixHelper.elementInverse(product.toDoubleMatrix(protocolM, N)).toBigIntMatrix(protocolM, N);
            product = Paillier.encrypt(product, N, G);


            //party2 get product
            BigInteger[][] tmp3 = new BigInteger[product.getRowNumber()][product.getColNumber()];
            for (int i = 0; i < product.getRowNumber(); i++) {
                tmp3[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(product.getMatrix()[i][0], r.getMatrix()[i][0], partyTwo.getPublicKey().getN());
            }
            product = new BigIntMatrix(tmp3);
            EP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(product));
            BigIntMatrix pi1 = partyOne.outputShare(partyTwo.outputES(EP1, product));
            BigIntMatrix pi2 = partyTwo.outputMatrixShare();

            BigIntMatrix[] pi = getElementProductShares(e1, pi1, e2, pi2, N, G, partyOne, partyTwo);
            pi1 = pi[0];
            pi2 = pi[1];
            
            BigIntMatrix[] wDiagonal = getElementProductShares(pi1, pi1, pi2, pi2, N, G, partyOne, partyTwo);
            wDiagonal[0] = pi1.subtractDecryptedMatrix(wDiagonal[0]);
            wDiagonal[1] = pi2.subtractDecryptedMatrix(wDiagonal[1]);
            
            DoubleMatrix dW1 = new DoubleMatrix(new double[x1.getRowNumber()][x1.getRowNumber()]);
            BigIntMatrix w1 = dW1.toBigIntMatrix(protocolM, N);
            BigInteger[][] wtmp1 = w1.getMatrix();
            for (int i = 0; i < w1.getColNumber(); i++) {
                wtmp1[i][i] = wDiagonal[0].getMatrix()[i][0];
            }
            w1 = new BigIntMatrix(wtmp1);

            DoubleMatrix dW2 = new DoubleMatrix(new double[x2.getRowNumber()][x2.getRowNumber()]);
            BigIntMatrix w2 = dW2.toBigIntMatrix(protocolM, N);
            BigInteger[][] wtmp2 = w2.getMatrix();
            for (int i = 0; i < w2.getColNumber(); i++) {
                wtmp2[i][i] = wDiagonal[1].getMatrix()[i][0];
            }
            w2 = new BigIntMatrix(wtmp2);

            BigIntMatrix[] xtwx = getProductShares(x1.getTransposedMatrix(), w1, x2.getTransposedMatrix(), w2, N, G, partyOne, partyTwo);
            xtwx = getProductShares(xtwx[0], x1, xtwx[1], x2, N, G, partyOne, partyTwo);


            // party 2 get encrypted pi1 and computes the value of - pi
            enPi1 = Paillier.encrypt(pi1, N, G);
            enPi2 = Paillier.encrypt(pi2, N, G);
            BigIntMatrix tmp4 = PaillierProperties.doAdd(enPi1, enPi2, N);
            BigInteger minusOne = FloatingPointNumber.doubleToBigInteger(-1, protocolM, N);

            BigInteger[][] tmp5 = new BigInteger[tmp4.getRowNumber()][tmp4.getColNumber()];
            for (int i = 0; i < tmp4.getRowNumber(); i++) {
                tmp5[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(tmp4.getMatrix()[i][0], minusOne, N);
            }
            tmp4 = new BigIntMatrix(tmp5);
            
            EP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(tmp4));
            pi1 = partyOne.outputShare(partyTwo.outputES(EP1, tmp4));
            pi2 = partyTwo.outputMatrixShare();

            BigIntMatrix[] part2 = getProductShares(x1.getTransposedMatrix(), y1.addDecryptedMatrix(pi1), x2.getTransposedMatrix(), y2.addDecryptedMatrix(pi2), N, G, partyOne, partyTwo);
            System.out.println("=======================part2: \n" + new DoubleMatrix(part2[0].addDecryptedMatrix(part2[1]).mod(N).toDouble(protocolM,
                N)));
            System.out.println("=======================xtwx: \n" + new DoubleMatrix(xtwx[0].addDecryptedMatrix(xtwx[1]).mod(N).toDouble(protocolM,
                N)));
            xtwx = matrixInversion(xtwx[0], xtwx[1], N, G, partyOne, partyTwo, protocolM);
            xtwx = getProductShares(xtwx[0], part2[0], xtwx[1], part2[1], N, G, partyOne, partyTwo);
            beta1 = beta1.addDecryptedMatrix(xtwx[0]);
            beta2 = beta2.addDecryptedMatrix(xtwx[1]);
        }
        System.out.println("=======================Final result: \n" + new DoubleMatrix(beta1.addDecryptedMatrix(beta2).mod(N).toDouble(protocolM,
                N)));

    }

    public BigIntMatrix[] matrixInversion(BigIntMatrix part1, BigIntMatrix part2, BigInteger N, BigInteger G, PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo, BigInteger protocolM) {
        BigInteger trace1 = part1.getTrace()
                .mod(N);
        BigInteger trace2 = part2.getTrace()
                .mod(N);
        BigInteger smallNum = FloatingPointNumber.doubleToBigInteger(
                SMALL_NUMBER, protocolM, N);

        BigInteger enTrace1 = Paillier.encrypt(trace1, N, G);
        BigInteger enTrace2 = Paillier.encrypt(trace2, N, G);
        BigInteger enSum = PaillierProperties.doAdd(enTrace1, enTrace2, N);
        BigInteger enP = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(enSum, smallNum, N);
        BigInteger enPEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(enP));
        trace1 = partyOne.outputShare(partyTwo.outputES(enPEP1, enP));
        trace2 = partyTwo.outputShare();

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
        BigIntMatrix matrixXShare1 = matrixMShare1.getIdentityMatrix(matrixMShare1.getColNumber()).multiply(x1);
        BigIntMatrix matrixXShare2 = matrixMShare2.getIdentityMatrix(matrixMShare2.getColNumber()).multiply(x2);

        for (int i = 0; i < 20; i++) {
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

//        System.out.println("MATRIX M: \n" + new DoubleMatrix(matrixMShare1.addDecryptedMatrix(matrixMShare2).mod(N).toDouble(protocolM,
//                N)));
//
//        //matrixXShare1.addDecryptedMatrix(matrixXShare2) is the inversion of X^T*X
//        System.out.println("INVERSTION OF MATRIX X^T*X: \n" + new DoubleMatrix(matrixXShare1.addDecryptedMatrix(matrixXShare2).mod(N).toDouble(protocolM,
//                N)));
        BigIntMatrix[] productShare = new BigIntMatrix[2];
        productShare[0] = matrixXShare1;
        productShare[1] = matrixXShare2;
        return productShare;
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

    // return shares of X*Y, XShare1 + XShare2 = X, YShare1 + YShare2 = Y
    public BigIntMatrix[] getElementProductShares(BigIntMatrix XShare1, BigIntMatrix YShare1, BigIntMatrix XShare2, BigIntMatrix YShare2, BigInteger N, BigInteger G, PartyWithPrivateKey partyOne, PartyWithoutPrivateKey partyTwo) {
        BigIntMatrix f1 = multiplyElement(XShare1, YShare1);
        f1 = Paillier.encrypt(f1, N, G);
        BigIntMatrix f2 = multiplyElement(XShare2, YShare2);
        f2 = Paillier.encrypt(f2, N, G);
        BigIntMatrix enXShare1 = Paillier.encrypt(XShare1, N, G);
        BigIntMatrix enYShare1 = Paillier.encrypt(YShare1, N, G);

        BigIntMatrix f3 = multiplyElementEnMatrixDeMatrix(enXShare1, YShare2, N);
        BigIntMatrix f4 = multiplyElementEnMatrixDeMatrix(enYShare1, XShare2, N);
        BigIntMatrix fProduct = PaillierProperties.doAdd(f1, f2, N);
        fProduct = PaillierProperties.doAdd(fProduct, f3, N);
        fProduct = PaillierProperties.doAdd(fProduct, f4, N);
        BigIntMatrix fEP1 = partyOne.outputEP1(partyTwo.encryptBeforeSentToPartyOne(fProduct));
        BigIntMatrix[] productShare = new BigIntMatrix[2];
        productShare[0] = partyOne.outputShare(partyTwo.outputES(fEP1, fProduct));
        productShare[1] = partyTwo.outputMatrixShare();
        return productShare;
    }

    public BigIntMatrix multiplyElement(BigIntMatrix x, BigIntMatrix y) {
        BigInteger[][] result = new BigInteger[x.getRowNumber()][y.getColNumber()];
        for (int i = 0; i < x.getRowNumber(); i++) {
            result[i][0] = x.getMatrix()[i][0].multiply(y.getMatrix()[i][0]);
        }
        return new BigIntMatrix(result);
    }

    public BigIntMatrix multiplyElementEnMatrixDeMatrix(BigIntMatrix x, BigIntMatrix y, BigInteger N) {
        BigInteger[][] result = new BigInteger[x.getRowNumber()][x.getColNumber()];
        for (int i = 0; i < x.getRowNumber(); i++) {
            result[i][0] = PaillierProperties.doMultiplyEncryptedNumberDecryptedNumber(x.getMatrix()[i][0], y.getMatrix()[i][0], N);
        }
        return new BigIntMatrix(result);
    }
}