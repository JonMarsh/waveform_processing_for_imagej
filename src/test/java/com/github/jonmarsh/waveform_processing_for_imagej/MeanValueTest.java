package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class MeanValueTest
{
	
	public MeanValueTest()
	{
	}


	/**
	 * Test of execute method, of class MeanValue.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of MeanValue.execute()");
		double[] waveforms = new double[] {3.0, 4.0, 5.0, 6.0, 1.0, 8.0};
		int recordLength = 3;
		double[] expResult = new double[] {4.0, 5.0};
		double[] result = MeanValue.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, Math.ulp(5.0));
		
		waveforms = null;
		recordLength = 3;
		result = MeanValue.execute(waveforms, recordLength);
		assertNull(result);
	}

}
