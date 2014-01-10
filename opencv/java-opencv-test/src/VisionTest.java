import static org.junit.Assert.*;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import org.opencv.core.Core;


public class VisionTest {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
 
	@Test
	public void Test1(){
	  Mat im=Highgui.imread("resources/1.png");
	  ImageProcessor proc=new ImageProcessor(false,1);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	}
	
	@Test
	public void Test2(){
	  Mat im=Highgui.imread("resources/2.png");
	  ImageProcessor proc=new ImageProcessor(false,2);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertTrue(detector.didSeeSilo());
	}
	
	@Test
	public void Test3(){
	  Mat im=Highgui.imread("resources/3.png");
	  ImageProcessor proc=new ImageProcessor(false,3);
	  VisionDetector detector=proc.process(im);
	  assertTrue(detector.didSeeRedBall());
	  assertFalse(detector.didSeeSilo());
	}
}
