package ca.ucalgary.seng300.a3;

public class ConfigPanelLogicListener {
	
	private GUI mygui;
	private ConfigPanelLogic mylogic;
	
	public ConfigPanelLogicListener(ConfigPanelLogic c, GUI g) {
		mygui = g;
		mylogic = c;
		mylogic.register(this);
	}
	
	public void priceUpdate(int buttonIndex, int newPrice) {
		double newPopCost;
		
		newPopCost = (double)newPrice / 100;
		
		mygui.setPopButtonText(buttonIndex, newPopCost);
	}
} //end class
