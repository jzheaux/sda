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

import session.web.PublicKeySessionBean;
import utils.message.MessagePublicKey;

@Path("/publicKey")
@Service
public class HandlePublicKey {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    PublicKeySessionBean publicKeySessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handlePublicKey(JAXBElement<MessagePublicKey> jaxbMessage) {
        MessagePublicKey message = jaxbMessage.getValue();
        publicKeySessionBean.handlePublicKey(message.getCurrentUser(), message.getPublicKey());
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("Public key is received").build();
    }
}
