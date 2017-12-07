/**
 * SENG 300 Group Assignment 3
 * 
 * Class that creates GUI for configuration panel of a simulated vending machine
 */

package ca.ucalgary.seng300.a3;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lsmr.vending.PopCan;
import org.lsmr.vending.hardware.CoinRack;
import org.lsmr.vending.hardware.ConfigurationPanel;
import org.lsmr.vending.hardware.VendingMachine;

public class GUIConfigPanel extends JFrame {
	
	// Panels and display
	private JTextField display;
	private GUIConfigButtons buttonPanel;
	private ConfigurationPanel configPanel;

	public GUIConfigPanel(VendingMachine vm) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 350);
		setTitle("Configuration Panel");
		configPanel = vm.getConfigurationPanel();
		}

	/**
	 * Initializes and creates configuration panel window and panels
	 */
	public void init() {
		setLayout(new BorderLayout());

		buttonPanel = new GUIConfigButtons(configPanel);

		createDisplay();
		buttonPanel.createButtons();

		getContentPane().add(buttonPanel, BorderLayout.CENTER);

		setVisible(true);
		setResizable(true);
	}

	/**
	 * Creates display as JTextField
	 */
	public void createDisplay() {		

		// Initialize JTextField with message that displays on opening config panel
		display = new JTextField("Select which aspect to configure: \n 0 - Set Pop Price\n Selection: ");
		
		display.setPreferredSize(new Dimension(1000, 75));
		display.setEditable(false); // So display cannot be edited by user without using onscreen buttons
		add(display, BorderLayout.NORTH);
		setVisible(true);
	}

	/**
	 * Changes message on text field 
	 * 
	 * @param message updated message to display
	 */
	public void updateDisplay(String message) {
		display.setText(message);
		setVisible(true);
	}
	
	
	
	/*
	 * Inner class to create buttons for Configuation Panel GUI
	 */
	private class GUIConfigButtons extends JPanel {
		
		// 26 letters + 10 numbers + shift + enter
		private int buttonLength = 38;
		private JButton[] buttons;
		private String[] upperChar = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
				"A", "S", "D", "F", "G", "H", "J", "K", "L", "Shift", "Z", "X", "C", "V", "B", "N", "M", "Enter"};
		
		private ConfigurationPanel configPanel;
		
		public GUIConfigButtons(ConfigurationPanel configPanel) {
			this.configPanel = configPanel;
		}

		/**
		 * Creates buttons for button panel and connects to appropriate listeners
		 */
		public void createButtons() {
			buttons = new JButton[buttonLength];
			
			for(int i = 0; i < buttonLength; i++) {
				buttons[i] = new JButton(upperChar[i]);
				buttons[i].setPreferredSize(new Dimension(100, 50));
				add(buttons[i]);
				buttons[i].addActionListener(new GUIConfigListening(i, configPanel));
			}

			setVisible(true);
		}
	}
}
