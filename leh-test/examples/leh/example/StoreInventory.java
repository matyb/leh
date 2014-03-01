package leh.example;

import java.util.List;
import java.util.Map;

import leh.example.food.FoodInventory;

public class StoreInventory {

	private Map<Shipment, List<FoodInventory>> shipments;

	public Map<Shipment, List<FoodInventory>> getShipments() {
		return shipments;
	}

	public void setShipments(Map<Shipment, List<FoodInventory>> shipments) {
		this.shipments = shipments;
	}
	
}
