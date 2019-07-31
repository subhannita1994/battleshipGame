import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JOptionPane;
/**
 * Class containing data and strategy for AI player (fleet location, self and attack data, fleet placement and attack strategy)
 * @author Group 3
 * @version 1.2
 */
public class Computer extends Player{

	private LinkedList<Ship> shipInfo = new LinkedList<Ship>();	//list of ship objects
	private int oppoSunkShips;	//number of sunk ships in opponent's fleet
	private HashMap<String, Integer> afloatShips = new HashMap<String,Integer>();	//list of healthy ships in opponent's fleet

	/**
	 * Constructs a new Computer object with name n and associated with game
	 * @param n	Name or ID of this Computer object
	 * @param game	Game object this is associated with
	 */
	public Computer(String n, Game game) {
		super(n, game);
		shipInfo.add(new Ship("Carrier",5));
		shipInfo.add(new Ship("Battleship",4));
		shipInfo.add(new Ship("Cruiser",3));
		shipInfo.add(new Ship("Submarine",3));
		shipInfo.add(new Ship("Destroyer",2));
		afloatShips.put("Carrier",5);
		afloatShips.put("Battleship",4);
		afloatShips.put("Cruiser",3);
		afloatShips.put("Submarine",3);
		afloatShips.put("Destroyer",2);
		this.oppoSunkShips = 0;
	}

	/**
	 * random set up fleet coordinates while respecting placement constraints
	 * Iterate until all ships are not placed: 
	 * 		1. generate a random start coordinate(x,y) and random alignment
	 * 		2. check if this ship can be placed from (x,y) with specified alignment
	 * 		3. if ship placement is possible:
	 * 			3.1. set this Ship object's location
	 * 			3.2. add to fleet
	 * 			3.3. draw on selfBoard
	 * Fire hidden submit button to move on to next phase of game
	 */
	public void setUpFleet() {

		Random r = new Random();
		while(super.getFleetSize()!=5) {
			Ship ship = shipInfo.getFirst();
			int randomX = r.nextInt(10);
			int randomY = r.nextInt(10);
			Alignment randomAlign = Alignment.HORIZONTAL;
			int a = r.nextInt(2);
			if(a==1)
				randomAlign = Alignment.VERTICAL;
			System.out.println("AI: "+ship.getName()+" trying from "+randomX+","+randomY+" "+randomAlign);
			if(super.checkPossible(ship.getSize(), randomX, randomY, randomAlign)) {
				ship.setAlignment(randomAlign);
				Coordinate[] coordinates = this.makeCoordinates(randomX, randomY, ship);
				System.out.println("result from makeCoordinates:");
				for(Coordinate c: coordinates)
					System.out.println(c.getX()+","+c.getY());
				ship.setLocation(coordinates);
				super.addShip(ship, coordinates);
				screen.getSelfBoard().draw();
				shipInfo.removeFirst();
			}
		}
	}

	/**
	 * factory of coordinates for specified ship at specified coordinates
	 * @param x		starting x coordinate
	 * @param y		starting y coordinate
	 * @param ship	Ship object for whom coordinates need to be made
	 * @return		array of coordinates for this ship
	 */
	private Coordinate[] makeCoordinates(int x, int y, Ship ship) {
		Coordinate[] coods = new Coordinate[ship.getSize()];
		if(ship.getAlignment().equals(Alignment.HORIZONTAL)) {
			for(int i=0;i<ship.getSize();i++) {
				coods[i]= new Coordinate(x,y);
				x++;
			}
		}
		else {
			for(int i=0;i<ship.getSize();i++) {
				coods[i]= new Coordinate(x,y);
				y++;
			}
		}
		return coods;
	}


	/**
	 * AI attack when at least one hit has been found
	 * Select n number of shots with highest probability of getting a hit
	 * When all shots are taken : 
	 * 		1. get judgment on shots
	 * 		2. update Computer's attackData and opponent's selfData
	 * 		3. draw on Computer's attackBoard
	 * 		4. check if opponent's entire fleet is sunk - if yes, display winning message and exit
	 * 		5. check if any of opponent's ship is sunk in this turn - if yes, update it's maximum allowable shots per turn and icon on center panel
	 * 		6. move on to next phase of game, i.e., intiate opponent's gamePlayScreen
	 * @param n		number of shots per turn
	 */
	public void attack(int n) {

		int i =0;
		int x = 0,y = 0;
		Player oppo = super.getGame().getOppo(this);
		LinkedList<int[]> probDist = attackStrategy();
		while(i!=n) {
			int[] temp = probDist.removeLast();
			x = temp[0];
			y = temp[1];
			if(attackData[y][x]==0) {
				System.out.println("AI: selected shot -"+x+","+y);
				super.setAttackData(x, y, dataValue.SHOT);
				Coordinate c = new Coordinate(x,y);
				oppo.hit(c);
				screen.getAttackBoard().draw();
				i++;
			}
		}
		System.out.println("All shots taken where shotsPerTurn = "+screen.getAttackBoard().getCurShotsPerTurn());
		screen.getTimer().getStopButton().doClick();
		LinkedList<Ship> sunkShips = oppo.getSunkShips();
		if(sunkShips.size() == 5) {
			JOptionPane.showMessageDialog(screen,"Congratulations! You win!");
			System.out.println(this.getName()+" wins");
			System.exit(0);
		}
		if(sunkShips.size()>this.oppoSunkShips) {
			for(Ship s : sunkShips) {
				this.afloatShips.remove(s.getName());
			}
			System.out.println("new ships sunk");
			oppo.getScreen().getAttackBoard().decreaseCurShotsPerTurn(sunkShips.size()-this.oppoSunkShips);	//update current shots per turn for opponent player
			oppoSunkShips = sunkShips.size();//update oppoSunkShips
			screen.getFleetAttack().updateFleet(sunkShips, oppo);
			oppo.getScreen().getFleetAttack().updateFleet(sunkShips, oppo);
		}
		//TODO update score of this player
		//TODO pause this screen for some time
		screen.setVisible(false);
		oppo.getScreen().gamePlayScreen();

	}

	/**
	 * AI attack strategy to find the probability of getting a hit on each cell
	 * Set initial probability of all cells to 0
	 * Iterate for each cell on board:
	 * 		1. Iterate for each healthy ship of opponent's fleet:  
	 * 			1.1. if this cell has not been targeted yet and ship can be placed starting from this cell, increment its probability
	 * 			1.2. if this cell has previously been hit, increment probability of available cells on all 4 sides
	 * @return	list of coordinates ordered from highest to lowest probability of getting a hit
	 */
	private LinkedList<int[]> attackStrategy() {
		// TODO Auto-generated method stub
		double[][] prob = new double[10][10];
		for(int i=0;i<10;i++)
			for(int j=0;j<10;j++)
				prob[i][j] = 0;
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				for(String shipName : afloatShips.keySet()) {
					int shipSize = afloatShips.get(shipName);
					int[] temp = checkPossibleHit(shipSize, j, i, Alignment.HORIZONTAL);
					if(temp!=null) {
						for(int jTemp = j; jTemp <= temp[0];jTemp++) {
							if(attackData[i][jTemp]==0)
								prob[i][jTemp]++;
							if(attackData[i][jTemp]==2) {
								int iN = i-1;
								if(iN>=0) 
									if(attackData[iN][jTemp]==0) 
										prob[iN][jTemp]+=2.5;
								int iS = i+1;
								if(iS<10)
									if(attackData[iS][jTemp]==0)
										prob[iS][jTemp]+=2.5;
								int jW = jTemp-1;
								if(jW>=0)
									if(attackData[i][jW]==0)
										prob[i][jW]+=2.5;
								int jE = jTemp+1;
								if(jE<10)
									if(attackData[i][jE]==0)
										prob[i][jE]+=2.5;
							}
						}
					}
					temp = checkPossibleHit(shipSize, j, i, Alignment.VERTICAL);
					if(temp!=null) {
						for(int iTemp = i; iTemp <= temp[1];iTemp++) {
							if(attackData[iTemp][j]==0)
								prob[iTemp][j]++;
							if(attackData[iTemp][j]==2) {
								int iN = iTemp-1;
								if(iN>=0) 
									if(attackData[iN][j]==0) 
										prob[iN][j]+=2.5;
								int iS = iTemp+1;
								if(iS<10)
									if(attackData[iS][j]==0)
										prob[iS][j]+=2.5;
								int jW = j-1;
								if(jW>=0)
									if(attackData[iTemp][jW]==0)
										prob[iTemp][jW]+=2.5;
								int jE = j+1;
								if(jE<10)
									if(attackData[iTemp][jE]==0)
										prob[iTemp][jE]+=2.5;
							}
						}
					}
				}
			}
		}

		printProbDist(prob);

		LinkedList<int[]> result = new LinkedList<int[]>();
		int[] temp = new int[2];
		double max = 0.0;
		for(int i=0;i<10;i++)
			for(int j=0;j<10;j++) {
				if(prob[i][j]>max) {
					max = prob[i][j];
					temp[0] = j;
					temp[1] = i;
				}
			}
		result.add(temp);
		System.out.println("Max :"+result.get(0)[1]+","+result.get(0)[0]);
		for(int i=0;i<10;i++) 
			for(int j=0;j<10;j++) 
				for(int index=0;index<result.size();index++) {
					int iCompare = result.get(index)[1];
					int jCompare = result.get(index)[0];
					//					System.out.println("comparing prob["+i+"]["+j+"] ("+prob[i][j]+") with prob["+iCompare+"]["+jCompare+"] ("+prob[iCompare][jCompare]+")");
					if(prob[i][j] <= prob[iCompare][jCompare]) {
						//						System.out.println("Adding at "+index);
						int[] newTemp = {j,i};
						result.add(index, newTemp);
						break;
					}
				}

		return result;
	}



	/**
	 * check if ship of size shipSize can be placed at (x,y) with alignment align on opponent's board
	 * @param shipSize	size of ship in question
	 * @param x		starting x coordinate
	 * @param y		starting y coordinate
	 * @param align	specified alignment of ship
	 * @return	if ship can be placed here, return the end coordinates; otherwise return null;
	 */
	public int[] checkPossibleHit(int shipSize, int x, int y, Alignment align) {
		int endX, endY;
		if(align.equals(Alignment.HORIZONTAL)) {
			endX = x + shipSize - 1;
			endY = y;
		}
		else {
			endX = x;
			endY = y + shipSize - 1;
		}
		//		System.out.print("checking from "+x+","+y+" to "+endX+","+endY+"--");
		if(endX >= 10 || endY >= 10) {	//edge constraints
			//			System.out.println("edge");
			return null;	
		}
		//check for collision : if this ship exists in fleet, disregard first coordinate
		for(int i = x;i<=endX;i++)
			for(int j = y;j<=endY;j++) {
				//				System.out.print(i+","+j+":");
				if(attackData[j][i]==3) {
					//					System.out.println("collision");
					return null;
				}
				//				System.out.println("no collision");
			}
		int[] temp = {endX, endY};
		return temp;

	}

	/**
	 * prints the probability distribution of getting a hit on opponent's board (for debugging purposes)
	 * @param prob	the 2D array of probability distribution
	 */
	public void printProbDist(double[][] prob) {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++)
				System.out.print(prob[i][j]+" ");
			System.out.println();
		}
	}

	/**
	 * AI attack strategy when no hits have been found
	 * Select n number of random cells that have not been targeted yet
	 * @param n		number of allowable shots per turn
	 */
	public void attackRandom(int n) {
		int i=0;
		Random r = new Random();
		Player oppo = this.getGame().getP1();
		while(i!=n) {
			int x = r.nextInt(10);
			int y = r.nextInt(10);
			if(attackData[y][x]==0) {
				System.out.println("AI: selected shot -"+x+","+y);
				super.setAttackData(x, y, dataValue.SHOT);
				Coordinate c = new Coordinate(x,y);
				oppo.hit(c);
				screen.getAttackBoard().draw();
				i++;
			}
		}
		System.out.println("All shots taken where shotsPerTurn = "+screen.getAttackBoard().getCurShotsPerTurn());
		screen.getTimer().getStopButton().doClick();
		LinkedList<Ship> sunkShips = oppo.getSunkShips();
		if(sunkShips.size() == 5) {
			JOptionPane.showMessageDialog(screen,"Congratulations! You win!");
			System.out.println(this.getName()+" wins");
			System.exit(0);
		}
		if(sunkShips.size()>this.oppoSunkShips) {
			for(Ship s : sunkShips) {
				this.afloatShips.remove(s.getName());
			}
			System.out.println("new ships sunk");
			oppo.getScreen().getAttackBoard().decreaseCurShotsPerTurn(sunkShips.size()-this.oppoSunkShips);	//update current shots per turn for opponent player
			oppoSunkShips = sunkShips.size();//update oppoSunkShips
			screen.getFleetAttack().updateFleet(sunkShips, oppo);
			oppo.getScreen().getFleetAttack().updateFleet(sunkShips, oppo);
		}
		//TODO update score of this player
		//TODO pause this screen for some time
		screen.setVisible(false);
		for(i=0;i<10;i++)
			for(int j=0;j<10;j++)
				if(attackData[i][j]==2) {
					screen.setFirstGameP2(false);
					break;
				}
		oppo.getScreen().gamePlayScreen();


	}






































}
