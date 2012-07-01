package cogito.online.processing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cogito.online.model.Order;
import cogito.online.model.Orders;

import com.thoughtworks.xstream.XStream;

/**
 * Unit test the auditing services
 * @author jeremydeane
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:orders-services-spring.xml"})
public class BatchServicesTest {
	
	@Autowired
	BatchServices batchServices;

	@Test
	public void testSingleThreadedProcessing() throws Exception {
		
		batchServices.singleThreadedProcessing(getOrdersFile().getOrders());
	}

	@Test
	public void testMultiThreadedProcessing() throws Exception {
		
		batchServices.multiThreadedProcessing(getOrdersFile().getOrders());

	}
	
	@Test
	public void testScalaProcessing() throws Exception {
		
		batchServices.functionalProcessing(getOrdersFile().getOrders());
	}	
	
    /**
     * Converts Batch XML to Pojo
     * @param fileName
     * @return String
     */
    private Orders getOrdersFile() throws Exception {
        StringBuffer text = new StringBuffer();
        Orders orders = null;
        BufferedReader in = null;
        String line = null;
        
        try {
        	
            in = new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream("/batch.xml")));

            while ((line = in.readLine()) != null) {
                text.append(line);
            }
            
    		XStream xstream = new XStream();
    		xstream.autodetectAnnotations(true);
    		
        	xstream.alias("order", Order.class);
    		xstream.alias("orders", Orders.class);
    		
    		orders = (Orders)xstream.fromXML(text.toString());            
			
		} finally {
		
			if (in != null) {
			
		        in.close();
			}
		}
        
        return orders;
    } 	
}