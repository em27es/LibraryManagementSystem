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

public class MyBooksFrame extends JFrame {
    private JTable myBooksTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    private int student_id;
    public MyBooksFrame(int student_id) {
        initComponents();
        loadMyBooks(student_id);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("My Books");
        setSize(600, 400);

        String[] columnNames = {"Book ID", "Title","Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        myBooksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(myBooksTable);

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new StudentDashboardFrame(student_id).setVisible(true);
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loadMyBooks(int student_id) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT Books.book_id " +
                         "FROM Reservations " +
                         "INNER JOIN Books ON Reservations.book_id = Books.book_id " +
                         "WHERE Reservations.student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, student_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int book_id = resultSet.getInt("book_id");
                
                
                tableModel.addRow(new Object[]{book_id});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
