package session.web.handlers;

import regression.ProtocolInformation;
import regression.Regression;
import utils.message.MessageDoubleMatrix;

public class DoubleMatrixSessionBeanHandler implements Runnable {

    private MessageDoubleMatrix message;

    public DoubleMatrixSessionBeanHandler(MessageDoubleMatrix message) {
        this.message = message;
    }

    @Override
    public void run() {
        handDoubleMatrix(message);
    }

    private void handDoubleMatrix(MessageDoubleMatrix message) {
         String protocolInformation = message.getProtocolInformation();
        if (protocolInformation.equals(ProtocolInformation.DO_SET_CACHE)) {
            setCache(message);
        }
    }
    
    private void setCache(MessageDoubleMatrix message){
        Regression.putCacheContent(message.getResultId(), message.getDoubleMatrix());
    }
}
