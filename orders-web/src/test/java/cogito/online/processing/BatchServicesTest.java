package cogito.online.processing;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cogito.online.model.Orders;

/**
 * Unit test the auditing services
 * @author jeremydeane
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:orders-services-spring.xml"})
public class BatchServicesTest {
	
	private static final Logger logger = LoggerFactory.getLogger
			(BatchServicesTest.class);	
	
	@Autowired
	BatchServices batchServices;

	@Test
	public void testSingleThreadedProcessing() throws Exception {
		
		batchServices.singleThreadedProcessing(getOrdersFile().getOrders());
	}

	@Test
	public void testJavaFireAndForget() throws Exception {
		
		batchServices.javaFireAndForget(getOrdersFile().getOrders());

	}
	
	@Test
	public void testJavaForkJoin() throws Exception {
		
		double batchTotal = batchServices.javaForkJoin(getOrdersFile().getOrders());
		
		System.out.println ("The total is " + batchTotal);
	}	
	
    /**
     * Converts Batch XML to Pojo
     * @param fileName
     * @return String
     */
    private Orders getOrdersFile() throws Exception {
            
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
            
    		InputStream stream = this.getClass().getClassLoader()
                    .getResourceAsStream("batch.xml");
			
    		Orders orders  = (Orders) unmarshaller.unmarshal(stream);

    		logger.debug(orders.toString());
        
        return orders;
    } 	
}