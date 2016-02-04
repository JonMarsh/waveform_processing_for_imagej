/*
 * Copyright 2016 ImageJ.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jonmarsh.waveform_processing_for_imagej;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */
public class MovingWindowRangeTest
{
	
	public MovingWindowRangeTest()
	{
	}
	
	
	/**
	 * Test of execute method, of class MovingWindowRange.
	 */
	@Test
	public void testExecute_3args_1()
	{
		System.out.println("Test of MovingWindowRange.execute(float[], int, int)");
		float[] waveforms = new float[] {1.0f, 5.0f, 2.0f, 3.0f, 2.2f, -1.0f, 4.0f, 5.0f, 0.0f, 0.0f, 3.2f, -0.5f, 1.1f, 2.2f, 1.1f, 4.0f};
		int recordLength = 16;
		int radius = 2;
		float[] expResult = new float[] {4.0f, 4.0f, 4.0f, 6.0f, 5.0f, 6.0f, 6.0f, 6.0f, 5.0f, 5.5f, 3.7f, 3.7f, 3.7f, 4.5f, 2.9f, 2.9f};
		MovingWindowRange.execute(waveforms, recordLength, radius);
		System.out.println(Arrays.toString(waveforms));
		System.out.println(Arrays.toString(expResult));
		assertArrayEquals(expResult, waveforms, 0.0f);
		
		waveforms = new float[] {1.0f, 5.0f, 2.0f, 3.0f, 2.2f, -1.0f, 4.0f, 5.0f, 0.0f, 0.0f, 3.2f, -0.5f, 1.1f, 2.2f, 1.1f, 4.0f};
		recordLength = 8;
		radius = 2;
		expResult = new float[] {4.0f, 4.0f, 4.0f, 6.0f, 5.0f, 6.0f, 6.0f, 6.0f, 3.2f, 3.7f, 3.7f, 3.7f, 3.7f, 4.5f, 2.9f, 2.9f};
		MovingWindowRange.execute(waveforms, recordLength, radius);
		assertArrayEquals(expResult, waveforms, 0.0f);
		
		waveforms = new float[] {1.0f, 5.0f, 2.0f, 3.0f, 2.2f, -1.0f, 4.0f, 5.0f, 0.0f, 0.0f, 3.2f, -0.5f, 1.1f, 2.2f, 1.1f, 4.0f};
		recordLength = 8;
		radius = 0;
		expResult = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
		MovingWindowRange.execute(waveforms, recordLength, radius);
		assertArrayEquals(expResult, waveforms, 0.0f);
	}

	/**
	 * Test of execute method, of class MovingWindowRange.
	 */
	@Test
	public void testExecute_3args_2()
	{
		System.out.println("Test of MovingWindowRange.execute(double[], int, int)");
		double[] waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		int recordLength = 16;
		int radius = 2;
		double[] expResult = new double[] {4.0, 4.0, 4.0, 6.0, 5.0, 6.0, 6.0, 6.0, 5.0, 5.5, 3.7, 3.7, 3.7, 4.5, 2.9, 2.9};
		MovingWindowRange.execute(waveforms, recordLength, radius);
		System.out.println(Arrays.toString(waveforms));
		System.out.println(Arrays.toString(expResult));
		assertArrayEquals(expResult, waveforms, 0.0f);
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 2;
		expResult = new double[] {4.0, 4.0, 4.0, 6.0, 5.0, 6.0, 6.0, 6.0, 3.2, 3.7, 3.7, 3.7, 3.7, 4.5, 2.9, 2.9};
		MovingWindowRange.execute(waveforms, recordLength, radius);
		assertArrayEquals(expResult, waveforms, 0.0f);
		
		waveforms = new double[] {1.0, 5.0, 2.0, 3.0, 2.2, -1.0, 4.0, 5.0, 0.0, 0.0, 3.2, -0.5, 1.1, 2.2, 1.1, 4.0};
		recordLength = 8;
		radius = 0;
		expResult = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		MovingWindowRange.execute(waveforms, recordLength, radius);
		assertArrayEquals(expResult, waveforms, 0.0f);
	}

}
