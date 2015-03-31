package session.web;

import org.springframework.stereotype.Service;

import session.web.handlers.GeneralInstructionSessionBeanHandler;
import utils.message.GeneralInstructionMessage;

@Service
public class GeneralInstructionSessionBean {

    public void handleGeneralInstruction(GeneralInstructionMessage message) {
        GeneralInstructionSessionBeanHandler handler = new GeneralInstructionSessionBeanHandler(message);
        Thread thread = new Thread(handler);
        thread.start();
    }
}
