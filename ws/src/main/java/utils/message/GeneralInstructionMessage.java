package utils.message;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import matrix.BigIntMatrix;

@XmlRootElement
public class GeneralInstructionMessage extends Message {

    String protocolInformation;
    List<String> matrixIdList;
    List<String> integerIdList;
    String resultId;
    int stepNumber;
    BigIntMatrix matrix;

    public String getProtocolInformation() {
        return protocolInformation;
    }

    public void setProtocolInformation(String protocolInformation) {
        this.protocolInformation = protocolInformation;
    }

    public List<String> getMatrixIdList() {
        return matrixIdList;
    }

    public void setMatrixIdList(List<String> matrixIdList) {
        this.matrixIdList = matrixIdList;
    }

    public List<String> getIntegerIdList() {
        return integerIdList;
    }

    public void setIntegerIdList(List<String> integerIdList) {
        this.integerIdList = integerIdList;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    @XmlElement(name = "bigIntMatrix")
    public BigIntMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(BigIntMatrix matrix) {
        this.matrix = matrix;
    }
}
