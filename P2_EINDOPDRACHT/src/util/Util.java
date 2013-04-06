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
			System.out.println(ch.getClientName());
			names.add(ch.getClientName());
		}
		return names; 
	}
	
	public static int sumArray(ArrayList<Integer> arr){
		int output = 0;
		for(int i : arr){
			output = output+i;
		}
		
		return output;
	}
	
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
