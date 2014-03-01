package leh.util;

import java.io.Serializable;

public interface Wrapper extends Serializable, LEHAware {

	Object getWrappedInstance();
	
}
