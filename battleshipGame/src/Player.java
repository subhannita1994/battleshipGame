import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class containing player data : fleet information, attack data, etc
 * @author Group 3
 * @version 1.2
 */
public class Player {

	protected Game game;	//the associated game
	protected String name;	
	protected Screen screen;	//the associated screen	
	protected HashMap<String,Ship> fleet;	//map of ship name to Ship object for quick retrieval
	protected int[][] selfData = new int[10][10];	//0=available/not targeted, 1=miss, 2=ship exists&not targeted, 3=hit
	protected int[][] attackData = new int[10][10];	//0=not yet targeted, 1=targeted shot, 2=hit, 3=miss

	/**
	 * Class constructor for a new player of name n.
	 * Sets up the selfData as 0,i.e., available at all locations
	 * @param n	name or ID of the player
	 * @param game	the associated Game object
	 */
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



	/**
	 * create new screen for this player
	 */
	public void addScreen() {
		this.screen = new Screen(this);
	}
	/**
	 * return screen of this player
	 * @return screen	the associated Screen object for this player
	 */
	public Screen getScreen() {
		return this.screen;
	}
	/**
	 * return associated game object
	 * @return game		the associated Game object
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * return name of this player
	 * @return name		Name or ID of this player
	 */
	public String getName() {
		return this.name;
	}



	/**
	 * This method is called by the player's self board to add the specified Ship object at specified coordinates.
	 * Ship already contains coordinates in its location (Ship.location)
	 * Self data of this player is updated by 2 at these coordinates
	 * @param ship	Ship object to be added to fleet
	 * @param coordinates	array of Coordinate specifying the location
	 */
	public void addShip(Ship ship, Coordinate[] coordinates) {
		this.fleet.put(ship.getName(),ship);
		this.updateSelfData(coordinates,2);
	}
	/**
	 * @param shipName	Name of ship to be searched in this player's fleet
	 * @return	true if fleet contains ship by this name, otherwise false
	 */
	public boolean containsShip(String shipName) {
		return this.fleet.containsKey(shipName);
	}


	/**
	 * @param shipName	Name or ID of ship
	 * @return ship of specified name from fleet
	 */
	public Ship getShip(String shipName) {
		return fleet.get(shipName);
	}

	/**
	 * @param shipName	Name or ID of ship
	 * @return	ship's first location, i.e. leftmost or topmost coordinate for horizontal alignment or vertical alignment respectively
	 */
	public Coordinate getFirstCoordinate(String shipName) {
		Ship ship = fleet.get(shipName);
		return ship.getLocation()[0];
	}
	/**
	 * @return fleet size of this player
	 */
	public int getFleetSize() {
		return this.fleet.keySet().size();
	}
	/**
	 * @return array of ships that are sunk
	 */
	public LinkedList<Ship> getSunkShips() {
		LinkedList<Ship> sunkShips = new LinkedList<Ship>();
		for(Ship ship : fleet.values())
			if(ship.isSunk())
				sunkShips.add(ship);
		return sunkShips;
	}

	/**
	 * @return data of self board where 0=available/not targeted, 1=miss, 2=ship exists&not targeted, 3=hit
	 */
	public int[][] getSelfData(){
		return this.selfData;
	}

	/**
	 * update data of self board with value at specified coordinates
	 * @param coordinates	array of Coordinate to be updated
	 * @param value 	new integral value (0=available/not targeted, 1=miss, 2=ship exists & not targeted, 3=hit)
	 */
	public void updateSelfData(Coordinate[] coordinates, int value) {
		for(Coordinate c : coordinates) 
			this.selfData[c.getY()][c.getX()] = value;
		printSelfData();
	}

	/**
	 * @return data of attack board where 0=not yet targeted, 1=targeted shot, 2=hit, 3=miss
	 */
	public int[][] getAttackData() {
		return this.attackData;
	}

	/**
	 * set player's attackData[x][y] to corresponding value for dataValue(0=not yet targeted, 1=targeted shot, 2=hit, 3=miss)
	 * @param x		x coordinate of attackData matrix
	 * @param y		y coordinate of attackData matrix
	 * @param value		dataValue corresponding to updated value
	 */
	public void setAttackData(int x, int y, dataValue value) {
		int temp =0;
		switch(value) {
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

	/**
	 * This method checks if a ship of specified size can be placed on the player's fleet while respecting the following constraints : 
	 * 1. The entirety of a ship must be placed on the board, i.e., edge constraints
	 * 2. No two ships cannot be placed next to one another,i.e., no two ships can have the same alignment & be right next to each another on the same horizontal/vertical line
	 * 3. No two ships can overlap 
	 * @param shipSize	the size of this ship
	 * @param x			the starting x coordinate
	 * @param y			the starting y coordinate
	 * @param align		the preferred alignment
	 * @return true if ship can be placed starting from (x,y) with preferred alignment without violating game constraints; 
	 * 			otherwise return false
	 */
	public boolean checkPossible(int shipSize, int x, int y, Alignment align) {
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
		if(endX >= 10 || endY >= 10) {	//edge constraints
			System.out.println("edge");
			return false;	
		}
		//check if any other ship is placed right next in the same alignment
		for(Ship s : this.fleet.values()) { 
			if(s.getAlignment().equals(align)) { 
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

	/**
	 * For each ship in this player's fleet, this method checks if any of them occupy specified coordinate.
	 * Updates selfData with 3 or 1 for hit or miss respectively
	 * Updates opponent player's attackData with 2 or 3 for hit or miss respectively
	 * @param c	Coordinate to be hit
	 * @return	true if hit; otherwise false
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




	/**
	 * print self data for debugging purposes
	 */
	public void printSelfData() {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++)
				System.out.print(selfData[i][j]+" ");
			System.out.println();
		}
	}


	/**
	 * print this player's attackData for debugging purposes
	 */
	public void printAttackData() {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++)
				System.out.print(attackData[i][j]+" ");
			System.out.println();
		}
	}





}
