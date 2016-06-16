package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class SpectralMaximumTest
{
	
	public SpectralMaximumTest()
	{
	}

	/**
	 * Test of execute method, of class SpectralMaximum.
	 */
	@Test
	public void testExecute_3args_2()
	{
		System.out.println("execute");
		double[] waveforms = new double[] {1, 3, 2, -1, -3, 0, -3, 1};
		int recordLength = 8;
		double deltaT = 0.01;
		double[] expResult = new double[] {12.5};
		double[] result = SpectralMaximum.execute(waveforms, recordLength, deltaT, WaveformUtils.WindowType.RECTANGLE, 0.0);
		assertArrayEquals(expResult, result, Math.ulp(0.0f));
		
		waveforms = new double[] {1, 3, 2, -1, -3, 0, -3, 1, 3, 2, -1, -3, 0, -3};
		recordLength = 7;
		deltaT = 0.01;
		expResult = new double[] {12.5, 12.5};
		result = SpectralMaximum.execute(waveforms, recordLength, deltaT, WaveformUtils.WindowType.RECTANGLE, 0.0);
		assertArrayEquals(expResult, result, Math.ulp(0.0f));
	}

}
