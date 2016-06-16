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
public class EnvelopeTest
{

	public EnvelopeTest()
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
	 * Test of execute method, of class Envelope.
	 */
	@Test
	public void testExecute_3args_2()
	{
		System.out.println("Test of Envelope.execute(double[], int, boolean)");
		double[] waveforms = new double[]{1, -1, 2, -2, 3, -2, -1, 1};
		int recordLength = 8;
		Envelope.execute(waveforms, recordLength, false);
		double[] expResult = new double[]{1.567516118317942, 1.426832005020369, 2.199042163676473, 2.038913905516878, 3.007140372316107, 3.215454933512449, 2.159679041518228, 1.489238089054235};
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));

		waveforms = new double[]{1, -1, 2, -2, 3, -2, -1, 1};
		recordLength = 8;
		Envelope.execute(waveforms, recordLength, true);
		expResult = new double[]{1.490882886475845, 1.517061162429006, 2.086003700290799, 2.16166484777592, 2.882450037522498, 3.294658621078905, 2.220323976894609, 1.408351904138277};
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));

		waveforms = new double[]{1, -1, 2, -2, 3, -2, -1, 1};
		recordLength = 8;
		Envelope.execute(waveforms, recordLength, false);
		expResult = new double[]{1.567516118317942, 1.426832005020369, 2.199042163676473, 2.038913905516878, 3.007140372316107, 3.215454933512449, 2.159679041518228, 1.489238089054235};
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));

		waveforms = new double[]{1, -1, 2, -2, 3, -2, -1, 1, 1, -1, 2, -2, 3, -2, -1, 1};
		recordLength = 8;
		Envelope.execute(waveforms, recordLength, false);
		expResult = new double[]{1.567516118317942, 1.426832005020369, 2.199042163676473, 2.038913905516878, 3.007140372316107, 3.215454933512449, 2.159679041518228, 1.489238089054235, 1.567516118317942, 1.426832005020369, 2.199042163676473, 2.038913905516878, 3.007140372316107, 3.215454933512449, 2.159679041518228, 1.489238089054235};
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));

		waveforms = new double[]{1, -1, 2, -2, 3, -2, -1, 1, -1, 2};
		recordLength = 10;
		Envelope.execute(waveforms, recordLength, false);
		expResult = new double[]{1.5578115082694171, 1.4727930412398540, 2.1807600916290708, 2.0072751180434869, 3.0484247110291984, 3.7383969927501579, 2.8952565090260505, 1.2518840334714916, 1.5578115082694171, 2.0716339192295456};
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));
	}

}
