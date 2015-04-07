package message.restful;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

import regression.User;
import utils.message.MessagePing;

public class TestJson {
//    @Test
//    public void testJson() {
//        Client c = Client.create();
//        WebResource r = c.resource("http://localhost:8080/SDASystem-war/rest/test");
//        String jsonRes = r.accept(MediaType.APPLICATION_JSON).get(String.class);
//        System.out.println(jsonRes);
//        String xmlRes = r.accept(MediaType.APPLICATION_XML).get(String.class);
//		System.out.println(xmlRes);
//
//        ClientResponse response = r.get(ClientResponse.class);
//		System.out.println( response.getStatus() );
//		System.out.println( response.getHeaders().get("Content-Type") );
//		String entity = response.getEntity(String.class);
//		System.out.println(entity);
////		
////		// 3, get JAXB response
////		GenericType<utils.message.TM> genericType = new GenericType<utils.message.TM>() {};
////		utils.message.TM contacts = r.accept(MediaType.APPLICATION_XML).get(genericType);
////		PaillierPublicKey contact = (PaillierPublicKey) contacts.getObj();
////                System.out.println(contact.getG() + ": " + contact.getN());
//    }

    @Test
    public void test() {
        List<User> userList = new ArrayList<User>();
        User u = new User();
        u.setIp("this is ip");
        u.setPort(10);
        userList.add(u);
        MessagePing message = new MessagePing();
        message.setCurrentUser(u);
        Client c = ClientBuilder.newClient();
		WebTarget r = c.target("http://localhost:8080/sda-ws/rest/ping");
        Response response = r.request()
                .post(Entity.xml(message));
        System.out.println(response.getStatus());
        System.out.println(response);
        System.out.println(response.readEntity(String.class));
    }
}