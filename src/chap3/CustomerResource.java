package chap3;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Path("/customers")
public class CustomerResource {

    // simulate DB scenario
    private Map<Integer, Customer> custerDB = new ConcurrentHashMap<Integer, Customer>();
    private AtomicInteger idCounter = new AtomicInteger();

    @POST
    @Consumes("application/xml")
    public Response createCustomer(InputStream is) {
        Customer customer = readCustomer(is);
        customer.setId(idCounter.incrementAndGet());
        custerDB.put(customer.getId(), customer);
        System.out.println("Created customer " + customer.getId());
        return Response.created(URI.create("/customers/" + customer.getId())).build();
    }

    // DOM parsing XML data
    private Customer readCustomer(InputStream is) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.getDocumentElement();
            Customer customer = new Customer();
            if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals("")) {
                customer.setId(Integer.valueOf(root.getAttribute("id")));
            }
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (element.getTagName().equals("first-name")) {
                    customer.setFirstName(element.getTextContent());
                } else if (element.getTagName().equals("last-name")) {
                    customer.setLastName(element.getTextContent());
                } else if (element.getTagName().equals("street")) {
                    customer.setStreet(element.getTextContent());
                } else if (element.getTagName().equals("city")) {
                    customer.setCity(element.getTextContent());
                } else if (element.getTagName().equals("state")) {
                    customer.setState(element.getTextContent());
                } else if (element.getTagName().equals("zip")) {
                    customer.setZip(element.getTextContent());
                } else if (element.getTagName().equals("country")) {
                    customer.setCountry(element.getTextContent());
                }
            }
            return customer;
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    public static void main(String[] args) {
        // TODO: test XML-object conversion
    }

}
