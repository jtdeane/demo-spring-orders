package cogito.online.actors;

import akka.dispatch.Future;
import cogito.online.model.Order;
import cogito.online.model.ProcessedOrder;

/**
 * Interface for Discount Typed Actor
 * @author jeremydeane
 */
public interface DiscountTypedActor {

	/**
	 * Calculate the discount amount of an order
	 * @param processedOrder
	 */
	public void calculateDiscountAmount(ProcessedOrder processedOrder);

	/**
	 * Lookup the discount for a particular item
	 * @param order
	 * @return Future<Double>
	 */
	public Future<Double> lookupDiscount(Order order);
}