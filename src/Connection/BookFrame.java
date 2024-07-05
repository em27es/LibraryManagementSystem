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

public class BookFrame extends JFrame {
    private JButton addButton = new JButton("Add Book");
    private JButton updateButton = new JButton("Update Book");
    private JButton deleteButton = new JButton("Delete Book");
    private JButton backButton = new JButton("Back");
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public BookFrame() {
        initComponents();
        loadBooks();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Books");
        setSize(600, 400); // Set standard dialog box size

        String[] columnNames = {"Book ID", "Book Name", "Book Status", "Publisher ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

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
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loadBooks() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Books";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

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

    private void addButtonActionPerformed(ActionEvent evt) {
        String name = JOptionPane.showInputDialog(this, "Enter Book Name");
        String status = JOptionPane.showInputDialog(this, "Enter Book Status");
        String publisherID = JOptionPane.showInputDialog(this, "Enter Publisher ID");

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO Books (book_name, book_status, publisher_id) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, status);
            preparedStatement.setInt(3, Integer.parseInt(publisherID));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book added successfully!");
            loadBooks();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to update.");
            return;
        }

        String bookID = tableModel.getValueAt(selectedRow, 0).toString();
        String name = JOptionPane.showInputDialog(this, "Enter New Name", tableModel.getValueAt(selectedRow, 1).toString());
        String status = JOptionPane.showInputDialog(this, "Enter New Status", tableModel.getValueAt(selectedRow, 2).toString());
        String publisherID = JOptionPane.showInputDialog(this, "Enter New Publisher ID", tableModel.getValueAt(selectedRow, 3).toString());

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE Books SET book_name = ?, book_status = ?, publisher_id = ? WHERE book_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, status);
            preparedStatement.setInt(3, Integer.parseInt(publisherID));
            preparedStatement.setInt(4, Integer.parseInt(bookID));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            loadBooks();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteButtonActionPerformed(ActionEvent evt) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        String book_id = tableModel.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = DBConnection.getConnection()) {
                // Start a transaction
                connection.setAutoCommit(false);

                // Delete dependent rows in related tables in the correct order
                String sqlDeleteMyBooks = "DELETE FROM MyBooks WHERE book_id = ?";
                PreparedStatement preparedStatementDeleteMyBooks = connection.prepareStatement(sqlDeleteMyBooks);
                preparedStatementDeleteMyBooks.setInt(1, Integer.parseInt(book_id));
                preparedStatementDeleteMyBooks.executeUpdate();

                String sqlDeleteBookReviews = "DELETE FROM BookReviews WHERE book_id = ?";
                PreparedStatement preparedStatementDeleteBookReviews = connection.prepareStatement(sqlDeleteBookReviews);
                preparedStatementDeleteBookReviews.setInt(1, Integer.parseInt(book_id));
                preparedStatementDeleteBookReviews.executeUpdate();
                
                String sqlDeleteBookCategories = "DELETE FROM BookCategories WHERE book_id = ?";
                PreparedStatement preparedStatementDeleteBookCategories = connection.prepareStatement(sqlDeleteBookCategories);
                preparedStatementDeleteBookCategories.setInt(1, Integer.parseInt(book_id));
                preparedStatementDeleteBookCategories.executeUpdate();
                
                String sqlDeleteBookAuthors = "DELETE FROM BookAuthors WHERE book_id = ?";
                PreparedStatement preparedStatementDeleteBookAuthors = connection.prepareStatement(sqlDeleteBookAuthors);
                preparedStatementDeleteBookAuthors.setInt(1, Integer.parseInt(book_id));
                preparedStatementDeleteBookAuthors.executeUpdate();
                
                String sqlDeleteReservations = "DELETE FROM Reservations WHERE book_id = ?";
                PreparedStatement preparedStatementDeleteReservations = connection.prepareStatement(sqlDeleteReservations);
                preparedStatementDeleteReservations.setInt(1, Integer.parseInt(book_id));
                preparedStatementDeleteReservations.executeUpdate();

                // Finally, delete the row in Books table
                String sqlDeleteBook = "DELETE FROM Books WHERE book_id = ?";
                PreparedStatement preparedStatementDeleteBook = connection.prepareStatement(sqlDeleteBook);
                preparedStatementDeleteBook.setInt(1, Integer.parseInt(book_id));
                int rowsAffected = preparedStatementDeleteBook.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit(); // Commit transaction
                    JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                    loadBooks();
                } else {
                    connection.rollback(); // Rollback transaction
                    JOptionPane.showMessageDialog(this, "Failed to delete the book. Please try again.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while deleting the book. Please try again.");
            }
        }
    }
}
