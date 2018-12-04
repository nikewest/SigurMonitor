package impl;

import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import sigur.SigurClient;
import sigur.SigurEvent;
import sigur.SigurEventHandler;
import ui.MainPanel;

public class SigurEventHandlerImpl implements SigurEventHandler {

	private JFrame jframe;
	private MainPanel mainPanel;
	private HashSet<String> allowedSenders = new HashSet<String>();
	private int allowedDirection;
	Properties properties;
	
	private SigurEventHandlerImpl() {				
	}
	
	private static SigurEventHandlerImpl instance;
	
	public static synchronized SigurEventHandlerImpl getInstance() {
		if(instance==null) {
			instance = new SigurEventHandlerImpl();
		}
		return instance;
	}
	
	@Override
	public boolean initialize(Properties eventHandlerProperties) {
		
		properties = eventHandlerProperties;
		
		String sendersSettings = eventHandlerProperties.getProperty(SigurSettingsManagerImpl.ALLOWED_SENDERS_RPOPERTY_KEY);		
		for(String senderString:sendersSettings.split(";")) {
			allowedSenders.add(senderString);
		}		
		allowedDirection = Integer.parseInt(eventHandlerProperties.getProperty(SigurSettingsManagerImpl.ALLOWED_DIRECTION_PROPERTY_KEY));
		
		jframe = new JFrame();
		mainPanel = new MainPanel(eventHandlerProperties.getProperty(SigurSettingsManagerImpl.MAIN_SCREEN_TEXT));			
		jframe.setContentPane(mainPanel);		
		jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (Integer.parseInt(eventHandlerProperties.getProperty(SigurSettingsManagerImpl.FULLSCREEN_USAGE_PROPERTY_KEY)) != 0) {
			jframe.setUndecorated(true);
		}		
		jframe.setVisible(true);
		jframe.toFront();
		
		return true;				
	}
	
	@Override
	public void showMessage(SigurEvent sigurEvent) {
		
		if(!allowedSenders.contains(sigurEvent.getSenderID()) || !(allowedDirection == 0 || allowedDirection == sigurEvent.getDirection())) {
			return;
		}
		
		String result = null;
		ImageIcon photoImageIcon = null;
		if (sigurEvent.getEventType() != null) {
			switch (sigurEvent.getEventType()) {
			case SUCCESS_ENTER:
				String visitorName = SigurClient.getInstance().getSigurDAO().getVisitorName(sigurEvent.getObjectID());
				//result = "<html><body style='text-align: center'>Добро пожаловать! " + visitorName;
				result = properties.getProperty(SigurSettingsManagerImpl.SUCCESS_ENTER_TEXT);
				result = result.replaceAll("\\[name\\]", visitorName);
				photoImageIcon = SigurClient.getInstance().getSigurDAO().getVisitorPhoto(sigurEvent.getObjectID()); 
				break;				
			case FAIL_FACE_SCAN:
				//result = "<html><body style='text-align: center'>Лицо не опознано.<br><br>Пожалуйста, посмотрите в камеру после ввода пин кода!";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_FACE_SCAN_TEXT);
				break;
			case FAIL_WRONG_CODE:
				//result = "<html><body style='text-align: center'>Неверный пин код.<br><br>Попробуйте еще раз или обратитесь в отдел продаж.";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_WRONG_CODE_TEXT);
				break;
			case FAIL_EXPIRED:
				//result = "<html><body style='text-align: center'>Срок действия абонемента истек.<br><br>Пожалуйста, обратитесь в отдел продаж.";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_EXPIRED_TEXT);
				break;
			case FAIL_TIME_LIMIT:
				//result = "<html><body style='text-align: center'>У Вас нет допуска в это время.<br><br>Пожалуйста, обратитесь в отдел продаж.";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_TIME_LIMIT_TEXT);
				break;
			default:
			}
		}
		if(result!=null) {
			mainPanel.showMessage(result, photoImageIcon);
		};		
	}

	@Override
	public void close() {
		jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
	}

}
