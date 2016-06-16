package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class MinimumValueTest
{
	
	public MinimumValueTest()
	{
	}

	/**
	 * Test of execute method, of class MinimumValue.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of MinimumValue.execute()");
		double[] waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, 1.0, 4.0, 5.0, -1.0, 0.0};
		int recordLength = 10;
		double[] expResult = new double[] {-1.0};
		double[] result = MinimumValue.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, 0.0);
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, 1.0, 4.0, 5.0, -1.0, 0.0};
		recordLength = 5;
		expResult = new double[] {1.0, -1.0};
		result = MinimumValue.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, 0.0);
	}

}
