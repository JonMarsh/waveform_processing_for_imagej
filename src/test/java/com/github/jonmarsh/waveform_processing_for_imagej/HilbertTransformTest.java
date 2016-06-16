/*
 * Copyright 2015 ImageJ.
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
public class HilbertTransformTest
{
	
	public HilbertTransformTest()
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
	 * Test of execute method, of class HilbertTransform.
	 */
	@Test
	public void testExecute_3args_2()
	{
		System.out.println("Test of HilbertTransform.execute(double[], boolean, int)");		
		double[] waveforms = new double[]{1, 2, 3, 2, -3, -1, 0, 1};
		boolean isForward = true;
		int recordLength = 8;
		HilbertTransform.execute(waveforms, isForward, recordLength);
		double[] expResult = new double[]{-0.914213562373094923, -0.896446609406726047, 0.207106781186547351, 3.724873734152915890, 1.914213562373094920, -1.603553390593273730, -1.207106781186547460, -1.224873734152916340};
		assertArrayEquals(expResult, waveforms, Math.ulp(4.0));

		waveforms = new double[]{1, 2, 3, 2, -3, -1, 0, 1};
		isForward = false;
		recordLength = 8;
		HilbertTransform.execute(waveforms, isForward, recordLength);
		expResult = new double[]{0.914213562373094923, 0.896446609406726047, -0.207106781186547351, -3.724873734152915890, -1.914213562373094920, 1.603553390593273730, 1.207106781186547460, 1.224873734152916340};
		assertArrayEquals(expResult, waveforms, Math.ulp(4.0));
}

	
}
