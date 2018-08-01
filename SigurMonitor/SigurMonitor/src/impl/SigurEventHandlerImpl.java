package impl;

import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Properties;

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
		
		String sendersSettings = eventHandlerProperties.getProperty(SigurSettingsManagerImpl.ALLOWED_SENDERS_RPOPERTY_KEY);		
		for(String senderString:sendersSettings.split(";")) {
			allowedSenders.add(senderString);
		}		
		allowedDirection = Integer.parseInt(eventHandlerProperties.getProperty(SigurSettingsManagerImpl.ALLOWED_DIRECTION_PROPERTY_KEY));
		
		jframe = new JFrame();
		mainPanel = new MainPanel();			
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
		if (sigurEvent.getEventType() != null) {
			switch (sigurEvent.getEventType()) {
			case SUCCESS_ENTER:

				String visitorName = SigurClient.getInstance().getSigurDAO().getVisitorName(sigurEvent.getObjectID());				
				
				result = "<html><body style='text-align: center'>����� ����������! " + visitorName;
				break;
			case FAIL_FACE_SCAN:
				result = "<html><body style='text-align: center'>���� �� ��������.<br><br>����������, ���������� � ������ ����� ����� ��� ����!";
				break;
			case FAIL_WRONG_CODE:
				result = "<html><body style='text-align: center'>�������� ��� ���.<br><br>���������� ��� ��� ��� ���������� � ����� ������.";
				break;
			case FAIL_EXPIRED:
				result = "<html><body style='text-align: center'>���� �������� ���������� �����.<br><br>����������, ���������� � ����� ������.";
				break;
			case FAIL_TIME_LIMIT:
				result = "<html><body style='text-align: center'>� ��� ��� ������� � ��� �����.<br><br>����������, ���������� � ����� ������.";
				break;
			default:
			}
		}
		if(result!=null) {
			mainPanel.showMessage(result);
		};		
	}

	@Override
	public void close() {
		jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
	}

}
