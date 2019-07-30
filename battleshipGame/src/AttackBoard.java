/*
 * Class representing attack board of each player
 * @author Group 3
 * @version 1.2
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;

public class AttackBoard extends JPanel implements MouseListener{

	private Player player;
	private JPanel self;
	private boolean attackGridListener = false;
	private JPanel[][] cells = new JPanel[10][10];
	private int shotsPerTurn;
	private int curShots;
	private int oppoSunkShips;
	private HashMap<Coordinate,Boolean> shots = new HashMap<Coordinate,Boolean>();
	
	//constructs a new attack board for Player p
	public AttackBoard(Player p) {
		this.player = p;
		this.shotsPerTurn = 5;
		this.curShots = 0;
		this.oppoSunkShips = 0;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        self = new JPanel();
        self.setLayout(new GridLayout(10,10));
        if(p.getName().equals("Player 1"))
        	this.add(new JLabel("Player 2"));
        else
        	this.add(new JLabel("Player 1"));

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
	
	
	
	//set listener of attack board
	public void setAttackGridListener(boolean b) {
		this.attackGridListener = b;
	}



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
			/*
			 * IF curShots<shots ->store in hit coordinates array
			 * ELSE
			 * set attackGridListener to false to avoid more clicks
			 * stop time
			 * reset curShots
			 * get hit or miss on each shot, 
			 * update AttackData and opponent's self data, 
			 * update ship's hashmap if hit
			 * if sunk then update attack panel and opponent's shots per turn
			 * draw()
			 * clear hit coordinates array
			 * update score of this player
			 * wait for a brief time (optional)
			 * set attackGridListener back to true
			 * hide this screen and show opponent's screen
			 * start opponent's timer
			 */
			
			
		}
		
	}


	//decreases current shots per turn of this player by i
	public void decreaseCurShotsPerTurn(int i) {
		if(this.shotsPerTurn - i > 0)
			this.shotsPerTurn -= i;
	}

	//sets the current shots per turn of this player to i
	public void setCurShotsPerTurn(int i) {
		this.shotsPerTurn = i;
	}
	
	//get current shots allowed per turn
	public int getCurShotsPerTurn() {
		return shotsPerTurn-curShots;
	}
	
	//paint each cell
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
