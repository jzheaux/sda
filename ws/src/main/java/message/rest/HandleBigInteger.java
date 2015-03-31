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

import session.web.BigIntegerSessionBean;
import utils.message.MessageBigInteger;

@Path("/bigInteger")
@Service
public class HandleBigInteger {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    BigIntegerSessionBean bigIntegerSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handleBigIntMatrix(JAXBElement<MessageBigInteger> jaxbMessage) {
        MessageBigInteger message = jaxbMessage.getValue();
        bigIntegerSessionBean.handleBigInteger(message);
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("BigInteger is received").build();
    }
}
