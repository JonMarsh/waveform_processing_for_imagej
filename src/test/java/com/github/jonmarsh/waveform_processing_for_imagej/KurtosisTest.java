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


public class KurtosisTest
{
	
	public KurtosisTest()
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
	 * Test of execute method, of class Kurtosis.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of Kurtosis.execute()");
		double[] waveforms = new double[] {1, 2, 5, 3, 2, 3, 1};
		int recordLength = 7;
		double[] expResult = new double[] {-0.33045806067816774};
		double[] result = Kurtosis.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, Math.ulp(1.0));
		
		waveforms = new double[] {1, 2, 5, 3, 2, 3, 1, 1};
		recordLength = 4;
		expResult = new double[] {-1.1542857142857144, -1.371900826446281};
		result = Kurtosis.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, Math.ulp(1.0f));
	}

}
