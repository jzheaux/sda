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

import session.web.DoubleMatrixSessionBean;
import utils.message.MessageDoubleMatrix;

@Path("/doubleMatrix")
@Component
public class HandleDoubleMatrix {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    DoubleMatrixSessionBean doubleMatrixSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handleBigIntMatrix(JAXBElement<MessageDoubleMatrix> jaxbMessage) {
        MessageDoubleMatrix message = jaxbMessage.getValue();
        doubleMatrixSessionBean.handleDoubleMatrix(message);
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("DoubleMatrix is received").build();
    }
}
