package sigur;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Properties;

public interface SigurConnectionProvider {

	boolean connectToServer(Properties connectionProperties);	
	String readMessageFromServer() throws SocketTimeoutException, IOException;		
	void freeResources();
}
