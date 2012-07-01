package cogito.online.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Batch of Orders
 * @author jeremydeane
 */
@XStreamAlias("orders")
public final class Orders {
	
	/**
	 * Default Constructor
	 * @param batchId
	 * @param date
	 * @param orders
	 */
	public Orders (String batchId, String date, List<Order> orders) {
		this.batchId=batchId;
		this.date=date;
		this.orders = orders;
	}
	
	@XStreamAsAttribute
	private final String batchId;
	
	@XStreamAsAttribute
	private final String date;		
	
	@XStreamImplicit(itemFieldName="order")
	private final List<Order> orders;

	@Override
	public String toString() {
		
		StringBuffer output = new StringBuffer("Batch ID ");
		output.append(batchId);
		output.append(" ");
		output.append(date);
		output.append(" order size: ");
		output.append(orders.size());
		
		return output.toString();
	}

	public String getBatchId() {
		return batchId;
	}

	public String getDate() {
		return date;
	}

	public List<Order> getOrders() {
		return orders;
	}
	
}