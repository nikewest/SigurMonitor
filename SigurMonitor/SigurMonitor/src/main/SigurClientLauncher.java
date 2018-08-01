package main;

import impl.SigurConnectionProviderImpl;
import impl.SigurDAOImpl;
import impl.SigurEventHandlerImpl;
import impl.SigurSettingsManagerImpl;
import sigur.SigurClient;
import sigur.SigurConnectionProvider;
import sigur.SigurDAO;
import sigur.SigurEventHandler;
import sigur.SigurSettingsManager;

public class SigurClientLauncher {
	
	public static void main(String[] args) {
		SigurClient sigurClient = SigurClient.getInstance();
		SigurConnectionProvider connectionProvider = SigurConnectionProviderImpl.getIntance();
		SigurSettingsManager settingsManager = SigurSettingsManagerImpl.getInstance();
		SigurEventHandler eventHandler = SigurEventHandlerImpl.getInstance();
		SigurDAO dao = SigurDAOImpl.getInstance();
		
		sigurClient.startClient(connectionProvider, settingsManager, eventHandler, dao);
	}	

}
