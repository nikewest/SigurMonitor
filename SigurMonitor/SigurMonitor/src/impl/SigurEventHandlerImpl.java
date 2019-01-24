package impl;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import sigur.SigurClient;
import sigur.SigurEvent;
import sigur.SigurEventHandler;
import ui.MainPanel;

public class SigurEventHandlerImpl implements SigurEventHandler {

	private static Color SUCCESS_COLOR = Color.decode("#BEF2B6");
	private static Color FAIL_COLOR = Color.decode("#E7847E");
	
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
		Color bgrColor = null;
		int messageTimeout = 1;		
		
		if (sigurEvent.getEventType() != null) {
			switch (sigurEvent.getEventType()) {
			case SUCCESS_ENTER:
				String visitorName = SigurClient.getInstance().getSigurDAO().getVisitorName(sigurEvent.getObjectID());
				//result = "<html><body style='text-align: center'>Добро пожаловать! " + visitorName;
				result = properties.getProperty(SigurSettingsManagerImpl.SUCCESS_ENTER_TEXT);				
				result = result.replaceAll("\\[name\\]", visitorName);
				photoImageIcon = SigurClient.getInstance().getSigurDAO().getVisitorPhoto(sigurEvent.getObjectID());							
				bgrColor = SUCCESS_COLOR;
				
				messageTimeout = Integer.parseInt(properties.getProperty(SigurSettingsManagerImpl.SUCCESSSCREEN_DURATION));
				playSound(SigurSettingsManagerImpl.SUCCESS_ENTER_AUDIO);
				break;				
			case FAIL_FACE_SCAN:
				//result = "<html><body style='text-align: center'>Лицо не опознано.<br><br>Пожалуйста, посмотрите в камеру после ввода пин кода!";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_FACE_SCAN_TEXT);
				bgrColor = FAIL_COLOR;
				
				messageTimeout = Integer.parseInt(properties.getProperty(SigurSettingsManagerImpl.FAILSCREEN_DURATION));
				playSound(SigurSettingsManagerImpl.FAIL_FACE_SCAN_AUDIO);
				break;
			case FAIL_WRONG_CODE:
				//result = "<html><body style='text-align: center'>Неверный пин код.<br><br>Попробуйте еще раз или обратитесь в отдел продаж.";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_WRONG_CODE_TEXT);
				bgrColor = FAIL_COLOR;
				
				messageTimeout = Integer.parseInt(properties.getProperty(SigurSettingsManagerImpl.FAILSCREEN_DURATION));
				playSound(SigurSettingsManagerImpl.FAIL_WRONG_CODE_AUDIO);
				break;
			case FAIL_EXPIRED:
				//result = "<html><body style='text-align: center'>Срок действия абонемента истек.<br><br>Пожалуйста, обратитесь в отдел продаж.";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_EXPIRED_TEXT);
				bgrColor = FAIL_COLOR;
				
				messageTimeout = Integer.parseInt(properties.getProperty(SigurSettingsManagerImpl.FAILSCREEN_DURATION));
				playSound(SigurSettingsManagerImpl.FAIL_EXPIRED_AUDIO);
				break;
			case FAIL_TIME_LIMIT:
				//result = "<html><body style='text-align: center'>У Вас нет допуска в это время.<br><br>Пожалуйста, обратитесь в отдел продаж.";
				result = properties.getProperty(SigurSettingsManagerImpl.FAIL_TIME_LIMIT_TEXT);
				bgrColor = FAIL_COLOR;
				
				messageTimeout = Integer.parseInt(properties.getProperty(SigurSettingsManagerImpl.FAILSCREEN_DURATION));
				playSound(SigurSettingsManagerImpl.FAIL_TIME_LIMIT_AUDIO);
				break;
			default:
			}
		}
		if(result!=null) {
			mainPanel.showMessage(result, photoImageIcon, bgrColor, messageTimeout);
		};		
	}

	@Override
	public void close() {
		jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
	}
	
	public void playSound(String fileName) {
		try {
			File audiofile = new File("audio/" + fileName);
			if (audiofile.exists()) {
				AudioInputStream ais = AudioSystem.getAudioInputStream(audiofile);
				DataLine.Info info = new Info(Clip.class, ais.getFormat());
				Clip clip = (Clip) AudioSystem.getLine(info);
				clip.open(ais);
				clip.start();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
