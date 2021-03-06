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


public class AlignWaveformsTest
{
	
	public AlignWaveformsTest()
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
	 * Test of execute method, of class AlignWaveforms.
	 */
	@Test
	public void testExecute_4args_2()
	{
		System.out.println("Test of AlignWaveforms.execute(double[], double[], int int)");
		double[] waveforms = new double[] {2.0, 3.0, 2.0, 5.0, 1.0, 0.0, 2.0, 1.0, 6.0, 1.0, 0.0, 1.0};
		double[] seedWaveform = new double[] {2.0, 3.0, 2.0, 5.0, 1.0, 0.0};
		int from = 0;
		int to = 6;
		AlignWaveforms.execute(waveforms, seedWaveform, from, to);
		double[] expResult = new double[] {2.0, 3.0, 2.0, 5.0, 1.0, 0.0, 1.0, 2.0, 1.0, 6.0, 1.0, 0.0};
		assertArrayEquals(expResult, waveforms, 0.0);
	}
}
