package battleship;

public class Location {
	private int x; // bow x first coordinate
	private int y; // bow y first coordinate
	private String orientation; // vertical or horizontal
	
	public Location() {
	}
	
	public Location(int x, int y, String orientation) {
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		if(!orientation.equals("vertical") ||  !orientation.equals("horizontal")) {
			throw new IllegalArgumentException("Only vertical and horizontal are valid arguements");
		}
		this.orientation = orientation;
	}
}