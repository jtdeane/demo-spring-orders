package cogito.online.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import cogito.online.model.Order;

/**
 * Processes a batch of orders using Java and Akka Actors
 * @author jeremydeane
 */
@Service
public class BatchServices implements ApplicationContextAware {
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	private ApplicationContext applicationContext;
	
    /*
     * Thread pool Acts as a throttle preventing out of memory exception
     */
    private final Executor pool = new ThreadPoolExecutor(10, 50, Long.MAX_VALUE, 
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2000), 
            new ThreadPoolExecutor.DiscardOldestPolicy());
    
    /*
     * Equation compliments of Venkat Subramaniam - 
     * Programming Concurrency on the JVM
     */  
    final int numberOfCores = Runtime.getRuntime().availableProcessors();
    final double blockingCoefficient = 0.9;
    final int poolSize = (int)(numberOfCores / (1 - blockingCoefficient));
    
    /**
	 * Process orders using a single thread
     * @param orders
     * @throws Exception
     */
	public void singleThreadedProcessing(List<Order> orders) throws Exception {
		
		logProcessingStart("Single Threaded", orders.size());		
		
		for (Order order : orders) {
			
	        Object[] constructorArguments = {order};
	        
	        OrderRunnable orderRunnable = (OrderRunnable) applicationContext.
	        		getBean("orderRunnable", constructorArguments);
	        
	        //process in same thread
	        orderRunnable.run();
		}
	}    
    
    /**
	 * Process orders using Java Thread Pool - Fire and Forget
     * @param orders
     * @throws Exception
     */
	public void javaFireAndForget(List<Order> orders) throws Exception {
		
		logProcessingStart("Java Fire & Forget", orders.size());
		
		for (Order order : orders) {
			
	        Object[] constructorArguments = {order};
	        
	        OrderRunnable orderRunnable = (OrderRunnable) applicationContext.
	        		getBean("orderRunnable", constructorArguments);
	        
	        //process in new thread
	        pool.execute(orderRunnable); 
		}
	}
	
    /**
	 * Process orders using Java Thread Pool - Fork-Join
     * @param orders
     * @throws Exception
     */
	public double javaForkJoin(List<Order> orders) throws Exception {
		
		//method variable
		double batchTotal = 0.0;
		
		logProcessingStart("Java Fork-Join", orders.size());
		
	    final List<Callable<Double>> callableList = 
	    		new ArrayList<Callable<Double>>();		
		
	    //create a list of order callables - returning sub-totals
		for (Order order : orders) {
			
	        Object[] constructorArguments = {order};
	        
	        OrderCallable orderCallable = (OrderCallable) applicationContext.
	        		getBean("orderCallable", constructorArguments);
	        
	        callableList.add(orderCallable);
		}

	    final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
		
		//get the future value of the order sub-totals
	    final List<Future<Double>> orderSubTotals = executorPool.invokeAll
	    		(callableList, 10000, TimeUnit.SECONDS);
	    
	    //calculate the total batch amount
	    for (Future<Double> orderSubtotal : orderSubTotals) {
		
	    	batchTotal += orderSubtotal.get();
		}
	    
	    executorPool.shutdown();
	    
	    log.debug("Batch Total " + batchTotal);
	    
	    return batchTotal;
	}	
	
	/**
	 * Log start of order processing
	 * @param processingType
	 * @param orderSize
	 */
	private void logProcessingStart (String processingType, int orderSize) {

		log.debug("\n");
		log.debug("********************************************************");
		log.debug("Started " + processingType + " processing of batch containing " 
				+ orderSize + " orders");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;	
	}
}