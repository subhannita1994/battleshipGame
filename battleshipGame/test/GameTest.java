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
public class GameTest {

	static Game game;
	static String player1;
	static String player2;
	/**
	 * Context : normal variation, human mode
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		game = new Game();
		game.setVisible(false);
		player1 = "Player 1";
		player2 = "Player 2";
		game.getNormalVariation().doClick();
		game.getHumanMode().doClick();
		game.getStartBtn().doClick();
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
	 * Test method for {@link Game#getP1()}.
	 */
	@Test
	public void testGetP1() {
		assertEquals(game.getP1().getName(),player1);
	}

	/**
	 * Test method for {@link Game#getP2()}.
	 */
	@Test
	public void testGetP2() {
		assertEquals(game.getP2().getName(),player2);
	}

	/**
	 * Test method for {@link Game#getOppo(Player)}.
	 */
	@Test
	public void testGetOppo() {
		assertEquals(game.getOppo(game.getP1()).getName(),player2);
		assertEquals(game.getOppo(game.getP2()).getName(),player1);
	}

}
