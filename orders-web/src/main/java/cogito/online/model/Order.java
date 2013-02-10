package cogito.online.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;


@XmlRootElement(name="order")
@XmlAccessorType(XmlAccessType.NONE)
public final class Order implements Comparable<Order>, Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	private final String id;
	
	@XmlAttribute
	private final String customer;
	
	@XmlAttribute
	private final String item;
	
	@XmlAttribute
	private final Integer amount;
	
	/**
	 * Default Constructor - Note the hints to the Jackson JSON Mapper.
	 * JAXB will use no arg constructor. However, the @ symbol is used
	 * to make compatible with JAXB transformations used within Jersey.
	 * @param id
	 * @param customer
	 * @param item
	 * @param amount
	 * @throws Exception
	 */
	@JsonCreator
	public Order (@JsonProperty("id") String id, 
			@JsonProperty("customer") String customer, 
			@JsonProperty("item") String item, 
			@JsonProperty("amount") String amount) throws Exception {
		
		if (amount != null) {
			
			this.amount = new Integer(amount);
		
		} else {
			
			this.amount = new Integer(0);
		}
				
		this.item = item;
		this.id = id;
		this.customer = customer;
	}
	
	/**
	 * Do not use - No-arg required by JAXB
	 */
    @SuppressWarnings("unused")
	private Order() throws Exception{
	    this (null, null, null, null);
	}	
	
	/**
	 * Mock resource processing time
	 * @param factor
	 */
	public void mockResourceProcessingTime(int factor) {
		
		if (amount <= 40) {
			fibonacci(amount*factor);
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
	
	@Override
	public int compareTo(Order that) {
		return ComparisonChain.start()
				.compare(this.id, that.id)
				.compare(this.customer, that.customer)
				.compare(this.item, that.item)
				.compare(this.amount, that.amount)
				.result();
	}	

	@Override
	public boolean equals(Object obj) {
		Order that = (Order) obj;
		return Objects.equal(this.hashCode(), that.hashCode());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id + customer + item + amount);
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

	public Integer getAmount() {
		return amount;
	}
}