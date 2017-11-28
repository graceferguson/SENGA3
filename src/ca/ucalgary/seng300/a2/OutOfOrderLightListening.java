
/**
 * Function for OutOfOrder Light listener
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */

package ca.ucalgary.seng300.a2;




import java.sql.Timestamp;

import org.lsmr.vending.hardware.Display;

public class OutOfOrderLightListening extends SharedMethodsLightListening {
	
	public OutOfOrderLightListening() {
		isActive = false;
	}
	
	// signature can change just used the DisplayLister as a template same message function in both for now
//	@Override
//	public void messageChange(Display display, String oldMessage, String newMessage) {
		// call event log here 
//		Timestamp currentTime = getCurrentTime();
//		String classType  = this.getClass().getName();
//		newMessage = currentTime+ classType + "\t" + newMessage;
//	}




	

}

