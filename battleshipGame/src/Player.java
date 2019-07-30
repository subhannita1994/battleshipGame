/*
 * Class containing player data (fleet location, self and attack data)
 * @author Group 3
 * @version 1.2
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Player {

	protected Game game;
	protected String name;
	protected Screen screen;
	protected HashMap<String,Ship> fleet;
	protected int[][] selfData = new int[10][10];	//0=available/not targeted, 1=miss, 2=ship exists&not targeted, 3=hit
	protected int[][] attackData = new int[10][10];	//0=not yet targeted, 1=targeted shot, 2=hit, 3=miss
	
	//constructs a new player of name n
	public Player(String n, Game game) {
		this.name = n;
		this.game = game;
	    fleet = new HashMap<String,Ship>();
	    for(int i=0;i<10;i++)
	    	for(int j=0;j<10;j++) {
	    		selfData[i][j] =0;
	    		attackData[i][j] =0;
	    	}
		System.out.println(name+" created");
	}
	
	//temporary constructor
	public Player(String n) {
		this(n,null);
	}
	
	//add new screen to this player
	public void addScreen() {
		this.screen = new Screen(this);
	}
	//return associated game object
	public Game getGame() {
		return this.game;
	}
	
	//return name of this player
	public String getName() {
		return this.name;
	}
	
	//return screen of this player
	public Screen getScreen() {
		return this.screen;
	}
	
	//adds ship of type shipName at coordinates
	public void addShip(Ship ship, Coordinate[] coordinates) {
		this.fleet.put(ship.getName(),ship);
		this.updateSelfData(coordinates,2);
	}
	
	//update data of self board with value at coordinates
	public void updateSelfData(Coordinate[] coordinates, int value) {
		for(Coordinate c : coordinates) 
			this.selfData[c.getY()][c.getX()] = value;
		printSelfData();
	}
	
	//return true if ship of type shipName exists in its fleet, else return false
	public boolean containsShip(String shipName) {
		return this.fleet.containsKey(shipName);
	}
	
	//delete and return ship of type shipName from this player's fleet
	public Ship deleteShip(String shipName) {
		updateSelfData(fleet.get(shipName).getLocation(),0);
		return this.fleet.remove(shipName);
		
	}
	
	//check if this ship can be placed at (x,y) with alignment align on board
	public boolean checkPossible(String shipName, int shipSize, int x, int y, Alignment align) {
		int endX, endY, prevX, prevY, nextX, nextY;
		if(align.equals(Alignment.HORIZONTAL)) {
			endX = x + shipSize - 1;
			endY = y;
			prevX = Math.max(x-1, 0);
			prevY = y;
			nextX = Math.min(endX+1, 9);
			nextY = y;
		}
		else {
			endX = x;
			endY = y + shipSize - 1;
			prevX = x;
			prevY = Math.max(y-1, 0);
			nextX = x;
			nextY = Math.min(endY+1, 9);
		}
		System.out.println("next and prev coordinates :"+nextX+","+nextY+" "+prevX+","+prevY);
		System.out.print("checking from "+x+","+y+" to "+endX+","+endY+"--");
		if(endX >= 10 || endY >= 10) {	//edge constraints
			System.out.println("edge");
			return false;	
		}
		//check if any other ship is placed right next in the same alignment
		for(Ship s : this.fleet.values()) { 
			System.out.print("looking through "+s.getName());
			if(s.getAlignment().equals(align)) { 
				System.out.println(" whose alignment is "+s.getAlignment());
				for(Coordinate c : s.getLocation()) { 
					System.out.println(c.getX()+","+c.getY());
					if((c.getX()==prevX && c.getY()==prevY) || (c.getX()==nextX && c.getY()==nextY)) {
						System.out.println("another ship close by");
						return false;
					}
				}
			}
		}
					
		//check for collision : if this ship exists in fleet, disregard first coordinate
		for(int i = x;i<=endX;i++)
			for(int j = y;j<=endY;j++) {
				System.out.print(i+","+j+":");
				if(selfData[j][i]==2) {
					System.out.println("collision");
					return false;
				}
				System.out.println("no collision");
			}
		return true;
	}
	
	
	
	//get first coordinate of this ship
	public Coordinate getFirstCoordinate(String shipName) {
		Ship ship = fleet.get(shipName);
		return ship.getLocation()[0];
	}
	
	//get data of self board where 0=available/not targeted, 1=miss, 2=ship exists&not targeted, 3=hit
	public int[][] getSelfData(){
		return this.selfData;
	}
	
	//return ship of type shipName from fleet
	public Ship getShip(String shipName) {
		return fleet.get(shipName);
	}
	
	//print self data for debugging
	public void printSelfData() {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++)
				System.out.print(selfData[i][j]+" ");
			System.out.println();
		}
	}
	
	//get fleet size of this player
	public int getFleetSize() {
		return this.fleet.keySet().size();
	}
	
	/*
	 * return true if Coordinate c is hit, else return false. 
	 * Update selfData and opponent's attackData where applicable
	 * Update ship's location hashmap 
	 */
	public boolean hit(Coordinate c) {
		System.out.print("Hitting "+name+" at Coordinate:"+c.getX()+","+c.getY()+"--");
		for(Ship ship : fleet.values()) {
			if(ship.hit(c)) {
				selfData[c.getY()][c.getX()] = 3;
				this.game.getOppo(this).setAttackData(c.getX(), c.getY(), dataValue.HIT);
				System.out.println(name+"'s self data:");
				printSelfData();
				System.out.println("Opponent's attack data:");
				this.game.getOppo(this).printAttackData();
				return true;
			}
		}
		selfData[c.getY()][c.getX()] = 1;
		game.getOppo(this).setAttackData(c.getX(), c.getY(), dataValue.MISS);
		System.out.println(name+"'s self data:");
		printSelfData();
		System.out.println("Opponent's attack data:");
		this.game.getOppo(this).printAttackData();
		return false;
	}
	
	//get array of ships that are sunk
	public LinkedList<Ship> getSunkShips() {
		LinkedList<Ship> sunkShips = new LinkedList<Ship>();
		for(Ship ship : fleet.values())
			if(ship.isSunk())
				sunkShips.add(ship);
		return sunkShips;
	}

	//set player's attackData[x][y] to corresponding value for dataValue(0=not yet targeted, 1=targeted shot, 2=hit, 3=miss)
	public void setAttackData(int x, int y, dataValue d) {
		int temp =0;
		switch(d) {
		case NOT_TARGETED : 
			temp =0; 
			break;
		case SHOT :
			temp =1;
			break;
		case HIT :
			temp =2;
			break;
		case MISS :
			temp=3;
			break;
		}
		attackData[y][x] = temp;
		printAttackData();
	}

	//get player's attackData
	public int[][] getAttackData() {
		return this.attackData;
	}

	//print this player's attackData for debugging purposes
	public void printAttackData() {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++)
				System.out.print(attackData[i][j]+" ");
			System.out.println();
		}
	}
	
}
