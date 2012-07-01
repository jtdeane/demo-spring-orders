package cogito.online.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

public class TransformationTest {
	

	@Test
	public void testOrderXmltoJava() throws Exception{
		
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		
    	xstream.alias("order", Order.class);
    	
    	String orderXML = getXmlFromFile("/order.xml");
    	
    	Order order = (Order) xstream.fromXML(orderXML);
    	
    	Assert.assertTrue(order.getId().equals("X1121"));
    	
	}
	
	@Test
	public void testBatchXmltoJava() throws Exception{
		
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		
    	xstream.alias("order", Order.class);
		xstream.alias("orders", Orders.class);
    	
		String batchXML = getXmlFromFile("/batch.xml");
		
		Orders orders = (Orders)xstream.fromXML(batchXML);
		
    	Assert.assertTrue(orders.getBatchId().equals("A1234"));
	}	
	
	@Test
	public void testOrderJavatoXml() throws Exception {
		
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		
    	xstream.alias("order", Order.class);
		
		Order order = new Order("1234", "Blain", "Dice", "2");
		
		String xml = xstream.toXML(order);
		
		Assert.assertTrue(xml.contains("Blain"));

	}
	
	@Test
	public void testBatchJavatoXml() throws Exception {
		
		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		
    	xstream.alias("order", Order.class);
		xstream.alias("orders", Orders.class);
		
		Order order = new Order("1234", "Blain", "Dice", "2");
		Order order2 = new Order("5678", "Blackstone", "Quarters", "4");
		
		ArrayList<Order> list = new ArrayList<Order>();
		list.add(order);
		list.add(order2);
		
		Orders orders = new Orders("B6744", "11202011", list);
		
		String xml = xstream.toXML(orders);
		
		Assert.assertTrue(xml.contains("Blackstone"));
	}
	
    /**
     * Helper method for retrieving test xml file
     * @param fileName
     * @return String
     */
    private String getXmlFromFile(String fileName) throws Exception {
        StringBuffer text = new StringBuffer();
        BufferedReader in = null;
        String line = null;
        
        try {
        	
            in = new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream(fileName)));

            while ((line = in.readLine()) != null) {
                text.append(line);
            }
			
		} finally {
		
			if (in != null) {
			
		        in.close();
			}
		}
        
        return text.toString();
    } 
}