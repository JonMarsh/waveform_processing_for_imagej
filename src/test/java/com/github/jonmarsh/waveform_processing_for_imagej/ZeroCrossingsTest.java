package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class ZeroCrossingsTest
{
	
	public ZeroCrossingsTest()
	{
	}
	
	@BeforeClass
	public static void setUpClass()
	{
	}
	
	@AfterClass
	public static void tearDownClass()
	{
	}
	
	@Before
	public void setUp()
	{
	}
	
	@After
	public void tearDown()
	{
	}

	/**
	 * Test of execute method, of class ZeroCrossings.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of ZeroCrossings.execute");
		double[] waveforms = new double[] {1.0, 1.0, 2.0, -1.0, 1.0, 2.0, -1.0, 2.0};
		int recordLength = 8;
		int[] expResult = new int[] {4};
		int[] result = ZeroCrossingCount.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result);
		
		waveforms = new double[] {1.0, 1.0, 2.0, -1.0, 1.0, 2.0, -1.0, 2.0};
		recordLength = 4;
		expResult = new int[] {1, 2};
		result = ZeroCrossingCount.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result);
		
		waveforms = new double[] {1.0, 1.0, 2.0, 0.0, 1.0, 2.0, 1.0, 2.0};
		recordLength = 8;
		expResult = new int[] {0};
		result = ZeroCrossingCount.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result);				
	}

}
