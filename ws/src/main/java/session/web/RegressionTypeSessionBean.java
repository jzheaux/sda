package session.web;

import org.springframework.stereotype.Service;

import regression.Regression;
import utils.message.MessageRegressionType;

@Service
public class RegressionTypeSessionBean {

    public void handleRegressionType(MessageRegressionType message) {
        Regression.setRegressionType(message.getRegressionType());
    }
}
