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

/**
 * Subtracts the specified quantity of each waveform in an image from every 
 * element in the waveform.  Each row is assumed to represent a single waveform. 
 * Results are computed in place.
 * 
 * @author Jon N. Marsh
 */


public class SubtractFromWaveform implements ExtendedPlugInFilter, DialogListener
{
	private int width;
	private GenericDialog gd;
	private static final String[] operationNames = {"Mean", "Median", "Linear fit"};
	public static final int MEAN = 0, MEDIAN = 1, LINEAR_FIT = 2;
	private static int operationChoice = MEAN;
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
		gd = new GenericDialog("Subtract From Waveform...");
		gd.addChoice("Value to subtract:", operationNames, operationNames[operationChoice]);
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
		operationChoice = gd.getNextChoiceIndex();

		return true;
	}
		
	@Override
	public void run(ImageProcessor ip) 
	{
		float[] pixels = (float[])ip.getPixels();

		execute(pixels, width, operationChoice);
	}
	
	/**
	 * Subtracts the specified quantity of each waveform in {@code waveforms}
	 * from every element in the waveform. {@code waveforms} is a
	 * one-dimensional array composed of a series of concatenated records, each
	 * of length {@code recordLength}. The operation is carried out on 
	 * {@code waveforms} in place.
	 *
	 * @param waveforms	   array of concatenated waveforms, assumed to be of length
	 *                     {@code recordLength}
	 * @param recordLength length of each record in {@code waveforms}
	 * @param operation    {@code MEAN} subtracts the mean value of each
	 *                     waveform from all its elements; {@code MEDIAN}
	 *                     subtracts the median value of each waveform all its
	 *                     elements; {@code LINEAR_FIT} computes the best fit
	 *                     line to the entire waveform and subtracts from each
	 *                     point the value of the line at that point
	 */
	public static final void execute(float[] waveforms, int recordLength, int operation)
	{
		if (waveforms != null && waveforms.length >= recordLength && recordLength > 0 && waveforms.length % recordLength == 0) {

			// compute number of records
			int numRecords = waveforms.length / recordLength;

			switch (operation) {

				case MEAN: {
					for (int i = 0; i < numRecords; i++) {

						// offset to current record
						int offset = i * recordLength;

						// compute mean for current record
						float mean = WaveformUtils.mean(waveforms, offset, offset + recordLength);

						// subtract mean from current record in place
						WaveformUtils.addScalarInPlace(waveforms, offset, offset + recordLength, -mean);
					}

					break;
				}

				case MEDIAN: {
					for (int i = 0; i < numRecords; i++) {

						// offset to current record
						int offset = i * recordLength;

						// compute median for current record
						float[] temp = Arrays.copyOfRange(waveforms, offset, offset + recordLength);
						float median = WaveformUtils.medianAndSort(temp);

						// subtract mean from current record in place
						WaveformUtils.addScalarInPlace(waveforms, offset, offset + recordLength, -median);
					}

					break;
				}

				case LINEAR_FIT: {
					// precompute global parameters for linear fit
					double n = (double) recordLength;
					double sumX = 0.5 * n * (n - 1.0);
					double sumXsumX = sumX * sumX;
					double sumXX = sumX * (2.0 * n - 1.0) / 3.0;

					for (int i = 0; i < numRecords; i++) {

						// offset to current record
						int offset = i * recordLength;

						// compute linear fit parameters for current record
						double sumY = 0.0;
						double sumXY = 0.0;
						for (int j = 0; j < recordLength; j++) {
							sumY += waveforms[offset + j];
							sumXY += j * waveforms[offset + j];
						}
						double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumXsumX);
						double intercept = (sumY - slope * sumX) / n;

						// subtract median from current record in place
						double value = intercept;
						for (int j = 0; j < recordLength; j++) {
							waveforms[offset + j] -= (float)value;
							value += slope;
						}

					}

					break;
				}

				default: {
					break;
				}
				
			}

		}

	}
	
	public static final void execute(double[] waveforms, int recordLength, int operation)
	{
		if (waveforms != null && waveforms.length >= recordLength && recordLength > 0 && waveforms.length % recordLength == 0) {

			// compute number of records
			int numRecords = waveforms.length / recordLength;

			switch (operation) {

				case MEAN: {
					for (int i = 0; i < numRecords; i++) {

						// offset to current record
						int offset = i * recordLength;

						// compute mean for current record
						double mean = WaveformUtils.mean(waveforms, offset, offset + recordLength);

						// subtract mean from current record in place
						WaveformUtils.addScalarInPlace(waveforms, offset, offset + recordLength, -mean);
					}

					break;
				}

				case MEDIAN: {
					for (int i = 0; i < numRecords; i++) {

						// offset to current record
						int offset = i * recordLength;

						// compute median for current record
						double[] temp = Arrays.copyOfRange(waveforms, offset, offset + recordLength);
						double median = WaveformUtils.medianAndSort(temp);

						// subtract mean from current record in place
						WaveformUtils.addScalarInPlace(waveforms, offset, offset + recordLength, -median);
					}

					break;
				}

				case LINEAR_FIT: {
					// precompute global parameters for linear fit
					double n = (double) recordLength;
					double sumX = 0.5 * n * (n - 1.0);
					double sumXsumX = sumX * sumX;
					double sumXX = sumX * (2.0 * n - 1.0) / 3.0;

					for (int i = 0; i < numRecords; i++) {

						// offset to current record
						int offset = i * recordLength;

						// compute linear fit parameters for current record
						double sumY = 0.0;
						double sumXY = 0.0;
						for (int j = 0; j < recordLength; j++) {
							sumY += waveforms[offset + j];
							sumXY += j * waveforms[offset + j];
						}
						double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumXsumX);
						double intercept = (sumY - slope * sumX) / n;

						// subtract median from current record in place
						double value = intercept;
						for (int j = 0; j < recordLength; j++) {
							waveforms[offset + j] -= value;
							value += slope;
						}

					}

					break;
				}

				default: {
					break;
				}
				
			}

		}

	}

	@Override
	public void setNPasses(int nPasses) {}
}
