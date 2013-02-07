package cogito.online.processing;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import cogito.online.model.Order;

/**
 * <code>OrderCallable</code> is used to process a future value of an order
 * @author jeremydeane
 * @version 1.0
 */
public class OrderCallable implements Callable<Double> {
	
	//set at runtime
	private final Order order;
	
	//injected by Spring
	private HashMap<String, String> discounts;
	private HashMap<String, String> prices;

	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Default Constructor
	 * @param order
	 * @param discounts
	 */
	public OrderCallable (Order order) {
		this.order = order;
	}
	
	/**
	 * Process the order
	 */
	public Double call() throws Exception {
		
		//method variables
		String price = null;
		String discount = null;
		Double charged = null;
		
		price = (String)prices.get(order.getItem());
		
		//calculate the base charge
		Double subTotal = order.getAmount() * (new Double(price));
		
		//apply discount
		if (discounts.containsKey(order.getItem())) {
			
			discount = (String) discounts.get(order.getItem());
			
			charged = subTotal * (1 - (new Double(discount).doubleValue()));
		
		} else {
			
			charged = subTotal;
		}
		
		//mock resource processing time
		order.mockResourceProcessingTime();
		
		//log final charge
		if (log.isDebugEnabled()) {
			
			logJavaOrder(price, discount, charged, subTotal);
		}
		
		return subTotal;
	}
	
	/**
	 * Log the processed order information
	 * @param price
	 * @param discount
	 * @param charged
	 * @param subTotal
	 */
	private void logJavaOrder(String price, String discount,
			Double charged, Double subTotal) {
		
		StringBuffer output = new StringBuffer("ORD ");
		output.append(order.getId());
		output.append(" PRI $" + price);
		output.append(" ST $" + subTotal);
		if (discount != null) {
			output.append(" DISC " + discount + "%");
		}
		output.append(" T $" + charged.intValue());
		
		log.debug(output.toString());
	}
	
	/* Used by Spring to Inject Prices and Discounts */
	public void setDiscounts(HashMap<String, String> discounts) {
		this.discounts = discounts;
	}

	public void setPrices(HashMap<String, String> prices) {
		this.prices = prices;
	}
}