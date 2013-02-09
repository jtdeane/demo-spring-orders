package cogito.online.actors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import cogito.online.model.Item;
import cogito.online.model.Order;
import cogito.online.model.Orders;

/**
 * Mapp the Item Types within an batch
 * @author jeremydeane
 */
public final class MapItemsActor extends UntypedActor {
	
	//injected by Spring
	private ActorRef reduceItemsActor;
	
	private final Logger log = Logger.getLogger(this.getClass());	

	@Override
	public void onReceive(Object message) throws Exception {
		
		//Map the item types within a batch of orders
		if (message instanceof Orders) {
			
			List<Order> orders = ((Orders) message).getOrders();
			
			List<Item> itemsList = new ArrayList<Item>();
			
			for (Order order : orders) {
				
				itemsList.add(new Item(order.getItem(), new Integer(1)));	
			}
			
			logItemList(itemsList);
			
			//reduce the items types
			reduceItemsActor.sendOneWay(itemsList);
			
		} else {
			
			//trigger unhandled event...
			unhandled(message);
		}
	}
	
	/**
	 * Log the mapped Item Types
	 * @param itemsList
	 */
	private void logItemList (List<Item> itemsList) {
		
		log.debug("Mapped Item Types \n");
		
		for (Item item : itemsList) {
			
			log.debug(item.toString());
		}
	}
	
	public void setReduceItemsActor(ActorRef reduceItemsActor) {
		this.reduceItemsActor = reduceItemsActor;
	}	
}