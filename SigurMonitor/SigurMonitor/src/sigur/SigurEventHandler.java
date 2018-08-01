package sigur;

import java.util.Properties;

public interface SigurEventHandler {
	
	void showMessage(SigurEvent sigurEvent);	
	boolean initialize(Properties eventHandlerProperties);
	void close();
}
