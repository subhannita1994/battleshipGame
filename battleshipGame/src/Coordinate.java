/*
 * Class enclosing x and y coordinates
 * @author Group 3
 * @version 1.2
 */
public class Coordinate {

	private int x;
	private int y;
	
	//constructs a new coordinate at (x,y) 
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//return x or column number
	public int getX() {
		return this.x;
	}
	
	//return y or row number
	public int getY() {
		return this.y;
	}
	
	//Overridden equals(Object) to implement comparison of two coordinates
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Coordinate))
			return false;
		Coordinate c = (Coordinate) o;
		if(c.getX() == this.x && c.getY() == this.y)
			return true;
		else
			return false;
	}
	
	//Overridden hashCode() to implement comparison of two coordinates
	@Override
	public int hashCode() {
		int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
	}
}
