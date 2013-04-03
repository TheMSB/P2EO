package game;

import java.util.ArrayList;

import exceptions.InvalidMoveException;

public class GameTester {

	//private ArrayList<String> names;
	
	public GameTester() {
		

	}

	  public static void main(String[] args) throws InvalidMoveException{
		  ArrayList<String> names = new ArrayList<String>();
		  System.out.println("eerste arg " + args[0]);
		  System.out.println("arg " + args[1]);
		  System.out.println("arg " + args[2]);
		  System.out.println("arg " + args[3]);
		  names.add(args[0]);
		  names.add(args[1]);
		  names.add(args[2]);
		  names.add(args[3]);
		  System.out.println(names);
		  //new Game(2, 2, names);
	  }
}
