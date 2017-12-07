package ca.ucalgary.seng300.a3;

public class ConfigPanelLogicListener {
	
	private GUI mygui;
	
	public ConfigPanelLogicListener(GUI g) {
		mygui = g;
		ConfigPanelLogic.getInstance().register(this);
	}
	
	public void priceUpdate(int buttonIndex, int newPrice) {
		double newPopCost;
		
		newPopCost = (double)newPrice / 100;
		
		mygui.setPopButtonText(buttonIndex, newPopCost);
	}
} //end class
