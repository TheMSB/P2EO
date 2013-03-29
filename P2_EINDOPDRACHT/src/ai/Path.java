package ai;

import java.util.ArrayList;

public class Path extends ArrayList<CellPoint> implements Comparable<Path>{
	
	public Path(CellPoint start){
		super();
		super.add(start);
	}
	
	public double getTotalWorth(){
		double points = 0;
		for(int i=0;i<super.size();i++){
			points = points + super.get(i).getW();
		}
		
		return points;
	}
	public double getAverageWorth(){
		return getTotalWorth()/super.size();
	}

	public Path copy(){
		Path copy = new Path(this.get(0));
		for(int i=1;i<this.size();i++){
			copy.add(this.get(i));
		}
		
		return copy;
	}
	
	@Override
	public int compareTo(Path o) {
		int compared=0;
		if(this.getAverageWorth()>=o.getAverageWorth()){
			compared = 1;
		}else{
			compared = -1;
		}
		
		return compared;
	}
	
	public String toString(){
		return "Avr Worth:  "+Math.round(getAverageWorth())+"    "+super.toString();
	}
}
