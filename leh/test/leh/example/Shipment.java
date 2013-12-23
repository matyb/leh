package leh.example;

import leh.annotations.Entity;
import leh.annotations.Identity;

@Entity
public class Shipment {

	@Identity(true)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
}
