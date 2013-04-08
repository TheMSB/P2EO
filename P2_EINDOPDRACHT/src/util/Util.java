package util;

import java.util.ArrayList;

import server.ClientHandler;

/**
 * This class contains some general methods which are usefull in different situations and dont really belong to a single class.
 * @author I3anaan
 *
 */
public class Util {

	/**
	 * Formats a given ArrayList of Strings into an ArrayList of integers
	 * @param arr	ArrayList to read
	 * @return	ArrayList of integers, read from the given arr
	 * @throws NumberFormatException	When it cannot convert one or more Strings to integers
	 */
	public static ArrayList<Integer> ConvertToInt(ArrayList<String> arr) throws NumberFormatException{
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(String s : arr){
			output.add(Integer.parseInt(s));
		}
		return output;
	}
	
	/**
	 * Concats an ArrayList into a single String
	 * @param arr	The ArrayList to convert
	 * @return	A String made by calling toString() on each object in the Array, with spaces inbetween
	 */
	public static <Elem> String concatArrayList(ArrayList<Elem> arr)
	{
		String output = "";
		for (Elem s : arr) {
			output = output + s + " ";
		}
		return output;
	}
	
	/**
	 * Makes an ArrayList<String> of player names from an ArrayList<ClientHandler>
	 * @param arr	The array to get names from
	 * @return	ArrayList<String> containing the names of the given arr.
	 * @ensure arr.get(0).getClientName() == result.get(0)
	 */
	public static ArrayList<String> makePlayerNameList(ArrayList<ClientHandler> arr){
		ArrayList<String> names = new ArrayList<String>();
		for(ClientHandler ch : arr){
			System.out.println(ch.getClientName());
			names.add(ch.getClientName());
		}
		return names; 
	}
	
	/**
	 * Sums an ArrayList<Integer> adding all the integers together
	 * @param arr	The ArrayList to sum
	 * @return	The sum of the given arr
	 */
	public static int sumArray(ArrayList<Integer> arr){
		int output = 0;
		for(int i : arr){
			output = output+i;
		}
		
		return output;
	}
	/**
	 * Gets the index of the max number in the ArrayList<Integer>
	 * @param arr	The ArrayList to check
	 * @return	Index of the highest Integer
	 */
	public static int getIndexOfMax(ArrayList<Integer> arr){
		int max = -1;
		boolean tie = false;
		for(int i=0;i<arr.size();i++){
			if(max<0 || arr.get(i)>arr.get(max)){
				max = i;
				tie = false;
			}else if(arr.get(i)==arr.get(max)){
				tie = true;
			}
		}
		if(tie){
			max = -1;
		}
		return max;
	}
}
