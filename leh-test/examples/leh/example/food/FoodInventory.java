package leh.example.food;

import leh.util.LEHAware;
import leh.util.LEH;

public class FoodInventory extends Food implements LEHAware {

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
		return LEH.getInstance().getEquals(this).equals(obj);
	}
	
	@Override
	public int hashCode() {
		return LEH.getInstance().getHashCode(this).hashCode();
	}
	
	@Override
	public String toString(){
		return LEH.getInstance().getToString(this).toString();
	}
	
}
