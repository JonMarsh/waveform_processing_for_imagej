/*
 * Copyright 2014 Jon N. Marsh.
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jon N. Marsh
 */
public class InterpolateClippedRegionsTest
{
	/**
	 * Test of execute method, of class InterpolateClippedRegions.
	 */
	@Test
	public void testExecute_3args_1()
	{
		System.out.println("Test of InterpolateClippedRegions.execute(float[], int, double)");
		float[] waveforms = null;
		int recordLength = 0;
		double threshold = 0.0;
		float[] expResult = null;
		float[] result = InterpolateClippedRegions.execute(waveforms, recordLength, threshold);
		assertNull(result);
	}

	/**
	 * Test of execute method, of class InterpolateClippedRegions.
	 */
	@Test
	public void testExecute_3args_2()
	{
		System.out.println("Test of InterpolateClippedRegions.execute(double[], int, double)");
		double[] waveforms = null;
		int recordLength = 0;
		double threshold = 0.0;
		double[] expResult = null;
		double[] result = InterpolateClippedRegions.execute(waveforms, recordLength, threshold);
		assertNull(result);
	}
	
}
