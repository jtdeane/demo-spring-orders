package cogito.online.actors;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

import akka.actor.TypedActor;
import akka.dispatch.Future;
import cogito.online.model.Order;
import cogito.online.model.ProcessedOrder;

/**
 * Implementation for Discount Typed Actor
 * @author jeremydeane
 */
public final class DiscountTypedActorImpl extends TypedActor implements DiscountTypedActor {
	
	//injected by Spring
	private HashMap<String, String> discounts;
	
	private final DecimalFormat df = new DecimalFormat("#%");
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Calculate the discount amount of an order
	 * @param processedOrder
	 */
	public void calculateDiscountAmount (ProcessedOrder processedOrder) {
		
		Double discountedSubTotal = new Double(0);
		
		//apply discount
		if (discounts.containsKey(processedOrder.getOrder().getItem())) {
			
			String discount = (String) discounts.get(processedOrder.getOrder().getItem());
			
			discountedSubTotal = processedOrder.getAmountSubTotal() * 
					(1 - (new Double(discount).doubleValue()));
		
		} else {
			
			discountedSubTotal = processedOrder.getAmountSubTotal();
		}
		
		//mock resource processing time
		processedOrder.getOrder().mockResourceProcessingTime(2);
		
		ProcessedOrder finalProcessedOrder = new ProcessedOrder.Builder().
				withOrder(processedOrder.getOrder()).
				withAmountSubTotal(processedOrder.getAmountSubTotal()).
				withDiscountedSubTotal(discountedSubTotal).build();
		
		/*
		 * We could go to another step in the pipeline (e.g. for persistance)
		 * but we are just going to log the discounted amount
		 */
		log.debug(finalProcessedOrder.toString());
		
	}
	
	/**
	 * Lookup the discount for a particular item
	 * @param order
	 * @return Future<Double>
	 */
	public Future<Double> lookupDiscount (Order order) {
		
		Double discount = new Double(0);
		
		//mock resource processing time
		order.mockResourceProcessingTime(2);

		if (discounts.containsKey(order.getItem())) {
			
			discount = new Double ((String) discounts.get(order.getItem()));
		}
		
		log.debug("Order ID: " + order.getId() + " Discount: " + df.format(discount));
		
		return future(discount);
	}	
	
	public void setDiscounts(HashMap<String, String> discounts) {
		this.discounts = discounts;
	}
}