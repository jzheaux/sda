package utils.message;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import matrix.BigIntMatrix;

@XmlRootElement
public class MessageBigIntMatrix extends Message {

    List<BigIntMatrix> bigIntMatrixList;
    String protocolInformation;
    String matrixId;
    int protocolStepNumber;
    String oppShareId;
    String resultId;
    List<String> additionalInformation;

    public MessageBigIntMatrix() {
        super();
    }

    public int getProtocolStepNumber() {
        return protocolStepNumber;
    }

    @XmlElement(name = "bigIntMatrix")
    public List<BigIntMatrix> getBigIntMatrixList() {
        return bigIntMatrixList;
    }

    public void setBigIntMatrixList(List<BigIntMatrix> bigIntMatrixList) {
        this.bigIntMatrixList = bigIntMatrixList;
    }

    public void setProtocolStepNumber(int protocolStepNumber) {
        this.protocolStepNumber = protocolStepNumber;
    }

    public String getProtocolInformation() {
        return protocolInformation;
    }

    public void setProtocolInformation(String protocolInformation) {
        this.protocolInformation = protocolInformation;
    }

    public String getMatrixId() {
        return matrixId;
    }

    public void setMatrixId(String matrixId) {
        this.matrixId = matrixId;
    }

    public String getOppShareId() {
        return oppShareId;
    }

    public void setOppShareId(String oppShareId) {
        this.oppShareId = oppShareId;
    }

    public List<String> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(List<String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
}
