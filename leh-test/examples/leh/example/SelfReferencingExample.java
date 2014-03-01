package leh.example;

import leh.annotations.Identity;
import leh.util.LEHAware;

public class SelfReferencingExample implements LEHAware {
	@Identity(true)
	public Object instance;
}
