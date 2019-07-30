/*
 * Class containing data for AI player (fleet location, self and attack data, fleet placement and attack strategy)
 * @author Group 3
 * @version 1.2
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

public class Computer extends Player{



	private LinkedList<Ship> shipInfo = new LinkedList<Ship>();
	private int oppoSunkShips;
	private HashMap<String, Integer> afloatShips = new HashMap<String,Integer>();

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

	//random set up fleet coordinates while respecting placement restraints
	public void setUpFleet() {
		/* iterate until fleetSize == 5
		 * ship = shipInfo.getFirst()
		 * generate random coordinate from (x,y) and random alignment
		 * check if ship placement is possible - super.checkPossible()
		 * if possible, set ship.setLocation
		 * super.addShip
		 * screen.getSelfBoard.draw()
		 * screen.getFleetSetUp.grayOut()
		 * shipInfo.removeFirst
		 * end iteration
		 * screen.getSubmit().doClick()
		 */

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
			if(super.checkPossible(ship.getName(), ship.getSize(), randomX, randomY, randomAlign)) {
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


	//set n coordinates to be shot this turn
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

	//sets list of all possible shots (in coordinate form) with their priority
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
					int[] temp = checkPossible(shipSize, j, i, Alignment.HORIZONTAL);
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
					temp = checkPossible(shipSize, j, i, Alignment.VERTICAL);
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



	//check if ship of size shipSize can be placed at (x,y) with alignment align on attackData
	private int[] checkPossible(int shipSize, int x, int y, Alignment align) {
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

	//prints the probability distribution of attacking opponent
	public void printProbDist(double[][] prob) {
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++)
				System.out.print(prob[i][j]+" ");
			System.out.println();
		}
	}

	//attack n number of random shots
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
