package leh.example;

import leh.annotations.Identity;
import leh.util.LEHAware;

public class Shipment implements LEHAware {

	@Identity(true)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
}
