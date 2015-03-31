package utils.message;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessageBigInteger extends Message {
    BigInteger bigInt;
    String protocolInformation;
    String integerId;
    int protocolStepNumber;
    String resultCasheKey;
    String oppShareCacheKey;
    String additionalInfo;

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public BigInteger getBigInt() {
        return bigInt;
    }

    public void setBigInt(BigInteger bigInt) {
        this.bigInt = bigInt;
    }

    public String getProtocolInformation() {
        return protocolInformation;
    }

    public void setProtocolInformation(String protocolInformation) {
        this.protocolInformation = protocolInformation;
    }

    public String getIntegerId() {
        return integerId;
    }

    public void setIntegerId(String integerId) {
        this.integerId = integerId;
    }

    public int getProtocolStepNumber() {
        return protocolStepNumber;
    }

    public void setProtocolStepNumber(int protocolStepNumber) {
        this.protocolStepNumber = protocolStepNumber;
    }

    public String getResultCasheKey() {
        return resultCasheKey;
    }

    public void setResultCasheKey(String resultCasheKey) {
        this.resultCasheKey = resultCasheKey;
    }

    public String getOppShareCacheKey() {
        return oppShareCacheKey;
    }

    public void setOppShareCacheKey(String oppShareCacheKey) {
        this.oppShareCacheKey = oppShareCacheKey;
    }
    
}
