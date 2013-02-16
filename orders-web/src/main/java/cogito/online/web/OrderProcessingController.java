package cogito.online.web;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cogito.online.model.BatchDeferredResult;
import cogito.online.model.Orders;
import cogito.online.model.ProcessedBatch;
import cogito.online.model.ResourceStatusConstants;
import cogito.online.processing.BatchServices;

import com.google.common.net.HttpHeaders;

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
	private BatchServices batchServices;

	@Autowired
	@Qualifier("statusCache")
	private Cache statusCache;
	
	@Autowired
	@Qualifier("batchCache")
	private Cache batchCache;	
	
	/**
	 * Accepts in a batch of orders and processes them
	 * @param orders
	 * @return Orders
	 * @throws Exception
	 */
	@RequestMapping(value = "order/java/single", method=RequestMethod.PUT)
	public @ResponseBody Orders putOrdersJavaSingle (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId());
				
		//single threaded Java code
		batchServices.singleThreadedProcessing(orders.getOrders());
		
		response.setStatus(HttpStatus.OK.value());

		return orders;
	}
	
	/**
	 * Accepts in a batch of orders and processes them
	 * @param orders
	 * @throws Exception
	 */
	@RequestMapping(value = "order/java/fireandforget", method=RequestMethod.PUT)
	public void putOrdersJavaFireAndForget (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId());
				
		//single threaded Java code
		batchServices.javaFireAndForget(orders.getOrders());
		
		response.setStatus(HttpStatus.ACCEPTED.value());
	}
	
	/**
	 * Accepts in a batch of orders and processes them
	 * @param orders
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "order/java/forkjoin", method=RequestMethod.PUT)
	public @ResponseBody String putOrdersJavaForkJoin (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId());
				
		//single threaded Java code
		double batchTotal = batchServices.javaForkJoin(orders.getOrders());
		
		response.setStatus(HttpStatus.OK.value());
		
		return "$" + Double.toString(Math.round(batchTotal));
	}
	
	/**
	 * Accepts in a batch of orders and processes them
	 * @param orders
	 * @throws Exception
	 */
	@RequestMapping(value = "order/akka/pipeline", method=RequestMethod.PUT)
	public void putOrdersAkkaFireAndForget (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId());
				
		//single threaded Java code
		batchServices.akkaActorPipeline(orders.getOrders());
		
		response.setStatus(HttpStatus.ACCEPTED.value());
	}
	
	/**
	 * Accepts in a batch of orders and processes them
	 * @param orders
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "order/akka/forkjoin", method=RequestMethod.PUT)
	public @ResponseBody String putOrdersAkkaForkJoin (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId());
				
		//single threaded Java code
		double batchTotal = batchServices.akkaActorForkJoin(orders.getOrders());
		
		response.setStatus(HttpStatus.OK.value());
		
		return "$" + Double.toString(Math.round(batchTotal));
	}
	
	/**
	 * Accepts in a batch of orders and processes them
	 * @param orders
	 * @throws Exception
	 */
	@RequestMapping(value = "order/akka/mapreduce", method=RequestMethod.PUT)
	public void putOrdersAkkaMapReduce (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId());
				
		//single threaded Java code
		batchServices.akkaMapReduceBatch(orders);
		
		response.setStatus(HttpStatus.ACCEPTED.value());
	}
	
	/**
	 * Accepts in a batch of orders and processes them asynchronously
	 * @param orders
	 * @throws Exception
	 */
	@RequestMapping(value = "batch/async", method=RequestMethod.PUT)
	public void putOrdersAsynchronously (@RequestBody Orders orders, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Processing Batch " + orders.getBatchId() + " asynchronously");
		
		//create a randome key, Future ID, and store in ehcache.
		String resourceStatusKey = UUID.randomUUID().toString();
		
		statusCache.putIfAbsent(new Element(resourceStatusKey, 
				ResourceStatusConstants.PROCESSING));
		
		ProcessedBatch processedBatch = new ProcessedBatch (orders.getBatchId(),
				resourceStatusKey, orders.getOrders());
		
		batchServices.processBatchAsynchronously(processedBatch);

		//in a production this URL would be generated based on the environment..
		response.setHeader(HttpHeaders.LOCATION, 
				"http://localhost:8080/orders-web/batch/total/future/" 
						+ resourceStatusKey);
		
		response.setStatus(HttpStatus.ACCEPTED.value());
	}
	
	/**
	 * Retrieve a batch total
	 * @param batchId
	 * @param response
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "batch/{batchId}/total", method=RequestMethod.GET)
	public @ResponseBody String getBatchTotal(@PathVariable String batchId, 
			HttpServletResponse response) throws Exception {
		
		logger.debug("Retrieving Batch Total " + batchId);
		
		String batchTotal = null;
		
		Element element = batchCache.get(batchId);
		
		if (element == null) {
			
			response.setStatus(HttpStatus.NOT_FOUND.value());
	
		} else {
			
			response.setStatus(HttpStatus.OK.value());
			
			batchTotal = "$" + Math.round((Double)element.getValue());
		}
		
		return batchTotal;
	}
	
	/**
	 * Retrieve a future batch total
	 * @param batchId
	 * @param response
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "batch/total/future/{statusKey}", method=RequestMethod.GET)
	public @ResponseBody String getBatchFutureTotal(@PathVariable String statusKey, 
			HttpServletResponse response) throws Exception {	
		
		String batchTotal = null;
		
		logger.debug("Retrieving Future Batch Total with Status Key " + statusKey);
		
		Element statusElement = statusCache.get(statusKey);
		
		if (statusElement != null) {
		
			String batchId = (String) statusElement.getValue();
			
			//still processing
			if (batchId.equals(ResourceStatusConstants.PROCESSING)) {
				
				//202 Accepted - Still Processing
				response.setStatus(HttpStatus.ACCEPTED.value());
				
			} else {
				
				Element batchElement = batchCache.get(batchId);			
				
				batchTotal = "$" + Math.round((Double)batchElement.getValue());
				
				//200 OK - Returning Batch Total
				response.setStatus(HttpStatus.OK.value());
				
				logger.debug("Found Future Batch " + batchId + " Total:  " 
						+ batchTotal);				
			}
			
		} else {
			
			//404 Resource Not Found - Still Processing
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}

		return batchTotal;
	}

	/**
	 * Process a batch in a different thread using Servlet 3 Asynchronous 
	 * Support - Implemented using Spring MVC DeferredResult
	 * @param orders
	 * @param response
	 * @return BatchDeferredResult
	 * @throws Exception
	 */
	@RequestMapping(value = "deferred/batch/total", method=RequestMethod.PUT)
	public @ResponseBody BatchDeferredResult putDeferredBatch 
		(@RequestBody Orders orders, HttpServletResponse response) 
				throws Exception {
		
		logger.debug("Processing Deferred Batch " + orders.getBatchId());
		
		BatchDeferredResult batchDeferredResult = 
				new BatchDeferredResult(6000L, orders.getBatchId());
		
		batchServices.processDeferredBatch(batchDeferredResult, orders);
	
		return batchDeferredResult;
	}	
}