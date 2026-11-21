/**
 * Author: Lon Smith, Ph.D.
 * Description: This is the framework for the database program. Additional requirements and functionality
 *    are to be built by you and your group.
 */

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import java.util.List;

public class EmployeeSearchFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDatabase;
	private JList<String> lstDepartment;
	private DefaultListModel<String> department = new DefaultListModel<String>();
	private JList<String> lstProject;
	private DefaultListModel<String> project = new DefaultListModel<String>();
	private JTextArea textAreaEmployee;
   
	private JCheckBox chckbxNotDept;
   private JCheckBox chckbxNotProject;
   /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmployeeSearchFrame frame = new EmployeeSearchFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EmployeeSearchFrame() {
		setTitle("Employee Search");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 347);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Database:");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblNewLabel.setBounds(21, 23, 59, 14);
		contentPane.add(lblNewLabel);
		
		txtDatabase = new JTextField();
		txtDatabase.setBounds(90, 20, 193, 20);
		contentPane.add(txtDatabase);
		txtDatabase.setColumns(10);
		
		JButton btnDBFill = new JButton("Fill");
		/**
		 * The btnDBFill should fill the department and project JList with the 
		 * departments and projects from your entered database name.
		 */
		btnDBFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            
            String dbName = txtDatabase.getText().trim();

        try {
            // Retrieve data using Member 1's service
            List<Department> departments =
                DatabaseService.loadDepartments(dbName);

            List<Project> projects =
                DatabaseService.loadProjects(dbName);

            // Reset existing data
            department.clear();
            project.clear();

            // Display departments
            for (Department d : departments) {
                department.addElement(d.getName());
            }

            // Display projects
            for (Project p : projects) {
                project.addElement(p.getName());
            }

            // Reset GUI selections
            lstDepartment.clearSelection();
            lstProject.clearSelection();
            chckbxNotDept.setSelected(false);
            chckbxNotProject.setSelected(false);
            textAreaEmployee.setText("");

        } catch (DatabaseException ex) {

            department.clear();
            project.clear();
            lstDepartment.clearSelection();
            lstProject.clearSelection();
            textAreaEmployee.setText("");

            JOptionPane.showMessageDialog(
                EmployeeSearchFrame.this,
                ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
});
		
		btnDBFill.setFont(new Font("Times New Roman", Font.BOLD, 12));
		btnDBFill.setBounds(307, 19, 68, 23);
		contentPane.add(btnDBFill);
		
		JLabel lblDepartment = new JLabel("Department");
		lblDepartment.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblDepartment.setBounds(52, 63, 89, 14);
		contentPane.add(lblDepartment);
		
		JLabel lblProject = new JLabel("Project");
		lblProject.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblProject.setBounds(255, 63, 47, 14);
		contentPane.add(lblProject);
		
		lstProject = new JList<String>(new DefaultListModel<String>());
		lstProject.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lstProject.setModel(project);
		lstProject.setBounds(225, 84, 150, 42);
      lstProject.setSelectionMode(
          ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
      );
		
      //adding scroll in the project section
      JScrollPane projectScroll = new JScrollPane(lstProject);
      projectScroll.setBounds(225, 84, 150, 42);
      contentPane.add(projectScroll);
		
		chckbxNotDept = new JCheckBox("Not");
		chckbxNotDept.setBounds(71, 133, 59, 23);
		contentPane.add(chckbxNotDept);
		
		chckbxNotProject = new JCheckBox("Not");
		chckbxNotProject.setBounds(270, 133, 59, 23);
		contentPane.add(chckbxNotProject);
		
		lstDepartment = new JList<String>(new DefaultListModel<String>());
		lstDepartment.setBounds(36, 84, 172, 40);
		
      //adding scroll on the department section
      JScrollPane departmentScroll = new JScrollPane(lstDepartment);
      departmentScroll.setBounds(36, 84, 172, 40);
      contentPane.add(departmentScroll);
		
      lstDepartment.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lstDepartment.setModel(department);
      lstDepartment.setSelectionMode(
          ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
      );
		
		JLabel lblEmployee = new JLabel("Employee");
		lblEmployee.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblEmployee.setBounds(52, 179, 89, 14);
		contentPane.add(lblEmployee);
		
		
      JButton btnSearch = new JButton("Search");

      btnSearch.addActionListener(new ActionListener() {
       @Override
      public void actionPerformed(ActionEvent e) {

        String dbName = txtDatabase.getText().trim();

        // Get selected departments and projects
        List<String> selectedDepartments =
                lstDepartment.getSelectedValuesList();

        List<String> selectedProjects =
                lstProject.getSelectedValuesList();

        // Read NOT checkbox selections
        boolean notDepartment = chckbxNotDept.isSelected();
        boolean notProject = chckbxNotProject.isSelected();

        try {
            EmployeeSearchService searchService =
                    new EmployeeSearchService();

            List<String> employees = searchService.searchEmployees(
                    dbName,
                    selectedDepartments,
                    selectedProjects,
                    notDepartment,
                    notProject
            );

            textAreaEmployee.setText("");

            if (employees.isEmpty()) {
                textAreaEmployee.setText("No employees found.");
            } else {
                textAreaEmployee.setText(
                        String.join("\n", employees)
                );
            }

        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(
                    EmployeeSearchFrame.this,
                    ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
});

		btnSearch.setBounds(80, 276, 89, 23);
		contentPane.add(btnSearch);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaEmployee.setText("");
            lstDepartment.clearSelection();
            lstProject.clearSelection();
            chckbxNotDept.setSelected(false);
            chckbxNotProject.setSelected(false);
			}
		});
		btnClear.setBounds(236, 276, 89, 23);
		contentPane.add(btnClear);
		
		textAreaEmployee = new JTextArea();
      textAreaEmployee.setEditable(false);
		textAreaEmployee.setBounds(36, 197, 339, 68);
		
      
      // Add scrolling to employee results
      JScrollPane employeeScroll = new JScrollPane(textAreaEmployee);
      employeeScroll.setBounds(36, 197, 339, 68);
      contentPane.add(employeeScroll);
	}
}
