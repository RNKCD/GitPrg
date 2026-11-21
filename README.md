# GitPrg

A Java Swing program that loads the departments and projects from a COMPANY style MySQL database, then searches for employees who are in, or not in, the departments and projects you select. Built for CSCI 4055.

## Features

- **Fill** loads the Department and Project lists from the database name you type. If the database cannot be opened, an error pop-up appears.
- Select one or several departments and projects. All lists are scrollable.
- **Search** lists the matching employees. Selected items in one list are combined with OR, and the department and project filters are combined with AND.
- The **Not** check boxes list employees who are not in the selected departments, or not working on the selected projects.
- **Clear** empties the Employee list, clears the selections and turns both Not boxes off.

## Tools used

- Java 21 and Java Swing for the window
- GUI window: the instructor's `EmployeeSearchFrame` template, which was built with Eclipse WindowBuilder. We did not use a GUI designer. Our additions (scroll panes, button actions) were written by hand in jGRASP.
- MySQL (MariaDB) from XAMPP for the database
- MySQL Connector/J 8.4.0 (`lib/mysql-connector-j-8.4.0.jar`) for JDBC
- jGRASP, Eclipse and Visual Studio Code for editing
- Git and GitHub for version control

## Setup

1. Install XAMPP and start **MySQL** from the XAMPP manager. The program expects `localhost:3306`, user `root`, empty password (the XAMPP defaults). If another MySQL is already using port 3306, stop it first.
2. Create the database by importing `database/company.sql`. It creates a database named `company` with the sample data (3 departments, 6 projects, 8 employees).
   - phpMyAdmin: open the Import tab, choose `database/company.sql`, and click Import. This needs Apache started too.
   - Terminal: `mysql -u root < database/company.sql`
   - If the `company` database already exists with tables, drop it first. Importing twice fails because the tables already exist.
3. Open the folder in Eclipse as an existing Java project. `.classpath` already has the Connector/J jar on the build path.

## Run

Run `EmployeeSearchFrame` (it contains `main`), type `company` in the Database box, and click **Fill**.

From a terminal in the project folder:

```
javac -d bin -cp lib/mysql-connector-j-8.4.0.jar src/*.java
java -cp bin:lib/mysql-connector-j-8.4.0.jar EmployeeSearchFrame
```

On Windows, use `;` instead of `:` in the classpath.

## Code layout

| File | Purpose |
|---|---|
| `src/EmployeeSearchFrame.java` | The window and button handlers |
| `src/DatabaseConnection.java` | Opens a JDBC connection (host, port, user and password constants) |
| `src/DatabaseService.java` | `loadDepartments` and `loadProjects` |
| `src/Department.java`, `src/Project.java` | Number and name for each list item |
| `src/DatabaseException.java` | Checked exception for any database failure |
| `src/EmployeeSearchService.java` | Employee search by department and project numbers, with Not support |
| `src/EmployeeSearchServiceTest.java` | Checks the search against the sample data (needs MySQL running) |
| `database/company.sql` | Creates and fills the `company` database |

## Tests

With MySQL running and the `company` database imported, run `EmployeeSearchServiceTest`. It prints PASS or FAIL for each case.
