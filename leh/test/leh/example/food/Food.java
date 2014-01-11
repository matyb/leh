package leh.example.food;

import leh.annotations.Identity;
import leh.util.LEHAware;

public class Food implements LEHAware {

	public enum FoodType {PIZZA, TACO};
	
	private int calories;
	private int sodium;
	@Identity(true)
	private FoodType type;
	
	public FoodType getType() {
		return type;
	}
	public void setType(FoodType type) {
		this.type = type;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public int getSodium() {
		return sodium;
	}
	public void setSodium(int sodium) {
		this.sodium = sodium;
	}	
	
}
