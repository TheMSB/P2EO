package ai;

public class CellPoint implements Comparable<CellPoint>{

	private int x;
	private int y;
	private double worth;
	
	public CellPoint(int x, int y, double worth){
		this.x = x;
		this.y = y;
		this.worth = worth;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return x;
	}
	public double getW(){
		return worth;
	}

	@Override
	public int compareTo(CellPoint o) {
		int compared = 0;
		if(this.worth >= o.getW()){
			compared = 1;
		}else{
			compared = -1;
		}
		return compared;
	}
	
	public String toString(){
		return x +" "+y+" Worth: "+worth;
	}
	
	
}
