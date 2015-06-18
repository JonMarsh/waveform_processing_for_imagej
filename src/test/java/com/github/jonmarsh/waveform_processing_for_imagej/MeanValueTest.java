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
	public void testExecute_floatArr_int()
	{
		System.out.println("Test of MeanValue.execute(float[], int)");
		float[] waveforms = new float[] {3.0f, 4.0f, 5.0f, 6.0f, 1.0f, 8.0f};
		int recordLength = 3;
		float[] expResult = new float[] {4.0f, 5.0f};
		float[] result = MeanValue.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, Math.ulp(5.0f));
		
		waveforms = null;
		recordLength = 3;
		result = MeanValue.execute(waveforms, recordLength);
		assertNull(result);
	}

	/**
	 * Test of execute method, of class MeanValue.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of MeanValue.execute(double[], int)");
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
