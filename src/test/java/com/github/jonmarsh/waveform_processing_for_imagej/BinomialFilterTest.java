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


public class BinomialFilterTest
{
	
	public BinomialFilterTest()
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
	 * Test of execute method, of class BinomialFilter.
	 */
	@Test
	public void testExecute_3args_2()
	{
		System.out.println("Test of BinomialFilter.execute(double[], int, int)");
		double[] waveforms = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 3.0, 2.0, 5.0, 1.0};
		int recordLength = 6;
		int nPasses = 2;
		double[] expResult = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.75, 2.0, 2.5, 2.9375, 3.125, 3.125};
		BinomialFilter.execute(waveforms, recordLength, nPasses);
		assertArrayEquals(expResult, waveforms, 0.0);
		
		waveforms = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 3.0, 2.0, 5.0, 1.0};
		recordLength = 6;
		nPasses = 0;
		expResult = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 3.0, 2.0, 5.0, 1.0};
		BinomialFilter.execute(waveforms, recordLength, nPasses);
		assertArrayEquals(expResult, waveforms, 0.0);
	}

}
