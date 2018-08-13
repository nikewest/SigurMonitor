package impl;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import sigur.SigurSettingsManager;

public class SigurSettingsManagerImpl implements SigurSettingsManager{

	private static SigurSettingsManagerImpl instance;
	private Properties properties = new Properties();
	
	public static final String FULLSCREEN_USAGE_PROPERTY_KEY = "fullscreenMode";
	
	public static final String MAIN_SCREEN_TEXT = "mainScreenText";
	public static final String SUCCESS_ENTER_TEXT = "successEnterText";
	public static final String FAIL_FACE_SCAN_TEXT = "failFaceScanText";
	public static final String FAIL_WRONG_CODE_TEXT = "failWrongCodeText";
	public static final String FAIL_EXPIRED_TEXT = "failExpiredText";
	public static final String FAIL_TIME_LIMIT_TEXT = "failTimeLimitText";
		
	public static final String CONNECTION_ATTEMPTS_PROPERTY_KEY = "connectionAttempts";
	public static final String CONNECTION_TIMEOUT_PROPERTY_KEY = "connectionTimeout";
	public static final String SERVER_PORT_PROPERTY_KEY = "serverPort";
	public static final String SERVER_ADDRESS_PROPERTY_KEY = "serverAddress";
	
	public static final String ALLOWED_DIRECTION_PROPERTY_KEY = "allowedDirection";
	public static final String ALLOWED_SENDERS_RPOPERTY_KEY = "allowedSenders";	
	public static final String SIGUR_USER_PROPERTY_KEY = "sigurUser";	
	public static final String SIGUR_PWD_PROPERTY_KEY = "sigurPwd";
	
	private SigurSettingsManagerImpl(){
		
		FileInputStream fis = null;
		// loading main settings
		try {
			fis = new FileInputStream("config.properties");
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {

				}
			}
		}

		loadTextFromFile(MAIN_SCREEN_TEXT, "mainscreen.text");
		loadTextFromFile(SUCCESS_ENTER_TEXT, "successenter.text");
		loadTextFromFile(FAIL_FACE_SCAN_TEXT, "failfacescan.text");
		loadTextFromFile(FAIL_WRONG_CODE_TEXT, "failwrongcode.text");
		loadTextFromFile(FAIL_EXPIRED_TEXT, "failexpired.text");
		loadTextFromFile(FAIL_TIME_LIMIT_TEXT, "failtimelimit.text");
		
	}
	
	private void loadTextFromFile(String settingsName, String fileName) {
		StringBuilder sb = new StringBuilder();
		try (FileReader fr = new FileReader(fileName)) {
			char[] cbuf = new char[256];
			int l;

			while ((l = fr.read(cbuf)) > 0) {
				if (l < 256) {
					cbuf = Arrays.copyOf(cbuf, l);
				}
				sb.append(cbuf);
			}
		} catch (IOException e) {
			//
		}
		properties.setProperty(settingsName, sb.toString());
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
		
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.MAIN_SCREEN_TEXT, properties.getProperty(SigurSettingsManagerImpl.MAIN_SCREEN_TEXT, ""));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.SUCCESS_ENTER_TEXT, properties.getProperty(SigurSettingsManagerImpl.SUCCESS_ENTER_TEXT, ""));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.FAIL_FACE_SCAN_TEXT, properties.getProperty(SigurSettingsManagerImpl.FAIL_FACE_SCAN_TEXT, ""));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.FAIL_WRONG_CODE_TEXT, properties.getProperty(SigurSettingsManagerImpl.FAIL_WRONG_CODE_TEXT, ""));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.FAIL_EXPIRED_TEXT, properties.getProperty(SigurSettingsManagerImpl.FAIL_EXPIRED_TEXT, ""));
		eventHandlerProperties.setProperty(SigurSettingsManagerImpl.FAIL_TIME_LIMIT_TEXT, properties.getProperty(SigurSettingsManagerImpl.FAIL_TIME_LIMIT_TEXT, ""));
		return eventHandlerProperties;
	}

	@Override
	public Properties getSyncSettings() {
		Properties syncProperties = new Properties();
		syncProperties.setProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, properties.getProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, ""));		
		return syncProperties;
	}
	
}
