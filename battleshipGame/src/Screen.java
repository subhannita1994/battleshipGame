
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Class representing screen of each player
 * @author Group 3
 * @version 1.2
 */
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

	/**
	 * constructs a new screen for Player p
	 * @param p	associated Player object who owns this screen
	 */
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

	/**
	 * set up screen for setUp phase, i.e., during ship placement
	 * @throws InterruptedException
	 */
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


	/**
	 * set up screen for game play,i.e., during attacking of opponent's ship
	 */
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

	/**
	 * Needed in Computer mode
	 * sets a flag if Computer has found a hit on opponent's board so that correct strategy is applied
	 * When true, Computer attacks random cells
	 * Once Computer finds a hit, the flag is set so that from the next time onwards it hits according to probability distribution
	 * @param b true if computer has not hit anything yet, false otherwise
	 */
	public void setFirstGameP2(boolean b) {
		firstGameP2 = b;
	}

	/**
	 * @return self board (where player's ships are placed)
	 */
	public SelfBoard getSelfBoard() {
		return this.selfBoard;
	}

	/**
	 * @return attack board (where player is selecting shots to target)
	 */
	public AttackBoard getAttackBoard() {
		return this.attackBoard;
	}

	/**
	 * @return center panel which shows the status of self and enemy ship health
	 */
	public FleetAttack getFleetAttack() {
		return this.fleetAttack;
	}

	/**
	 * @return	timer of this player
	 */
	public timer getTimer() {
		return this.timer;
	}

	/**
	 * Useful to control game play.
	 * For example, once the second player ship placement is complete, this method is called to initiate attack phase
	 * @return	hidden submit button of screen which is deliberately fired to move on to next phase
	 */
	public JButton getSubmit() {
		return this.submit;
	}

	/**
	 * This method is called by player's timer object to set the text
	 * @return timer label of this player 
	 */
	public JLabel getTimerLabel() {
		return this.timerLabel;
	}

	/** Listener to hidden submit button
	 * Useful to control listeners of players' self and attack board
	 * This method is called upon during the following types of events:
	 * 		1. After Player 1's fleet placement is complete, display Player 2's self board
	 * 		2. After Player 2's fleet placement is complete, initiate game by calling Player 1 screen's gamePlayScreen()
	 * 		3. During attack phase, after each player's turn, display the other player's gamePlayScreen()
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
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

	/**
	 * update the text of "Shots left:" panel
	 * @param sh number of shots left this turn
	 */
	public void updateShots(int sh) {
		this.shots.setText("Shots left: "+sh);
	}

}
