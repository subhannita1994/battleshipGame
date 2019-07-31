import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
/**
 * Class representing attack board of each player
 * @author Group 3
 * @version 1.2
 */
public class AttackBoard extends JPanel implements MouseListener{

	private Player player;	//the associated player
	private JPanel self;	//the JPanel containing the board
	private boolean attackGridListener = false;	//controller for when to turn on attack grid to listening mode
	private JPanel[][] cells = new JPanel[10][10];	//array of JPanels for the board
	private int shotsPerTurn;	//allowable maximum shots per turn
	private int curShots;	//number of shots left for this turn (useful in salvo variation)
	private int oppoSunkShips;	//number of ships sunk in opponent's fleet (useful in salvo variation)
	private HashMap<Coordinate,Boolean> shots = new HashMap<Coordinate,Boolean>();	//maps coordinates of attempted shots to whether they were hit or miss (useful in salvo variation) 
	
	/**
	 * constructs a new attack board for Player p
	 * @param p	the Player object
	 */
	public AttackBoard(Player p) {
		this.player = p;
		this.shotsPerTurn = 5;
		this.curShots = 0;
		this.oppoSunkShips = 0;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        self = new JPanel();
        self.setLayout(new GridLayout(10,10));
        this.add(new JLabel("Select your shots here!"));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
            	JPanel firstCell = new JPanel();
            	firstCell.setName(i+","+j);
            	firstCell.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
                firstCell.setPreferredSize(new Dimension(40, 40)); // for demo purposes only
                firstCell.setBackground(Color.blue);
                cells[j][i] = firstCell;
                self.add(firstCell);
            }
        }
        self.addMouseListener(this);
        this.add(self);
        System.out.println("Attack board for "+player.getName()+" created");
	}
	
	
	
	/**
	 * set the attackGridListener
	 * @param b true if attackBoard is supposed to listen; false otherwise
	 */
	public void setAttackGridListener(boolean b) {
		this.attackGridListener = b;
	}



	/** This method is called when player clicks on attack board but ignores if the cell is already clicked before.
	 * Player gets to take all allowable shots per turn before getting to know the outcome (hit/miss) in the next turn
	 * Once all allowable shots are taken the following are done:
	 * 		1. timer for this player is stopped
	 * 		2. shots counter is reset
	 * 		3. judgment is passed on all the shots (hit/miss) 
	 * 		4. show results(or paint) on this player's attack board and opponent's self board according to judgment
	 * 		5. For Salvo variation : if new ships are sunk from opponent's fleet: 
	 * 			5.1. decrease opponent's allowable shots per turn by 1
	 * 			5.2. update oppoSunkShips field
	 * 			5.3. update icons in the center panel
	 * 		6. if all ships sunk from opponent's fleet, display message and close
	 * Hide this Screen and get opponent's gamePlay Screen
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(this.attackGridListener) {
			System.out.println("here"+player.getName());
			int x = (int) e.getX()/40;
			int y = (int) e.getY()/40;
			System.out.println("Attack Board: clicked "+x+","+y);
			if(player.getAttackData()[y][x]!=0) {
				System.out.println("But this is already shot at before - so ignored");
				return;
			}
			this.curShots++;	//increment current shots taken
			System.out.println("Current shots:"+curShots);
			player.getScreen().updateShots(shotsPerTurn-curShots);
			shots.put(new Coordinate(x,y),false);	//add targeted coordinate to hashmap
			this.player.setAttackData(x,y,dataValue.SHOT);
			draw();	//draw targeted coordinates
			 if (curShots==shotsPerTurn) {	//if all shots taken
				System.out.println("All shots taken where shotsPerTurn = "+shotsPerTurn);
				this.player.getScreen().getTimer().getStopButton().doClick();	//stop timer. 
				attackGridListener = false;	//temporarily set attackgridlistener to false to avoid more clicks
				System.out.println("attackGridListener set to "+attackGridListener);
				curShots = 0;	//reset curShots
				for(Coordinate c : shots.keySet()) {	
					shots.put(c, player.getGame().getOppo(player).hit(c));	//update each shot with hit/miss
				}
				draw();	//draw hit and miss shots
				shots.clear();
				Player oppo = player.getGame().getOppo(player);
				LinkedList<Ship> sunkShips = oppo.getSunkShips();
				if(sunkShips.size() == 5) {
					JOptionPane.showMessageDialog(this,"Congratulations! You win!");
					System.out.println(player.getName()+" wins");
					System.exit(0);
				}
				if(sunkShips.size()>this.oppoSunkShips) {
					System.out.println("new ships sunk");
					oppo.getScreen().getAttackBoard().decreaseCurShotsPerTurn(sunkShips.size()-this.oppoSunkShips);	//update current shots per turn for opponent player
					oppoSunkShips = sunkShips.size();//update oppoSunkShips
					this.player.getScreen().getFleetAttack().updateFleet(sunkShips, oppo);
					oppo.getScreen().getFleetAttack().updateFleet(sunkShips, oppo);
				}
				//TODO update score of this player
				//TODO pause this screen for some time
				player.getScreen().setVisible(false);
				attackGridListener = true;
				oppo.getScreen().gamePlayScreen();
				
			}
		}
	}


	/**
	 * decreases current shots per turn of this player by i, but keeps it minimum to 0 (so that same method can be used for both normal and salvo variations)
	 * @param i	decrement
	 */
	public void decreaseCurShotsPerTurn(int i) {
		if(this.shotsPerTurn - i > 0)
			this.shotsPerTurn -= i;
	}

	/**
	 * sets the current shots per turn of this player to i
	 * @param i	specified current shots per turn
	 */
	public void setCurShotsPerTurn(int i) {
		this.shotsPerTurn = i;
	}
	
	/**
	 * get current shots allowed per turn
	 * @return maximum allowable shots per turn - number of shots already taken this turn
	 */
	public int getCurShotsPerTurn() {
		return shotsPerTurn-curShots;
	}
	
	/**
	 * paint each cell of attack board
	 * blue = available
	 * dark gray = miss
	 * red = hit
	 * white = attempted shot
	 */
	public void draw() {
		int temp[][] = this.player.getAttackData();
		Color c = null;
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				if(temp[i][j]==0)
					c = Color.blue;
				else if (temp[i][j]==1)
					c = Color.DARK_GRAY;
				else if(temp[i][j]==2)
					c = Color.red;
				else if(temp[i][j]==3)
					c=Color.white;
				cells[j][i].setBackground(c);
			}
		}
	}



	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
