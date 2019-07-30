/*
 * Class representing screen of each player
 * @author Group 3
 * @version 1.2
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Screen extends JFrame implements ActionListener{

	private Player player;
	private SelfBoard selfBoard;
	private AttackBoard attackBoard;
	private FleetAttack fleetAttack;
	private JLabel timerLabel;
	private timer timer;
	private JButton submit;
	private boolean setUp = true;
	private static boolean firstGameP1 = true;
	private static boolean firstGameP2 = true;
	private JLabel shots;
	private Timer t;
	
	//constructs a new screen for Player p
	public Screen(Player p) {
		super(p.getName());
        this.player = p;
		this.selfBoard = new SelfBoard(p);
		this.attackBoard = new AttackBoard(p);
		this.fleetAttack = new FleetAttack(p);
		this.timerLabel = new JLabel("         ");
		this.timer = new timer(this);
		this.setLayout(new BorderLayout());
		shots = new JLabel();
		shots.putClientProperty("id", "shots");
	}
	
	//set up screen for setUp phase
	public void setUpScreen() throws InterruptedException {
		if(setUp) {
			
			this.add(this.selfBoard, BorderLayout.CENTER);
			this.submit = new JButton();
        	submit.addActionListener(this);
		}
		this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("setup Screen for "+player.getName()+" displayed");
		if(this.player instanceof Computer) {
			t = new Timer(2, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent evt) {
	                submit.doClick();
	            }
	        });
			t.setRepeats(false);
			t.start();
		}
	}
	

	//set up screen for game play
	public void gamePlayScreen() {
		if (firstGameP1==true) {
			//if right after setup phase
			JOptionPane.showMessageDialog(this,"Select 5 cells on your left board to hit your opponent. Click OK to start your timer.");
			firstGameP1 = false;
		}
		
		this.timer.getStartButton().doClick();
		this.setLayout(new BorderLayout());
		if(!setUp) {
			
			this.add(attackBoard,BorderLayout.WEST);
			this.add(fleetAttack.getPanel(),BorderLayout.CENTER);
			this.add(selfBoard.getBoard(),BorderLayout.EAST);
			Box verticalBox = Box.createVerticalBox();
			shots.setText("Shots left: "+attackBoard.getCurShotsPerTurn());
	        verticalBox.add(shots, BorderLayout.NORTH);
	        verticalBox.add(timerLabel, BorderLayout.SOUTH);
	        this.add(verticalBox,BorderLayout.SOUTH);
		}
		this.pack();
		this.selfBoard.draw();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(this.player instanceof Computer) {	//if this player is computer 
			this.attackBoard.setAttackGridListener(false);	//no need to turn on attackBoard listener
			if(firstGameP2)
				((Computer) this.player).attackRandom(this.attackBoard.getCurShotsPerTurn());
			else
				((Computer) this.player).attack(this.attackBoard.getCurShotsPerTurn());	//AI attack
		}
		else	//else if this player is human, set attackBoard listener to true
			this.attackBoard.setAttackGridListener(true);	
		
	}
	
	//set firstGameP2 flag : true if computer has not hit anything yet, false otherwise
	public void setFirstGameP2(boolean b) {
		firstGameP2 = b;
	}
	
	//return self board of this screen
	public SelfBoard getSelfBoard() {
		return this.selfBoard;
	}
	
	//return attack board of this screen
	public AttackBoard getAttackBoard() {
		return this.attackBoard;
	}
	
	
	
	//return fleet panel of this screen during game play
	public FleetAttack getFleetAttack() {
		return this.fleetAttack;
	}
	
	//return timer panel of this screen
	public timer getTimer() {
		return this.timer;
	}
	
	//return submit button of this screen
	public JButton getSubmit() {
		return this.submit;
	}
	
	//return timer label of this screen
	public JLabel getTimerLabel() {
		return this.timerLabel;
	}
	
	//action listener for submit button
	public void actionPerformed(ActionEvent e) {
		if(this.player.getFleetSize() < 5)
			JOptionPane.showMessageDialog(this,"Fleet incomplete");
		else {	//fleet complete 
			this.setUp = false;	//wrap up this player's setup phase
			this.selfBoard.setSelfGridListener(false);	//self board should no longer be listening
			this.setVisible(false);	//hide this screen
			Player oppo = this.player.getGame().getOppo(player);
			if(player.getName().equals("Player 1")) {
				//if "submit" was clicked by player 1 
				Screen p2Screen = oppo.getScreen();	//get player 2's screen
				if(oppo instanceof Computer) {	//if player 2 is computer
					p2Screen.getSelfBoard().setSelfGridListener(false);	//no need to turn on its self board listener
					((Computer) oppo).setUpFleet();	//AI setup fleet
				}
				else	//if player 2 is human, turn on it's self board listener
					p2Screen.getSelfBoard().setSelfGridListener(true);
				try {
					p2Screen.setUpScreen(); //set up player 2's screen
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}	
			}
			else {
				//start game
				Screen p1Screen = oppo.getScreen();
				System.out.println("game starts!");
				p1Screen.gamePlayScreen();
			}
		}
			
	}

	//update label of shots per turn
	public void updateShots(int sh) {
		this.shots.setText("Shots left: "+sh);
	}
	
}
