package message.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import session.web.DataSourceUserSessionBean;
import utils.message.MessageDataSourceUsers;

@Path("/dataSourceUserInformation")
@Component
public class HandleDataSourceUserInformation {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Inject
    DataSourceUserSessionBean dataSourceUserSessionBean;

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putDataSourceUsers(JAXBElement<MessageDataSourceUsers> jaxbMessage) {
        MessageDataSourceUsers message = jaxbMessage.getValue();
        dataSourceUserSessionBean.handleDataSourceUserInformation(message.getUserList());
        return Response.created(uriInfo.getAbsolutePath()).entity("message id: " + message.getId()).build();
    }
}
