package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

/**
 * Computes the sum of the squared values of each input waveform and returns the
 * result in a new image. Each row in the input image is assumed to represent a
 * single waveform. The sum of the squared values of the {@code i}<SUP>th</SUP>
 * waveform (row) and {@code j}<SUP>th</SUP> slice is displayed in the
 * {@code i}<SUP>th</SUP> row and {@code j}<SUP>th</SUP> column of the output
 * image.
 *
 * @author Jon N. Marsh
 */

public class SumOfSquares implements ExtendedPlugInFilter
{
    private ImagePlus resultImp;
	private ImageProcessor resultProcessor;
	private float[] resultPixels;
    private int width, height, stackSize, resultWidth;
	private String title, resultTitle;
    private PlugInFilterRunner pfr;
    private final int flags = DOES_32 + DOES_STACKS + PARALLELIZE_STACKS + FINAL_PROCESSING;
	
    public int setup(String arg, ImagePlus imp) 
    {
        // perform final processing here
		if (arg.equals("final")) {
            if (resultImp != null) {
				resultImp.show();
                IJ.resetMinAndMax();
                return DONE;
            }
        }

        if (imp == null) {
            IJ.noImage();
            return DONE;
        }

        width = imp.getWidth();
        height = imp.getHeight();
		stackSize = imp.getStackSize();
		title = imp.getTitle();
		resultWidth = stackSize;
		resultTitle = title + " sum of squares";
		
		resultImp = IJ.createImage(resultTitle, "32-bit", resultWidth, height, 1);
		resultProcessor = resultImp.getProcessor();
		resultPixels = (float[])resultProcessor.getPixels();

        return flags;
    }	

    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
    {
        // No dialog needed for this plugin, but this method allows access to the PlugInFilterRunner
		this.pfr = pfr;

		return flags;
    }

    public void run(ImageProcessor ip) 
    {
        int currentSlice = pfr.getSliceNumber();
        float[] pixels = (float[])ip.getPixels();

		float[] sumSqrs = execute(pixels, width);
		for (int i=0; i<height; i++) {
			resultPixels[i*resultWidth+(currentSlice-1)] = sumSqrs[i];
		}
    }

	/**
	 * Returns an array representing the sum of the squared values of each record in
	 * {@code waveforms}, where each record has {@code recordLength} elements.
	 * Output is null if {@code waveforms==null}, {@code recordLength<=0},
	 * {@code waveforms.length<recordLength}, or if {@code waveforms.length} is
	 * not evenly divisible by {@code recordLength}.
	 *
	 * @param waveforms    one-dimensional array composed of a series of
	 *                     concatenated records, each of size equal to
	 *                     {@code recordLength}
	 * @param recordLength size of each record in {@code waveforms}
	 * @return array of sum of the squared values of input waveforms
	 */
	public static float[] execute(float[] waveforms, int recordLength)
	{
		if (waveforms != null && recordLength > 0 && waveforms.length >= recordLength && waveforms.length%recordLength == 0) {
			
			// compute number of records
			int numRecords = waveforms.length/recordLength;
			
			// allocate output array
			float[] sumSqrs = new float[numRecords];
			
			// loop over all records
			for (int i=0; i<numRecords; i++) {
				
				// compute row offset
				int offset = i*recordLength;
				
				// compute sum of the squared values of current waveform
				double sum = 0.0;
				for (int j=0; j<recordLength; j++) {
					double value = waveforms[offset+j];
					sum += value*value;
				}
				sumSqrs[i] = (float)sum;

			}
			
			return sumSqrs;
				
		}
		
		return null;
	}
	
	/**
	 * Returns an array representing the sum of the squared values of each record in
	 * {@code waveforms}, where each record has {@code recordLength} elements.
	 * Output is null if {@code waveforms==null}, {@code recordLength<=0},
	 * {@code waveforms.length<recordLength}, or if {@code waveforms.length} is
	 * not evenly divisible by {@code recordLength}.
	 *
	 * @param waveforms    one-dimensional array composed of a series of
	 *                     concatenated records, each of size equal to
	 *                     {@code recordLength}
	 * @param recordLength size of each record in {@code waveforms}
	 * @return array of sum of the squared values of input waveforms
	 */
	public static double[] execute(double[] waveforms, int recordLength)
	{
		if (waveforms != null && recordLength > 0 && waveforms.length >= recordLength && waveforms.length%recordLength == 0) {
			
			// compute number of records
			int numRecords = waveforms.length/recordLength;
			
			// allocate output array
			double[] sumSqrs = new double[numRecords];
			
			// loop over all records
			for (int i=0; i<numRecords; i++) {
				
				// compute row offset
				int offset = i*recordLength;
				
				// compute sum of the squared values of current waveform
				double sum = 0.0;
				for (int j=0; j<recordLength; j++) {
					double value = waveforms[offset+j];
					sum += value*value;
				}
				sumSqrs[i] = sum;

			}
			
			return sumSqrs;
				
		}
		
		return null;
	}
	
    public void setNPasses(int nPasses) {}
	
}
