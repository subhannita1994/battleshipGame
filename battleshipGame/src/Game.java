import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/*
 * Main driver class of Battleship
 * @author Group 3
 * @version 1.2
 */
public class Game extends JFrame implements ActionListener{

	private static Player player1;
	private static Player player2;
	private JRadioButton salvoVariation;
	private JRadioButton normalVariation;
	private JRadioButton computerMode;
	private JRadioButton humanMode;
	private ButtonGroup variation;
	private ButtonGroup mode;
	private JButton start;
	
	//constructs a new game of two players
	public Game() {
		
		super();
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Welcome to Battleship Game!", JLabel.CENTER), BorderLayout.NORTH);
		JPanel gameInstructions = new JPanel();
		gameInstructions.setLayout(new GridBagLayout());
		//TODO : game instructions
		this.add(gameInstructions, BorderLayout.CENTER);
		JPanel selection = new JPanel();
		selection.setLayout(new BorderLayout());
		selection.add(new JLabel("Select a variation and mode of play"), BorderLayout.NORTH);
		variation = new ButtonGroup();
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		mode = new ButtonGroup();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		salvoVariation = new JRadioButton("Salvo Variation");
		salvoVariation.setActionCommand("salvo");
		normalVariation = new JRadioButton("Normal Variation");
		normalVariation.setActionCommand("normal");
		variation.add(salvoVariation);
		variation.add(normalVariation);
		panel1.add(normalVariation,BorderLayout.NORTH);
		panel1.add(salvoVariation,BorderLayout.SOUTH);
		computerMode = new JRadioButton("Play against Computer");
		computerMode.setActionCommand("computer");
		humanMode = new JRadioButton("Play against another player");
		humanMode.setActionCommand("human");
		mode.add(computerMode);
		mode.add(humanMode);
		panel2.add(computerMode,BorderLayout.SOUTH);
		panel2.add(humanMode,BorderLayout.NORTH);
		selection.add(panel1, BorderLayout.WEST);
		selection.add(panel2, BorderLayout.EAST);
		start = new JButton("Start Game!");
		start.addActionListener(this);
		selection.add(start,BorderLayout.SOUTH);
		this.add(selection, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	//return player 1
	public Player getP1() {
		return this.player1;
	}
	
	//return player 2
	public Player getP2() {
		return this.player2;
	}
	
	//get opponent player object of p
	public Player getOppo(Player p) {
		if(p.getName().equals("Player 1"))
			return this.player2;
		else
			return this.player1;
	}
	//main driver function
	public static void main(String[] args) {
		
		
		Game game = new Game();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(variation.getSelection() == null || mode.getSelection() == null)
			JOptionPane.showMessageDialog(this,"Select a variation as well as mode of play");
		else {
			player1 = new Player("Player 1",this);
			if(mode.getSelection().getActionCommand().equals("computer")) {
				System.out.println("computer selected");
				player2 = new Computer("Player 2",this);
			}
			else {
				System.out.println("human selected");
				player2 = new Player("Player 2",this);
			}
			player1.addScreen();
			player2.addScreen();
			if(variation.getSelection().getActionCommand().equals("salvo")) {
				System.out.println("salvo selected");
				//shots per turn set to 5 initially by default
			}
			else { 
				System.out.println("normal selected");
				player1.getScreen().getAttackBoard().setCurShotsPerTurn(1);
				player2.getScreen().getAttackBoard().setCurShotsPerTurn(1);
			}
			this.setVisible(false);
			//TODO: game instructions
			JOptionPane.showMessageDialog(this,"Set up your fleet by dragging ships to the board");
			player1.getScreen().getSelfBoard().setSelfGridListener(true);
			try {
				player1.getScreen().setUpScreen();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			player2.getScreen().setVisible(false);
		}	
	}

	/**
	 * @return the salvoVariation
	 */
	public JRadioButton getSalvoVariation() {
		return salvoVariation;
	}

	/**
	 * @return the normalVariation
	 */
	public JRadioButton getNormalVariation() {
		return normalVariation;
	}

	/**
	 * @return the computerMode
	 */
	public JRadioButton getComputerMode() {
		return computerMode;
	}

	/**
	 * @return the humanMode
	 */
	public JRadioButton getHumanMode() {
		return humanMode;
	}
	
	/**
	 * @return the start button to initiate game
	 */
	public JButton getStartBtn() {
		return start;
	}
	
}
