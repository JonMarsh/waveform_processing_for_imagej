package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class SumOfSquaresTest
{
	
	/**
	 * Test of execute method, of class SumOfSquares.
	 */
	@Test
	public void testExecute_doubleArr_int()
	{
		System.out.println("execute");
		double[] waveforms = new double[] {1, 2, -1, -8, 4, 5, -2, 0};
		int recordLength = 8;
		double[] expResult = new double[] {115.0};
		double[] result = SumOfSquares.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, Math.ulp(expResult[0]));

		waveforms = new double[] {1, 2, -1, -8, 4, 5, -2, 0};
		recordLength = 4;
		expResult = new double[] {70.0, 45.0};
		result = SumOfSquares.execute(waveforms, recordLength);
		assertArrayEquals(expResult, result, Math.ulp(expResult[0]));
	}

}
