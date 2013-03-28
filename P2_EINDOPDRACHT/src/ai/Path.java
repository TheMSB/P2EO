package ai;

import java.util.ArrayList;

public class Path extends ArrayList<CellPoint>{
	
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
}
