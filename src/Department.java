/**
 * A department row: its number and name. toString() returns the name so a JList shows it.
 */
public class Department {

	private final int number;
	private final String name;

	public Department(int number, String name) {
		this.number = number;
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
