package cogito.online.processing

import akka.actor.Actor
import akka.actor.Props
import scala.reflect._
import scala.collection._
import scala.collection.JavaConversions._
import org.apache.log4j.Logger
import cogito.online.model.Order
import akka.actor.ActorSystem
import akka.routing.SmallestMailboxRouter



object AkkaManager {
  val system = ActorSystem("MySystem")
  
  def newRouter(jPrices:java.util.Map[String, String], jDiscounts:java.util.Map[String, String]) = {
	  system.actorOf(Props(new AkkaManager(jPrices,jDiscounts)).withRouter(SmallestMailboxRouter(20)))
  }
  
}

//complements of The Uncarved Blog
trait LogHelper {
    val loggerName = this.getClass.getName
    lazy val logger = Logger.getLogger(loggerName)
}

class AkkaManager(jPrices:java.util.Map[String, String], 
	jDiscounts:java.util.Map[String, String]) extends Actor with LogHelper {
	
	val prices:Map[String, String] = jPrices
	val discounts:Map[String, String] = jDiscounts

	implicit def javaToScalaDouble(d: java.lang.Double) = d.doubleValue
	
	def receive() = {
		case order: Order => processOrder(order)
		case m => unhandled(m)
	}
	
	def processOrder (order:Order):Unit = {
		
		//get the price and calculate sub-total
		val price = prices.getOrElse(order.getItem(), "0").toDouble;
		
		val subTotal = order.getAmount() * price
		
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
		order.finishCalc();
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
			
			sender ! (order,price,discount,subTotal,charged)
	}
} 