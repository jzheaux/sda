package utils.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import protocol.ProtocolParameters;

@XmlRootElement
public class MessageProtocolParameters extends Message {

    ProtocolParameters parameters;

    public MessageProtocolParameters() {
        super();
    }

    @XmlElement(name = "protocolParameters")
    public ProtocolParameters getParameters() {
        return parameters;
    }

    public void setParameters(ProtocolParameters parameters) {
        this.parameters = parameters;
    }
}
