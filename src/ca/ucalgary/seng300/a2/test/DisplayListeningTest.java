

/**
 * Function for display listening test
 * Elodie Boudes 10171818, Grace Ferguson 30004869, 
 * Tae Chyung 10139101, Karndeep Dhami 10031989, 
 * Andrew Garcia-Corley 10015169 & Michael de Grood 10134884
 */
package ca.ucalgary.seng300.a2.test;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.vending.hardware.*;

import ca.ucalgary.seng300.a2.DisplayListening;
import ca.ucalgary.seng300.a2.LogFile;

public class DisplayListeningTest {
	
	private DisplayListening displayListeningTest;
	private Display displayTest;

	@Before
	public void setUp() throws Exception {
		displayListeningTest = new DisplayListening();
		displayTest = new Display();
		LogFile.createLogFile();
	}

	@Test
	public void testIsOn() {
		displayListeningTest.enabled(displayTest);
		assertEquals(true, displayListeningTest.isOn());
	}
	
	@Test
	public void testNotOn() {
		displayListeningTest.disabled(displayTest);
		assertEquals(false, displayListeningTest.isOn());
	}

	@Test
	public void testMessageChange() {
		String prevMessage = "";
		displayListeningTest.setPrevMessage(prevMessage);
		
		String currMessage = "";
		displayListeningTest.setCurrMessage(currMessage);
		
		for(int i = 0; i<5; i++) {
			String message = "Test";
			displayListeningTest.messageChange(displayTest, prevMessage, message);
			assertEquals(message, displayListeningTest.getCurrMessage());
			assertEquals(prevMessage, displayListeningTest.getPrevMessage());
		}
	}
	
}
