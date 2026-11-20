/**
 * Thrown when the database cannot be opened or queried.
 * The GUI catches it and shows the "Database could not be opened." pop-up.
 */
public class DatabaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
