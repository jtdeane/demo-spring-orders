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
import org.springframework.context.ApplicationContext;
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
	private ApplicationContext applicationContext;	
	
	//set once during execution of test
	private Orders orders;
	
	@Autowired
	BatchServices batchServices;


	@Test
	public void testSingleThreadedProcessing() throws Exception {
		
		batchServices.singleThreadedProcessing(getOrdersFile().getOrders());
	}

	@Test
	public void testJavaFireAndForget() throws Exception {
		
		batchServices.javaFireAndForget(getOrdersFile().getOrders());
		
		//to view output
		Thread.sleep(3000);
	}
	
	@Test
	public void testJavaForkJoin() throws Exception {
		
		batchServices.javaForkJoin(getOrdersFile().getOrders());
	}
	
	@Test
	public void testAkkaActorPipeline() throws Exception {
		
		batchServices.akkaActorPipeline(getOrdersFile().getOrders());
		
		//to view output
		Thread.sleep(3000);
	}
	
	@Test
	public void testAkkaActorForkJoin() throws Exception {
		
		batchServices.akkaActorForkJoin(getOrdersFile().getOrders());
	}
	
	@Test
	public void testAkkaMapReduceBatch() throws Exception {
		
		batchServices.akkaMapReduceBatch(getOrdersFile());
		
		//to view output
		Thread.sleep(3000);
	}	
	
    /**
     * Converts Batch XML to Pojo
     * @param fileName
     * @return String
     */
    private Orders getOrdersFile() throws Exception {
    	
    	if (this.orders != null) {
    		return orders;
    	}
            
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
		
		this.orders = orders;

		logger.debug(orders.toString());
        
        return orders;
    } 	
}