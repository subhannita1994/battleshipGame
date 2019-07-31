
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.URL;
/**
 * class for displaying the ships panel in attack phase
 * @author Group 3
 * @version 1.2
 */
public class FleetAttack extends JPanel {
	private JPanel panel;
	private Player player;
	private String[] shipNames = {"Carrier","Battleship","Cruiser","Submarine","Destroyer"};
	ImageIcon sunk = createImageIcon("/images/shipSunk.png", "sunk ship");
	ImageIcon healthy = createImageIcon("/images/shipHealthy.png", "healthy ship");

	/**
	 * constructs a new fleet panel for Player p
	 * @param p Player object associated with this fleet panel
	 */
	public FleetAttack(Player p) {
		super();
		this.player = p;
		this.panel = new JPanel(new GridLayout(6,3));
		panel.add(new JLabel("Enemy"));
		panel.add(new JLabel(""));
		panel.add(new JLabel("You"));
		for(String shipName : shipNames) {
			JLabel lbl1 = new JLabel(healthy);
			lbl1.putClientProperty("player", p.getGame().getOppo(p).getName());
			lbl1.putClientProperty("shipName", shipName);
			JLabel lbl2 = new JLabel(healthy);
			lbl2.putClientProperty("player", p.getName());
			lbl2.putClientProperty("shipName",shipName);
			panel.add(lbl1);
			panel.add(new JLabel(shipName));
			panel.add(lbl2);
		}
		System.out.println("Attack panel for "+p.getName()+" created");
	}

	/**
	 * @return	this panel
	 */
	public JPanel getPanel() {
		return this.panel;
	}

	/**
	 * update ship health
	 * @param sunkShips	list of ships that are sunk
	 * @param player	Player object having the sunk ships
	 */
	public void updateFleet(LinkedList<Ship> sunkShips, Player player) {
		if(player == null)
			System.out.println("no player set");
		LinkedList<String> temp = new LinkedList<String>();
		for(Ship ship : sunkShips) {
			temp.add(ship.getName());
		}
		for(Component c : panel.getComponents()) {
			if(c instanceof JLabel) {
				if(temp.contains(((JLabel) c).getClientProperty("shipName")))
					if(((JLabel)c).getClientProperty("player").equals(player.getName()))
						((JLabel)c).setIcon(sunk);
			}

		}

	}

	/** 
	 * @param path	location of resource
	 * @param description	description of this icon
	 * @return Returns an ImageIcon, or null if the path was invalid.
	 */
	public ImageIcon createImageIcon(String path, String description) {
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			System.out.println(description+" icon fetched");
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
