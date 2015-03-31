package utils.message;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessageRegressionType extends Message{
    String regressionType;
    
    public MessageRegressionType(){
        super();
    }

    public String getRegressionType() {
        return regressionType;
    }

    public void setRegressionType(String regressionType) {
        this.regressionType = regressionType;
    }
    
}
