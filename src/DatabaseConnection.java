import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens connections to a MySQL database. Defaults match a local XAMPP install.
 */
public class DatabaseConnection {

	private static final String HOST = "localhost";
	private static final int PORT = 3306;
	private static final String USER = "root";
	private static final String PASSWORD = "";

	private DatabaseConnection() {
	}

	/**
	 * Opens a connection to the given database. The caller must close it.
	 */
	public static Connection getConnection(String dbName) throws SQLException {
		String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + dbName;
		return DriverManager.getConnection(url, USER, PASSWORD);
	}
}
