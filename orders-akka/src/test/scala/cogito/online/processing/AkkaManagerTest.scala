package cogito.online.processing

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.dispatch.Await
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout
import org.scalatest.fixture.NoArgTestWrapper
import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterAll
import akka.testkit.TestProbe
import cogito.online.model.Order
import org.apache.log4j.BasicConfigurator

object AkkaManagerSpec {
   class EchoActor extends Actor {
    def receive = {
      case x ⇒ sender ! x
    }
  }
}

class AkkaManagerSpec extends WordSpec 
	with BeforeAndAfterAll {
  
  import AkkaManagerSpec._
  
  val _system = ActorSystem("MyTest")
  BasicConfigurator.configure()
  val prices = new java.util.HashMap[String,String]()
  val discounts = new java.util.HashMap[String,String]()
  prices.put("item","1");
  discounts.put("item","0.1");
  val order = new Order("id","cust","item","10")
  
  override def afterAll() = {
    _system.shutdown()
  }
  
 "An akka manager" must {
   "respond with the information logged" in {
     
     val probe = TestProbe()(_system)
     val actorRef = TestActorRef(new AkkaManager(prices,discounts))(_system)
     
     (actorRef ! order) (probe.ref)
     probe.expectMsgClass(classOf[Tuple5[Order,Double,Double,Double,Double]]) //((order,1.0,0.1,10.0,9.0))
   }
   
   "correctly perform calculations" in {

     val probe = TestProbe()(_system)
     val actorRef = TestActorRef(new AkkaManager(prices,discounts))(_system)
     
     (actorRef ! order) (probe.ref)
     probe.expectMsg((order,1.0,0.1,10.0,9.0))
     
   }
 }
  
}