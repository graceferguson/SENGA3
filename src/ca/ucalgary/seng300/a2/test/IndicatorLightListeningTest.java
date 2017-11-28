package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lsmr.vending.hardware.IndicatorLight;

import ca.ucalgary.seng300.a2.IndicatorLighListening;
import ca.ucalgary.seng300.a2.LogFile;

public class IndicatorLightListeningTest {
	
	IndicatorLighListening indicatorLighListening = new IndicatorLighListening();
//	String MessageTest = "test message";
	IndicatorLight light = new IndicatorLight();


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		IndicatorLighListening indicatorLighListening = new IndicatorLighListening();
		IndicatorLight light = new IndicatorLight();
		light.register(indicatorLighListening);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testWriteLog() throws Exception {
		LogFile.createLogFile();
		light.activate();
		indicatorLighListening.activated(light);
		assertTrue("Light isActive value is now true".equals(indicatorLighListening.getCurrMessage()));
		light.deactivate();
		indicatorLighListening.activated(light);
		assertTrue("Light isActive value was true".equals(indicatorLighListening.getPrevMessage()));
		
	}
	
	@Test
	public void testActivated() throws IOException {
		light.activate();
		indicatorLighListening.activated(light);
		assertEquals(light.isActive(),indicatorLighListening.getisActive());
	
		
		
		
	}
	
	@Test
	public void testDeactive() throws IOException {
		light.deactivate();
		indicatorLighListening.deactivated(light);
		assertEquals(light.isActive(),indicatorLighListening.getisActive());
		

		
	}

}
