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

import session.web.PingSessionBean;
import utils.message.MessagePing;

@Path("/ping")
@Service
public class HandlePing {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    PingSessionBean pingSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handlePing(JAXBElement<MessagePing> jaxbMessage) {
        pingSessionBean.handlePing(jaxbMessage.getValue().getCurrentUser());
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("Ping message is received").build();
    }
}
