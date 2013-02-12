package cogito.online.actors;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import akka.actor.TypedActor;
import akka.dispatch.Future;
import cogito.online.model.Order;
import cogito.online.model.ProcessedOrder;

public final class PriceTypedActorImpl extends TypedActor implements PriceTypedActor {
	
	//injected by Spring
	private HashMap<String, String> prices;
		
	
	private final Logger log = Logger.getLogger(this.getClass());	
	
	/**
	 * Calculate the order amount; context used to pass on calculated amount
	 * to next Actor in the pipeline
	 * @param order
	 * @param context
	 */
	public void calculateAmount (Order order, ApplicationContext context) {
		
		String price = (String)prices.get(order.getItem());
		
		//calculate the base charge
		Double amountSubTotal = order.getAmount() * (new Double(price));
		
		//mock resource processing time
		order.mockResourceProcessingTime(4);
		
		ProcessedOrder processedOrder = new ProcessedOrder.Builder().
				withOrder(order).withAmountSubTotal(amountSubTotal).build();
		
		log.debug(processedOrder.toString());
		
		//send to the next actor
		DiscountTypedActor discountTypedActor = 
				(DiscountTypedActor) context.getBean("discountActor");
		
		
		discountTypedActor.calculateDiscountAmount(processedOrder);
	}
	
	/**
	 * Caclulate and return the order amount
	 * @param order
	 * @return Future<Double>
	 */
	public Future<Double> calculateAndReturnAmount (Order order) {
		
		String price = (String)prices.get(order.getItem());
		
		//calculate the base charge
		Double amountSubTotal = order.getAmount() * (new Double(price));
		
		//mock resource processing time
		order.mockResourceProcessingTime(4);
		
		StringBuffer output = new StringBuffer("Order ID: ");
		output.append(order.getId());
		output.append(" Amount SubTotal: $");
		output.append(amountSubTotal);
		
		log.debug(output.toString());
		
		return future(amountSubTotal);
	}	

	public void setPrices(HashMap<String, String> prices) {
		this.prices = prices;
	}
}