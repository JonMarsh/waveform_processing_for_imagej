package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class MaximumIndexTest
{
	
	/**
	 * Test of execute method, of class MaximumIndex.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("Test of MaximumIndex.execute()");
		double[] waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0};
		int recordLength = 10;
		int[] expResult = new int[] {1};
		int[] result = MaximumIndex.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result);

		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0};
		recordLength = 5;
		expResult = new int[] {1, 2};
		result = MaximumIndex.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result);
	}

}
