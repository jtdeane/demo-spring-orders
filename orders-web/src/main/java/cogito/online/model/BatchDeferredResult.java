package cogito.online.model;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * Deferred Result of Batch Processing
 * @author jeremydeane
 */
public final class BatchDeferredResult extends DeferredResult<String> {

	private final String batchId;
	private String batchTotal;
	
	public BatchDeferredResult (long timeout, String batchId) {
		
		super (timeout);
		this.batchId = batchId;
	}

	public void setBatchTotal(String batchTotal) {
		this.batchTotal = batchTotal;
	}

	public String getBatchId() {
		return batchId;
	}

	public String getBatchTotal() {
		return batchTotal;
	}
}