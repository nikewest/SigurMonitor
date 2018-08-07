package impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sigur.SigurConnectionProvider;
import sigur.SigurTextProtocol;

public class SigurConnectionProviderImpl implements SigurConnectionProvider {

	private static final Logger logger = LogManager.getLogger(SigurConnectionProviderImpl.class.getName());
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private static SigurConnectionProviderImpl instance;
	
	private SigurConnectionProviderImpl() {		
	}
	
	public static synchronized SigurConnectionProviderImpl getIntance() {
		if(instance==null) {
			instance = new SigurConnectionProviderImpl();
		}
		return instance;
	}
	
	@Override
	public boolean connectToServer(Properties connectionProperties) {
		
		logger.info("connecting to server...");
		
		String serverAddress = connectionProperties.getProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY);
		int serverPort = Integer.parseInt(connectionProperties.getProperty(SigurSettingsManagerImpl.SERVER_PORT_PROPERTY_KEY));
		int connectionTimeOut = Integer.parseInt(connectionProperties.getProperty(SigurSettingsManagerImpl.CONNECTION_TIMEOUT_PROPERTY_KEY));
		int connectionAttempts = Integer.parseInt(connectionProperties.getProperty(SigurSettingsManagerImpl.CONNECTION_ATTEMPTS_PROPERTY_KEY));
				
		while (connectionAttempts > 0 || connectionAttempts == 0) {
			try {
				InetAddress inetAddress = InetAddress.getByName(serverAddress);
				if (inetAddress.isReachable(10 * 1000)) {
					socket = new Socket(InetAddress.getByName(serverAddress), serverPort);
					socket.setSoTimeout(connectionTimeOut * 1000);// server message waiting timeout
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					logger.info("connection success");
					return true;
				}
			} catch (Exception e) {
				//
				logger.error(e.getMessage());
			}
			if (connectionAttempts > 1) {
				connectionAttempts--;
			} else {
				break;
			}
		}

		logger.info("connection failed");
		return false;
	}
	
	@Override
	public boolean loginToServer(Properties connectionProperties) {		
		String answer;
		
		String sigurUser = connectionProperties.getProperty(SigurSettingsManagerImpl.SIGUR_USER_PROPERTY_KEY);
		String sigurPwd = connectionProperties.getProperty(SigurSettingsManagerImpl.SIGUR_PWD_PROPERTY_KEY);			
		
		sendCommandToServer(SigurTextProtocol.getSigurLoginCommand(SigurTextProtocol.VERSION_1_8, sigurUser, sigurPwd));		
		try {
			answer = readMessageFromServer();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;			
		}
		if(!answer.equals(SigurTextProtocol.RESPONSE_SUCCESS_TEXT_1_8)) {			
			return false;
		}
		
		sendCommandToServer(SigurTextProtocol.getSubscribeCommand(SigurTextProtocol.VERSION_1_8));		
		try {
			answer = readMessageFromServer();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		if(!answer.equals(SigurTextProtocol.RESPONSE_SUCCESS_TEXT_1_8)) {
			return false;
		}
		
		return true;
	}
	
	private void sendCommandToServer(String commandText) {
		writer.println(commandText);
		writer.flush();
	}
	
	@Override
	public String readMessageFromServer() throws IOException {
		String message = null;
		try {
			message = reader.readLine();			
		} catch (IOException e) {
			throw e;
		} 
		logger.info("Message from server: " + message);
		return message;		
	}

	@Override
	public void freeResources() {
		if(reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
				// 
				logger.error(e.getMessage());
			}
		}
		if(writer!=null) {
			writer.close();
		}
		if(socket!=null) {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

}
