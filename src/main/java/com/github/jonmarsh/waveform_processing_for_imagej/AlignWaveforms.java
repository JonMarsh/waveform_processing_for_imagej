package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import ij.util.Tools;
import java.awt.AWTEvent;
import java.util.Arrays;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 * This plug-in filter aligns each row of the image to a user-specified row (or
 * row segment) using FFT-based cross-correlation. Each row is assumed to
 * represent a single waveform. Shifting is accomplished by simple rotation of
 * the row. For stacks, all rows are aligned to the row or row segment selected
 * from the currently displayed slice.
 * <p>
 * @author	Jon N. Marsh
 */
public class AlignWaveforms implements ExtendedPlugInFilter, DialogListener
{
	private static int seedIndex = 0;
	private static int loIndex = 0;
	private static int hiIndex = 1;
	private int width, height;
	private GenericDialog gd;
	private float[] seedPixels;
	private float[] seedWaveform;
	private final int flags = DOES_32 + DOES_STACKS + PARALLELIZE_STACKS + KEEP_PREVIEW;

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

		if (imp.getType() != ImagePlus.GRAY32) {
			IJ.error("Image must be 32-bit floating point format");
			return DONE;
		}

		width = imp.getWidth();
		height = imp.getHeight();
		seedPixels = (float[])imp.getProcessor().getPixelsCopy();
		seedWaveform = new float[width];

		return flags;
	}

	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
	{
		gd = new GenericDialog("Align Waveforms...");
		gd.addNumericField("Waveform to align with:", seedIndex, 0);
		gd.addNumericField("Start index", loIndex, 0);
		gd.addNumericField("End index", hiIndex, 0);
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
		seedIndex = (int)gd.getNextNumber();
		loIndex = (int)gd.getNextNumber();
		hiIndex = (int)gd.getNextNumber();

		boolean noError = (!gd.invalidNumber()
				&& seedIndex >= 0
				&& seedIndex < height
				&& loIndex >= 0
				&& loIndex < (width - 1)
				&& hiIndex > loIndex
				&& hiIndex < width);

		if (noError) {
			seedWaveform = Arrays.copyOfRange(seedPixels, seedIndex * width, (seedIndex + 1) * width);
		}

		return noError;
	}

	@Override
	public void run(ImageProcessor ip)
	{
		float[] pixels = (float[])ip.getPixels();
		double[] pixelsDouble = Tools.toDouble(pixels);
		
		execute(pixelsDouble, Tools.toDouble(seedWaveform), loIndex, hiIndex);
		
		for (int i=0; i<pixels.length; i++) {
			pixels[i] = (float)pixelsDouble[i];
		}
		
	}

	/**
	 * Performs an in-place alignment of each array segment in {@code waveforms}
	 * with {@code seedWavefom} within the specified range of indices.
	 * {@code waveforms} is assumed to be composed of a series of signals (of
	 * size given by {@code seedWaveform.length}) concatenated sequentially in a
	 * one-dimensional array. The alignment is performed by simple rotation of
	 * each segment.
	 * <p>
	 * @param waveforms	   array of concatenated waveforms, each of length
	 *                     {@code seedWaveform.length}
	 * @param seedWaveform	array with which to align each signal in
	 *                     {@code waveforms}
	 * @param from	        start index (inclusive) of subsection of
	 *                     {@code seedWaveform} to be used for alignment
	 * @param to           end index (exclusive) of subsection of
	 *                     {@code seedWaveform} to be used for alignment
	 */
	public static void execute(double[] waveforms, double[] seedWaveform, int from, int to)
	{
		final int w = seedWaveform.length;

		// determine number of records
		final int h = waveforms.length / w;

		// compute padded waveform length
		final int pw = w + WaveformUtils.amountToPadToNextPowerOf2(w);

		// initialize seed waveform copy and compute FFT
		final double[] seedRe = new double[pw];
		final double[] seedIm = new double[pw];
		System.arraycopy(seedWaveform, from, seedRe, from, to - from);
		FastFourierTransformer.transformInPlace(new double[][]{seedRe, seedIm}, DftNormalization.STANDARD, TransformType.FORWARD);

		// loop over each waveform
		for (int i = 0; i < h; i++) {
			// compute row offset
			int offset = i * w;

			// initialize waveform copies
			double[] tempRe = new double[pw];
			double[] tempIm = new double[pw];
			for (int j = 0; j < w; j++) {
				tempRe[j] = (double)waveforms[offset + j];
			}

			// compute cross-correlation
			FastFourierTransformer.transformInPlace(new double[][]{tempRe, tempIm}, DftNormalization.STANDARD, TransformType.FORWARD);
			double[] corrRe = new double[pw];
			double[] corrIm = new double[pw];
			for (int j = 0; j < pw; j++) {
				corrRe[j] = tempRe[j] * seedRe[j] + tempIm[j] * seedIm[j];
				corrIm[j] = tempRe[j] * seedIm[j] - tempIm[j] * seedRe[j];
			}
			FastFourierTransformer.transformInPlace(new double[][]{corrRe, corrIm}, DftNormalization.STANDARD, TransformType.INVERSE);

			// find index of maximum value of cross-correlation array
			int maxIndex = WaveformUtils.maxIndex(corrRe);

			// because of symmetry of fft, shift > pw/2 corresponds to leftward (negative) rotation
			if (maxIndex >= pw / 2) {
				maxIndex -= pw;
			}

			// rotate waveform in place
			WaveformUtils.rotateArrayInPlace(waveforms, maxIndex, offset, offset + w);
		}
	}

	@Override
	public void setNPasses(int nPasses)
	{
	}
}
