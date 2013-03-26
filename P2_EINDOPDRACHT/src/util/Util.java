package util;

import java.util.ArrayList;

import server.ClientHandler;

public class Util {

	public static ArrayList<Integer> ConvertToInt(ArrayList<String> arr) throws NumberFormatException{
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(String s : arr){
			output.add(Integer.parseInt(s));
		}
		//TODO wordt de exception automatisch doorgegeven?
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
	
	public static ArrayList<String> makePlayerNameList(ArrayList<ClientHandler> arr){
		ArrayList<String> names = new ArrayList<String>();
		for(ClientHandler ch : arr){
			names.add(ch.getClientName());
		}
		return names; 
	}
}
