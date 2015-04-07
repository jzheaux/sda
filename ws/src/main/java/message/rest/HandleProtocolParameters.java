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

import org.springframework.stereotype.Component;

import session.web.ProtocolParametersSessionBean;
import utils.message.MessageProtocolParameters;

@Path("/protocolParameters")
@Component
public class HandleProtocolParameters {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    ProtocolParametersSessionBean protocolParametersSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handlePublicKey(JAXBElement<MessageProtocolParameters> jaxbMessage) {
        MessageProtocolParameters message = jaxbMessage.getValue();
        protocolParametersSessionBean.handleProtocolParameters(message.getParameters());
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("Public key is received").build();
    }
}
