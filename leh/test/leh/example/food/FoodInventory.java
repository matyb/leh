package leh.example.food;

import leh.util.Entity;
import leh.util.LEH;

public class FoodInventory extends Food implements Entity {

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
		return LEH.getInstance().isEqual(this, obj);
	}
	
	@Override
	public int hashCode() {
		return LEH.getInstance().getHashCode(this);
	}
	
	@Override
	public String toString(){
		return LEH.getInstance().getToString(this);
	}
	
}
