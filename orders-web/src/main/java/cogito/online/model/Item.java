package cogito.online.model;

/**
 * An instance of an Item Type within an <code>Order</code>
 * @author jeremydeane
 */
public final class Item {
	
	private final String type;
	private final Integer count;
	
	public Item (String type, Integer count) {
		
		this.type = type;
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public Integer getAmount() {
		return count;
	}

	@Override
	public String toString() {
		
		return ("Item Type " + type + " Count " + count);
	}
	
	
}