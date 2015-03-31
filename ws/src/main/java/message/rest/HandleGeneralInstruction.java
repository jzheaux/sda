package message.rest;

import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Service;

import session.web.GeneralInstructionSessionBean;
import utils.message.GeneralInstructionMessage;

@Path("/generalInstruction")
@Service
public class HandleGeneralInstruction {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    GeneralInstructionSessionBean generalInstructionSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handleGeneralInstruction(JAXBElement<GeneralInstructionMessage> jaxbMessage) {
        GeneralInstructionMessage message = jaxbMessage.getValue();
        generalInstructionSessionBean.handleGeneralInstruction(message);
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("GeneralInstruction is received").build();
    }
}
