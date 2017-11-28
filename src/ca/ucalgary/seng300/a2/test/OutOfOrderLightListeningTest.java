package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lsmr.vending.hardware.IndicatorLight;

import ca.ucalgary.seng300.a2.LogFile;
import ca.ucalgary.seng300.a2.OutOfOrderLightListening;

public class OutOfOrderLightListeningTest {

	
	OutOfOrderLightListening outOfOrderLightListening = new OutOfOrderLightListening();
//	String MessageTest = "test message";
	IndicatorLight light = new IndicatorLight();

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LogFile.createLogFile();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		OutOfOrderLightListening ofOrderLightListening = new OutOfOrderLightListening();
		IndicatorLight light = new IndicatorLight();
		light.register(ofOrderLightListening);
		
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteLog() throws Exception {
		LogFile.createLogFile();
		light.activate();
		outOfOrderLightListening.activated(light);
		assertTrue("Light isActive value is now true".equals(outOfOrderLightListening.getCurrMessage()));
		light.deactivate();
		outOfOrderLightListening.activated(light);
		assertTrue("Light isActive value was true".equals(outOfOrderLightListening.getPrevMessage()));
		
	}
	
	@Test
	public void testActivated() throws IOException {
		light.activate();
		outOfOrderLightListening.activated(light);
		assertEquals(light.isActive(),outOfOrderLightListening.getisActive());
		
		
		
	}
	
	@Test
	public void testDeactive() throws IOException {
		light.deactivate();
		outOfOrderLightListening.deactivated(light);
		assertEquals(light.isActive(),outOfOrderLightListening.getisActive());
		

		
	}

	

	
}
