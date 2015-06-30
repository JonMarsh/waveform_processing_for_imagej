/*
 * Copyright 2015 Jon Marsh.
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
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import java.awt.AWTEvent;

/**
 * This plug-in filter computes the Hilbert transform of each horizontal line in
 * an image. It works on arbitrarily-sized array lengths, by zero-padding up to
 * the next power of 2 and implementing an efficient FFT routine. The returned
 * data is truncated to the original length and written over the original image
 * data.
 * <p>
 * @author Jon N. Marsh
 */
public class HilbertTransform implements ExtendedPlugInFilter, DialogListener
{
	private int width;
	private static boolean isForward = true;
	private GenericDialog gd;
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
		gd = new GenericDialog("Hilbert Transform");
		gd.addCheckbox("Forward transform (reverse if unchecked)", isForward);
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return DONE;
		}

		return flags;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{
		isForward = gd.getNextBoolean();

		return true;
	}

	@Override
	public void run(ImageProcessor ip)
	{
		// get pixel values of current processor
		float[] pixels = (float[])ip.getPixels();

		// compute envelopes
		execute(pixels, isForward, width);
	}

	/**
	 * Computes Hilbert transform of input waveforms, assumed to be of length
	 * {@code recordLength} and concatenated and stored in one-dimensional input
	 * array {@code waveforms}. Each waveform is zero-padded to the next largest
	 * power of 2 if necessary in order to make use of FFTs for efficiency, and
	 * the final output is truncated to the original length. Results are
	 * computed in place. For efficiency, no error checking is performed on
	 * validity of inputs.
	 * <p>
	 * @param waveforms    input
	 * @param isForward    {@code true} for forward transform,
	 *                     {@code false for inverse}
	 * @param recordLength length of each waveform in points
	 */
	public static final void execute(float[] waveforms, boolean isForward, int recordLength)
	{
		int numberOfRecords = waveforms.length / recordLength;

		int paddedWidth = recordLength + WaveformUtils.amountToPadToNextPowerOf2(recordLength);

		// perform computations on row-by-row basis
		for (int i = 0; i < numberOfRecords; i++) {

			// compute row offset 
			int offset = i * recordLength;

			// initialize temporary padded array copy
			double[] waveformCopy = new double[paddedWidth];
			for (int j = 0; j < recordLength; j++) {
				waveformCopy[j] = waveforms[offset + j];
			}

			// compute Hilbert transform
			WaveformUtils.fastHilbertTransformPowerOf2(waveformCopy, isForward);

			// copy transform into original waveform array, truncating at original width
			for (int j = 0; j < recordLength; j++) {
				waveforms[offset + j] = (float)waveformCopy[j];
			}
		}
	}

	/**
	 * Computes Hilbert transform of input waveforms, assumed to be of length
	 * {@code recordLength} and concatenated and stored in one-dimensional input
	 * array {@code waveforms}. Each waveform is zero-padded to the next largest
	 * power of 2 if necessary in order to make use of FFTs for efficiency, and
	 * the final output is truncated to the original length. Results are
	 * computed in place. For efficiency, no error checking is performed on
	 * validity of inputs.
	 * <p>
	 * @param waveforms    input
	 * @param isForward    {@code true} for forward transform,
	 *                     {@code false for inverse}
	 * @param recordLength length of each waveform in points
	 */
	public static final void execute(double[] waveforms, boolean isForward, int recordLength)
	{
		int numberOfRecords = waveforms.length / recordLength;

		int paddedWidth = recordLength + WaveformUtils.amountToPadToNextPowerOf2(recordLength);

		// perform computations on row-by-row basis
		for (int i = 0; i < numberOfRecords; i++) {

			// compute row offset 
			int offset = i * recordLength;

			// initialize temporary padded array copy
			double[] waveformCopy = new double[paddedWidth];
			for (int j = 0; j < recordLength; j++) {
				waveformCopy[j] = waveforms[offset + j];
			}

			// compute Hilbert transform
			WaveformUtils.fastHilbertTransformPowerOf2(waveformCopy, isForward);

			// copy transform into original waveform array, truncating at original width
			System.arraycopy(waveformCopy, 0, waveforms, offset, recordLength);
		}
	}

	@Override
	public void setNPasses(int nPasses)
	{
	}

}
