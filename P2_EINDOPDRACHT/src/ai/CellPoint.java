package ai;

public class CellPoint implements Comparable<CellPoint>{

	private int x;
	private int y;
	private double 	winWorth;
	private double	connectionWorth;
	private double	blockWorth;
	int type;
	
	public CellPoint(int x, int y, double winWorth, double connectionWorth, double blockWorth){
		this.x = x;
		this.y = y;
		this.winWorth = winWorth;
		this.connectionWorth = connectionWorth;
		this.blockWorth = blockWorth;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public double getW(){
		return getWW()+getCW()+getBW();
	}
	public double getWW(){
		return winWorth;
	}
	public double getCW(){
		return connectionWorth;
	}
	public double getBW(){
		return blockWorth;
	}
	
	public void setBestType(int type){
		this.type = type;
	}

	public int getBestType(){
		return type;
	}
	
	@Override
	public int compareTo(CellPoint o) {
		int compared = 0;
		if(getW() >= o.getW()){
			compared = 1;
		}else{
			compared = -1;
		}
		return compared;
	}
	
	public String toString(){
		return x +" "+y+" WW "+winWorth +" CW "+connectionWorth +" BW "+blockWorth + " TW "+getW();
	}
	
	
}
