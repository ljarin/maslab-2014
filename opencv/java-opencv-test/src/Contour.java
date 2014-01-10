import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


public class Contour {
	private MatOfPoint original;
	private MatOfPoint2f conv2f=new MatOfPoint2f();
	private double area;
	private RotatedRect boundingRect;
	private double rectArea;
	private double ellArea;
	private boolean isNull=true;
	private boolean isRect=false;
	private boolean isEll=false;
	private Point center;
	
	public Contour(MatOfPoint c){
		if (c==null){
			isNull=true;
		} else {
			original=c;
			original.convertTo(conv2f, CvType.CV_32FC2);
			area=Imgproc.contourArea(original);
			boundingRect = Imgproc.minAreaRect(conv2f);
			rectArea=boundingRect.size.area();
			ellArea=boundingRect.size.area()/4*Math.PI;
			isRect=area/rectArea>0.8;
			isEll=area/ellArea>0.95;
			
			Moments m1 = Imgproc.moments(original, false);			
			double cx,cy;
			cx =  (m1.get_m10() / m1.get_m00());
			cy =  (m1.get_m01() / m1.get_m00());
			center=new Point(cy,cx);
		}
	}
	
	public double area(){
		return area;
	}
	
	public double rectArea(){
		return rectArea;
	}
	
	public double ellArea(){
		return ellArea;
	}
	
	public boolean isRect(){
		return isRect;	
	}
	
	public boolean isEll(){
		return isEll;
	}
	
	public Point center(){
		return center;
	}
	
	public boolean isSomethingBig(){
		return area>50;
	}
}
