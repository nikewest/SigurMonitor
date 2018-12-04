package sigur;

import java.util.Properties;

import javax.swing.ImageIcon;

public interface SigurDAO {
	
	boolean getVisitorsFromServer(Properties syncSettings);	
	String getVisitorName(int id);
	ImageIcon getVisitorPhoto(int id);
	
}
