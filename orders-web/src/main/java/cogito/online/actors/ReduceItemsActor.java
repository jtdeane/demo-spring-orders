package cogito.online.actors;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import akka.actor.UntypedActor;
import cogito.online.model.Item;

/**
 * Reduce a list of item types to sum thier frequency
 * @author jeremydeane
 */
public final class ReduceItemsActor extends UntypedActor {
	
	private final Logger log = Logger.getLogger(this.getClass());	

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object message) throws Exception {
		
		if (message instanceof List) {
				
			ConcurrentHashMap<String, Integer> reducedItemsMap = 
					new ConcurrentHashMap<String, Integer>();
						
			for (Item item : (List<Item>)message) {
				
				String itemType = item.getType();
				
				if (reducedItemsMap.containsKey(itemType)) {
				
					Integer itemTypeCount = (Integer) reducedItemsMap.get(itemType);

					itemTypeCount++;
					
					reducedItemsMap.put(itemType, itemTypeCount);
				
				} else {
					
					reducedItemsMap.putIfAbsent(itemType, Integer.valueOf(1));
				}		
			}
			
			Map<String, Integer> sortedMap = 
					new TreeMap<String, Integer>(reducedItemsMap);
			
			logReducedItemTypes(sortedMap);
			
		} else {
			
			//trigger unhandled event...
			unhandled(message);
		}
	}
	
	/**
	 * Log the reduced item types
	 * @param itemsMap
	 */
	private void logReducedItemTypes (Map<String, Integer> itemsMap) {
		
		log.debug("Reduce Item Types - Frequency Summary");
		log.debug(itemsMap.toString());
	}
}