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

import session.web.RegressionTypeSessionBean;
import utils.message.MessageRegressionType;

@Path("/regressionType")
@Service
public class HandleRegressionType {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    RegressionTypeSessionBean regressionTypeSessionBean;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response handleRegressionType(JAXBElement<MessageRegressionType> jaxbMessage) {
        MessageRegressionType message = jaxbMessage.getValue();
        regressionTypeSessionBean.handleRegressionType(message);
        return Response.status(HttpURLConnection.HTTP_ACCEPTED).entity("Regression type message is received").build();
    }
}
