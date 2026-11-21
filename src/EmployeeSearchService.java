import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Employee search logic.
 *
 * Assumed COMPANY-style schema:
 * EMPLOYEE(SSN, FNAME, LNAME, DNO)
 * DEPARTMENT(DNUMBER, DNAME)
 * PROJECT(PNUMBER, PNAME)
 * WORKS_ON(ESSN, PNO)
 *
 * Selection semantics:
 * - Selected values within one list use OR (match any selection).
 * - Department and project constraints use AND (match both groups).
 * - NOT reverses membership in the selected group.
 * - Empty selections do not filter, even if NOT is checked.
 *
 * Departments and projects are identified by number (Department.getNumber(),
 * Project.getNumber()), because names are not guaranteed to be unique.
 */
public final class EmployeeSearchService {

    /**
     * Searches the named MySQL database using the shared connection helper.
     * Closes the connection after searching, and reports failures consistently
     * with DatabaseService.
     */
    public List<String> searchEmployees(
            String dbName,
            List<Integer> selectedDepartments,
            List<Integer> selectedProjects,
            boolean notDepartment,
            boolean notProject) throws DatabaseException {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new DatabaseException("Database name is empty.");
        }
        Connection connection;
        try {
            connection = DatabaseConnection.getConnection(dbName.trim());
        } catch (SQLException ex) {
            throw new DatabaseException("Could not open database '" + dbName.trim() + "'", ex);
        }
        try (Connection conn = connection) {
            return searchEmployees(conn, selectedDepartments, selectedProjects,
                    notDepartment, notProject);
        } catch (SQLException ex) {
            throw new DatabaseException("Could not search employees in '" + dbName.trim() + "'", ex);
        }
    }


    /**
     * Searches employees and returns formatted display rows for the Swing text area.
     * Uses PreparedStatement parameters for every selected number.
     * The connection is not closed here.
     */
    public List<String> searchEmployees(
            Connection connection,
            List<Integer> selectedDepartments,
            List<Integer> selectedProjects,
            boolean notDepartment,
            boolean notProject) throws SQLException {

        Objects.requireNonNull(connection, "connection cannot be null");
        List<Integer> departments = normalize(selectedDepartments);
        List<Integer> projects = normalize(selectedProjects);

        StringBuilder sql = new StringBuilder(
                "SELECT e.FNAME, e.LNAME, e.SSN FROM EMPLOYEE e WHERE 1=1");
        List<Integer> parameters = new ArrayList<>();

        if (!departments.isEmpty()) {
            sql.append(notDepartment ? " AND NOT EXISTS (" : " AND EXISTS (");
            sql.append("SELECT 1 FROM DEPARTMENT d "
                    + "WHERE d.DNUMBER = e.DNO AND d.DNUMBER IN (")
               .append(placeholders(departments.size()))
               .append("))");
            parameters.addAll(departments);
        }

        if (!projects.isEmpty()) {
            sql.append(notProject ? " AND NOT EXISTS (" : " AND EXISTS (");
            sql.append("SELECT 1 FROM WORKS_ON w "
                    + "WHERE w.ESSN = e.SSN AND w.PNO IN (")
               .append(placeholders(projects.size()))
               .append("))");
            parameters.addAll(projects);
        }

        sql.append(" ORDER BY e.LNAME, e.FNAME, e.SSN");

        List<String> matches = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setInt(i + 1, parameters.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    matches.add(String.format("%s %s (%s)",
                            rs.getString("FNAME"), rs.getString("LNAME"), rs.getString("SSN")));
                }
            }
        }
        return matches;
    }

    private static List<Integer> normalize(List<Integer> values) {
        List<Integer> result = new ArrayList<>();
        if (values == null) {
            return result;
        }
        for (Integer value : values) {
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    private static String placeholders(int count) {
        return String.join(", ", Collections.nCopies(count, "?"));
    }
}
