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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnm
 */
public class MovingWindowZeroCrossingCountTest
{
	
	public MovingWindowZeroCrossingCountTest()
	{
	}
	
	@BeforeClass
	public static void setUpClass()
	{
	}
	
	@AfterClass
	public static void tearDownClass()
	{
	}
	
	@Before
	public void setUp()
	{
	}
	
	@After
	public void tearDown()
	{
	}

	/**
	 * Test of execute method, of class MovingWindowZeroCrossingCount.
	 */
	@Test
	public void testExecute()
	{
		System.out.println("Test of MovingWindowZeroCrossingCount");
		double[] waveforms = null;
		int recordLength = 0;
		int radius = 0;
		MovingWindowZeroCrossingCount.execute(waveforms, recordLength, radius);
		assertNull(waveforms);
		
		waveforms = new double[] {1.0, 1.0, 2.0, -1.0, 1.0, 2.0, -1.0, 2.0};
		recordLength = 8;
		radius = 2;
		double[] expResult = new double[] {0.0, 1.0, 2.0, 2.0, 3.0, 3.0, 3.0, 4.0};
		MovingWindowZeroCrossingCount.execute(waveforms, recordLength, radius);
		assertArrayEquals(expResult, waveforms, 0.0);
		
		waveforms = new double[] {1.0, 1.0, 2.0, -1.0, 1.0, 2.0, -1.0, 2.0};
		recordLength = 4;
		radius = 1;
		expResult = new double[] {0.0, 0.0, 1.0, 2.0, 0.0, 1.0, 2.0, 2.0};
		MovingWindowZeroCrossingCount.execute(waveforms, recordLength, radius);
		assertArrayEquals(expResult, waveforms, 0.0);
	}
	
}
