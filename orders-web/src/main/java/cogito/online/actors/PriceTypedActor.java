package cogito.online.actors;

import org.springframework.context.ApplicationContext;

import akka.dispatch.Future;
import cogito.online.model.Order;

/**
 * Interface for Price Typed Actor
 * @author jeremydeane
 */
public interface PriceTypedActor {

	/**
	 * Calculate the order amount; context used to pass on calculated amount
	 * to next Actor in the pipeline
	 * @param order
	 * @param context
	 */
	public void calculateAmount(Order order, ApplicationContext context);

	/**
	 * Caclulate and return the order amount
	 * @param order
	 * @return Future<Double>
	 */
	public Future<Double> calculateAndReturnAmount(Order order);
}