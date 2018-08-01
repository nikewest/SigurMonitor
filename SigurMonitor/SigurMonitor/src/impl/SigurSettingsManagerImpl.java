package impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import sigur.SigurSettingsManager;

public class SigurSettingsManagerImpl implements SigurSettingsManager{

	private static SigurSettingsManagerImpl instance;
	private Properties properties = new Properties();
	
	public static final String FULLSCREEN_USAGE_PROPERTY_KEY = "fullscreenMode";
	
	public static final String CONNECTION_ATTEMPTS_PROPERTY_KEY = "connectionAttempts";
	public static final String CONNECTION_TIMEOUT_PROPERTY_KEY = "connectionTimeout";
	public static final String SERVER_PORT_PROPERTY_KEY = "serverPort";
	public static final String SERVER_ADDRESS_PROPERTY_KEY = "serverAddress";
	
	public static final String ALLOWED_DIRECTION_PROPERTY_KEY = "allowedDirection";
	public static final String ALLOWED_SENDERS_RPOPERTY_KEY = "allowedSenders";	
	public static final String SIGUR_USER_PROPERTY_KEY = "sigurUser";	
	public static final String SIGUR_PWD_PROPERTY_KEY = "sigurPwd";
	
	private SigurSettingsManagerImpl(){
		
		FileInputStream fis=null;
		try {
			fis = new FileInputStream("config.properties");
			properties.load(fis);
		} catch (IOException e) {			
			e.printStackTrace();
		} finally {
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					
				}
			}
		}
	}
	
	public static synchronized SigurSettingsManagerImpl getInstance() {
		if(instance == null) {
			instance = new SigurSettingsManagerImpl();
		}
		return instance;
	}
	
	@Override
	public Properties getConnectionSettings() {		
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, ""));
		connectionProperties.setProperty(SigurSettingsManagerImpl.SERVER_PORT_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.SERVER_PORT_PROPERTY_KEY,"0"));
		connectionProperties.setProperty(SigurSettingsManagerImpl.CONNECTION_TIMEOUT_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.CONNECTION_TIMEOUT_PROPERTY_KEY,"0"));
		connectionProperties.setProperty(SigurSettingsManagerImpl.CONNECTION_ATTEMPTS_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.CONNECTION_ATTEMPTS_PROPERTY_KEY,"1"));
		
		connectionProperties.setProperty(SigurSettingsManagerImpl.SIGUR_USER_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.SIGUR_USER_PROPERTY_KEY,""));
		connectionProperties.setProperty(SigurSettingsManagerImpl.SIGUR_PWD_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.SIGUR_PWD_PROPERTY_KEY,""));
		return connectionProperties;
	}

	@Override
	public Properties getEventHandlerSettings() {
		Properties eventHandlerProperties = new Properties();
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.FULLSCREEN_USAGE_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.FULLSCREEN_USAGE_PROPERTY_KEY, "0"));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.ALLOWED_SENDERS_RPOPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.ALLOWED_SENDERS_RPOPERTY_KEY, ""));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.ALLOWED_DIRECTION_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.ALLOWED_DIRECTION_PROPERTY_KEY, "0"));
		return eventHandlerProperties;
	}

	@Override
	public Properties getSyncSettings() {
		Properties syncProperties = new Properties();
		syncProperties.setProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, ""));		
		return syncProperties;
	}
	
}
