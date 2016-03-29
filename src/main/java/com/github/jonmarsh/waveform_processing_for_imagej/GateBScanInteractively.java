package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.util.Tools;
import java.awt.AWTEvent;
import java.awt.Choice;
import java.awt.Polygon;
import java.awt.TextField;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This plug-in filter is used to auto-detect borders in a B-scan for the
 * purpose of creating gates and/or windowed waveforms from input data. Each
 * horizontal line of the input image is assumed to represent an individual
 * record. Gate positions are set by searching for the first peak (or nadir)
 * above (or below) a desired threshold level, or the first threshold crossing.
 * The output can be either a new image comprised of the gated waveform
 * segments, the original waveforms set to zero outside the gates, a single ROI
 * corresponding to the gated portion of each individual record, a single
 * polygon ROI that encircles each all gated regions, or a single polyline ROI
 * that follows the detected border.  The output gates can be applied to the 
 * input image, or a different image with the same dimensions.
  * 
 * @author Jon N. Marsh
 */

public class GateBScanInteractively implements ExtendedPlugInFilter, DialogListener
{
	private ImagePlus imp, altImage;
	private ImageProcessor processor;
	private float[] pixels, reversedPixels;
	private int[] gatePositions = null;
	private int recordLength, numberOfRecords;
	private static int autoStartSearchIndex = 0;
	private static int offsetIndex = 0;
	private static int gateLengthPoints = 10;
	private static float threshold = 0.0f;
	private static int smoothingRadius = 1;
	private static boolean searchBackwards = false;
	private String[] suitableImageTitles;
	private static final int POS_THRESHOLD = 0, NEG_THRESHOLD = 1, POS_PEAK = 2, NEG_PEAK = 3;
	private static final String[] detectionTypes = {"Positive-going threshold", "Negative-going threshold", "First peak above threshold", "First nadir below threshold"};
	private static int detectionType = POS_PEAK;
	private static final int SINGLE_ROI = 0, MULTIPLE_ROIS = 1, BORDER_LINE_ROI = 2;
	private static final String[] roiOutputTypes = {"Gated region (polygon ROI)", "Gated region (multiple line ROIs)", "Border line ROI"};
	private static int roiOutputChoice = MULTIPLE_ROIS;
	private static final int NO_SIGNAL_OUTPUT = 0, GATED_SEGMENTS = 1, GATED_WAVEFORMS = 2;
	private static final String[] signalOutputTypes = {"None", "Gated segments", "Gated waveforms"};
	private static int signalOutputChoice = NO_SIGNAL_OUTPUT;
	private static boolean isWindowFunctionApplied = false;
	private static final String[] windowTypes = WaveformUtils.WindowType.stringValues();
	private static int windowChoice = WaveformUtils.WindowType.HAMMING.ordinal();
	private Choice windowTypeComboBox; 
	private static double windowParameter = 0.5;
	private TextField windowParameterTextField;
	private int altImageIndex = 0;
	private GenericDialog gd;
	private final int flags = DOES_32 + FINAL_PROCESSING;
	
	@Override
	public int setup(String arg, ImagePlus imp) 
	{
		if (arg.equals("final")) {
			doFinalProcessing();
			return DONE;
		}
		
		this.imp = imp;
		if (imp == null) {
			IJ.noImage();
			return DONE;
		}
		
		if (imp.getType() != ImagePlus.GRAY32) {
			IJ.error("Image must be 32-bit grayscale");
			return DONE;
		}
		
		if (imp.getRoi() != null) {
			imp.setRoi(0, 0, 0, 0);
		}
		
		processor = imp.getProcessor();
		recordLength = imp.getWidth();
		numberOfRecords = imp.getHeight();
		pixels = (float[])processor.getPixelsCopy();
		reversedPixels = Arrays.copyOf(pixels, pixels.length);
		for (int i=0; i<numberOfRecords; i++) {
			WaveformUtils.reverseArrayInPlace(reversedPixels, i*recordLength, (i+1)*recordLength);
		}
		gatePositions = new int[numberOfRecords];
		suitableImageTitles = getMatchingImages();		
				
		return flags;
	}

	@Override
	public int showDialog(ImagePlus ip, String string, PlugInFilterRunner pfr)
	{		
		gd = new GenericDialog("Create B-Scan Gates for \""+imp.getTitle()+"\"");
		
		gd.addNumericField("Start search at index", autoStartSearchIndex, 0, 8, "");
		gd.addNumericField("Offset from detected border", offsetIndex, 0, 8, "points");
		gd.addNumericField("Threshold", threshold, 3, 8, "");
		gd.addNumericField("Gate length", gateLengthPoints, 0, 8, "points");
		gd.addChoice("Detection method:", detectionTypes, detectionTypes[detectionType]);
		gd.addCheckbox("Reverse search", searchBackwards);
		gd.addNumericField("Smoothing radius", smoothingRadius, 0, 8, "points");
		gd.addChoice("ROI output:", roiOutputTypes, roiOutputTypes[roiOutputChoice]);
		gd.addChoice("Apply gates to:", suitableImageTitles, suitableImageTitles[altImageIndex]);
		gd.addChoice("Signal output:", signalOutputTypes, signalOutputTypes[signalOutputChoice]);
		gd.addCheckbox("Apply window function", isWindowFunctionApplied);
		gd.addChoice("Window type:", windowTypes, windowTypes[windowChoice]);
		windowTypeComboBox = (Choice)(gd.getChoices().get(4));
		windowTypeComboBox.setEnabled(isWindowFunctionApplied);
		gd.addNumericField("Window parameter", windowParameter, 3, 8, "");
		windowParameterTextField = (TextField)(gd.getNumericFields().get(5));
		windowParameterTextField.setEnabled(isWindowFunctionApplied && WaveformUtils.WindowType.values()[windowChoice].usesParameter());
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
		autoStartSearchIndex = (int)gd.getNextNumber();
		offsetIndex = (int)gd.getNextNumber();
		threshold = (float)gd.getNextNumber();
		gateLengthPoints = (int)gd.getNextNumber();		
		detectionType = gd.getNextChoiceIndex();
		searchBackwards = gd.getNextBoolean();
		roiOutputChoice = gd.getNextChoiceIndex();
		smoothingRadius = (int)gd.getNextNumber();
		altImageIndex = gd.getNextChoiceIndex();
		signalOutputChoice = gd.getNextChoiceIndex();
		isWindowFunctionApplied = gd.getNextBoolean();
		windowChoice = gd.getNextChoiceIndex();
		windowParameter = gd.getNextNumber();
		
		windowTypeComboBox.setEnabled(isWindowFunctionApplied);
		windowParameterTextField.setEnabled(isWindowFunctionApplied && WaveformUtils.WindowType.values()[windowChoice].usesParameter());
		
		return (!gd.invalidNumber() 
				&& (autoStartSearchIndex >= 0 && autoStartSearchIndex < recordLength-1)
				&& (gateLengthPoints > 0 && gateLengthPoints <= recordLength )
				&& (smoothingRadius >= 0 && smoothingRadius <= numberOfRecords));

	}
		
	private void doFinalProcessing()
	{
		if (altImageIndex != 0) {
			altImage = WindowManager.getImage(suitableImageTitles[altImageIndex]);
		} else {
			altImage = imp;
		}
		
		float[] weights;
		if (isWindowFunctionApplied) {
			weights = Tools.toFloat(WaveformUtils.windowFunction(WaveformUtils.WindowType.values()[windowChoice], gateLengthPoints, windowParameter, false));
		} else {
			weights = new float[gateLengthPoints];
			Arrays.fill(weights, 1.0f);
		}

		if (roiOutputChoice != BORDER_LINE_ROI) { // Don't output any waveforms if a border ROI is desired
			if (signalOutputChoice == GATED_SEGMENTS) {
				ImagePlus gatedImage = IJ.createImage(altImage.getTitle()+" gated segments", "32-bit", gateLengthPoints, numberOfRecords, 1);
				ImageProcessor gatedImageProcessor = gatedImage.getProcessor();
				float[] gatedPixels = gateSegments(altImage, gatePositions, gateLengthPoints, weights, searchBackwards);
				gatedImageProcessor.setPixels(gatedPixels);
				gatedImage.show();
				IJ.resetMinAndMax();
			} else if (signalOutputChoice == GATED_WAVEFORMS) {
				ImagePlus gatedImage = IJ.createImage(altImage.getTitle() + " gated waveforms", "32-bit", recordLength, numberOfRecords, 1);
				ImageProcessor gatedImageProcessor = gatedImage.getProcessor();
				float[] gatedPixels = gateWaveforms(altImage, gatePositions, gateLengthPoints, weights, searchBackwards);
				gatedImageProcessor.setPixels(gatedPixels);
				gatedImage.show();
				IJ.resetMinAndMax();			
			}
		}
		
		RoiManager rm = RoiManager.getInstance();
		if (rm == null) {
			rm = new RoiManager();
		}
		if (roiOutputChoice == MULTIPLE_ROIS) {
			generateMultipleLineROIs(rm, gatePositions, gateLengthPoints, searchBackwards);
		} else {
			rm.add(imp, imp.getRoi(), -1);
		}
		
	}
	
	@Override
	public void run(ImageProcessor ip)
	{		
		// All this does is display a ROI showing gate/border values; the actual output is performed in the "final" processing step
		gatePositions = medianFilter1D(computeGateStartPositions(pixels, reversedPixels, recordLength, numberOfRecords, autoStartSearchIndex, offsetIndex, threshold, searchBackwards), smoothingRadius, -1);
		if (roiOutputChoice == BORDER_LINE_ROI) {	
			PolygonRoi roi = createBorderROI(gatePositions);
			imp.setRoi(roi);
		}
		else {
			PolygonRoi roi = createSingleROI(gatePositions, gateLengthPoints, searchBackwards);
			imp.setRoi(roi);
		}
	}

	/* Extract gated waveform segments and apply window function if necessary */
	private float[] gateSegments(ImagePlus imp, int[] gateStartPositions, int gateLength, float[] weights, boolean searchBackwards)
	{
		float[] pix = (float[])(imp.getProcessor()).getPixels();
		int h = imp.getHeight();
		int w = imp.getWidth();
		float[] gatedArray = new float[h * gateLength];
		Arrays.fill(gatedArray, Float.NaN);

		for (int i = 0; i < h; i++) {
			int gateStart = gateStartPositions[i];
			int rowOffset1 = i*w;
			int rowOffset2 = i*gateLength;
			if (gateStart < 0 || gateStart > w-1) { // invalid gate, so no boundary detected -- set all values to NaN)
				for (int j=0; j<gateLength; j++) {
					// array already initialized to NaN, so nothing to do
				}
			} else if (gateStart + gateLength > w && !searchBackwards) { // end of gate set past end of waveform, so pad with NaN
				for (int j=0; j<w-gateStart; j++) {
					gatedArray[rowOffset2+j] = weights[j]*pix[rowOffset1+gateStart+j];
				}
			} else if (gateStart < (gateLength-1) && searchBackwards) { // beginning of gate set past beginning of waveform, so pad with zeroes
				for (int j=0; j<gateStart+1; j++) {
					gatedArray[rowOffset2+(gateLength-(gateStart+1))+j] = weights[gateLength-(gateStart+1)+j]*pix[rowOffset1+j];
				}
			} else { // gate is within waveform limits
				int start = searchBackwards ? gateStart-gateLength+1 : gateStart;
				for (int j=0; j<gateLength; j++) {
					gatedArray[rowOffset2+j] = weights[j]*pix[rowOffset1+start+j];
				}
			}
		}
		
		return gatedArray;
	}

	/* Gate waveforms and apply window function if necessary */
	private float[] gateWaveforms(ImagePlus imp, int[] gateStartPositions, int gateLength, float[] weights, boolean searchBackwards)
	{
		float[] pix = (float[])(imp.getProcessor()).getPixels();
		int h = imp.getHeight();
		int w = imp.getWidth();
		float[] gatedArray = new float[h * w];
		
		for (int i = 0; i < h; i++) {
			int gateStart = gateStartPositions[i];
			int rowOffset = i*w;
			if (gateStart < 0 || gateStart > w-1) { // invalid gate, so no boundary detected -- set all values to zero)
				for (int j=0; j<gateLength; j++) {
					// array already initialized to zero, so nothing to do
				}
			} else if (gateStart + gateLength > w && !searchBackwards) { // end of gate set past end of waveform, so just pad with zeroes
				for (int j=0; j<w-gateStart; j++) {
					gatedArray[rowOffset+gateStart+j] = weights[j]*pix[rowOffset+gateStart+j];
				}
			} else if (gateStart < (gateLength-1) && searchBackwards) { // beginning of gate set past beginning of waveform, so pad with zeroes
				for (int j=0; j<gateStart+1; j++) {
					gatedArray[rowOffset+j] = weights[(gateLength-gateStart-1)+j]*pix[rowOffset+j];
				}
			} else { // gate is within waveform limits
				int start = searchBackwards ? gateStart-gateLength+1 : gateStart;
				for (int j = 0; j < gateLength; j++) {
					gatedArray[rowOffset + start + j] = weights[j] * pix[rowOffset + start + j];
				}
			}
		}
		
		return gatedArray;
	}
	
	/* Generate border line ROI */
	private PolygonRoi createBorderROI(int[] gateStartPositions)
	{
		int length = gateStartPositions.length;
		int[] xPoints = new int[length * 2];
		int[] yPoints = new int[length * 2];
		for (int i = 0; i < length; i++) {
			int j = i*2;
			xPoints[j] = gateStartPositions[i];
			xPoints[j+1] = gateStartPositions[i];
			yPoints[j] = i;
			yPoints[j+1] = (j/2)+1;			
		}
		
		return new PolygonRoi(xPoints, yPoints, 2*gateStartPositions.length, Roi.POLYLINE);
	}

	/* Generate single ROI for entire image */
	private PolygonRoi createSingleROI(int[] gateStartPositions, int gateLength, boolean searchBackwards)
	{
		int offset = searchBackwards ? -gateLength + 1 : gateLength;
		int length = gateStartPositions.length;
		int[] xPoints = new int[length * 4];
		int[] yPoints = new int[length * 4];
		for (int i = 0; i < length; i++) {
			int j = i*2;
			xPoints[j] = gateStartPositions[i];
			xPoints[j+1] = gateStartPositions[i];
			yPoints[j] = i;
			yPoints[j+1] = (j/2)+1;			
		}
		for (int i=xPoints.length-1, j=0; i>=length*2; i--, j++) {
			xPoints[i] = xPoints[j] + offset;
			yPoints[i] = yPoints[j];
		}
		
		return new PolygonRoi(new Polygon(xPoints, yPoints, length * 4), Roi.POLYGON);
	}
	
	/* Generate ROIs for individual lines and add to RoiManager */
	private void generateMultipleLineROIs(RoiManager rm, int[] gateStartPositions, int gateLength, boolean searchBackwards)
	{
		int length = gateStartPositions.length;
		for (int i=0; i<length; i++) {
			int start = searchBackwards ? (gateStartPositions[i] - gateLength) + 1 : gateStartPositions[i];
			rm.addRoi(new Roi(start, i, gateLength, 1));
		}
	}
	
	/* Computes gate start positions */
	private int[] computeGateStartPositions(float[] pixels, float[] reversedPixels, int recordLength, int numberOfRecords, int searchStartPoint, int offsetPoint, float threshold, boolean searchBackwards)
	{
		float[] pix = searchBackwards ? reversedPixels : pixels;
		float[] tempArray = new float[recordLength];
		int[] gateStartPositions = new int[numberOfRecords];

		for (int i = 0; i < numberOfRecords; i++) {
			int gateStart;

			System.arraycopy(pix, (i * recordLength) + searchStartPoint, tempArray, 0, recordLength - searchStartPoint);
			switch (detectionType) {
				case POS_THRESHOLD:
					gateStart = thresholdDetectPositive(tempArray, threshold);
					break;
				case NEG_THRESHOLD:
					gateStart = thresholdDetectNegative(tempArray, threshold);
					break;
				case POS_PEAK:
					gateStart = peakDetectMaximum(tempArray, threshold);
					break;
				case NEG_PEAK:
					gateStart = peakDetectMinimum(tempArray, threshold);
					break;
				default:
					gateStart = -1;
			}
			if (gateStart >= 0) { // valid gate point
				gateStartPositions[i] = gateStart + offsetPoint + searchStartPoint;
				if (searchBackwards) {
					gateStartPositions[i] = (recordLength-1) - gateStartPositions[i];
				}
			} else { // if no valid gate point was found, set to position just outside array
				gateStartPositions[i] = searchBackwards ? -1 : recordLength;
			}

		}

		return gateStartPositions;
	}

	/* Returns the index of the first maximum peak value found which exceeds the specified threshold.  Returns -1 if no peaks above threshold are detected. */
	private int peakDetectMaximum(float[] a, float threshold)
	{
		for (int i=1; i<a.length-1; i++) {
			if (a[i] > threshold && a[i-1] < a[i] && a[i+1] < a[i]) {
				return i;
			}
		}
		
		return -1;
	}

	/* Returns the index of the first minimum peak value found which is less than the specified threshold.  Returns -1 if no peaks below threshold are detected. */
	private int peakDetectMinimum(float[] a, float threshold)
	{
		for (int i=1; i<a.length-1; i++) {
			if (a[i] < threshold && a[i-1] > a[i] && a[i+1] > a[i]) {
				return i;
			}
		}
		
		return -1;
	}

	/* Returns the index of the first point which exceeds the specified threshold.  Returns -1 if no values above threshold are detected. */
	private int thresholdDetectPositive(float[] a, float threshold)
	{
		for (int i=0; i<a.length-1; i++) {
			if (a[i]<=threshold && a[i+1]>threshold) {
				return i+1;
			}
		}
		
		return -1;
	}

	/* Returns the index of the first point which is less than the specified threshold.  Returns -1 if no values below threshold are detected. */
	private int thresholdDetectNegative(float[] a, float threshold)
	{
		for (int i=0; i<a.length-1; i++) {
			if (a[i]>=threshold && a[i+1]<threshold) {
				return i+1;
			}
		}
		
		return -1;
	}

	private int[] medianFilter1D(int[] a, int radius, int valueToIgnore)
	{
		int[] filteredArray = new int[a.length];
		
		if (radius > 0) {
			for (int i=0; i<a.length; i++) {
				filteredArray[i] = getMedianValueAtIndex(a, i, radius, valueToIgnore);
			}
		} else {
			System.arraycopy(a, 0, filteredArray, 0, a.length);
		}
		
		return filteredArray;
	}

	private int getMedianValueAtIndex(int[] a, int index, int radius, int valueToIgnore)
	{
		int minIndex = Math.max(0, index-radius);
		int maxIndex = Math.min(a.length-1, index+radius);
		int span = (maxIndex-minIndex)+1;
		int[] temp = new int[span];
		ArrayList<Integer> v = new ArrayList<>();

		System.arraycopy(a, minIndex, temp, 0, span);
		Arrays.sort(temp);

		for (int i=0; i<span; i++) {
			if (temp[i] != valueToIgnore) {
				v.add(temp[i]);
			}
		}

		int size = v.size();
		if (size == 1) {
			return(v.get(0));
		}
		else if (size%2 == 1) {
			return(v.get((size-1)/2));
		}
		else if (size != 0) {
			return (int)( (v.get(size/2)+v.get((size/2)-1))/2 );
		}
		else {
			return valueToIgnore;
		}	
	}

	/* Returns list of matching images to blend (i.e. images with same dimensions and stacksizes) */
	private String[] getMatchingImages()
	{
		int width = recordLength;
		int height = numberOfRecords;
		int thisID = imp.getID();
		int[] fullList = WindowManager.getIDList();
		ArrayList<String> matches = new ArrayList<>(fullList.length);
		matches.add("Current image");
		for (int i=0; i<fullList.length; i++) {
			ImagePlus imp2 = WindowManager.getImage(fullList[i]);
			if (imp2.getWidth()==width && imp2.getHeight()==height && fullList[i]!=thisID) {
				String name = imp2.getTitle();
				if (!matches.contains(name)) {
					matches.add(name);
				}
			}
		}
        String[] matchingImages = new String[matches.size()];
		for (int i=0; i<matches.size(); i++) {
			matchingImages[i] = (String)matches.get(i);
		}
		return matchingImages;
	}

	@Override
	public void setNPasses(int i) {}
	
}

