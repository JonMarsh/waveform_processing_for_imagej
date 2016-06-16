package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class MedianValueTest
{
	
	public MedianValueTest()
	{
	}

	/**
	 * Test of execute method, of class MedianValue.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of MedianValue.execute()");
		System.out.println("execute");
		double[] waveforms = new double[] {1.0, 5.0, 3.0, 2.0, 2.2, -1.0, 4.4, 10.0, -9.0, 5.0};
		int recordLength = 5;
		double[] expResult = new double[] {2.2, 4.4};
		double[] result = MedianValue.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, 0.0);
	}

}
