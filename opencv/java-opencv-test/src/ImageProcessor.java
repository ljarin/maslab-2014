import java.awt.Color;
import java.awt.Point;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Moments;
import org.opencv.core.MatOfPoint;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

public class ImageProcessor {

	private static Scalar redLeft1 = new Scalar(170, 160, 100);
	private static Scalar redRight1 = new Scalar(180, 256, 256);

	private static Scalar redLeft2 = new Scalar(0, 160, 100);
	private static Scalar redRight2 = new Scalar(8, 256, 256);

	private static Scalar greenLeft = new Scalar(170, 140, 170);
	private static Scalar greenRight = new Scalar(180, 256, 256);

	private static Scalar redLeftS = new Scalar(170, 190, 170);
	private static Scalar redRightS = new Scalar(180, 256, 256);

	private static double minimalArea = 50;
	private static Size picSize = new Size(320, 240);
	Mat hierarchy;	
	Mat imSlave;
	Mat im2;
	List<MatOfPoint> contours;
	

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	boolean log;
	int testnumber;

	/**
	 * Constructor 
	 * log and testnumber for debugging purposes
	 * @param log
	 * @param testnumber
	 */
	public ImageProcessor(boolean log, int testnumber) {
		this.log = log;
		this.testnumber = testnumber;
		imSlave = new Mat();
		im2 = new Mat();
		hierarchy = new Mat();
		contours = new ArrayList<MatOfPoint>();
	}

	/**
	 * resamples the image to a new size
	 * 
	 * @param source
	 *            the source image
	 * @param newSize
	 *            the new size
	 * @return the new image
	 */
	private void resample(Mat source) {		
		Imgproc.resize(source, source, picSize, 0, 0, Imgproc.INTER_NEAREST);
		blur(source);	
	}

	private void blur(Mat source) {
		Imgproc.GaussianBlur(source, source, new Size(5, 5), 3);

	}

	/**
	 * Returns an image thresholded in the given color
	 * 
	 * @param imgHSV
	 *            image to threshold
	 * @return
	 */
	private Mat getColor(Mat imgHSV, Color color) {

		Scalar rangeLeft = new Scalar(0, 0, 0), rangeRight = new Scalar(0, 0, 0);

		if (color.equals(Color.red)) {
			rangeLeft = redLeft1;
			rangeRight = redRight1;
		} else {
			rangeLeft = greenLeft;
			rangeRight = greenRight;
		}

		if (color == Color.red) {
			int rotation = 128 - 255; // 255 = red
			Core.add(imgHSV, new Scalar(rotation, 0, 0), imgHSV);
		}

		Mat im1 = new Mat(imgHSV.size(), CvType.CV_8U, new Scalar(1));
		Core.inRange(imgHSV, rangeLeft, rangeRight, im1);

		if (color == Color.red) {
			Mat im2 = new Mat(imgHSV.size(), CvType.CV_8U, new Scalar(1));
			Core.inRange(imgHSV, redLeft2, redRight2, im2);
			Core.bitwise_or(im1, im2, im1);
		}

		return im1;
	}

	/**
	 * Find two largest contours in the image
	 * 
	 * @param im2
	 *            source image
	 * @param toEdit
	 * @param testnumber
	 * @return a pair of contours
	 */
	private Contour[] findTwoLargestContours(Mat im2, Mat toEdit) {
		
		// find the contours of all white spots
		Imgproc.findContours(im2, contours,hierarchy, Imgproc.RETR_EXTERNAL,
				Imgproc.CHAIN_APPROX_NONE);

		// checking if any contours found
		if (contours.size() == 0) {
			Contour[] contourPair = new Contour[0];
			return contourPair;
		}

		// find the two largest contours
		double largestArea = 0, secondLargestArea = 0;
		int largestIndex = 0, secondLargestIndex = 0;
		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if (area > largestArea) {
				secondLargestArea = largestArea;
				secondLargestIndex = largestIndex;
				largestArea = area;
				largestIndex = i;
			} else if (area > secondLargestArea) {
				secondLargestArea = area;
				secondLargestIndex = i;
			}
		}
		
		if (log) {
			Imgproc.drawContours(toEdit, contours, -1, new Scalar(0, 130, 130),
					2);
			Imgproc.drawContours(toEdit, contours, largestIndex, new Scalar(0,
					255, 0), 2);
			Imgproc.drawContours(toEdit, contours, secondLargestIndex,
					new Scalar(0, 255, 0), 2);
			Highgui.imwrite("resources/contours" + testnumber + ".png", toEdit);
		}

		Contour[] contourPair = new Contour[2];
		if (largestIndex == secondLargestIndex) {
			contourPair = new Contour[1];
			contourPair[0] = new Contour(contours.get(largestIndex));
		} else {
			contourPair[0] = new Contour(contours.get(largestIndex));
			contourPair[1] = new Contour(contours.get(secondLargestIndex));
		}
		return contourPair;
	}

	private void analyzeContours(Contour[] contours, Color color,
			VisionDetector detector, Mat toEdit) {

		boolean sawLabel = false;
		boolean sawBall = false;

		for (int i = 0; i < contours.length; i++) {
			if (contours[i].isRect() && !sawLabel) {
				detector.sawLabel(color, contours[i].center().x,
						contours[i].center().y);
				System.out.println("sawLabel " + color.toString());
				sawLabel=true;
			} else if (contours[i].isEll() && !sawBall) {
				detector.sawBall(color, contours[i].center().x,
						contours[i].center().y);
				System.out.println("sawBall " + color.toString());
				sawBall=true;
			} else if (contours[i].isSomethingBig() && !sawBall){
				detector.sawBall(color, contours[i].center().x,
						contours[i].center().y);
				System.out.println("sawBall " + color.toString());
				sawBall=true;
			}

			if (log) {
				byte[] data = { (byte) 255, (byte) 0, (byte) 0 };
				for (int k = -2; k < 3; k++) {
					for (int j = -2; j < 3; j++) {
						toEdit.put((int) contours[i].center().x + j,
								(int) contours[i].center().y + k, data);
					}
				}
			}

		}
		
		if (log) {
			Highgui.imwrite("resources/centroids" + testnumber + ".png", toEdit);
		}

	}

	public VisionDetector process(Mat rawImage) {
		Timer timer=new Timer();
		timer.start();
		timer.start();
		
		
		VisionDetector detector = new VisionDetector();	
		resample(rawImage);
		
		
		if (log) {
			rawImage.copyTo(imSlave);
		}
		
		
		timer.print("resampling");
		timer.start();
		
		
		Imgproc.cvtColor(rawImage, im2, Imgproc.COLOR_BGR2HSV);
		im2 = getColor(im2, Color.red);
		if (log) {
			Highgui.imwrite("resources/filtered" + testnumber + ".png", im2);
		}
		
		
		timer.print("threshholding");
		timer.start();
		
		
		Contour[] contours = findTwoLargestContours(im2, imSlave);
		analyzeContours(contours, Color.red, detector, imSlave);
		
		timer.print("finding contours");
		timer.print("everything");	 
		
		 
		return detector;
	}

}
