

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 * Class containing ship size and location
 * @author Group 3
 * @version 1.2
 */
public class Ship {

	private String name;
	private int size;
	private Coordinate[] location;
	private HashMap<Coordinate,Boolean> hit;
	private Alignment alignment;

	//constructs new ship of size s, name n, alignment horizontal and empty location coordinates
	public Ship(String n, int s) {
		this.name = n;
		this.size = s;
		this.alignment = Alignment.HORIZONTAL;
		this.location = new Coordinate[size];
		this.hit = new HashMap<Coordinate,Boolean>();
	}

	//return name of this ship
	public String getName() {
		return this.name;
	}

	//return size of this ship
	public int getSize() {
		return this.size;
	}

	//return array of Coordinates of this ship
	public Coordinate[] getLocation() {
		return this.location;
	}

	//check if ship is hit at Coordinate c and return true if hit, else return false. Also, update its hashmap (hit) if hit
	public boolean hit(Coordinate c) {
		if(this.hit.containsKey(c)) {
			this.hit.put(c, true);
			System.out.print(name+":"+this.hit.get(c));
			return true;
		}
		return false;
	}

	//return true if ship is sunk else return false
	public boolean isSunk() {
		if(hit.containsValue(false))
			return false;
		else
			return true;
	}

	//return alignment of ship
	public Alignment getAlignment() {
		return this.alignment;
	}

	//set alignment of ship
	public void setAlignment(Alignment a) {
		this.alignment = a;
	}

	//set location of ship to coordinates
	public void setLocation(Coordinate[] coordinates) {
		this.location = coordinates;
		for(Coordinate c : coordinates)
			hit.put(c,false);
	}

	//set location of ship to null,i.e., when ship is not yet placed on board
	public void clearLocation() {
		Arrays.fill(location, null);
		for(Coordinate c : hit.keySet())
			hit.remove(c);
	}
}
