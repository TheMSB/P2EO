package util;

import java.util.ArrayList;

public class Util {

	public static ArrayList<Integer> ConvertToInt(ArrayList<String> arr){
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(String s : arr){
			output.add(Integer.parseInt(s));
		}
		
		return output;
	}
}
