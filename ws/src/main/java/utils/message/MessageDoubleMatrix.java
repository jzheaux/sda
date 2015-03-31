package utils.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import matrix.DoubleMatrix;

@XmlRootElement
public class MessageDoubleMatrix extends Message {

    String resultId;
    String protocolInformation;
    DoubleMatrix doubleMatrix;

    public MessageDoubleMatrix() {
        super();
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    @XmlElement(name = "doubleIntMatrix")
    public DoubleMatrix getDoubleMatrix() {
        return doubleMatrix;
    }

    public void setDoubleMatrix(DoubleMatrix doubleMatrix) {
        this.doubleMatrix = doubleMatrix;
    }

    public String getProtocolInformation() {
        return protocolInformation;
    }

    public void setProtocolInformation(String protocolInformation) {
        this.protocolInformation = protocolInformation;
    }

}
