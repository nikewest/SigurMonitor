package ui;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private volatile Boolean wasAction = true;
	private volatile int waiterTimeout = 0;
	private final String mainScreenText; 
	
	public void setTimer(int value) {
		waiterTimeout = value;
	}
	
	public void setWasAction(boolean wasAction) {
		this.wasAction = wasAction;
	}
	
	private class EventWaiter extends Thread {

		public EventWaiter() {
			start();
		}

		@Override
		public void run() {
			while (true) {
				if (MainPanel.this.waiterTimeout>0) {
					try {
						sleep(1000);
						MainPanel.this.waiterTimeout--;						
					} catch (InterruptedException e) {
					}
				} else {
					doWakeUpStuff();					
				}				
			}
		}
		
		public void doWakeUpStuff() {
			synchronized (MainPanel.this.wasAction) {
				if(MainPanel.this.wasAction) {
					setWasAction(false);
					//MainPanel.this.writeToLabel("<html><body style='text-align: center'>1. ������� ������������ ��� �� ��� <br><br>2. ������� # <br><br>3. ���������� � ������!");
					MainPanel.this.writeToLabel(mainScreenText);
					MainPanel.this.showPhoto(null);
				}
				setTimer(3);
			}			
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
		
		new EventWaiter();		
	}
	
	public void showMessage(String message, ImageIcon photoImageIcon) {
		synchronized (wasAction) {
			setTimer(3);
			showPhoto(photoImageIcon);
			writeToLabel(message);
			setWasAction(true);
		}		
	}
	
	public synchronized void writeToLabel(String message) {
		messageLabel.setText(message);
	}
	
	public synchronized void showPhoto(ImageIcon photoImageIcon) {
		photoLabel.setIcon(photoImageIcon);
	}
}
