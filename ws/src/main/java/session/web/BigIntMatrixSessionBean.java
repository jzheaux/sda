package session.web;

import org.springframework.stereotype.Service;

import session.web.handlers.BigIntMatrixSessionBeanHandler;
import utils.message.MessageBigIntMatrix;

@Service
public class BigIntMatrixSessionBean {

     public void handleBigIntMatrix(MessageBigIntMatrix message) {
         BigIntMatrixSessionBeanHandler handler = new BigIntMatrixSessionBeanHandler(message);
         Thread thread = new Thread(handler);
         thread.start();
     }  
}
