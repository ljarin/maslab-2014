import java.awt.Color;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;


public class VisionDetector {
	
	HashMap<Color,Boolean>  sawBall=new HashMap<Color,Boolean>();
	HashMap<Color,Boolean>  sawLabel=new HashMap<Color,Boolean>();
	HashMap<Color,SimpleEntry<Double,Double>>  ball=new HashMap<Color,SimpleEntry<Double,Double>>();
	HashMap<Color,SimpleEntry<Double,Double>>  label=new HashMap<Color,SimpleEntry<Double,Double>>();
	HashMap<Color,Double>  ballDiameter=new HashMap<Color,Double>();
	
	
	public VisionDetector(){
		sawBall.put(Color.red, false);
		sawBall.put(Color.green, false);
		sawLabel.put(Color.red, false);
		sawLabel.put(Color.green, false);
	}
	
	public synchronized void sawBall(Color color, double x, double y){
		sawBall.put(color, true);
		ball.put(color, new SimpleEntry<Double,Double>(x,y));
	}
		
	public synchronized void sawLabel(Color color, double x, double y){
		sawLabel.put(color, true);
		label.put(color, new SimpleEntry<Double,Double>(x,y));
	}	
	
	public synchronized boolean didSeeRedBall(){
		return sawBall.get(Color.red);
	}
	
	public synchronized boolean didSeeGreenBall(){
		return sawBall.get(Color.green);
	}
	
	public synchronized boolean didSeeSilo(){
		return sawLabel.get(Color.red);
	}
	
	public synchronized boolean didSeeReactor(){
		return sawLabel.get(Color.green);
	}
}
