import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the department and project lists from a COMPANY style database.
 */
public class DatabaseService {

	private DatabaseService() {
	}

	/**
	 * Returns all departments sorted by name.
	 */
	public static List<Department> loadDepartments(String dbName) throws DatabaseException {
		String sql = "SELECT Dnumber, Dname FROM DEPARTMENT ORDER BY Dname";
		List<Department> departments = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection(dbName);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				departments.add(new Department(rs.getInt("Dnumber"), rs.getString("Dname")));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Could not load departments from " + dbName, e);
		}
		return departments;
	}
}
