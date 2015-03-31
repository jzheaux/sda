package session.web;

import org.springframework.stereotype.Service;

import session.web.handlers.DoubleMatrixSessionBeanHandler;
import utils.message.MessageDoubleMatrix;

@Service
public class DoubleMatrixSessionBean {

    public void handleDoubleMatrix(MessageDoubleMatrix message) {
        DoubleMatrixSessionBeanHandler handler = new DoubleMatrixSessionBeanHandler(message);
        Thread thread = new Thread(handler);
        thread.start();
    }
}
