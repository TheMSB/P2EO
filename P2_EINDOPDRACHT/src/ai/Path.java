package ai;

import java.util.ArrayList;

/**
 * A custom form of ArrayList<CellPoint> that implements Comparable. This way it
 * can compare itself to other Paths, and it has some custom methods to get the
 * worth of the path.
 * 
 * @author I3anaan
 * 
 */
public class Path extends ArrayList<CellPoint> implements Comparable<Path> {

	/**
	 * Constructor
	 * 
	 * @param start
	 *            The first CellPoint in this Path
	 */
	public Path(CellPoint start) {
		super();
		super.add(start);
	}

	/**
	 * @return The total worth of all the CellPoints in this Path combined.
	 */
	public double getTotalWorth() {
		double points = 0;
		for (int i = 0; i < super.size(); i++) {
			points = points + super.get(i).getW();
		}

		return points;
	}

	/**
	 * @return The average worth per move (per CellPoint) for this Path.
	 */
	public double getAverageWorth() {
		return getTotalWorth() / super.size();
	}

	/**
	 * @return The average worth per move (per CellPoing) for this Path,
	 *         excluding EffortToWin worth.
	 */
	public double getAverageWorthNeutral() {
		double points = 0;
		for (int i = 0; i < super.size(); i++) {
			points = points + super.get(i).getCW() + super.get(i).getBW();
		}

		return points / super.size();
	}

	/**
	 * Copies this Path, and returns the new copy
	 * 
	 * @return a copy of this Path
	 */
	public Path copy() {
		Path copy = new Path(this.get(0));
		for (int i = 1; i < this.size(); i++) {
			copy.add(this.get(i));
		}

		return copy;
	}

	/**
	 * Compares this Path to another Path, comparisons are made upon checking
	 * the AverageWorth() of both Paths, and then seeing whoever has the highest
	 */
	@Override
	public int compareTo(Path o) {
		int compared = 0;
		if (this.getAverageWorth() >= o.getAverageWorth()) {
			compared = 1;
		} else {
			compared = -1;
		}

		return compared;
	}

	/**
	 * Returns the average worth of this Path + all the objects in it
	 */
	public String toString() {
		return "Avr Worth:  " + Math.round(getAverageWorth()) + "    "
				+ super.toString();
	}
}
