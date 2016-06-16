package com.github.jonmarsh.waveform_processing_for_imagej;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */


public class MovingWindowSumOfSquaresTest
{
	
	public MovingWindowSumOfSquaresTest()
	{
	}

	/**
	 * Test of execute method, of class MovingWindowSumOfSquares.
	 */
	@Test
	public void testExecute_6args_2()
	{
		System.out.println("Test of MovingWindowSumOfSquares.execute()");
		double[] waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		int recordLength = 16;
		int radius = 2;
		WaveformUtils.WindowType windowType = WaveformUtils.WindowType.RECTANGLE;
		double windowParameter = 0.0;
		boolean logOutput = false;
		double[] expResult = new double[] {2.36, 2.56, 1.7536, 1.7536, 1.3936, 2.2336, 1.8736, 1.68, 2.0496, 1.4196, 0.468, 0.6616, 0.71, 0.9404, 0.9788, 1.124};
		MovingWindowSumOfSquares.execute(waveforms, recordLength, radius, windowType, windowParameter, logOutput);
		assertArrayEquals(expResult, waveforms, Math.ulp(2.56));
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 2;
		windowType = WaveformUtils.WindowType.RECTANGLE;
		windowParameter = 0.0;
		logOutput = false;
		expResult = new double[] {2.36, 2.56, 1.7536, 1.7536, 1.3936, 2.2336, 2.5136, 2.36, 0.8192, 0.4196, 0.468, 0.6616, 0.71, 0.9404, 0.9788, 1.124};
		MovingWindowSumOfSquares.execute(waveforms, recordLength, radius, windowType, windowParameter, logOutput);
		assertArrayEquals(expResult, waveforms, Math.ulp(2.56));
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 2;
		windowType = WaveformUtils.WindowType.HANNING;
		windowParameter = 0.0;
		logOutput = false;
		expResult = new double[] {3.29166666666666667, 3.32638888888888889, 2.61, 1.73305555555555556, 1.30166666666666667, 1.64972222222222222, 3.5475, 4.79166666666666667, 0.14222222222222222, 0.64173611111111111, 1.16180555555555556, 0.77701388888888889, 0.53208333333333333, 0.801875, 1.45375, 1.99625};
		MovingWindowSumOfSquares.execute(waveforms, recordLength, radius, windowType, windowParameter, logOutput);
		assertArrayEquals(expResult, waveforms, Math.ulp(4.0));
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 0;
		windowType = WaveformUtils.WindowType.HANNING;
		windowParameter = 0.0;
		logOutput = false;
		expResult = new double[] {1.0, 25.0, 4.0, 9.0, 4.84, 1.0, 16.0, 25.0, 0.0, 0.0, 10.24, 0.25, 1.21, 4.84, 1.21, 16.0};
		MovingWindowSumOfSquares.execute(waveforms, recordLength, radius, windowType, windowParameter, logOutput);
		assertArrayEquals(expResult, waveforms, Math.ulp(25.0));
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 16;
		radius = 2;
		windowType = WaveformUtils.WindowType.RECTANGLE;
		windowParameter = 0.0;
		logOutput = true;
		expResult = new double[] {0.3729120029701070, 0.4082399653118500, 0.2439305368042750, 0.2439305368042750, 0.1441381376635880, 0.3490054009430670, 0.2726768777282880, 0.2253092817258630, 0.3116691124006110, 0.1521659906755550, -0.3297541469258760, -0.1794045034555100, -0.1487416512809250, -0.0266873795470982, -0.0093060393202484, 0.0507663112330423};
		MovingWindowSumOfSquares.execute(waveforms, recordLength, radius, windowType, windowParameter, logOutput);
		assertArrayEquals(expResult, waveforms, Math.ulp(10.0));
		
	}

}
