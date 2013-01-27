package cogito.online.model;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TransformationTest {
	
	private static final Logger logger = LoggerFactory.getLogger
			(TransformationTest.class);
	
	@Test
	public void testOrderJSONtoJAVA() throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		
		InputStream input = IOUtils.toInputStream
				("{\"id\":\"X1251\",\"customer\":\"Blain\",\"item\":\"Dice\",\"amount\":2}");
		
		//Unmarshell to Java
		Order order = mapper.readValue(input, Order.class);
		
		logger.debug(order.toString());
		
    	assertTrue(order.getCustomer().equals("Blain"));
    	
	}
		
	
	@Test
	public void testOrdersJSONtoJAVA() throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		
		StringBuffer sb = new StringBuffer ();
		
		sb.append("{\"batchId\":\"A1234\",\"date\":\"07252012\",\"orders\":[");
		sb.append("{\"id\":\"X1351\",\"customer\":\"Turner\",\"item\":\"Rings\",\"amount\":4},");
		sb.append("{\"id\":\"X1352\",\"customer\":\"Turner\",\"item\":\"Dice\",\"amount\":2},");
		sb.append("{\"id\":\"X1353\",\"customer\":\"Turner\",\"item\":\"Rainbow Scarf\",\"amount\":1}");
		sb.append("]}");
		
		InputStream input = IOUtils.toInputStream (sb.toString());
		
		//Unmarshell to Java
		Orders orders = mapper.readValue(input, Orders.class);
		
		logger.debug(orders.toString());
		
    	assertTrue(orders.getOrders().size() == 3);
    	
	}	
	
	@Test
	public void testOrderJAVAtoJSON() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		 
		//Marshell Order to Java
		Order order = new Order("X1251", "Blain", "Dice", "2");
		
		String json = mapper.writeValueAsString(order);
		
		logger.debug(json);
		
		assertTrue(json.contains("Blain"));
	}
	
	@Test
	public void testOrdersJAVAtoJSON() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		 
		//Marshell Order to Java
		Order order = new Order("X1351", "Turner", "Rings", "4");
		Order order2 = new Order("X1352", "Turner", "Dice", "2");
		Order order3 = new Order("X1353", "Turner", "Rainbow Scarf", "1");
		
		List<Order> list = Arrays.asList(order, order2, order3);
		
		Orders orders = new Orders("A1234", "07252012", list);
		
		String json = mapper.writeValueAsString(orders);
		
		logger.debug(json);
		
		assertTrue(json.contains("Turner"));
	}	
	
	@Test
	public void testOrderXMLtoJAVA() throws Exception{
		
		//Unmarshell to Java
		JAXBContext context = JAXBContext.newInstance(Order.class);			
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setEventHandler(new ValidationEventHandler() {

            @Override
            public boolean handleEvent(ValidationEvent event) {
                logger.error((event.getMessage()));
                return true;
            }}

        );
		
		InputStream stream = IOUtils.toInputStream
			("<order id=\"X1355\" customer=\"Palmer\" item=\"Rainbow Scarf\" amount=\"6\"/>", "UTF-8");
			
		Order order  = (Order) unmarshaller.unmarshal(stream);

		logger.debug(order.toString());
		
		assertEquals(order.getAmount(), new Integer(6));    	
	}
	
	@Test
	public void testOrdersXMLtoJAVA() throws Exception{
		
		//Unmarshell to Java
		JAXBContext context = JAXBContext.newInstance(Orders.class);			
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setEventHandler(new ValidationEventHandler() {

            @Override
            public boolean handleEvent(ValidationEvent event) {
                logger.error((event.getMessage()));
                return true;
            }}

        );
		
		StringBuffer sb = new StringBuffer ();
		
		sb.append("<orders date=\"07252012\" batchId=\"A1234\">");
		sb.append("<order amount=\"4\" item=\"Rings\" customer=\"Turner\" id=\"X1351\"/>");
		sb.append("<order amount=\"2\" item=\"Dice\" customer=\"Turner\" id=\"X1352\"/>");
		sb.append("<order amount=\"1\" item=\"Rainbow Scarf\" customer=\"Turner\" id=\"X1353\"/>");
		sb.append("</orders>");
		
		InputStream stream = IOUtils.toInputStream (sb.toString());
			
		Orders orders  = (Orders) unmarshaller.unmarshal(stream);

		logger.debug(orders.toString());
		
    	assertTrue(orders.getOrders().size() == 3);    	
	}	
	
	@Test
	public void testOrderJAVAtoXML() throws Exception {
		
		JAXBContext context = JAXBContext.newInstance(Order.class);
		Marshaller marsheller = context.createMarshaller();
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		Order order = new Order("X1351", "Turner", "Rings", "4");
		
		marsheller.marshal(order, outStream);
		
		String xml = outStream.toString();
		
		logger.debug(xml);

		assertTrue(xml.contains("Turner"));
	}
	
	@Test
	public void testOrdersJAVAtoXML() throws Exception {
		
		JAXBContext context = JAXBContext.newInstance(Orders.class);
		Marshaller marsheller = context.createMarshaller();
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		Order order = new Order("X1351", "Turner", "Rings", "4");
		Order order2 = new Order("X1352", "Turner", "Dice", "2");
		Order order3 = new Order("X1353", "Turner", "Rainbow Scarf", "1");
		
		List<Order> list = Arrays.asList(order, order2, order3);
		
		Orders orders = new Orders("A1234", "07252012", list);
		
		marsheller.marshal(orders, outStream);
		
		String xml = outStream.toString();
		
		logger.debug(xml);

		assertTrue(xml.contains("Turner"));
	}	

}