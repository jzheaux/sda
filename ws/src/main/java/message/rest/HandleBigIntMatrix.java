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

import session.web.BigIntMatrixSessionBean;
import utils.message.MessageBigIntMatrix;

@Path("/bigIntMatrix")
@Component
public class HandleBigIntMatrix {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    BigIntMatrixSessionBean bigIntMatrixSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handleBigIntMatrix(JAXBElement<MessageBigIntMatrix> jaxbMessage) {
        MessageBigIntMatrix message = jaxbMessage.getValue();
        bigIntMatrixSessionBean.handleBigIntMatrix(message);
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("BigIntMatrix is received").build();
    }
}
