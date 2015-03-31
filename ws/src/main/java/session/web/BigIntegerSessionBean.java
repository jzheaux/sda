package session.web;

import org.springframework.stereotype.Service;

import session.web.handlers.BigIntegerSessionBeanHandler;
import utils.message.MessageBigInteger;

@Service
public class BigIntegerSessionBean {

    public void handleBigInteger(MessageBigInteger message) {
        BigIntegerSessionBeanHandler handler = new BigIntegerSessionBeanHandler(message);
        Thread thread = new Thread(handler);
         thread.start();
    }
}
