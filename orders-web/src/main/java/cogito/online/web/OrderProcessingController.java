package cogito.online.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cogito.online.model.Orders;
import cogito.online.processing.BatchServices;

/**
 * Handles submissions of batch orders
 * @author jeremydeane
 *
 */
@Controller
@RequestMapping("/")
public class OrderProcessingController {
	
	private static final Logger logger = LoggerFactory.getLogger
			(OrderProcessingController.class);
	
	@Autowired
	BatchServices batchServices;
	
	/**
	 * Accepts in a batch of orders and based on the URI parameter 
	 * uses a specific processing technique {single, multi, scala, akka}
	 * @param orders
	 * @param processor
	 * @return Orders
	 * @throws Exception
	 */
	@RequestMapping(value = "order/{processor}", method=RequestMethod.PUT)
	public @ResponseBody Orders putOrders (@RequestBody Orders orders, 
			@PathVariable String processor) throws Exception {
		
		//process batch using Scala code
		if (processor.toLowerCase().equals("scala")) {
			
			batchServices.functionalProcessing(orders.getOrders());
			
		} else if (processor.toLowerCase().equals("single")) {
			
			//single threaded Java code
			batchServices.singleThreadedProcessing(orders.getOrders());
			
		} else if (processor.toLowerCase().equals("akka")) {
			
			batchServices.akkaProcessing(orders.getOrders());
		
		} else {
			
			//multi-threaded Java code
			batchServices.multiThreadedProcessing(orders.getOrders());
		}
		
		if (logger.isDebugEnabled()) {
			
			logger.debug(processor + " processor handling: " +  orders);
		}
		
		return  orders;
	}	
}