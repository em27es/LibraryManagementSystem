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

public class StdBooksFrame extends JFrame {

    private JButton backButton = new JButton("Back");
    private JButton requestButton = new JButton("Request Book");
    private JButton showReviewsButton = new JButton("Show Reviews");
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private int student_id;
    
    public StdBooksFrame(int student_id) {
        initComponents();
        loadBooks();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Books");
        setSize(600, 400);

        String[] columnNames = {"Book ID", "Title", "Status", "Publisher ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new StudentDashboardFrame(student_id).setVisible(true);
                dispose();
            }
        });

        requestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                requestBook();
            }
        });

        showReviewsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showBookReviews();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(requestButton);
        buttonPanel.add(showReviewsButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void loadBooks() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Books";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                int book_id = resultSet.getInt("book_id");
                String book_name = resultSet.getString("book_name");
                String book_status = resultSet.getString("book_status");
                int publisher_id = resultSet.getInt("publisher_id");
                tableModel.addRow(new Object[]{book_id, book_name, book_status, publisher_id});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void requestBook() {
        String studentIdStr = JOptionPane.showInputDialog(this, "Enter your Student ID:", "Request Book", JOptionPane.PLAIN_MESSAGE);
        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.");
            return;
        }

        int student_id;
        try {
            student_id = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID format.");
            return;
        }

        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to request.");
            return;
        }

        int book_id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection connection = DBConnection.getConnection()) {
            // Check if the student_id exists in the Students table
            String checkStudentSql = "SELECT COUNT(*) FROM Students WHERE student_id = ?";
            PreparedStatement checkStudentStmt = connection.prepareStatement(checkStudentSql);
            checkStudentStmt.setInt(1, student_id);
            ResultSet studentResultSet = checkStudentStmt.executeQuery();

            if (studentResultSet.next() && studentResultSet.getInt(1) > 0) {
                // Insert into Reservations
                String sql = "INSERT INTO Reservations (student_id, book_id) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, student_id);
                preparedStatement.setInt(2, book_id);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Book request submitted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit book request.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student ID does not exist.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while submitting the request: " + e.getMessage());
        }
    }

    private void showBookReviews() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to view reviews.");
            return;
        }

        int book_id = (int) tableModel.getValueAt(selectedRow, 0);
        new BookReviewsFrame(book_id).setVisible(true);
    }
}
