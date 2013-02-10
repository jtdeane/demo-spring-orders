package cogito.online.model;

import java.io.Serializable;
import java.util.List;

/**
 * Batch information for asynchronous processing
 * @author jeremydeane
 */
public final class ProcessedBatch implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	private final String batchId;
	private final String resourceStatusKey;
	private final List<Order> orders;

	public ProcessedBatch (String batchId, String resourceStatusKey, 
			List<Order> orders) {
		
		this.batchId = batchId;
		this.resourceStatusKey = resourceStatusKey;
		this.orders = orders;
	}
	
	public String getBatchId() {
		return batchId;
	}
	
	public String getResourceStatusKey() {
		return resourceStatusKey;
	}

	public List<Order> getOrders() {
		return orders;
	}
	
	@Override
	public String toString() {
		
		StringBuffer output = new StringBuffer ("Processing Batch Data: \n");
		output.append("Batch ID: ");
		output.append(batchId);
		output.append("\n");
		output.append("Resource Status Key: ");
		output.append(resourceStatusKey);
		output.append("\n");
		output.append("Order Size: ");
		output.append(orders.size());
		
		return output.toString();
	}	
}