package chap3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
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
        return Response.created(URI.create("/JAX-RS_RESTful/services/customers/" + customer.getId())).build();
    }

//    @GET
//    @Path("{id}")
//    @Produces("application/xml")
//    public StreamingOutput getCustomer(@PathParam("id") int id) {
//        final Customer customer = custerDB.get(id);
//        if (customer == null) {
//            throw new WebApplicationException(Response.Status.NOT_FOUND);
//        }
//        return new StreamingOutput() {
//            @Override
//            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
//                outputCustomer(outputStream, customer);
//            }
//        };
//    }

    @GET
    @Path("{id}")
    @Produces("application/xml")
    public Customer getCustomer(@PathParam("id") int id) {
        Customer customer = custerDB.get(id);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return customer;
    }

    @PUT
    @Path("{id}")
    @Consumes("application/xml")
    public void updateCustomer(@PathParam("id") int id, InputStream is) {
        Customer update = readCustomer(is);
        Customer current = custerDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setStreet(update.getStreet());
        current.setState(update.getState());
        current.setZip(update.getZip());
        current.setCountry(update.getCountry());
    }

    private void outputCustomer(OutputStream os, Customer customer) {
        PrintStream writer = new PrintStream(os);
        writer.println("<customer id=\"" + customer.getId() + "\">");
        writer.println("   <first-name>" + customer.getFirstName() + "</first-name>");
        writer.println("   <last-name>" + customer.getLastName() + "</last-name>");
        writer.println("   <street>" + customer.getStreet() + "</street>");
        writer.println("   <city>" + customer.getCity() + "</city>");
        writer.println("   <state>" + customer.getState() + "</state>");
        writer.println("   <zip>" + customer.getZip() + "</zip>");
        writer.println("   <country>" + customer.getCountry() + "</country>");
        writer.println("</customer>");
    }

    // DOM parsing XML data
    private Customer readCustomer(InputStream is) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(is);
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

}
