package sigur;

import java.util.Properties;

public interface SigurDAO {
	
	boolean getVisitorsFromServer(Properties syncSettings);	
	String getVisitorName(int id);
	
}
