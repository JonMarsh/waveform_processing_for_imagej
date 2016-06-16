package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import ij.util.Tools;

/**
 * Computes the mean waveform from a set of input waveforms.  Each row in the 
 * input image is assumed to represent a single waveform.  If the input image is
 * a stack, each slice of the output is the  mean waveform from the original 
 * input slice. The original image is replaced by the resulting average(s).
 * 
 * @author Jon N. Marsh
 */

public class AverageWaveforms implements ExtendedPlugInFilter
{
    private ImagePlus avgImp;
	ImageStack stack, avgStack;
    private int width, height, stackSize;
	private String title;
    private PlugInFilterRunner pfr;
    private final int flags = DOES_32 + DOES_STACKS + PARALLELIZE_STACKS + FINAL_PROCESSING;
	
	@Override
    public int setup(String arg, ImagePlus imp) 
    {
		if (arg.equals("final")) {
            if (avgImp != null) {
				imp.setStack(title+" average", avgStack);
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
		
		if (height == 1 && stackSize == 1) {
			return DONE;
		}
		
		avgImp = IJ.createImage("", "32-bit", width, 1, stackSize);
		avgStack = avgImp.getStack();
		
        return flags;
    }	
		
	@Override
	public void run(ImageProcessor ip) 
	{
		// retrieve image slice being operated on
		int currentSlice = pfr.getSliceNumber();
		
		// get pixel array reference for output data for this slice 
		ImageProcessor avgProcessor = avgStack.getProcessor(currentSlice);
		float[] avgPixels = (float[])avgProcessor.getPixels();
		
		// get pixel array reference for current input data
		float[] pixels = (float[])ip.getPixels();
		double[] pixelsDouble = Tools.toDouble(pixels);

		// do averaging
		double[] avg = execute(pixelsDouble, width);
		
		// copy average waveform into output pixel array
		for (int i=0; i<width; i++) {
			avgPixels[i] = (float)avg[i];
		}
	}
	
	/**
	 * Returns a single array of size {@code recordLength} that is the element-by-element
	 * average of each record in {@code waveforms}.  {@code waveforms} is a one-dimensional
	 * array composed of a series of concatenated waveforms, each of size {@code recordLength}.
	 * 
	 * @param waveforms		array of concatenated waveforms, assumed to of length {@code recordLength}
	 * @param recordLength	size of each record in {@code waveforms}
	 * @return				array of size {@code recordLength} that represents the 
	 *						element-by-element average of each record in {@code waveforms}
	 *						(null if {@code waveforms==null}, {@code recordLength>waveforms.length}, or {@code recordLength<=0})
	 */
	public static double[] execute(double[] waveforms, int recordLength)
	{
		if (waveforms == null || recordLength > waveforms.length || recordLength <= 0) {
			return null;
		}
		
		double[] avgWaveform = new double[recordLength];
		int numRecords = waveforms.length/recordLength;
		
		// loop over elements in output average waveform
		for (int i=0; i<recordLength; i++) {
			
			double sum = 0.0;
			
			// loop over all input waveform values at index i
			for (int j=0; j<numRecords; j++) {
				sum += waveforms[j*recordLength+i];
			}
			avgWaveform[i] = sum/numRecords;
		}
		
		return avgWaveform;
	}
	
	// no dialog is displayed for this plugin, but we use this method to get a reference to the PlugInFilterRunner
	@Override
	public int showDialog(ImagePlus ip, String string, PlugInFilterRunner pfr)
	{
		this.pfr = pfr;
		
		return flags;
	}

	@Override
	public void setNPasses(int i)
	{
	}
		
}
