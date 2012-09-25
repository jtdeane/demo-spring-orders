package cogito.online.model;

import java.util.concurrent.Semaphore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("order")
public final class Order {

	
	@XStreamAsAttribute
	private final String id;
	
	@XStreamAsAttribute
	private final String customer;
	
	@XStreamAsAttribute
	private final String item;
	
	@XStreamAsAttribute
	private final int amount;
	
	private Semaphore calculation = new Semaphore(0);
	
	/**
	 * Default Constructor
	 * @param map
	 */
	public Order (String id, String customer, String item, 
			String amount) throws Exception {
		
		this.amount = new Integer(amount);
		this.item = item;
		this.id = id;
		this.customer = customer;
	}
	
	public void finishCalc() {
		if (calculation != null)
			calculation.release();
		else
			System.out.println("WTF");
	}
	
	public void waitForCalcToFinish() throws InterruptedException {
		calculation.acquire();
	}
	
	private Object readResolve() {
		calculation = new Semaphore(0);
		return this;
	}
	
	/**
	 * Mock resource processing time
	 * @param price
	 */
	public void mockResourceProcessingTime() {
		
		if (amount <= 40) {
			fibonacci(amount*4);
		} else {
			fibonacci(40);
		}
	}
	
	/**
	 * Calculate Fibonacio
	 * @param n
	 * @return int
	 */
	private int fibonacci(int n) {
		if (n < 2) {
			return n;
		} else {
			return fibonacci(n-1)+fibonacci(n-2);
		}
	}


	@Override
	public String toString() {
		
		StringBuffer output = new StringBuffer("Order ID: ");
		output.append(id);
		output.append(" ");
		
		output.append("Customer:");
		output.append(customer);
		output.append(" ");
		
		output.append("Item: ");
		output.append(item);
		output.append(" ");
		
		output.append("Amount: ");
		output.append(amount);
		
		return output.toString();
	}

	public String getId() {
		return id;
	}


	public String getCustomer() {
		return customer;
	}


	public String getItem() {
		return item;
	}


	public int getAmount() {
		return amount;
	}
}