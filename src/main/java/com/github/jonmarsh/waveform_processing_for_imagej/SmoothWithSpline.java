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

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import static ij.plugin.filter.ExtendedPlugInFilter.KEEP_PREVIEW;
import static ij.plugin.filter.PlugInFilter.DOES_32;
import static ij.plugin.filter.PlugInFilter.DOES_STACKS;
import static ij.plugin.filter.PlugInFilter.DONE;
import static ij.plugin.filter.PlugInFilter.FINAL_PROCESSING;
import static ij.plugin.filter.PlugInFilter.PARALLELIZE_STACKS;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import java.awt.AWTEvent;

/**
 *
 * @author Jon N. Marsh
 */
public class SmoothWithSpline implements ExtendedPlugInFilter, DialogListener
{
	private int width;
	private static double smoothingParameter = 1.0;
	private static double stdev = 0.0;
	private GenericDialog gd;
	private PlugInFilterRunner pfr;
	private final int flags = DOES_32 + DOES_STACKS + PARALLELIZE_STACKS + KEEP_PREVIEW + FINAL_PROCESSING;

	@Override
	public int setup(String arg, ImagePlus imp)
	{
		if (arg.equals("final")) {
			IJ.resetMinAndMax();
			return DONE;
		}

		if (imp == null) {
			IJ.noImage();
			return DONE;
		}

		width = imp.getWidth();

		return flags;
	}

	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
	{
		this.pfr = pfr;

		gd = new GenericDialog("Smooth with Spline...");
		gd.addNumericField("Smoothing parameter value", smoothingParameter, 1, 6, "");
		gd.addNumericField("Estimated standard deviation", stdev, 4, 6, "");
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return DONE;
		}

		return flags;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent awte)
	{
		smoothingParameter = gd.getNextNumber();
		stdev = gd.getNextNumber();

		return (!gd.invalidNumber() && stdev >= 0.0 && smoothingParameter >= 0.0);
	}

	@Override
	public void run(ImageProcessor ip)
	{
		float[] pixels = (float[])ip.getPixels();
		execute(pixels, width, smoothingParameter, stdev);
	}

	/**
	 * Computes smoothing spline for each record in {@code waveforms} using the
	 * method described by Reinsch in Numerische Mathematik 10, 177-183 (1967).
	 * {@code waveforms} is a one-dimensional array composed of a series of
	 * concatenated records, each of length {@code recordLength}. The spline is
	 * a natural spline, e.g. the second derivative is zero at the endpoints.
	 * The smoothing spline reverts to a simple cubic spline that passes through
	 * every original data point when {@code smoothingParameter=0}. For
	 * smoothing splines, setting {@code smoothingParameter=1.0} is usually a
	 * good choice. Very large values for {@code smoothingParameter} yield a
	 * straight-line fit to the input data. {@code standardDeviation} is
	 * typically a measure of the standard deviation of the system noise or the
	 * variation of the part of the signal that should be "smoothed out" by the
	 * smoothing spline algorithm. Each record in {@code waveforms} is assumed
	 * to have uniform spacing between elements. Internal computations are
	 * performed with double precision. No action is performed if
	 * {@code waveforms} is {@code null}, {@code recordLength<=3},
	 * {@code waveforms.length<recordLength}, or {@code waveform.length} is not
	 * evenly divisible by {@code recordLength}. No error checking is performed
	 * on range limits; if the values are negative or outside the range of the
	 * array, a runtime exception may be thrown.
	 * <p>
	 * @param waveforms          input waveforms concatenated into 1-D array
	 * @param recordLength       length of each waveform in points
	 * @param smoothingParameter must be greater than or equal to zero
	 * @param stdev              magnitude of variation that should be "smoothed
	 *                           out," must be greater than or equal to zero
	 */
	public static final void execute(float[] waveforms, int recordLength, double smoothingParameter, double stdev)
	{
		if (waveforms != null && recordLength > 3 && waveforms.length >= recordLength && waveforms.length % recordLength == 0) {
			
			// compute number of records
			int numRecords = waveforms.length / recordLength;
			
			// peform computations on a row-by-row basis
			for (int i = 0; i < numRecords; i++) {
				
				// offset to current record
				int offset = i * recordLength;
				
				// compute interpolant coefficients
				double[][] coeffs = WaveformUtils.smoothingSplineInterpolantUniformSpacing(waveforms, offset, offset + recordLength, stdev, 1.0, smoothingParameter);
				
				// copy 'a' coefficients into result array
				for (int j=0; j<recordLength; j++) {
					waveforms[offset + j] = (float)coeffs[0][j];
				}
				
			}
			
		}

	}

	/**
	 * Computes smoothing spline for each record in {@code waveforms} using the
	 * method described by Reinsch in Numerische Mathematik 10, 177-183 (1967).
	 * {@code waveforms} is a one-dimensional array composed of a series of
	 * concatenated records, each of length {@code recordLength}. The spline is
	 * a natural spline, e.g. the second derivative is zero at the endpoints.
	 * The smoothing spline reverts to a simple cubic spline that passes through
	 * every original data point when {@code smoothingParameter=0}. For
	 * smoothing splines, setting {@code smoothingParameter=1.0} is usually a
	 * good choice. Very large values for {@code smoothingParameter} yield a
	 * straight-line fit to the input data. {@code standardDeviation} is
	 * typically a measure of the standard deviation of the system noise or the
	 * variation of the part of the signal that should be "smoothed out" by the
	 * smoothing spline algorithm. Each record in {@code waveforms} is assumed
	 * to have uniform spacing between elements. Internal computations are
	 * performed with double precision. No action is performed if
	 * {@code waveforms} is {@code null}, {@code recordLength<=3},
	 * {@code waveforms.length<recordLength}, or {@code waveform.length} is not
	 * evenly divisible by {@code recordLength}. No error checking is performed
	 * on range limits; if the values are negative or outside the range of the
	 * array, a runtime exception may be thrown.
	 * <p>
	 * @param waveforms          input waveforms concatenated into 1-D array
	 * @param recordLength       length of each waveform in points
	 * @param smoothingParameter must be greater than or equal to zero
	 * @param stdev              magnitude of variation that should be "smoothed
	 *                           out," must be greater than or equal to zero
	 */
	public static final void execute(double[] waveforms, int recordLength, double smoothingParameter, double stdev)
	{
		if (waveforms != null && recordLength > 3 && waveforms.length >= recordLength && waveforms.length % recordLength == 0) {
			
			// compute number of records
			int numRecords = waveforms.length / recordLength;
			
			// peform computations on a row-by-row basis
			for (int i = 0; i < numRecords; i++) {
				
				// offset to current record
				int offset = i * recordLength;
				
				// compute interpolant coefficients
				double[][] coeffs = WaveformUtils.smoothingSplineInterpolantUniformSpacing(waveforms, offset, offset + recordLength, stdev, 1.0, smoothingParameter);
				
				// copy 'a' coefficients into result array
				System.arraycopy(coeffs[0], 0, waveforms, offset, recordLength);
				
			}
			
		}

	}
	
	@Override
	public void setNPasses(int n)
	{
	}

}
