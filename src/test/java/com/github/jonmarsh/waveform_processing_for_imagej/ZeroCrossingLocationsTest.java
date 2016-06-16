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

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jon N. Marsh
 */
public class ZeroCrossingLocationsTest
{
	
	public ZeroCrossingLocationsTest()
	{
	}
	

	/**
	 * Test of execute method, of class ZeroCrossingLocations.
	 */
	@Test
	public void testExecuteDoubleArgs()
	{
		System.out.println("execute, double args, LINEAR");
		double[] waveforms = new double[]{0.0, 2.0, -0.5, -1.0, -0.25, 2.5, 0.0, 1.0};
		int recordLength = 8;
		int interpolationMethod = ZeroCrossingLocations.LINEAR;
		double[][][] expResult = new double[][][]{{{0.0, 0.0}, {1.0, 0.8}, {4.0, 0.090909090909090909}, {6.0, 0.0}}};
		double[][][] result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		assertArrayEquals(expResult, result);
		
		System.out.println("execute, double args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, 2.0, -0.5, -1.0, -0.25, 2.5, 0.0, 1.0};
		recordLength = 8;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = new double[][][]{{{0.0, 0.0}, {1.0, 0.8019907962206931}, {4.0, 0.08724147932651083}, {6.0, 0.0}, {6.0, 0.504938459126981}}};
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		System.out.println(Arrays.toString(result[0]));
		assertArrayEquals(expResult, result);

		System.out.println("execute, double args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, 2.0, -0.5, -1.0, -0.25, 2.5, 0.0, 1.0, 0.0, 2.0, -0.5, -1.0, -0.25, 2.5, 0.0, 1.0};
		recordLength = 8;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = new double[][][]{{{0.0, 0.0}, {1.0, 0.8019907962206931}, {4.0, 0.08724147932651083}, {6.0, 0.0}, {6.0, 0.504938459126981}}, {{0.0, 0.0}, {1.0, 0.8019907962206931}, {4.0, 0.08724147932651083}, {6.0, 0.0}, {6.0, 0.504938459126981}}};
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		System.out.println(Arrays.toString(result[0]));
		assertArrayEquals(expResult, result);
		
		System.out.println("execute, double args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, -0.5, 0.23, 0.95, -0.14, -0.02, -0.02, -0.21, 0.32, 0.25, 0.5, -1.13, -1.44, -0.1, -0.38414905};
		recordLength = 15;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = new double[][][]{{{0.0, 0.0}, {1.0, 0.7929222826067188}, {3.0, 0.8466182436854719}, {5.0, 0.06266630541668228}, {5.0, 0.9283914914733904}, {7.0, 0.4056260712236802}, {10.0, 0.381710191338657}, {13.0, 0.28099249958110384}, {13.0, 0.28200724341862093}}};
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		System.out.println(Arrays.toString(result[0]));
		assertArrayEquals(expResult, result);
		
		System.out.println("execute, double args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, -0.5, 0.23, 0.95, -0.14, -0.02, -0.02, -0.21, 0.32, 0.25, 0.5, -1.13, -1.44, -0.1, -0.38414905, 0.0};
		recordLength = 16;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = new double[][][]{{{0.0, 0.0}, {1.0, 0.7929222927366231}, {3.0, 0.846618132037994}, {5.0, 0.06266721907567385}, {5.0, 0.9283879580834553}, {7.0, 0.405667844700292}, {10.0, 0.3824936282695642}, {15.0, 0.0}}};
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		System.out.println(Arrays.toString(result[0]));
		assertArrayEquals(expResult, result);
		
		System.out.println("execute, float args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, -0.5, 0.23, 0.95, -0.14, -0.02, -0.02, -0.21, 0.32, 0.25, 0.5, -1.13, -1.44, -0.1, -0.38414905, 0.0};
		recordLength = 17;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = null;
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		assertArrayEquals(expResult, result);

		System.out.println("execute, float args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, -0.5, 0.23, 0.95, -0.14, -0.02, -0.02, -0.21, 0.32, 0.25, 0.5, -1.13, -1.44, -0.1, -0.38414905, 0.0};
		recordLength = 15;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = null;
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		assertArrayEquals(expResult, result);
		
		System.out.println("execute, float args, CUBIC_SPLINE");
		waveforms = new double[]{0.0, -0.5, 0.23, 0.95, -0.14, -0.02, -0.02, -0.21, 0.32, 0.25, 0.5, -1.13, -1.44, -0.1, -0.38414905, 0.0};
		recordLength = 0;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = null;
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		assertArrayEquals(expResult, result);
		
		System.out.println("execute, float args, CUBIC_SPLINE");
		waveforms = null;
		recordLength = 8;
		interpolationMethod = ZeroCrossingLocations.CUBIC_SPLINE;
		expResult = null;
		result = ZeroCrossingLocations.execute(waveforms, recordLength, interpolationMethod);
		assertArrayEquals(expResult, result);
		
	}

}
