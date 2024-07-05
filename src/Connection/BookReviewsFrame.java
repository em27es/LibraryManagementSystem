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

public class BookReviewsFrame extends JFrame {

    private JButton backButton = new JButton("Back");
    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private int book_id;

    public BookReviewsFrame(int book_id) {
        this.book_id = book_id;
        initComponents();
        loadReviews();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Book Reviews");
        setSize(600, 400); // Set standard dialog box size

        String[] columnNames = {"Review ID", "Book ID", "Rating", "Review Text"};
        tableModel = new DefaultTableModel(columnNames, 0);
        reviewTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reviewTable);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loadReviews() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM bookreviews WHERE book_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, book_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int review_id = resultSet.getInt("review_id");
                int book_id = resultSet.getInt("book_id");
                
                int rating = resultSet.getInt("rating");
                String review_text = resultSet.getString("review_text");
                
                tableModel.addRow(new Object[]{review_id, book_id, rating, review_text});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
