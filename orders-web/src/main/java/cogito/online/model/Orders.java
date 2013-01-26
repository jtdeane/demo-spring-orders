package cogito.online.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Batch of Orders
 * @author jeremydeane
 */
@XmlRootElement(name="orders")
@XmlAccessorType(XmlAccessType.NONE)
public final class Orders {	
	
	@XmlAttribute
	private final String batchId;
	
	@XmlAttribute
	private final String date;		
	
	@XmlElement (name="order")	
	private final List<Order> orders;
	
	/**
	 * Default Constructor
	 * @param batchId
	 * @param date
	 * @param orders
	 */
	@JsonCreator
	public Orders (@JsonProperty("batchId") String batchId, 
			@JsonProperty("date") String date, 
			@JsonProperty("orders")List<Order> orders) {
		this.batchId=batchId;
		this.date=date;
		this.orders = orders;
	}	
	
	/**
	 * Do not use - No-arg required by JAXB
	 */
    @SuppressWarnings("unused")
	private Orders() {
	    this (null, null, null);
	}	

	@Override
	public String toString() {
		
		StringBuffer output = new StringBuffer("Batch ID ");
		output.append(batchId);
		output.append(" ");
		output.append(date);
		output.append(" order size: ");
		output.append(orders.size());
		
		return output.toString();
	}

	public String getBatchId() {
		return batchId;
	}

	public String getDate() {
		return date;
	}

	public List<Order> getOrders() {
		return orders;
	}
	
}