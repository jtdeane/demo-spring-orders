package cogito.online.processing;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import cogito.online.model.Order;

/**
 * Processes a batch of orders using Java or using Scala, a functional 
 * programming language, Scala
 * 
 * @author jeremydeane
 */
@Service
public class BatchServices implements ApplicationContextAware {
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	private ApplicationContext applicationContext;
	
    /*
     * Thread pool Acts as a throttle preventing out of memory exception
     */
    private Executor pool = new ThreadPoolExecutor(10, 50, Long.MAX_VALUE, 
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2000), 
            new ThreadPoolExecutor.DiscardOldestPolicy());

	private ActorRef router;
    
    /**
	 * Process orders using a single thread
     * @param orders
     * @throws Exception
     */
	public void singleThreadedProcessing(List<Order> orders) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("\n");
			log.debug("********************************************************");
			log.debug("Started single-threaded processing of batch containing " 
					+ orders.size() + " orders");
		}
		
		for (Order order : orders) {
			
	        Object[] constructorArguments = {order};
	        
	        OrderProcess orderProcess = (OrderProcess) applicationContext.
	        		getBean("orderProcess", constructorArguments);
	        
	        //process in same thread
	        orderProcess.run();
		}
		
		waitForCalcsToComplete(orders);
	}    
    
    /**
	 * Process orders using Java Thread Pool
     * @param orders
     * @throws Exception
     */
	public void multiThreadedProcessing(List<Order> orders) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("\n");
			log.debug("********************************************************");
			log.debug("Started multi-threaded processing of batch containing " 
					+ orders.size() + " orders");
		}
		
		for (Order order : orders) {
			
	        Object[] constructorArguments = {order};
	        
	        OrderProcess orderProcess = (OrderProcess) applicationContext.
	        		getBean("orderProcess", constructorArguments);
	        
	        //process in new thread
	        pool.execute(orderProcess); 
		}
		
		waitForCalcsToComplete(orders);
	}
	
	/**
	 * Process orders using Scala Actors
	 * @param orders
	 * @throws Exception
	 */
	public void functionalProcessing(List<Order> orders) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("\n");
			log.debug("********************************************************");
			log.debug("Started functional processing of batch containing " 
					+ orders.size() + " orders");
		}
		
		FunctionalManager functionalManager = (FunctionalManager) 
				applicationContext.getBean("functionalManager");
		
		functionalManager.start();
		
		for (Order order : orders) {
				
			functionalManager.$bang(order);
		}
		
		waitForCalcsToComplete(orders);
	}

	/**
	 * Process orders using Akka Actors
	 * @param orders
	 * @throws InterruptedException 
	 * @throws Exception
	 */
	public void akkaProcessing(List<Order> orders) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("\n");
			log.debug("********************************************************");
			log.debug("Started akka processing of batch containing " 
					+ orders.size() + " orders");
		}
		
		for (Order order : orders) {
			router.tell(order);
		}
		
		waitForCalcsToComplete(orders);
	}

	private void waitForCalcsToComplete(List<Order> orders)
			throws InterruptedException {
		for (Order order : orders) {
			order.waitForCalcToFinish();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
		router  = (ActorRef) applicationContext.getBean("akkaManager");
	}

}