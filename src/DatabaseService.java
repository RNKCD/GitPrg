import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the department and project lists from a COMPANY style database.
 * Every failure is reported as a DatabaseException.
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
		try (Connection conn = open(dbName);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				departments.add(new Department(rs.getInt("Dnumber"), rs.getString("Dname")));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Could not read departments from '" + dbName + "'", e);
		}
		return departments;
	}

	/**
	 * Returns all projects sorted by name.
	 */
	public static List<Project> loadProjects(String dbName) throws DatabaseException {
		String sql = "SELECT Pnumber, Pname FROM PROJECT ORDER BY Pname";
		List<Project> projects = new ArrayList<>();
		try (Connection conn = open(dbName);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				projects.add(new Project(rs.getInt("Pnumber"), rs.getString("Pname")));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Could not read projects from '" + dbName + "'", e);
		}
		return projects;
	}

	/**
	 * Opens a connection, turning a blank name or a connection failure into a DatabaseException.
	 */
	private static Connection open(String dbName) throws DatabaseException {
		if (dbName == null || dbName.trim().isEmpty()) {
			throw new DatabaseException("Database name is empty.");
		}
		try {
			return DatabaseConnection.getConnection(dbName.trim());
		} catch (SQLException e) {
			throw new DatabaseException("Could not open database '" + dbName + "'", e);
		}
	}
}
