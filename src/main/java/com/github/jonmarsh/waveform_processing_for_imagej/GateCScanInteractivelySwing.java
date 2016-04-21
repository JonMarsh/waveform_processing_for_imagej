package com.github.jonmarsh.waveform_processing_for_imagej;

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.plugin.filter.RankFilters;
import ij.plugin.filter.GaussianBlur;
import java.awt.Color;
import java.awt.Panel;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Jon N. Marsh
 */
public class GateCScanInteractivelySwing implements PlugIn
{
	static int autoStartSearchPoint = 0;
	static int offsetPoint = 0;
	static int gateLength = 10;
	static float threshold = 0.0f;
	static boolean searchBackwards = false;
	static final int MEDIAN = 0;
	static final int GAUSSIAN = 1;
	static final int MEAN = 2;
	static final String[] filters = {"Median", "Gaussian", "Mean"};
	static int filterSelection = MEDIAN;
	static final int PEAK_DETECT = 0;
	static final int THRESHOLD_DETECT = 1;
	static final String[] detectionMethods = {"Peak", "Threshold"};
	static int detectionMethodSelection = PEAK_DETECT;
	static double smoothingRadius = 1.0;
	static boolean outputGatePositions = true;
	static boolean outputGateROIs = true;
	static boolean outputGatedSegments = true;
	static boolean outputGatedWaveforms = true;
	static String pluginTitle = "Create gates for ";
	static GateCScanInteractivelySwing instance;

	ImagePlus inputImage, gateImage;
	ImageStack stack;
	ImageCanvas gateCanvas;
	boolean gatesExist = false;
	int recordLength, recordsPerFrame, numberOfFrames, currentSlice, inputImageID;
	double[] globalMinAndMax;
	String title;
	String[] suitableImages;
	int[] suitableImageIDs;
	ImageProcessor gateProcessor;

	@Override
	public void run(String arg)
	{
		if (instance == null) {
			inputImage = IJ.getImage();
			if (inputImage != null) {
				instance = this;
				inputImageID = inputImage.getID();
				stack = inputImage.getStack();
				title = inputImage.getTitle();
				recordLength = inputImage.getWidth();
				recordsPerFrame = inputImage.getHeight();
				numberOfFrames = inputImage.getImageStackSize();
				currentSlice = inputImage.getSlice();
				globalMinAndMax = WaveformUtils.getGlobalMinAndMax(inputImage);
				gateImage = IJ.createImage(pluginTitle + title, "32-bit", recordsPerFrame, numberOfFrames, 1);
				gateProcessor = gateImage.getProcessor();
				gateCanvas = new ImageCanvas(gateImage);
				suitableImageIDs = getSuitableImageIDs(inputImage, gateImage);

				CreateGatesForCScanDialog createGatesForCScanDialog = new CreateGatesForCScanDialog(gateImage, gateCanvas);
			} else {
				IJ.noImage();
			}
		} else {
			WindowManager.toFront(WindowManager.getFrame(gateImage.getTitle()));
		}
	}

	private class CreateGatesForCScanDialog extends ImageWindow implements ActionListener, ImageListener
	{

		Panel mainPanel;
		GateCScanInteractivelySwingControlPanel panel;

		/* Constructor */
		CreateGatesForCScanDialog(ImagePlus gateImagePlus, ImageCanvas gateImageCanvas)
		{
			super(gateImagePlus, gateImageCanvas);
			addPanel();
			ImagePlus.addImageListener(this);
		}

		/* Add control panel underneath gate image on user interface window */
		private void addPanel()
		{
			panel = new GateCScanInteractivelySwingControlPanel();
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
				Logger.getLogger(GateCScanInteractivelySwing.class.getName()).log(Level.SEVERE, null, ex);
			}
			SwingUtilities.updateComponentTreeUI(panel);
			
			panel.searchStartPointTextField.setText("" + autoStartSearchPoint);
			panel.offsetTextField.setText("" + offsetPoint);
			panel.thresholdTextField.setText(IJ.d2s(threshold, 3));
			panel.gateLengthTextField.setText("" + gateLength);
			panel.searchBackwardsCheckbox.setSelected(searchBackwards);
			panel.createGatesButton.addActionListener(this);
			panel.smoothGatesButton.addActionListener(this);
			for (String filter : filters) {
				panel.filterComboBox.addItem(filter);
			}
			panel.filterComboBox.setSelectedIndex(filterSelection);
			for (String detectionMethod : detectionMethods) {
				panel.detectionMethodComboBox.addItem(detectionMethod);
			}
			panel.detectionMethodComboBox.setSelectedIndex(detectionMethodSelection);
			panel.smoothingRadiusTextField.setText(IJ.d2s(smoothingRadius, 3));
			panel.outputGatePositionsCheckbox.setSelected(outputGatePositions);
			panel.outputGateROIsCheckbox.setSelected(outputGateROIs);
			panel.outputGatedRegionsCheckbox.setSelected(outputGatedSegments);
			panel.outputGatedWaveformsCheckbox.setSelected(outputGatedWaveforms);
			for (int imageID : suitableImageIDs) {
				panel.gateApplicationComboBox.addItem(WindowManager.getImage(imageID).getTitle());
			}
			panel.cancelButton.addActionListener(this);
			panel.okButton.addActionListener(this);

			mainPanel = new Panel();
			BoxLayout mainLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
			mainPanel.setLayout(mainLayout);
			mainPanel.add(panel);

			add(mainPanel);
			pack();
		}

		/* Respond to button press on front panel */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton source = (JButton)e.getSource();
			
			if (source == panel.cancelButton) {
				ImagePlus.removeImageListener(this);
				this.close();
				instance = null;
			}
			
			try {
				autoStartSearchPoint = Integer.parseInt(panel.searchStartPointTextField.getText());
				offsetPoint = Integer.parseInt(panel.offsetTextField.getText());
				threshold = Float.parseFloat(panel.thresholdTextField.getText());
				gateLength = Integer.parseInt(panel.gateLengthTextField.getText());
				smoothingRadius = Double.parseDouble(panel.smoothingRadiusTextField.getText());
				searchBackwards = panel.searchBackwardsCheckbox.isSelected();
				
				if (source == panel.createGatesButton) {
					detectionMethodSelection = panel.detectionMethodComboBox.getSelectedIndex();
					float[] gatePositions = computeGateStartPositionsForStack(stack, autoStartSearchPoint, offsetPoint, threshold, detectionMethodSelection, searchBackwards);
					gateProcessor.setPixels(gatePositions);
					Overlay overlay = new Overlay(new Line(0, currentSlice - 1, recordsPerFrame, currentSlice - 1));
					overlay.setStrokeColor(Color.red);
					gateImage.setOverlay(overlay);
					gatesExist = true;
					IJ.resetMinAndMax();
					float[] gatePositionsForDisplayedSlice = Arrays.copyOfRange(gatePositions, (currentSlice - 1) * recordsPerFrame, currentSlice * recordsPerFrame);
					drawSingleROI(WindowManager.getImage(inputImageID), gatePositionsForDisplayedSlice, gateLength, searchBackwards);
				}
				
				if (source == panel.smoothGatesButton) {
					if (gatesExist) {
						filterSelection = panel.filterComboBox.getSelectedIndex();
						switch (filterSelection) {
							case MEDIAN: {
								RankFilters rf = new RankFilters();
								rf.rank(gateProcessor, smoothingRadius, RankFilters.MEDIAN);
								break;
							}
							case GAUSSIAN: {
								GaussianBlur gb = new GaussianBlur();
								gb.blurGaussian(gateProcessor, smoothingRadius, smoothingRadius, 0.01);
								break;
							}
							case MEAN: {
								RankFilters rf = new RankFilters();
								rf.rank(gateProcessor, smoothingRadius, RankFilters.MEAN);
								break;
							}
							default:
						}
						gateImage.updateAndDraw();
						IJ.resetMinAndMax();
						float[] gatePositions = (float[]) gateProcessor.getPixels();
						float[] gatePositionsForDisplayedSlice = Arrays.copyOfRange(gatePositions, (currentSlice - 1) * recordsPerFrame, currentSlice * recordsPerFrame);
						drawSingleROI(WindowManager.getImage(inputImageID), gatePositionsForDisplayedSlice, gateLength, searchBackwards);
					}
				}
				
				if (source == panel.okButton) {
					if (gatesExist) {
						/*
						 * Assign user values to variables used to indicate
						 * checkbox selections -- this is done only to ensure the
						 * static variables are reinitialized to previous values
						 * next time plugin is called
						 */
						searchBackwards = panel.searchBackwardsCheckbox.isSelected();
						outputGateROIs = panel.outputGateROIsCheckbox.isSelected();
						outputGatePositions = panel.outputGatePositionsCheckbox.isSelected();
						outputGatedSegments = panel.outputGatedRegionsCheckbox.isSelected();
						outputGatedWaveforms = panel.outputGatedWaveformsCheckbox.isSelected();
						
						int selectedImageID = suitableImageIDs[panel.gateApplicationComboBox.getSelectedIndex()]; // apply gating to the desired image (not necessarily the image the gates were computed with)
						float[] gatePositions = (float[]) gateProcessor.getPixels();
						
						if (outputGateROIs) {
							RoiManager rm = RoiManager.getInstance();
							if (rm == null) {
								rm = new RoiManager();
							}
							for (int i = 0; i < WindowManager.getImage(inputImageID).getStackSize(); i++) {
								WindowManager.getImage(inputImageID).setSlice(i + 1);
								float[] gatePositionsForThisSlice = Arrays.copyOfRange(gatePositions, i * recordsPerFrame, (i + 1) * recordsPerFrame);
								PolygonRoi roi = createSingleROI(gatePositionsForThisSlice, gateLength, searchBackwards);
								roi.setPosition(i+1);
								rm.add(WindowManager.getImage(inputImageID), roi, -1);
							}
						}
						if (outputGatedSegments) {
							ImagePlus gatedImage = createGatedImage(WindowManager.getImage(selectedImageID), gatePositions, gateLength, searchBackwards);
							gatedImage.show();
							IJ.resetMinAndMax();
						}
						if (outputGatePositions) {
							ImagePlus borderImage = new ImagePlus(title + " gate positions", gateProcessor.duplicate());
							borderImage.show();
						}
						if (outputGatedWaveforms) {
							ImagePlus gatedWaveformImage = createdGatedWaveformImage(WindowManager.getImage(selectedImageID), gatePositions, gateLength, searchBackwards);
							gatedWaveformImage.show();
							IJ.resetMinAndMax();
						}
					}
					ImagePlus.removeImageListener(this);
					this.close();
					instance = null;
				}
				
			} catch (NumberFormatException nfe) {
				IJ.error("Invalid input parameter");
			}
		}

		/* Respond to image being closed */
		@Override
		public void imageClosed(ImagePlus image)
		{
			if (image.getID() == inputImageID) {
				IJ.error("Source image closed!");
				ImagePlus.removeImageListener(this);
				this.close();
				instance = null;
			} else if (image.getID() == instance.gateImage.getID()) {
				ImagePlus.removeImageListener(this);
				this.close();
				instance = null;
			} else {
				suitableImageIDs = getSuitableImageIDs(inputImage, gateImage);
				panel.gateApplicationComboBox.removeAll();
				for (int imageID : suitableImageIDs) {
					panel.gateApplicationComboBox.addItem(WindowManager.getImage(imageID).getTitle());
				}
			}
		}

		/* Respond to image being opened */
		@Override
		public void imageOpened(ImagePlus image)
		{
			suitableImageIDs = getSuitableImageIDs(inputImage, gateImage);
			panel.gateApplicationComboBox.removeAll();
			for (int imageID : suitableImageIDs) {
				panel.gateApplicationComboBox.addItem(WindowManager.getImage(imageID).getTitle());
			}
		}

		/* Respond to current image being updated by scrolling through frames */
		@Override
		public void imageUpdated(ImagePlus image)
		{
			if (image.getID() == inputImageID) {
				if (image.getSlice() != currentSlice) {
					currentSlice = image.getSlice();
					if (gatesExist) {
						Overlay overlay = new Overlay(new Line(0, currentSlice - 1, recordsPerFrame, currentSlice - 1));
						overlay.setStrokeColor(Color.red);
						gateImage.setOverlay(overlay);
						float[] gatePositions = (float[])gateProcessor.getPixels();
						float[] gatePositionsForDisplayedSlice = Arrays.copyOfRange(gatePositions, (currentSlice - 1) * recordsPerFrame, currentSlice * recordsPerFrame);
						drawSingleROI(image, gatePositionsForDisplayedSlice, gateLength, searchBackwards);
					}
				}
			}
		}

	}

	/* Create new ImagePlus from gated segments of input image */
	private ImagePlus createGatedImage(ImagePlus inputImage, float[] gateStartPositions, int gateLength, boolean searchBackwards)
	{
		int nPoints = inputImage.getWidth();
		int nRecords = inputImage.getHeight();
		int stackSize = inputImage.getStackSize();
		ImagePlus gatedImage = IJ.createImage(inputImage.getTitle() + "_gatedSegments", "32-bit", gateLength, nRecords, stackSize);
		ImageStack gatedStack = gatedImage.getStack();
		ImageStack inputStack = inputImage.getStack();
		for (int slice = 1; slice <= stackSize; slice++) {
			float[] inputPixels = (float[]) inputStack.getPixels(slice);
			float[] gatedPixels = (float[]) gatedStack.getPixels(slice);
			for (int i = 0; i < nRecords; i++) {
				int gateIndex = ((slice - 1) * nRecords) + i;
				if (gateStartPositions[gateIndex] < 0 || gateStartPositions[gateIndex] > nPoints-1) { // invalid gate, so no boundary detected -- set all values to zero
					// new arrays are already initialized to zero, so nothing to do here
				} else if (gateStartPositions[gateIndex] + gateLength >= nPoints && !searchBackwards) { // end of gate set past end of waveform, so just pad with zeroes
					System.arraycopy(inputPixels, (i * nPoints) + (int)gateStartPositions[gateIndex], gatedPixels, i * gateLength, nPoints - (int)gateStartPositions[i]);
				} else if (gateStartPositions[gateIndex] < (gateLength-1) && searchBackwards) { // beginning of gate set past beginning of waveform, so pad with zeroes
					System.arraycopy(inputPixels, i*nPoints, gatedPixels, (i*gateLength)+gateLength-((int)gateStartPositions[gateIndex]+1), (gateLength-(int)gateStartPositions[gateIndex])+1);
				} else {	// gate is within waveform limits
					System.arraycopy(inputPixels, (i * nPoints) + (int)gateStartPositions[gateIndex], gatedPixels, i * gateLength, gateLength);
				}
			}
		}

		return gatedImage;
	}

	/* Create new ImagePlus with gated full-length waveforms (zero-padded) */
	private ImagePlus createdGatedWaveformImage(ImagePlus inputImage, float[] gateStartPositions, int gateLength, boolean searchBackwards)
	{
		int nPoints = inputImage.getWidth();
		int nRecords = inputImage.getHeight();
		int stackSize = inputImage.getStackSize();
		ImagePlus gatedImage = IJ.createImage(inputImage.getTitle() + "_gatedWaveforms", "32-bit", nPoints, nRecords, stackSize);
		ImageStack gatedStack = gatedImage.getStack();
		ImageStack inputStack = inputImage.getStack();
		for (int slice = 1; slice <= stackSize; slice++) {
			float[] inputPixels = (float[]) inputStack.getPixels(slice);
			float[] gatedPixels = (float[]) gatedStack.getPixels(slice);
			for (int i = 0; i < nRecords; i++) {
				int gateIndex = ((slice - 1) * nRecords) + i;
				if (gateStartPositions[gateIndex] < 0 || gateStartPositions[gateIndex] > nPoints-1) { // invalid gate, so no boundary detected -- set all values to zero
					// new arrays are already initialized to zero, so nothing to do here
				} else if (gateStartPositions[gateIndex] + gateLength >= nPoints && !searchBackwards) { // end of gate set past end of waveform, so just pad with zeroes
					System.arraycopy(inputPixels, (i * nPoints) + (int)gateStartPositions[gateIndex], gatedPixels, (i * nPoints) + (int)gateStartPositions[gateIndex], nPoints - (int)gateStartPositions[i]);
				} else if (gateStartPositions[gateIndex] < (gateLength-1) && searchBackwards) { // beginning of gate set past beginning of waveform, so pad with zeroes
					System.arraycopy(inputPixels, i*nPoints, gatedPixels, i*nPoints, gateLength-(int)gateStartPositions[gateIndex]+1);
				} else {	// gate is within waveform limits
					System.arraycopy(inputPixels, (i * nPoints) + (int)gateStartPositions[gateIndex], gatedPixels, (i * nPoints) + (int)gateStartPositions[gateIndex], gateLength);
				}
			}
		}

		return gatedImage;
	}
	
	/* Computes gate start positions for entire stack */
	private float[] computeGateStartPositionsForStack(ImageStack stack, int searchStartPoint, int offsetPoint, float threshold, int detectionType, boolean searchBackwards)
	{
		int nPoints = stack.getWidth();
		int nRecords = stack.getHeight();
		int stackSize = stack.getSize();
		float[] gateStartPositions = new float[nRecords * stackSize];
		float[] pixelValues;

		for (int slice = 1; slice <= stackSize; slice++) {
			pixelValues = (float[]) stack.getProcessor(slice).convertToFloat().getPixels();
			float[] sliceGates = computeGateStartPositions(pixelValues, nPoints, nRecords, searchStartPoint, offsetPoint, threshold, detectionType, searchBackwards);
			System.arraycopy(sliceGates, 0, gateStartPositions, (slice - 1) * nRecords, nRecords);
		}

		return gateStartPositions;
	}

	/* Computes gate start positions for individual slice, assumed to be represented by data in 'pixels' */
	private float[] computeGateStartPositions(float[] pixels, int recordLength, int numberOfRecords, int searchStartPoint, int offsetPoint, float threshold, int detectionType, boolean searchBackwards)
	{
		float[] tempArray = new float[recordLength];
		float[] gateStartPositions = new float[numberOfRecords];

		for (int i = 0; i < numberOfRecords; i++) {
			int gateStart = -1;
			if (searchBackwards) {
				for (int j = 0, k = ((i + 1) * recordLength) - 1 - searchStartPoint; k >= i * recordLength; j++, k--) {
					tempArray[j] = pixels[k];
				}
				switch (detectionType) {
					case PEAK_DETECT: 
						gateStart = peakDetect(tempArray, threshold);
						break;
					case THRESHOLD_DETECT:
						gateStart = thresholdDetect(tempArray, threshold);
						break;
					default:
				}
				gateStartPositions[i] = (gateStart >= 0) ? recordLength - (gateStart + offsetPoint + searchStartPoint) - 1 : -1;
			} else {
				System.arraycopy(pixels, (i * recordLength) + searchStartPoint, tempArray, 0, recordLength - searchStartPoint);
				switch (detectionType) {
					case PEAK_DETECT: 
						gateStart = peakDetect(tempArray, threshold);
						break;
					case THRESHOLD_DETECT:
						gateStart = thresholdDetect(tempArray, threshold);
						break;
					default:
				}
				gateStartPositions[i] = (gateStart >= 0) ? gateStart + offsetPoint + searchStartPoint : recordLength;
			}

		}

		return gateStartPositions;
	}

	/* Draw single ROI onto current slice of input image */
	private void drawSingleROI(ImagePlus image, float[] gateStartPositions, int gateLength, boolean reverseSearch)
	{
		image.setRoi(createSingleROI(gateStartPositions, gateLength, reverseSearch));
	}

	/* Create single ROI for current slice */
	private PolygonRoi createSingleROI(float[] gateStartPositions, int gateLength, boolean searchBackwards)
	{
		int direction = searchBackwards ? -1 : 1;
		int length = gateStartPositions.length;
		int[] xPoints = new int[length * 4];
		int[] yPoints = new int[length * 4];
		for (int i = 0; i < length; i++) {
			int j = i*2;
			xPoints[j] = (int)gateStartPositions[i];
			xPoints[j+1] = (int)gateStartPositions[i];
			yPoints[j] = i;
			yPoints[j+1] = (j/2)+1;			
		}
		for (int i=xPoints.length-1, j=0; i>=length*2; i--, j++) {
			xPoints[i] = xPoints[j] + direction * gateLength;
			yPoints[i] = yPoints[j];
		}
		
		return new PolygonRoi(new Polygon(xPoints, yPoints, length * 4), Roi.POLYGON);
	}

	/* Returns the index of the first peak value found which exceeds the specified threshold. Returns -1 if no peaks above threshold are detected. */
	private int peakDetect(float[] a, float threshold)
	{
		int inflectionPoint = -1;
		float previousValue = a[0];
		float currentValue = a[1];
		for (int i = 1; i < a.length - 1; i++) {
			float nextValue = a[i + 1];
			if (currentValue > threshold) {
				if (currentValue > previousValue && currentValue >= nextValue) {
					inflectionPoint = i;
				}
				if (currentValue >= previousValue && currentValue > nextValue) {
					if (i >= inflectionPoint) {
						return (inflectionPoint + i) / 2;
					}
				}
			}
			previousValue = currentValue;
			currentValue = nextValue;
		}

		return -1;
	}
	
	/* Returns the index of the first value found which exceeds the specified threshold. Returns -1 if no peaks above threshold are detected */
	private int thresholdDetect(float[] a, float threshold)
	{
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i] <= threshold && a[i + 1] > threshold) {
				return i + 1;
			}
		}

		return -1;
	}
	
	private int[] getSuitableImageIDs(ImagePlus impToCheckAgainst, ImagePlus gateImage)
	{
		int width = impToCheckAgainst.getWidth();
		int height = impToCheckAgainst.getHeight();
		int depth = impToCheckAgainst.getStackSize();
		int channels = impToCheckAgainst.getProcessor().getNChannels();
		int thisID = impToCheckAgainst.getID();
		int[] fullList = WindowManager.getIDList();
		ArrayList<Integer> suitableIDs = new ArrayList<>(); //will hold image ID's of suitable images
		suitableIDs.add(thisID); // make sure the first element is the image we're comparing everything to
		for (int imageIDtoCheck : fullList) {
			ImagePlus impFromList = WindowManager.getImage(imageIDtoCheck);
			if (impFromList.getWidth() == width && impFromList.getHeight() == height && impFromList.getProcessor().getNChannels() == channels && impFromList.getStackSize() == depth && imageIDtoCheck != thisID && imageIDtoCheck != gateImage.getID()) {
				if (!suitableIDs.contains(imageIDtoCheck)) {
					suitableIDs.add(imageIDtoCheck); // enter only if a different image than the one we're checking against
				}
			}
		}
		int[] id = new int[suitableIDs.size()];
		for (int i = 0; i < suitableIDs.size(); i++) {
			id[i] = suitableIDs.get(i);
		}

		return id;
	}
}
