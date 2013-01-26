package cogito.online.processing

import scala.actors._, Actor._
import scala.reflect._
import scala.collection._
import collection.jcl.Conversions._
import java.util.List
import org.apache.log4j.Logger;
import cogito.online.model.Order



//complements of The Uncarved Blog
trait LogHelper {
    val loggerName = this.getClass.getName
    lazy val logger = Logger.getLogger(loggerName)
}

class FunctionalManager(jPrices:java.util.Map[String, String], 
	jDiscounts:java.util.Map[String, String]) extends Actor with LogHelper {
	
	val prices:mutable.Map[String, String] = jPrices
	val discounts:mutable.Map[String, String] = jDiscounts

	implicit def javaToScalaDouble(d: java.lang.Double) = d.doubleValue
	
	def act() {
		loop {
			receive {
        		case order: Order => processOrder(order)
			}
		}
	}
	
	def processOrder (order:Order):Unit = {
		
		//get the price and calculate sub-total
		val price = prices.getOrElse(order.getItem(), "0").toDouble;
		
		val subTotal = order.getAmount().doubleValue() * price
		
		//get the discount and apply
		val discount = discounts.getOrElse(order.getItem(), "0").toDouble;
		
		//mock resource processing time
		order.mockResourceProcessingTime();
		
		if (discount > 0) {
			
			val percentage = 1 - discount
			
			logScalaOrder(order, price, discount, 
				subTotal, subTotal*percentage)
		
		} else {
			
			logScalaOrder(order, price, discount, 
				subTotal, subTotal)
		}
	}
	
	def logScalaOrder(order: Order, price: Double, discount: Double, 
		subTotal: Double, charged: Double) = {

			var output = "ORD " + order.getId() 
			
			output = output + (" PRI $" + price.intValue())
			output = output + (" ST $" + subTotal.intValue())
			 
			if (discount > 0) {
				output = output + (" DISC " + discount + "%")
			}
			
			output = output + (" T $" + charged.intValue())	

			logger.debug(output)
	}
} 