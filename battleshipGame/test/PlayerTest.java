import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */

/**
 * @author Group 3
 *
 */
public class PlayerTest {

	private static Game game;
	private static Player player1; 
	private static Player player2;
	private static Coordinate[] validCoods;
	private static Ship validShip;
	private static Ship sunkShip;
	private static int[][] expectedSelfData;
	private static int[][] expectedAttackData;
	/**
	 * Context : normal variation
	 * 			 human mode
	 * 			 player1 has 2 ships : 
	 * 				1. validShip at {(1,1),(2,1),(3,1)}
	 * 				2. sunkShip at {(5,6),(5,7)}; status : sunk
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		game = new Game();
		game.setVisible(false);
		game.getNormalVariation().doClick();
		game.getHumanMode().doClick();
		game.getStartBtn().doClick();
		player1 = game.getP1();
		player2 = game.getP2();
		expectedSelfData = new int[10][10];
		expectedAttackData = new int[10][10];
		for(int i =0;i<10;i++)
			for(int j =0;j<10;j++) {
				expectedSelfData[i][j] = 0;
				expectedAttackData[i][j] = 0;
			}
		validShip = new Ship("validShip",3);
		validCoods = new Coordinate[3];
		validCoods[0] = new Coordinate(1,1);
		validCoods[1] = new Coordinate(2,1);
		validCoods[2] = new Coordinate(3,1);
		validShip.setLocation(validCoods);
		player1.addShip(validShip, validCoods);
		expectedSelfData[1][1] = 2;
		expectedSelfData[1][2] = 2;
		expectedSelfData[1][3] = 2;
		sunkShip = new Ship("sunkShip",3);
		Coordinate[] sunkCoods = new Coordinate[2];
		sunkCoods[0] = new Coordinate(5,6);
		sunkCoods[1] = new Coordinate(5,7);
		sunkShip.setLocation(sunkCoods);
		player1.addShip(sunkShip, sunkCoods);
		player1.hit(new Coordinate(5,6));
		player1.hit(new Coordinate(5,7));
		expectedSelfData[6][5] = 3;
		expectedSelfData[7][5] = 3;
		expectedAttackData[6][5] = 2;
		expectedAttackData[7][5] = 2;
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		game.dispose();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	/**
	 * Test method for {@link Player#getScreen()}.
	 */
	@Test
	public void testGetScreen() {
		assertNotNull(player1.getScreen());
		assertNotNull(player2.getScreen());
	}

	/**
	 * Test method for {@link Player#getGame()}.
	 */
	@Test
	public void testGetGame() {
		assertNotNull(player1.getGame());
		assertNotNull(player2.getGame());
	}

	/**
	 * Test method for {@link Player#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals(player1.getName(),"Player 1");
		assertEquals(player2.getName(),"Player 2");
	}
	
	

	/**
	 * Test method for checking if ship placement respects edge constraints.
	 */
	@Test
	public void testEdgeShip() {
		assertFalse(player1.checkPossible(2, 9, 9, Alignment.HORIZONTAL));
	}

	/**
	 * Test method for checking if ship placement respects overlapping constraints.
	 */
	@Test
	public void testCollisionShip() {
		assertFalse(player1.checkPossible(3, 2, 0, Alignment.VERTICAL));
	}

	/**
	 * Test method for checking if no two ships can be placed beside each other.
	 */
	@Test
	public void testNextShip() {
		assertFalse(player1.checkPossible(3, 4, 1, Alignment.HORIZONTAL));
	}

	/**
	 * Test method for {@link Player#getFirstCoordinate(java.lang.String)}.
	 */
	@Test
	public void testGetFirstCoordinate() {
		assertEquals(player1.getFirstCoordinate("validShip"),new Coordinate(1,1));
	}

	/**
	 * Test method for {@link Player#getFleetSize()}.
	 */
	@Test
	public void testGetFleetSize() {
		assertEquals(player1.getFleetSize(),2);
	}

	/**
	 * Test method for {@link Player#getSunkShips()}.
	 */
	@Test
	public void testGetSunkShips() {
		Ship[] sunk = new Ship[1];
		sunk[0] = sunkShip;
		assertArrayEquals(player1.getSunkShips().toArray(),sunk);
	}

	/**
	 * Test method for {@link Player#getSelfData()}.
	 */
	@Test
	public void testGetSelfData() {
		int[][] selfData = player1.getSelfData();
		for(int i=0;i<10;i++)
			assertArrayEquals(selfData[i],expectedSelfData[i]);
	}

	/**
	 * Test method for {@link Player#getAttackData()}.
	 */
	@Test
	public void testGetAttackData() {
		int[][] attackData = player2.getAttackData();
		for(int i =0;i<10;i++)
			for(int j=0;j<10;j++)
				assertEquals("coordinate:"+i+","+j,expectedAttackData[i][j],attackData[i][j]);
	}

	
	/**
	 * Test method for {@link Player#hit(Coordinate)}.
	 */
	@Test
	public void testHit() {
		expectedAttackData[7][7] = 3;
		assertFalse(player1.hit(new Coordinate(7,7)));
	}

}
