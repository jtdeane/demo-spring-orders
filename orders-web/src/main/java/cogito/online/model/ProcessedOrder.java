package cogito.online.model;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * Captures information about an processed order. Leverages Builder pattern
 * for construction
 * @author jeremydeane
 */
public final class ProcessedOrder implements Serializable {
	
	private static final long serialVersionUID = 2L;	
	
	private final Order order;
	private final Double amountSubTotal;
	private final Double discountedSubTotal;
	
	private ProcessedOrder(Builder builder) {
	  this.order = builder.order;
	  this.amountSubTotal = builder.amountSubTotal;
	  this.discountedSubTotal = builder.discountedSubTotal;
	}
	
	public static class Builder{

		private Order order;
		private Double amountSubTotal;
		private Double discountedSubTotal;
		
		public Builder withOrder(Order order) {
		  this.order = order;
		  return this;
		}
		
		public Builder withAmountSubTotal(Double amountSubTotal) {
		  this.amountSubTotal = amountSubTotal;
		  return this;
		}
		
		public Builder withDiscountedSubTotal(Double discountedSubTotal) {
		  this.discountedSubTotal = discountedSubTotal;
		  return this;
		}

		public ProcessedOrder build() {
		  validate();
		  return new ProcessedOrder(this);
		}
		
		private void validate() {
		  Preconditions.checkNotNull(order, "order may not be null");
		}
	}
	
	public Order getOrder() {
		return order;
	}
	
	public Double getAmountSubTotal() {
		return amountSubTotal;
	}
	
	public Double getDiscountedSubTotal() {
		return discountedSubTotal;
	}
	
	@Override
	public String toString() {
		
		StringBuffer output = new StringBuffer("Order ID: ");
		output.append(order.getId());
		output.append(" Amount SubTotal: $");
		
		if (amountSubTotal != null) {
			
			output.append(amountSubTotal);
		}
		
		output.append(" Discounted SubTotal: $");
		
		if (discountedSubTotal != null) {
			
			output.append(discountedSubTotal);
		}
		
		return output.toString();
	}	
}