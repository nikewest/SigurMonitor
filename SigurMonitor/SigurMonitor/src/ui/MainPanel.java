package ui;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class MainPanel extends JPanel {
	
	private static final Logger logger = LogManager.getLogger(MainPanel.class.getName());
	
	private static final long serialVersionUID = 1L;
	
	private final String mainScreenText;
	
	private EventWaiter eventWaiter; 
	
	private class EventWaiter extends Thread {

		private int messageTimeout;
		
		public EventWaiter(int messageTimeout) {
			this.messageTimeout = messageTimeout; 
			start();
		}

		@Override
		public void run() {
			try {
				sleep(messageTimeout*1000);
				doWakeUpStuff();
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}		
		}
		
		public void doWakeUpStuff() {
			MainPanel.this.writeToLabel(mainScreenText);
			MainPanel.this.showPhoto(null);
			MainPanel.this.setBackground(new Color(238, 238, 238));
		}
	}

	JLabel messageLabel;
	JLabel photoLabel;

	/**
	 * Create the panel.
	 */
	public MainPanel(String mainScreenText) {
		
		setPreferredSize(new Dimension(650, 480));
		setLayout(new BorderLayout(0, 0));

		messageLabel = new JLabel();
		messageLabel.setFocusable(false);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		messageLabel.setFont(new Font("Dialog", Font.BOLD, 48));
		add(messageLabel, BorderLayout.CENTER);

		this.mainScreenText = mainScreenText;
		
		photoLabel = new JLabel();
		photoLabel.setFocusable(false);
		add(photoLabel, BorderLayout.WEST);
		
		//new EventWaiter();
		eventWaiter = new EventWaiter(0);
	}
	
	public void showMessage(String message, ImageIcon photoImageIcon, Color backgroundColor, int messageTimeout) {
		
		showPhoto(photoImageIcon);
		writeToLabel(message);
		if (backgroundColor != null) {
			this.setBackground(backgroundColor);
		}

		try {
			eventWaiter.interrupt();
			eventWaiter.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}

		eventWaiter = new EventWaiter(messageTimeout);
		
	}
	
	public synchronized void writeToLabel(String message) {
		messageLabel.setText(message);
	}
	
	public synchronized void showPhoto(ImageIcon photoImageIcon) {
		photoLabel.setIcon(photoImageIcon);
	}
}
