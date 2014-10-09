package chap3;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class Test {
    
    private static final String URL = "http://10.100.8.70:9090/JAX-RS_RESTful/services/customers";

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        try {
           System.out.println("*** Create a new Customer ***");

           String xml = "<customer>"
                   + "<first-name>Bill</first-name>"
                   + "<last-name>Burke</last-name>"
                   + "<street>256 Clarendon Street</street>"
                   + "<city>Boston</city>"
                   + "<state>MA</state>"
                   + "<zip>02115</zip>"
                   + "<country>USA</country>"
                   + "</customer>";

           Response response = client.target(URL).request().post(Entity.xml(xml));
           System.out.println(response.getStatus());
           if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
           String location = response.getLocation().toString();
           System.out.println("Location: " + location);
           response.close();

           System.out.println("*** GET Created Customer **");
           String customer = client.target(location).request().get(String.class);
           System.out.println(customer);

           String updateCustomer = "<customer>"
                   + "<first-name>William</first-name>"
                   + "<last-name>Burke</last-name>"
                   + "<street>256 Clarendon Street</street>"
                   + "<city>Boston</city>"
                   + "<state>MA</state>"
                   + "<zip>02115</zip>"
                   + "<country>USA</country>"
                   + "</customer>";
           response = client.target(location).request().put(Entity.xml(updateCustomer));
           if (response.getStatus() != 204) throw new RuntimeException("Failed to update");
           response.close();
           System.out.println("**** After Update ***");
           customer = client.target(location).request().get(String.class);
           System.out.println(customer);
        } finally {
           client.close();
        }
    }
}
