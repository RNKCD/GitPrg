import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Checks EmployeeSearchService against the sample COMPANY data.
 * Run with the MySQL server started and the "company" database imported.
 * Optional argument: the database name (default "company").
 * Departments: Research 5, Administration 4, Headquarters 1.
 * Projects: ProductX 1, ProductY 2, ProductZ 3, Computerization 10,
 * Reorganization 20, Newbenefits 30.
 */
public class EmployeeSearchServiceTest {

    private static final List<Integer> NONE = Collections.emptyList();
    private static int failures = 0;
    private static String db = "company";

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            db = args[0];
        }
        System.out.println("Starting Employee Search Tests on '" + db + "'...");

        check("No selection returns everyone", NONE, NONE, false, false, 8);
        check("Not boxes ignored when nothing selected", NONE, NONE, true, true, 8);
        check("Research", Arrays.asList(5), NONE, false, false, 4);
        check("Not Research", Arrays.asList(5), NONE, true, false, 4);
        check("Research and Administration", Arrays.asList(5, 4), NONE, false, false, 7);
        check("ProductX", NONE, Arrays.asList(1), false, false, 2);
        check("Not ProductX", NONE, Arrays.asList(1), false, true, 6);
        check("ProductX and ProductY", NONE, Arrays.asList(1, 2), false, false, 3);
        check("Research and ProductX", Arrays.asList(5), Arrays.asList(1), false, false, 2);
        check("Research and not ProductX", Arrays.asList(5), Arrays.asList(1), false, true, 2);

        try {
            new EmployeeSearchService().searchEmployees("no_such_db", NONE, NONE, false, false);
            fail("Bad database name should throw DatabaseException");
        } catch (DatabaseException e) {
            System.out.println("PASS: Bad database name throws DatabaseException");
        }

        try {
            new EmployeeSearchService().searchEmployees("  ", NONE, NONE, false, false);
            fail("Blank database name should throw DatabaseException");
        } catch (DatabaseException e) {
            System.out.println("PASS: Blank database name throws DatabaseException");
        }

        System.out.println(failures == 0 ? "All tests passed." : failures + " test(s) FAILED.");
        if (failures > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, List<Integer> departments, List<Integer> projects,
            boolean notDepartment, boolean notProject, int expected) throws DatabaseException {
        List<String> result = new EmployeeSearchService()
                .searchEmployees(db, departments, projects, notDepartment, notProject);
        if (result.size() == expected) {
            System.out.println("PASS: " + name + " (" + expected + ")");
        } else {
            fail(name + ": expected " + expected + " but got " + result.size() + " " + result);
        }
    }

    private static void fail(String message) {
        failures++;
        System.out.println("FAIL: " + message);
    }
}
