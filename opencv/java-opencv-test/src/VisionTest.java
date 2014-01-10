import static org.junit.Assert.*;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import org.opencv.core.Core;


public class VisionTest {
	
	private static boolean log=false;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
 
	
	@Test
	public void twoBallsSilo(){
	  Mat im=Highgui.imread("resources/1.png");
	  ImageProcessor proc=new ImageProcessor(log,1);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	}
	
	@Test
	public void twoBallsOverlapAndSilo(){
	  Mat im=Highgui.imread("resources/2.png");
	  ImageProcessor proc=new ImageProcessor(log,2);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	}
	
	/*@Test
	public void ballInFrontOfSilo(){
	  Mat im=Highgui.imread("resources/3.png");
	  ImageProcessor proc=new ImageProcessor(false,3);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	} */
	
	@Test
	public void GreenRedBallSilo(){
	  Mat im=Highgui.imread("resources/4.png");
	  ImageProcessor proc=new ImageProcessor(log,4);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	  assertTrue(detector.didSeeGreenBall());
	  assertFalse(detector.didSeeReactor());
	}
	
	@Test
	public void GreenAndRedCorner(){
	  Mat im=Highgui.imread("resources/5.png");
	  ImageProcessor proc=new ImageProcessor(log,5);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertFalse(detector.didSeeSilo());
	  assertTrue(detector.didSeeRedBall());
	  assertFalse(detector.didSeeReactor());
	}
	
	@Test
	public void redCloseGreenBehind(){
	  Mat im=Highgui.imread("resources/6.png");
	  ImageProcessor proc=new ImageProcessor(log,6);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertFalse(detector.didSeeSilo());
	  assertTrue(detector.didSeeGreenBall());
	  assertFalse(detector.didSeeReactor());
	}
	
	@Test
	public void greenNearSilo(){
	  Mat im=Highgui.imread("resources/7.png");
	  ImageProcessor proc=new ImageProcessor(log,7);
	  VisionDetector detector=proc.process(im);
	  assertFalse(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	  assertTrue(detector.didSeeGreenBall());
	}
}
