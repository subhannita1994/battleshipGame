import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;

public class SelfBoard extends JPanel implements ActionListener{

	private HashMap<String,Ship> shipInfo = new HashMap<String,Ship>();
	private String[] shipNames = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
	private Ship shipTemp = null;
	private JPanel[][] cells = new JPanel[10][10];
	private Player player;
	private mouseListener ml;
	private boolean isSelfGridListener = false;
	private LinkedList<JToggleButton> alignmentBtns = new LinkedList<JToggleButton>();
	private LinkedList<JPanel> shipPanels = new LinkedList<JPanel>();
	private JPanel board;
	
	public SelfBoard(Player p) {
		super();
		player = p;
		ml = new mouseListener(this);
		addMouseListener(ml);
		addMouseMotionListener(ml);

		shipInfo.put("Carrier", new Ship("Carrier",5));
		shipInfo.put("Battleship", new Ship("Battleship",4));
		shipInfo.put("Cruiser", new Ship("Cruiser",3));
		shipInfo.put("Submarine", new Ship("Submarine",3));
		shipInfo.put("Destroyer", new Ship("Destroyer",2));

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();


		for(int i = 0;i< 10; i+=2) {

			Ship ship = shipInfo.get(shipNames[i/2]);
			JPanel align = new JPanel();
			JToggleButton btn = new JToggleButton(Alignment.HORIZONTAL.toString());
			btn.putClientProperty("shipName", shipNames[i/2]);
			btn.addActionListener(this);
			align.add(btn);
			align.setPreferredSize(new Dimension(150,50));
			c.gridx = 0;
			c.gridy = i;
			c.gridheight = 1;
			c.gridwidth = 1;
			alignmentBtns.add(btn);
			add(align,c);

			JPanel shipPanel = new JPanel();
			shipPanel.setPreferredSize(new Dimension(200,50));
			shipPanel.putClientProperty("shipName",shipNames[i/2]);
			JLabel shipImage = new JLabel();
			ImageIcon shipIcon = new ImageIcon(getClass().getResource("/images/"+shipNames[i/2]+".png"));
			shipImage.setIcon(shipIcon);
			shipPanel.add(shipImage);
			c.gridx = 1;
			c.gridy = i;
			c.gridheight = 1;
			c.gridwidth = 1;
			shipPanels.add(shipPanel);
			add(shipPanel,c);

			JPanel shipTitle = new JPanel();
			shipTitle.add(new JLabel(shipNames[i/2]+" Size: "+ship.getSize()));
			shipTitle.setPreferredSize(new Dimension(250,30));
			c.gridx = 0;
			c.gridy = i+1;
			c.gridheight = 1;
			c.gridwidth = 2;
			add(shipTitle,c);

		}


		board = new JPanel(new GridLayout(10,10));
		for(int i =0;i<10;i++)
			for(int j=0;j<10;j++) {
				JPanel cell = new JPanel();
				cell.setBackground(Color.blue);
				cell.setPreferredSize(new Dimension(40,40));
				cell.setBorder(new LineBorder(Color.black,1));
				cell.putClientProperty("i",i);
				cell.putClientProperty("j", j);
				cells[i][j] = cell;
				board.add(cell);
			}

		board.setPreferredSize(new Dimension(400,400));
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 10;
		c.gridheight = 10;

		add(board,c);



	}

	public void setShipTemp(String shipName) {
		System.out.println("ship type set to "+shipName);
		this.shipTemp = shipInfo.get(shipName);
	}
	public Ship getShipTemp() {
		return this.shipTemp;
	}

	public Player getPlayer() {
		return this.player;
	}
	private class mouseListener extends MouseAdapter{

		private SelfBoard gt;
		private LinkedList<String> placedShips;

		public mouseListener(SelfBoard gt) {
			this.gt = gt;
			this.placedShips = new LinkedList<String>();
		}
		@Override
		public void mousePressed(MouseEvent e) {
			if(gt.getSelfGridListener()) {
				String shipName = (String) ((JComponent)((JComponent) e.getSource()).getComponentAt(e.getPoint())).getClientProperty("shipName");
				if(shipName!=null && !placedShips.contains(shipName)) {
					gt.setShipTemp(shipName);
					System.out.println("pressed "+shipName);
				}
			}


		}

		@Override
		public void mouseReleased(MouseEvent e) {

			if(gt.getSelfGridListener()) {
				if(gt.getShipTemp()!=null) {
					try {
						Point p = new Point((int)(e.getPoint().getX()-350), (int)e.getPoint().getY());
						JComponent cell = (JComponent) ((JComponent) e.getSource()).getComponentAt(e.getPoint()).getComponentAt(p);
						Ship ship = gt.getShipTemp();
						System.out.println("release "+ship.getName()+" to "+cell.getName());
						int x = (int)cell.getClientProperty("j");
						int y = (int)cell.getClientProperty("i");
						if(gt.getPlayer().checkPossible(ship.getSize(), x,y, ship.getAlignment())) {
							/*TODO
							 * place ship if possible
							 * update player's selfdata
							 * update ship's location
							 * draw
							 * set this shipPanel to disable mousePressed
							 * set this alignment to disable ----
							 */

							Coordinate[] coordinates = this.makeCoordinates(x, y, ship.getName());	//make coordinates according to ship alignment
							ship.setLocation(coordinates);	//add this location to this ship
							gt.getPlayer().addShip(ship, coordinates);	//add this ship to this player's fleet
							draw();	//draw this ship on board
							placedShips.add(ship.getName());
							gt.disableAlignmentBtn(ship.getName());
							gt.removeIcon(ship.getName());
							System.out.println(placedShips.toString()+" are placed");
							if(gt.getPlayer().getFleetSize()==5)
								gt.getPlayer().getScreen().getSubmit().doClick();	//call this screen's actionPerformed(submit)

						}

					}catch(Exception notACell) {
					}
					gt.setShipTemp(null);
				}
			}


		}

		//return coordinates ArrayList from (x,y) according to size and alignment
		public Coordinate[] makeCoordinates(int x, int y, String shipName) {
			Ship ship = shipInfo.get(shipName);
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

		@Override
		public void mouseDragged(MouseEvent e) {
			if(gt.getSelfGridListener()) {
				if(gt.getShipTemp()!=null) {
					try {
						Point p = new Point((int)(e.getPoint().getX()-350), (int)e.getPoint().getY());
						JComponent cell = (JComponent) ((JComponent) e.getSource()).getComponentAt(e.getPoint()).getComponentAt(p);
						System.out.println("dragged from"+gt.getShipTemp().getName()+" to "+cell.getClientProperty("i")+","+cell.getClientProperty("j"));
						gt.drawTemporaryPlacement((int)cell.getClientProperty("i"),(int)cell.getClientProperty("j"));

					}catch(Exception notACell) {
					}
				}
			}

		}



	}

	//repaint cells 
	public void draw() {
		int temp[][] = this.player.getSelfData();
		Color c = null;
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				if(temp[i][j]==0)
					c = Color.blue;
				else if(temp[i][j]==1)
					c = Color.cyan;
				else if (temp[i][j]==2)
					c = Color.DARK_GRAY;
				else if(temp[i][j]==3)
					c = Color.magenta;
				cells[i][j].setBackground(c);
			}
		}
	}

	public void removeIcon(String name) {
		for(JPanel p : shipPanels)
			if(p.getClientProperty("shipName").equals(name)) {
				p.removeAll();
				return;
			}	
	}

	public boolean getSelfGridListener() {
		return isSelfGridListener;
	}

	public void disableAlignmentBtn(String name) {
		for(JToggleButton b : alignmentBtns)
			if(b.getClientProperty("shipName").equals(name)) {
				b.setEnabled(false);
				return;
			}
	}

	//repaint cells	
	public void drawTemporaryPlacement(int iStart, int jStart) {
		draw();
		int temp[][] = this.player.getSelfData();
		Color c = null;
		Ship ship = getShipTemp();
		int iEnd = iStart, jEnd = jStart;
		if(this.player.checkPossible(ship.getSize(), jStart, iStart, ship.getAlignment())) {
			if(ship.getAlignment().equals(Alignment.VERTICAL)) 
				iEnd = iStart + ship.getSize() - 1;
			else
				jEnd = jStart + ship.getSize() - 1;
			for(int i = iStart; i<=iEnd;i++)
				for(int j = jStart;j<=jEnd;j++)
					cells[i][j].setBackground(Color.green);
		}

	}



	@Override
	public void actionPerformed(ActionEvent e) {
		Alignment newAlignment;
		if(e.getActionCommand().equals(Alignment.HORIZONTAL.toString()))
			newAlignment = Alignment.VERTICAL;
		else
			newAlignment = Alignment.HORIZONTAL;
		Ship ship = shipInfo.get(((JComponent) e.getSource()).getClientProperty("shipName"));
		ship.setAlignment(newAlignment);
		((JToggleButton) e.getSource()).setText(newAlignment.toString());
	}

	public void setSelfGridListener(boolean b) {
		this.isSelfGridListener = b ;
	}

	public JPanel getBoard() {
		this.removeAll();
		setLayout(new GridLayout(10,10));
		for(int i =0;i<10;i++)
			for(int j=0;j<10;j++) {
				JPanel cell = new JPanel();
				cell.setBackground(Color.blue);
				cell.setPreferredSize(new Dimension(40,40));
				cell.setBorder(new LineBorder(Color.black,1));
				cell.putClientProperty("i",i);
				cell.putClientProperty("j", j);
				cells[i][j] = cell;
				add(cell);
			}
		return this;
	}

}
