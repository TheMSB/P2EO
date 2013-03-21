package util;

import java.util.ArrayList;

public class Util {

	public static ArrayList<Integer> ConvertToInt(ArrayList<String> arr) throws NumberFormatException{
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(String s : arr){
			output.add(Integer.parseInt(s));
		}
		
		return output;
	}
	
	public static <Elem> String concatArrayList(ArrayList<Elem> arr)
	{
		String output = "";
		for (Elem s : arr) {
			output = output + s + " ";
		}
		return output;
	}
}
