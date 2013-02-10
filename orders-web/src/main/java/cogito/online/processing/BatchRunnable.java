package cogito.online.processing;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import cogito.online.model.ProcessedBatch;

/**
 * Processs a batch in a new thread
 * @author jeremydeane
 */
public class BatchRunnable implements Runnable {
	
	private final Logger log = Logger.getLogger(this.getClass());

	//set at runtime
	private final ProcessedBatch processedBatch;
	
	//injected by spring
	private Cache statusCache;
	private Cache batchCache;
	private BatchServices batchServices;
	

	public BatchRunnable (ProcessedBatch processedBatch) {
		this.processedBatch = processedBatch;
	}		
	
	@Override
	public void run() {		
		
		try {
			
			log.debug("Asynchronous " + processedBatch.toString());
			
			double batchTotal = batchServices.akkaActorForkJoin
					(processedBatch.getOrders());
			
			batchCache.put(new Element(processedBatch.getBatchId(), 
					new Double(batchTotal)));
			
			//mock wait time - 5 seconds
			Thread.sleep(5000);
			
			statusCache.put(new Element (processedBatch.getResourceStatusKey(),
					processedBatch.getBatchId()));
			
		} catch (Exception e) {
			
			throw new RuntimeException("Failed to process batch asynchronously");
		}
	}
	
	public void setStatusCache(Cache statusCache) {
		this.statusCache = statusCache;
	}
	
	public void setBatchCache(Cache batchCache) {
		this.batchCache = batchCache;
	}

	public void setBatchServices(BatchServices batchServices) {
		this.batchServices = batchServices;
	}
}