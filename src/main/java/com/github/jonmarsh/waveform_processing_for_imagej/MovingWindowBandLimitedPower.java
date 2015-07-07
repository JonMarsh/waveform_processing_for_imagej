package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import java.awt.AWTEvent;
import java.awt.TextField;
import java.util.Arrays;

/**
 * This plug-in filter moves a sliding gate along each waveform (horizontal
 * line) in an image, applies a symmetric window function to each gated segment
 * centered around the current index, and then returns the mean value of the log
 * power spectrum within the specified frequency range at that index. The input
 * data are overwritten by the new computed values. The length of the moving
 * window is equal to {@code 2*radius+1}. A plain DFT is used here since the
 * window lengths and integration ranges are usually quite short, and we can use
 * a lookup table for the sine & cosine values.
 * <p>
 * @author Jon N. Marsh
 */
public class MovingWindowBandLimitedPower implements ExtendedPlugInFilter, DialogListener
{
	private int width, height;
	private int radius = 1;
	private static final String[] windowTypes = WaveformUtils.WindowType.stringValues();
	private static int windowChoice = WaveformUtils.WindowType.HAMMING.ordinal();
	private static double windowParameter = 0.5;
	private static TextField windowParameterTextField;
	private static double deltaT = 1.0;
	private static double loFrequency = 0.0;
	private static double hiFrequency = 0.1;
	private static double windowLengthTime = 0.1;
	private GenericDialog gd;
	private int loIndex, hiIndex;
	private double[][] cosArray;
	private double[][] sinArray;
	private double[] weights;
	private final int flags = DOES_32 + DOES_STACKS + PARALLELIZE_STACKS + KEEP_PREVIEW + FINAL_PROCESSING;

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
		height = imp.getHeight();

		return flags;
	}

	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
	{
		gd = new GenericDialog("Moving Window Band-Limited Power");
		gd.addNumericField("Sampling interval (deltaT)", deltaT, 4);
		gd.addNumericField("Low frequency cutoff", loFrequency, 4, 8, "(units of 1/deltaT)");
		gd.addNumericField("High frequency cutoff", hiFrequency, 4, 8, "(units of 1/deltaT)");
		gd.addNumericField("Window length", windowLengthTime, 0, 4, "(units of deltaT)");
		gd.addChoice("Weight function", windowTypes, windowTypes[windowChoice]);
		gd.addNumericField("Window parameter", windowParameter, 4);
		windowParameterTextField = (TextField)(gd.getNumericFields().get(4));
		windowParameterTextField.setEnabled(WaveformUtils.WindowType.values()[windowChoice].usesParameter());
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return DONE;
		}

		// precompute Fourier coefficients, window weights, etc.
		loIndex = (int)(loFrequency / deltaT);
		hiIndex = (int)(hiFrequency / deltaT);
		cosArray = new double[hiIndex - loIndex][width];
		sinArray = new double[hiIndex - loIndex][width];
		for (int k = loIndex; k < hiIndex; k++) {
			double constant = -2 * Math.PI * k / width;
			for (int n = 0; n < width; n++) {
				double constant2 = constant * n;
				cosArray[k - loIndex][n] = Math.cos(constant2);
				sinArray[k - loIndex][n] = Math.sin(constant2);
			}
		}
		weights = WaveformUtils.windowFunction(WaveformUtils.WindowType.values()[windowChoice], 2 * radius + 1, windowParameter, true);
		radius = (int)(windowLengthTime / deltaT);
		if (radius % 2 != 0) {
			radius += 1;
		}

		return flags;
	}

	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{
		deltaT = gd.getNextNumber();
		loFrequency = gd.getNextNumber();
		hiFrequency = gd.getNextNumber();
		windowLengthTime = gd.getNextNumber();
		windowChoice = gd.getNextChoiceIndex();
		windowParameter = gd.getNextNumber();

		windowParameterTextField.setEnabled(WaveformUtils.WindowType.values()[windowChoice].usesParameter());

		return (!gd.invalidNumber()
				&& windowLengthTime > 0.0
				&& deltaT > 0.0
				&& loFrequency >= 0.0
				&& hiFrequency > loFrequency
				&& hiFrequency < 0.5 / deltaT);
	}

	public void run(ImageProcessor ip)
	{
		float[] pixels = (float[])ip.getPixels();

		for (int i = 0; i < height; i++) {	// loop over all waveforms within current slice
			float[] tempArray = slidingWindowAvgPower(Arrays.copyOfRange(pixels, i * width, (i + 1) * width));
			for (int j = 0; j < tempArray.length; j++) {
				pixels[i * width + j] = (float)tempArray[j]; // store computed values in original dataset to re-use memory
				if (IJ.escapePressed()) // provide option for aborting this potentially long calculation
				{
					return;
				}
			}
		}

		IJ.resetMinAndMax();

	}

	/* We use a plain DFT here since the window lengths and integration ranges are usually quite short, and we can use a lookup table for the sin & cos values */
	private float[] slidingWindowAvgPower(float[] a)
	{
		int length = a.length;
		float[] result = new float[length];
		double norm = 1.0 / (hiIndex - loIndex);

		// Take care of points near beginning of waveform
		for (int i = 0; i < radius; i++) {
			int count = radius + i + 1;

			// Apply window
			double[] temp = new double[count];
			for (int j = 0; j < count; j++) {
				temp[j] = weights[(radius - i) + j] * a[j];
			}

			// Sum over frequencies
			double sum = 0.0;
			for (int k = loIndex; k < hiIndex; k++) {
				double sumRe = 0.0;
				double sumIm = 0.0;
				for (int j = 0, n = 0; j < count; j++, n++) {
					sumRe += temp[j] * cosArray[k - loIndex][n];
					sumIm += temp[j] * sinArray[k - loIndex][n];
				}
				sum += Math.log10(sumRe * sumRe + sumIm * sumIm);
			}
			result[i] = (float)(10.0 * sum * norm);
		}

		// Take care of middle region
		for (int i = radius; i < length - radius; i++) {
			int count = 2 * radius + 1;

			// Apply window
			double[] temp = new double[count];
			for (int j = i - radius, h = 0; j <= i + radius; j++, h++) {
				temp[h] = weights[h] * a[j];
			}

			// Sum over frequencies
			double sum = 0.0;
			for (int k = loIndex; k < hiIndex; k++) {
				double sumRe = 0.0;
				double sumIm = 0.0;
				for (int j = 0, n = i - radius; j < count; j++, n++) {
					sumRe += temp[j] * cosArray[k - loIndex][n];
					sumIm += temp[j] * sinArray[k - loIndex][n];
				}
				sum += Math.log10(sumRe * sumRe + sumIm * sumIm);
			}
			result[i] = (float)(10.0 * sum * norm);
		}

		// Take care of points near end of waveform
		for (int i = length - radius; i < length; i++) {
			int count = radius + length - i;

			// Apply window
			double[] temp = new double[count];
			for (int j = 0; j < count; j++) {
				temp[j] = weights[j] * a[(i - radius) + j];
			}

			// Sum over frequencies
			double sum = 0.0;
			for (int k = loIndex; k < hiIndex; k++) {
				double sumRe = 0.0;
				double sumIm = 0.0;
				for (int j = 0, n = (i - radius) + j; j < count; j++, n++) {
					sumRe += temp[j] * cosArray[k - loIndex][n];
					sumIm += temp[j] * sinArray[k - loIndex][n];
				}
				sum += Math.log10(sumRe * sumRe + sumIm * sumIm);
			}
			result[i] = (float)(10.0 * sum * norm);
		}

		return result;
	}

	public void setNPasses(int nPasses)
	{
	}

}
