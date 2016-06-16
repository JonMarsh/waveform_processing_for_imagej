package com.github.jonmarsh.waveform_processing_for_imagej;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */
public class MovingWindowWeightedAverageTest
{

	public MovingWindowWeightedAverageTest()
	{
	}

	/**
	 * Test of execute method, of class MovingWindowWeightedAverage.
	 */
	@Test
	public void testExecute_5args_2()
	{
		System.out.println("Test of MovingWindowWeightedAverage.execute(double[], int, int, WaveformUtils.WindowType, double)");
		double[] waveforms = new double[]{1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		int recordLength = 16;
		int radius = 2;
		WaveformUtils.WindowType windowType = WaveformUtils.WindowType.RECTANGLE;
		double windowParameter = 0.0;
		double[] expResult = new double[]{3f, 3.2, 2.64, 2.24, 2.04, 2.64, 2.04, 1.6, 2.44, 1.54, 0.76, 1.2, 1.42, 1.58, 1.9, 2.12};
		MovingWindowWeightedAverage.execute(waveforms, recordLength, radius, windowType, windowParameter);
		assertArrayEquals(expResult, waveforms, Math.ulp(3.0));

		waveforms = new double[]{1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 2;
		windowType = WaveformUtils.WindowType.RECTANGLE;
		expResult = new double[]{3, 3.2, 2.64, 2.24, 2.04, 2.64, 2.84, 2.2, 1.28, 0.54, 0.76, 1.2, 1.42, 1.58, 1.9, 2.12};
		MovingWindowWeightedAverage.execute(waveforms, recordLength, radius, windowType, windowParameter);
		assertArrayEquals(expResult, waveforms, Math.ulp(3.0));

		waveforms = new double[]{1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 2;
		windowType = WaveformUtils.WindowType.HANNING;
		expResult = new double[]{3.166666666666667, 3.083333333333333, 2.933333333333333, 2.383333333333333, 1.733333333333333, 1.883333333333333, 2.85, 3.5, 0.533333333333333, 0.758333333333333, 1.033333333333333, 1.091666666666667, 1.15, 1.575, 2.1, 2.25};
		MovingWindowWeightedAverage.execute(waveforms, recordLength, radius, windowType, windowParameter);
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));

		waveforms = new double[]{1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 0;
		windowType = WaveformUtils.WindowType.RECTANGLE;
		expResult = new double[]{1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		MovingWindowWeightedAverage.execute(waveforms, recordLength, radius, windowType, windowParameter);
		assertArrayEquals(expResult, waveforms, Math.ulp(0.0));
	}

}
