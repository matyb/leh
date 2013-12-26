package leh.example;

import leh.annotations.Identity;
import leh.util.Entity;

public class Shipment implements Entity {

	@Identity(true)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
}
