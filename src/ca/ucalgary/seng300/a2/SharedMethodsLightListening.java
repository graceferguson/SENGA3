/**
 * Function for light listener 
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */


package ca.ucalgary.seng300.a2;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.lsmr.vending.hardware.AbstractHardware;
import org.lsmr.vending.hardware.AbstractHardwareListener;
import org.lsmr.vending.hardware.Display;
import org.lsmr.vending.hardware.IndicatorLight;
import org.lsmr.vending.hardware.IndicatorLightListener;

public class SharedMethodsLightListening  implements IndicatorLightListener{
	
	
	protected boolean isActive; 
	private String prevMessage = "";
	private String currMessage = "";
	

	
	public void messageChange(String oldMessage, String newMessage) {
		// call event log here 
		
		String currentTime = LogFile.df.format(LogFile.dateobj) ;
		String classType  = this.getClass().getName();
		newMessage = classType + "\t" + newMessage;
		try {
			LogFile.writeLog( currentTime +"\t" + newMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public Boolean getisActive() {
			return isActive;
	}
	public String getPrevMessage() {
		return prevMessage;
}
	public String getCurrMessage() {
		return currMessage;
}
	
	 
	
	private void setMessage (IndicatorLight light) {
		prevMessage  = "Light isActive value was " + isActive;
		isActive = light.isActive();
		currMessage = "Light isActive value is now " + isActive; 
		messageChange(prevMessage,currMessage);
		
	}
	
	@Override
	public void activated(IndicatorLight light) {
		setMessage(light);
		
	}
	
	@Override
	public void deactivated(IndicatorLight light) {
		setMessage(light);
	
	}
	
	
	@Override
	public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
		// TODO Auto-generated method stub
		
	}


}
