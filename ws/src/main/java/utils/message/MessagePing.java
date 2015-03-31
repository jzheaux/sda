package utils.message;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessagePing extends Message {

    String content = "ping";

    public MessagePing() {
        super();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
