package ai;

import java.awt.Point;

/**
 * A custom Cell which also saves the worth of that location.
 * It extends Point, as it needs to save a X and Y coordinate
 * Worth is subdivided into 3 categories:
 * WinWorth	-	How easy to win this Cell
 * ConnectionWorth	-	How connected this Cell is, and how much connections you get from it
 * BlockWorth	-	How much it blocks other colors from spreading across the map
 * 
 * It also saves the best color and type to set on this Cell
 * 
 * 
 * @author I3anaan
 *
 */
public class CellPoint extends Point implements Comparable<CellPoint>{

	/**
	 * How easy to win this Cell
	 */
	private double 	winWorth;
	/**
	 * How connected this Cell is, and how much connections you get from it
	 */
	private double	connectionWorth;
	/**
	 * How much it blocks other colors from spreading across the map
	 */
	private double	blockWorth;
	/**
	 * The best type of stone to place here
	 */
	private int type;
	/**
	 * The best color to place here
	 */
	private int color;
	
	/**
	 * Constructs a new CellPoint
	 * @param x		X coordinate
	 * @param y		Y coordinate
	 * @param winWorth	How easy to win this Cell
	 * @param connectionWorth	How connected this Cell is, and how much connections you get from it
	 * @param blockWorth	How much it blocks other colors from spreading across the map
	 */
	public CellPoint(int x, int y, double winWorth, double connectionWorth, double blockWorth){
		super(x,y);
		this.winWorth = winWorth;
		this.connectionWorth = connectionWorth;
		this.blockWorth = blockWorth;
	}
	/**
	 * @return	The total worth of this Cell
	 */
	public double getW(){
		return getWW()+getCW()+getBW();
	}
	
	/**
	 * @return	The WinWorth
	 */
	public double getWW(){
		return winWorth;
	}
	/**
	 * @return	The ConnectionWorth
	 */
	public double getCW(){
		return connectionWorth;
	}
	
	/**
	 * @return	The BlockWorth
	 */
	public double getBW(){
		return blockWorth;
	}
	
	/**
	 * Sets the best type to the given type
	 * @param type	New best type
	 */
	public void setBestType(int type){
		this.type = type;
	}

	/**
	 * @return	The best type to place on this Cell
	 */
	public int getBestType(){
		return type;
	}
	
	/**
	 * Sets the best color
	 * @param color	The new best color
	 */
	public void setBestColor(int color){
		this.color = color;
	}

	/**
	 * @return	The best color to place on this Cell
	 */
	public int getBestColor(){
		return color;
	}
	
	/**
	 * Compares this CellPoint to another CellPoint.
	 * In comparing it checks whoever has the highest total worth.
	 */
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
	
	/**
	 * @return The coordinates and different worths.
	 */
	public String toString(){
		return x +" "+y+" WW "+winWorth +" CW "+connectionWorth +" BW "+blockWorth + " TW "+getW();
	}
	
	
}
