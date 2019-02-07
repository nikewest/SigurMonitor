package sigur;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SigurClient{
	
	private static final Logger logger = LogManager.getLogger(SigurClient.class.getName());

	private SigurServerListenerThread workerThread;
	
	long heapSize;
	long heapMaxSize;
	long heapFreeSize;
	
	private class SigurServerListenerThread extends Thread {
		@Override
		public void run() {
					
			heapSize = Runtime.getRuntime().totalMemory();
			heapMaxSize = Runtime.getRuntime().maxMemory();
			heapFreeSize = Runtime.getRuntime().freeMemory();
			logger.info(String.format("Heap: %d / %d / %d", heapSize, heapMaxSize, heapFreeSize));
			logger.info("test");
			
			if(!dao.getVisitorsFromServer(settingsManager.getSyncSettings())) {
				stopClient();
			}						
			
			if (connectToServer()) {
				String response = null;
				while (isRunning.get()) {
					try {
						response = connectionProvider.readMessageFromServer();
					} catch (IOException ioe) {
						
						logger.error(ioe.toString());
						
						//check heap
						heapSize = Runtime.getRuntime().totalMemory();
						heapMaxSize = Runtime.getRuntime().maxMemory();
						heapFreeSize = Runtime.getRuntime().freeMemory();
						
						logger.info(String.format("Heap: %d / %d / %d", heapSize, heapMaxSize, heapFreeSize));						
						
						if (!connectToServer()) {
							// can't connect to server
							isRunning.set(false);
							break;
						}
					}
					if (response != null) {						
						eventHandler.showMessage(new SigurEvent(response));
					}
				}
			}
			stopClient();
		}
	}

	private static SigurClient instance;
	
	private SigurConnectionProvider connectionProvider;
	private SigurEventHandler eventHandler;
	private SigurDAO dao;
	private SigurSettingsManager settingsManager;
	
	private volatile AtomicBoolean isRunning = new AtomicBoolean(false);
	
	private SigurClient(){}
	
	public static synchronized SigurClient getInstance() {
		
		if(instance==null) {
			instance = new SigurClient();
		}
		return instance;
		
	}		
	
	public synchronized void startClient(SigurConnectionProvider connectionProvider, SigurSettingsManager settingsManager, SigurEventHandler eventHandler, SigurDAO dao) {
		
		this.connectionProvider = connectionProvider;
		this.settingsManager = settingsManager;
		this.eventHandler = eventHandler;
		this.dao = dao;
		
		if(isRunning.get()) {
			return;
		}
		
		isRunning.set(true);
		
		eventHandler.initialize(settingsManager.getEventHandlerSettings());
		
		workerThread = new SigurServerListenerThread();
		workerThread.start();
		
	}
	
	public synchronized void stopClient() {
		isRunning.set(false);
		connectionProvider.freeResources();
		eventHandler.close();
	}
		
	private boolean connectToServer() {
		if(connectionProvider.connectToServer(settingsManager.getConnectionSettings())) {			
			return true;			
		};
		return false;
		
	}
	
	public SigurDAO getSigurDAO() {
		return dao;
	}
	
}
