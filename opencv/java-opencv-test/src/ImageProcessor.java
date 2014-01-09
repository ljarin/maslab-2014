import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import org.opencv.imgproc.Moments;
import org.opencv.core.MatOfPoint;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;

public class ImageProcessor {
	
	private static Scalar redLeft1=new Scalar(170, 190, 170);
	private static Scalar redRight1=new Scalar(180, 256, 256);
	private static Scalar redLeft2=new Scalar(0, 190, 170);
	private static Scalar redRight2=new Scalar(8, 256, 256);
	
	private static double minimalArea=50;
	private static Size picSize=new Size(640,480);

	private static Mat getRedColor(Mat imgHSV) {
		Mat im1 = new Mat(imgHSV.size(), CvType.CV_8U, new Scalar(1));
		Mat im2 = new Mat(imgHSV.size(), CvType.CV_8U, new Scalar(1));
		Mat im3 = new Mat(imgHSV.size(), CvType.CV_8U, new Scalar(1));
		Core.inRange(imgHSV, redLeft1, redRight1, im1);
		Core.inRange(imgHSV, redLeft2, redRight2, im2);
		Core.bitwise_or(im1, im2, im3);
		return im3;
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static void findCentroids(Mat im2, Mat toEdit){
		List<MatOfPoint> contours=new ArrayList<MatOfPoint>();
		Mat hierarchy=new Mat();
		Mat im=new Mat();
		im2.copyTo(im);
		//find the contours of all white spots
		Imgproc.findContours(im, contours, hierarchy,  Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
		
		//checking if any contours found
		if (contours.size()==0){
			return ;
		};
		
		//find the two largest contours
		double largestArea=0, secondLargestArea=0;
		int largestIndex=0, secondLargestIndex=0;
		for( int i = 0; i< contours.size(); i++ )
		{
		    if(contours.get(i).size().area() > largestArea){
		        secondLargestArea = largestArea;
		        secondLargestIndex = largestIndex;
		        largestArea = contours.get(i).size().area();
		        largestIndex = i;
		    }else if(contours.get(i).size().area() > secondLargestArea){
		        secondLargestArea = contours.get(i).size().area();
		        secondLargestIndex = i;
		    }
		}

		Imgproc.drawContours(toEdit, contours, largestIndex, new Scalar(0,255,0), 2);
		
	    boolean firstBall=contours.get(largestIndex).size().area()>minimalArea;
	    boolean secondBall=contours.get(secondLargestIndex).size().area()>minimalArea && largestIndex!=secondLargestIndex;
	
		if (firstBall){
	    	if (secondBall)	System.out.println("two balls");
	    	else System.out.println("one ball");
	    } else System.out.println("no balls");
		
		//find their centroids
		Moments m1 = Imgproc.moments( contours.get(largestIndex), false );
		Moments m2= Imgproc.moments( contours.get(secondLargestIndex), false );
		
		
		int[] x=new int[2],y=new int[2];

		x[0]= (int)(m1.get_m10()/m1.get_m00()); y[0]= (int) (m1.get_m01()/m1.get_m00()); 		
		x[1]= (int)(m2.get_m10()/m2.get_m00()); y[1]= (int) (m2.get_m01()/m2.get_m00()); 
		
	    
	    byte[] data={(byte)255,(byte)0,(byte)0};
	    for (int u=0; u<1; u++){
	    	for (int i=-2; i<3; i++){
	    		for (int j=-2; j<3; j++){
	    			toEdit.put(y[u]+j, x[u]+i, data);
	    		}
	    	}
	    }
	}

	public static Mat process(Mat rawImage) {
		Mat im = new Mat(picSize, CvType.CV_16UC3);

		Imgproc.blur(rawImage, im, new Size(9, 9));
		Imgproc.resize(rawImage, im, picSize, 0, 0,
				Imgproc.INTER_NEAREST);
		
		Mat imnew=new Mat();
		im.copyTo(imnew);
		
		
		Imgproc.GaussianBlur(im, im, new Size(5, 5), 3); // smooth the original
															// image using
															// Gaussian kernel

		Mat im2 = new Mat();		
		Imgproc.cvtColor(im, im2, Imgproc.COLOR_BGR2HSV); // Change the color
															// format from BGR
															// to HSV
		im2 = getRedColor(im2);
		
		
		Imgproc.GaussianBlur(im2, im2, new Size(5,5), 3); //smooth the
		//original image using Gaussian kernel
		findCentroids(im2,imnew);
		return imnew;
	}

}
