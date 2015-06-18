package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import java.awt.AWTEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * This plugin uses cubic spline interpolation to overwrite data in waveforms
 * where values exceed a user-specified threshold. This may be useful in
 * instances where a waveform is "clipped" by exceeding the range of an ADC, and a 
 * qualitative estimation the waveform's original shape is desired. 
 * <p>
 * Several important caveats should be noted. Interpolated values are obviously
 * just that -- interpolated -- and so the output should be used with care. This
 * algorithm performs better for highly oversampled waveforms than in cases
 * where the digitization rate is near the Nyquist limit. Additionally, if there
 * are values outside the threshold at the beginning or end of the waveform
 * (i.e., outside the cubic spline fit), the algorithm must extrapolate, which
 * is almost guaranteed to produce strange artifacts.
 * <p>
 *
 * @author Jon N. Marsh
 */
public class InterpolateClippedRegions implements ExtendedPlugInFilter, DialogListener
{

	private int width;
	private GenericDialog gd;
	private static double clippingThreshold = 1.0;
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

		return flags;
	}

	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
	{
		gd = new GenericDialog("Interpolate Clipped Waveforms With Cubic Spline");
		gd.addNumericField("Clipping amplitude threshold", clippingThreshold, 4);
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return DONE;
		}

		return flags;
	}

	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{
		clippingThreshold = gd.getNextNumber();

		return (!gd.invalidNumber() && clippingThreshold >= 0.0);
	}

	public void run(ImageProcessor ip)
	{
		float[] pixels = (float[])ip.getPixels();
		float[] interpolatedPixels = execute(pixels, width, clippingThreshold);
		if (interpolatedPixels != null) {
			System.arraycopy(interpolatedPixels, 0, pixels, 0, pixels.length);
		}
	}

	public static final float[] execute(float[] waveforms, int recordLength, double threshold)
	{
		if (waveforms == null || recordLength <= 3 || waveforms.length < recordLength || waveforms.length % recordLength != 0) {
			return null;
		}

		// compute number of records
		int numRecords = waveforms.length / recordLength;

		// initialize output array
		float[] interpolatedWaveforms = Arrays.copyOf(waveforms, waveforms.length);

		// peform computations on a row-by-row basis
		for (int i = 0; i < numRecords; i++) {

			// offset to current record
			int offset = i * recordLength;

			// create arrays of valid x and y values -- they'll be larger than necessary
			double[] validX = new double[recordLength];
			double[] validY = new double[recordLength];
			int numberValid = 0;
			for (int j = 0; j < recordLength; j++) {
				double value = waveforms[offset + j];
				if (Math.abs(value) < threshold) {
					validX[numberValid] = j;
					validY[numberValid] = value;
					numberValid++;
				}
			}

			// if there are fewer valid points than required for a cubic fit, just fill with zeros
			if (numberValid < 4) {

				Arrays.fill(waveforms, offset, offset + recordLength, 0.0f);

			} else if (numberValid < recordLength) {

				// compute interpolant coefficients
				double[][] coeffs = WaveformUtils.cubicSplineInterpolant(Arrays.copyOf(validX, numberValid), Arrays.copyOf(validY, numberValid));

				// create a sorted map to hold spline coefficients (except for last one, which has zero values for all except constant coefficient
				TreeMap<Double, double[]> splineMap = new TreeMap<Double, double[]>();
				for (int j = 0; j < numberValid - 1; j++) {
					splineMap.put(validX[j], new double[]{coeffs[0][j], coeffs[1][j], coeffs[2][j], coeffs[3][j]});
//					IJ.log("x="+validX[j]+": "+coeffs[0][j]+", "+coeffs[1][j]+", "+coeffs[2][j]+", "+coeffs[3][j]);
				}

				// if first valid point occurs after time zero, we have to extrapolate :-(
				if (validX[0] != 0.0) {
					for (int j = 0; j < (int)validX[0]; j++) {
						double h = j - validX[0];
						interpolatedWaveforms[offset + j] = (float)(coeffs[0][0] + h * (coeffs[1][0] + h * (coeffs[2][0] + h * coeffs[3][0])));
					}
				}

				// interpolate remaining points; note that extrapolation may occur if last point is not at the end of the waveform
				for (int j = (int)validX[0]; j < recordLength; j++) {
					Map.Entry<Double, double[]> entry = splineMap.floorEntry((double)j);
					double h = j - entry.getKey();
					interpolatedWaveforms[offset + j] = (float)(entry.getValue()[0] + h * (entry.getValue()[1] + h * (entry.getValue()[2] + h * entry.getValue()[3])));
				}

			}

		}

		return interpolatedWaveforms;
	}

	public static final double[] execute(double[] waveforms, int recordLength, double threshold)
	{
		if (waveforms == null || recordLength <= 3 || waveforms.length < recordLength || waveforms.length % recordLength != 0) {
			return null;
		}

		// compute number of records
		int numRecords = waveforms.length / recordLength;

		// initialize output array
		double[] interpolatedWaveforms = Arrays.copyOf(waveforms, waveforms.length);

		// peform computations on a row-by-row basis
		for (int i = 0; i < numRecords; i++) {

			// offset to current record
			int offset = i * recordLength;

			// create arrays of valid x and y values -- they'll be larger than necessary
			double[] validX = new double[recordLength];
			double[] validY = new double[recordLength];
			int numberValid = 0;
			for (int j = 0; j < recordLength; j++) {
				double value = waveforms[offset + j];
				if (Math.abs(value) < threshold) {
					validX[numberValid] = j;
					validY[numberValid] = value;
					numberValid++;
				}
			}

			// if there are fewer valid points than required for a cubic fit, just fill with zeros
			if (numberValid < 4) {

				Arrays.fill(waveforms, offset, offset + recordLength, 0.0f);

			} else if (numberValid < recordLength) {

				// compute interpolant coefficients
				double[][] coeffs = WaveformUtils.cubicSplineInterpolant(Arrays.copyOf(validX, numberValid), Arrays.copyOf(validY, numberValid));

				// create a sorted map to hold spline coefficients (except for last one, which has zero values for all except constant coefficient
				TreeMap<Double, double[]> splineMap = new TreeMap<Double, double[]>();
				for (int j = 0; j < numberValid - 1; j++) {
					splineMap.put(validX[j], new double[]{coeffs[0][j], coeffs[1][j], coeffs[2][j], coeffs[3][j]});
//					IJ.log("x="+validX[j]+": "+coeffs[0][j]+", "+coeffs[1][j]+", "+coeffs[2][j]+", "+coeffs[3][j]);
				}

				// if first valid point occurs after time zero, we have to extrapolate :-(
				if (validX[0] != 0.0) {
					for (int j = 0; j < (int)validX[0]; j++) {
						double h = j - validX[0];
						interpolatedWaveforms[offset + j] = (float)(coeffs[0][0] + h * (coeffs[1][0] + h * (coeffs[2][0] + h * coeffs[3][0])));
					}
				}

				// interpolate remaining points; note that extrapolation may occur if last point is not at the end of the waveform
				for (int j = (int)validX[0]; j < recordLength; j++) {
					Map.Entry<Double, double[]> entry = splineMap.floorEntry((double)j);
					double h = j - entry.getKey();
					interpolatedWaveforms[offset + j] = (float)(entry.getValue()[0] + h * (entry.getValue()[1] + h * (entry.getValue()[2] + h * entry.getValue()[3])));
				}

			}

		}

		return interpolatedWaveforms;
	}

	public void setNPasses(int nPasses)
	{
	}

}
