package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class WaveformMathTest
{
	
	public WaveformMathTest()
	{
	}

	/**
	 * Test of execute method, of class WaveformMath.
	 */
	@Test
	public void testExecute_doubleArr()
	{
		System.out.println("execute");
		double[] waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		double[] waveform = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		int operation = WaveformMath.ADD;
		WaveformMath.execute(waveforms, waveform, operation);
		double[] expResult = new double[] {2.0, 10.0, 4.0, 6.0, 4.4, -2.0, 8.0, 10.0, 0.0, 0.0, 6.4, -1.0, 2.2, 4.4, 2.2, 8.0};
		assertArrayEquals(expResult, waveforms, Math.ulp(1.0));
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		waveform = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		operation = WaveformMath.SUBTRACT;
		WaveformMath.execute(waveforms, waveform, operation);
		expResult = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, -0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		assertArrayEquals(expResult, waveforms, Math.ulp(0.0));

		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		waveform = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		operation = WaveformMath.MULTIPLY;
		WaveformMath.execute(waveforms, waveform, operation);
		expResult = new double[] {1.0, 25.0, 4.0, 9.0, 4.84, 1.0, 16.0, 25.0, 0.0, 0.0, 10.24, 0.25, 1.21, 4.84, 1.21, 16.0};
		assertArrayEquals(expResult, waveforms, Math.ulp(25.0));

		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		waveform = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		operation = WaveformMath.DIVIDE;
		WaveformMath.execute(waveforms, waveform, operation);
		expResult = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.NaN, Double.NaN, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		assertArrayEquals(expResult, waveforms, Math.ulp(1.0));

		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		waveform = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0};
		operation = WaveformMath.DIVIDE;
		WaveformMath.execute(waveforms, waveform, operation);
		expResult = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.6, -0.1666666666666667, 0.5, -2.2, 0.275, 0.8};
		assertArrayEquals(expResult, waveforms, Math.ulp(1.0));

	}
	
}
