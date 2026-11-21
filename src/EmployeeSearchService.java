import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Member 3 - employee search logic.
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
 * This class accepts a JDBC Connection from the existing database layer;
 * it does not open or close that connection.
 */
public final class EmployeeSearchService {

    /**
     * Searches the named MySQL database using Member 1's connection helper.
     * Closes the connection after searching, and reports failures consistently
     * with DatabaseService.
     */
    public List<String> searchEmployees(
            String dbName,
            List<String> selectedDepartments,
            List<String> selectedProjects,
            boolean notDepartment,
            boolean notProject) throws DatabaseException {
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new DatabaseException("Database name is empty.");
        }
        try (Connection connection = DatabaseConnection.getConnection(dbName.trim())) {
            return searchEmployees(connection, selectedDepartments, selectedProjects,
                    notDepartment, notProject);
        } catch (SQLException ex) {
            throw new DatabaseException("Could not search employees in '" + dbName.trim() + "'", ex);
        }
    }


    /**
     * Searches employees and returns formatted display rows for the Swing text area.
     * Uses PreparedStatement parameters for all user-selected names.
     */
    public List<String> searchEmployees(
            Connection connection,
            List<String> selectedDepartments,
            List<String> selectedProjects,
            boolean notDepartment,
            boolean notProject) throws SQLException {

        Objects.requireNonNull(connection, "connection cannot be null");
        List<String> departments = normalize(selectedDepartments);
        List<String> projects = normalize(selectedProjects);

        StringBuilder sql = new StringBuilder(
                "SELECT e.FNAME, e.LNAME, e.SSN FROM EMPLOYEE e WHERE 1=1");
        List<String> parameters = new ArrayList<>();

        if (!departments.isEmpty()) {
            sql.append(notDepartment ? " AND NOT EXISTS (" : " AND EXISTS (");
            sql.append("SELECT 1 FROM DEPARTMENT d "
                    + "WHERE d.DNUMBER = e.DNO AND d.DNAME IN (")
               .append(placeholders(departments.size()))
               .append("))");
            parameters.addAll(departments);
        }

        if (!projects.isEmpty()) {
            sql.append(notProject ? " AND NOT EXISTS (" : " AND EXISTS (");
            sql.append("SELECT 1 FROM WORKS_ON w "
                    + "JOIN PROJECT p ON p.PNUMBER = w.PNO "
                    + "WHERE w.ESSN = e.SSN AND p.PNAME IN (")
               .append(placeholders(projects.size()))
               .append("))");
            parameters.addAll(projects);
        }

        sql.append(" ORDER BY e.LNAME, e.FNAME, e.SSN");

        List<String> matches = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    matches.add(String.format("%s %s (%s)", rs.getString("FNAME"), rs.getString("LNAME"), rs.getString("SSN")));
                }
            }
        }
        return matches;
    }

private static List<String> normalize(List<String> values) {
    List<String> result = new ArrayList<>();

    if (values == null) {
        return result;
    }

    for (String value : values) {
        if (value != null && !value.trim().isEmpty()) {
            result.add(value.trim());
        }
    }

    return result;
}

private static String placeholders(int count) {
    return String.join(", ",
        java.util.Collections.nCopies(count, "?"));
}

}
    