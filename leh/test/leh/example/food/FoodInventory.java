package leh.example.food;

import leh.annotations.Entity;
import leh.util.LogicalEqualsHashCode;

@Entity
public class FoodInventory extends Food {

	private long cost;
	private long price;
	
	public long getCost() {
		return cost;
	}
	public void setCost(long cost) {
		this.cost = cost;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
	
	@Override
	public boolean equals(Object obj) {
		return LogicalEqualsHashCode.getInstance().isEqual(this, obj);
	}
	
	@Override
	public int hashCode() {
		return LogicalEqualsHashCode.getInstance().getHashCode(this);
	}
	
	@Override
	public String toString(){
		return LogicalEqualsHashCode.getInstance().getToString(this);
	}
	
}
