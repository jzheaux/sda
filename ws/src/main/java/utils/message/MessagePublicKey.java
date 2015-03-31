package utils.message;

import cryptosystem.PaillierPublicKey;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessagePublicKey extends Message {

    private PaillierPublicKey publicKey;

    public MessagePublicKey() {
        super();
    }

    @XmlElement(name = "PaillierPublicKey")
    public PaillierPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PaillierPublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
