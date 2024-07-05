package Connection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentFrame extends JFrame {
    private JButton addButton = new JButton("Add Student");
    private JButton updateButton = new JButton("Update Student");
    private JButton deleteButton = new JButton("Delete Student");
    private JButton backButton = new JButton("Back");
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentFrame() {
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Students");
        setSize(600, 400); // Set standard dialog box size

        String[] columnNames = {"Student ID", "Student Name", "Student Password"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Set custom colors and font
        Color backgroundColor = new Color(60, 63, 65);
        Color buttonColor = new Color(75, 110, 175);
        Color textColor = new Color(187, 187, 188);
        Font font = new Font("Arial", Font.PLAIN, 14);

        getContentPane().setBackground(backgroundColor);
        addButton.setBackground(buttonColor);
        updateButton.setBackground(buttonColor);
        deleteButton.setBackground(buttonColor);
        backButton.setBackground(buttonColor);
        addButton.setForeground(textColor);
        updateButton.setForeground(textColor);
        deleteButton.setForeground(textColor);
        backButton.setForeground(textColor);
        addButton.setFont(font);
        updateButton.setFont(font);
        deleteButton.setFont(font);
        backButton.setFont(font);
        studentTable.setFont(font);
        studentTable.setForeground(textColor);
        studentTable.setBackground(backgroundColor);
        studentTable.getTableHeader().setFont(font);
        studentTable.getTableHeader().setForeground(textColor);
        studentTable.getTableHeader().setBackground(buttonColor);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addButtonActionPerformed(evt);
                
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateButtonActionPerformed(evt);
                
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteButtonActionPerformed(evt);
                
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new AdminDashboardFrame().setVisible(true);
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loadStudents() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Students";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int student_id = resultSet.getInt("student_id");
                String student_name = resultSet.getString("student_name");
                String student_password = resultSet.getString("student_password");
                tableModel.addRow(new Object[]{student_id, student_name, student_password});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addButtonActionPerformed(ActionEvent evt) {
        String username = JOptionPane.showInputDialog(this, "Enter Student Name");
        String password = JOptionPane.showInputDialog(this, "Enter Student Password");

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO Students (student_name, student_password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student added successfully!");
            loadStudents();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.");
            return;
        }

        String studentID = tableModel.getValueAt(selectedRow, 0).toString();
        String name = JOptionPane.showInputDialog(this, "Enter New Name", tableModel.getValueAt(selectedRow, 1).toString());
        String password = JOptionPane.showInputDialog(this, "Enter New Password", tableModel.getValueAt(selectedRow, 2).toString());

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE Students SET student_name = ?, student_password = ? WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, Integer.parseInt(studentID));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student updated successfully!");
            loadStudents();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteButtonActionPerformed(ActionEvent evt) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }

        String student_id = tableModel.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = DBConnection.getConnection()) {
                // Start a transaction
                connection.setAutoCommit(false);

                // Delete dependent rows in related tables in the correct order
                String sqlDeleteMyFines = "DELETE FROM MyFines WHERE student_id = ?";
                PreparedStatement preparedStatementDeleteMyFines = connection.prepareStatement(sqlDeleteMyFines);
                preparedStatementDeleteMyFines.setInt(1, Integer.parseInt(student_id));
                preparedStatementDeleteMyFines.executeUpdate();
                
                String sqlDeleteFines = "DELETE FROM Fines WHERE student_id = ?";
                PreparedStatement preparedStatementDeleteFines = connection.prepareStatement(sqlDeleteFines);
                preparedStatementDeleteFines.setInt(1, Integer.parseInt(student_id));
                preparedStatementDeleteFines.executeUpdate();
                
                String sqlDeleteReservations = "DELETE FROM Reservations WHERE student_id = ?";
                PreparedStatement preparedStatementDeleteReservations = connection.prepareStatement(sqlDeleteReservations);
                preparedStatementDeleteReservations.setInt(1, Integer.parseInt(student_id));
                preparedStatementDeleteReservations.executeUpdate();
                
                String sqlDeleteMyBooks = "DELETE FROM MyBooks WHERE student_id = ?";
                PreparedStatement preparedStatementDeleteMyBooks = connection.prepareStatement(sqlDeleteMyBooks);
                preparedStatementDeleteMyBooks.setInt(1, Integer.parseInt(student_id));
                preparedStatementDeleteMyBooks.executeUpdate();

                // Finally, delete the row in Students table
                String sqlDeleteStudent = "DELETE FROM Students WHERE student_id = ?";
                PreparedStatement preparedStatementDeleteStudent = connection.prepareStatement(sqlDeleteStudent);
                preparedStatementDeleteStudent.setInt(1, Integer.parseInt(student_id));
                int rowsAffected = preparedStatementDeleteStudent.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit(); // Commit transaction
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                    loadStudents();
                } else {
                    connection.rollback(); // Rollback transaction
                    JOptionPane.showMessageDialog(this, "Failed to delete the student. Please try again.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while deleting the student. Please try again.");
            }
        }
    }
}
