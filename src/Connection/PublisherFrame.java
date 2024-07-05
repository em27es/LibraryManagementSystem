package Connection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PublisherFrame extends JFrame {
    private JButton addButton = new JButton("Add Publisher");
    private JButton updateButton = new JButton("Update Publisher");
    private JButton deleteButton = new JButton("Delete Publisher");
    private JButton backButton = new JButton("Back");
    private JTable publisherTable;
    private DefaultTableModel tableModel;

    public PublisherFrame() {
        initComponents();
        loadPublishers();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Publishers");
        setSize(600, 400); // Set standard dialog box size

        String[] columnNames = {"Publisher ID", "Publisher Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        publisherTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(publisherTable);

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
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, "Center");
        getContentPane().add(buttonPanel, "South");

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loadPublishers() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Publishers";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int publisher_id = resultSet.getInt("publisher_id");
                String publisher_name = resultSet.getString("publisher_name");
               
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                
                tableModel.addRow(new Object[]{publisher_id, publisher_name, email, phone});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addButtonActionPerformed(ActionEvent evt) {

        String name = JOptionPane.showInputDialog(this, "Enter Publisher Name");
        String email = JOptionPane.showInputDialog(this, "Enter Publisher Email");
        String phone = JOptionPane.showInputDialog(this, "Enter Publisher Phone");

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO Publishers (publisher_name,email, phone) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
         
            preparedStatement.setString(1, name);
            
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
           
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Publisher added successfully!");
            loadPublishers();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isPublisherIDExists(String publisher_id) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Publishers WHERE publisher_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(publisher_id));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        int selectedRow = publisherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a publisher to update.");
            return;
        }

        String publisherID = tableModel.getValueAt(selectedRow, 0).toString();
        String name = JOptionPane.showInputDialog(this, "Enter New Publisher Name", tableModel.getValueAt(selectedRow, 1).toString());
        
        String email = JOptionPane.showInputDialog(this, "Enter New Publisher Email", tableModel.getValueAt(selectedRow, 2).toString());
        String phone = JOptionPane.showInputDialog(this, "Enter New Publisher Phone", tableModel.getValueAt(selectedRow, 3).toString());
        

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE Publishers SET publisher_name = ?, email = ?, phone = ? WHERE publisher_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
           
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
           
            preparedStatement.setInt(4, Integer.parseInt(publisherID));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Publisher updated successfully!");
            loadPublishers();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

private void deleteButtonActionPerformed(ActionEvent evt) {
    int selectedRow = publisherTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a publisher to delete.");
        return;
    }

    int publisher_id = (int) tableModel.getValueAt(selectedRow, 0);

    try (Connection connection = DBConnection.getConnection()) {
        // Start a transaction
        connection.setAutoCommit(false);

        try {
            // First, delete dependent rows in books_issued table
            String sqlDeleteMyBooks = "DELETE FROM MyBooks WHERE book_id = ?";
            PreparedStatement preparedStatementDeleteMyBooks = connection.prepareStatement(sqlDeleteMyBooks);
            preparedStatementDeleteMyBooks.setInt(1, publisher_id);
            preparedStatementDeleteMyBooks.executeUpdate();
            
            String sqlDeleteBookCategories = "DELETE FROM BookCategories WHERE book_id = ?";
            PreparedStatement preparedStatementDeleteBookCategories = connection.prepareStatement(sqlDeleteBookCategories);
            preparedStatementDeleteBookCategories.setInt(1, publisher_id);
            preparedStatementDeleteBookCategories.executeUpdate();
            
            String sqlDeleteReservations = "DELETE FROM Reservations WHERE book_id = ?";
            PreparedStatement preparedStatementDeleteReservations = connection.prepareStatement(sqlDeleteReservations);
            preparedStatementDeleteReservations.setInt(1, publisher_id);
            preparedStatementDeleteReservations.executeUpdate();
            
            String sqlDeleteBookAuthors = "DELETE FROM BookAuthors WHERE book_id = ?";
            PreparedStatement preparedStatementDeleteBookAuthors = connection.prepareStatement(sqlDeleteBookAuthors);
            preparedStatementDeleteBookAuthors.setInt(1, publisher_id);
            preparedStatementDeleteBookAuthors.executeUpdate();
            
            String sqlDeleteBookReviews = "DELETE FROM BookReviews WHERE book_id = ?";
            PreparedStatement preparedStatementDeleteBookReviews = connection.prepareStatement(sqlDeleteBookReviews);
            preparedStatementDeleteBookReviews.setInt(1, publisher_id);
            preparedStatementDeleteBookReviews.executeUpdate();

            // Then, delete rows in books table
            String sqlDeleteBooks = "DELETE FROM books WHERE publisher_id = ?";
            PreparedStatement preparedStatementDeleteBooks = connection.prepareStatement(sqlDeleteBooks);
            preparedStatementDeleteBooks.setInt(1, publisher_id);
            preparedStatementDeleteBooks.executeUpdate();

            // Finally, delete the row in Publishers table
            String sqlDeletePublisher = "DELETE FROM Publishers WHERE publisher_id = ?";
            PreparedStatement preparedStatementDeletePublisher = connection.prepareStatement(sqlDeletePublisher);
            preparedStatementDeletePublisher.setInt(1, publisher_id);
            int rowsAffected = preparedStatementDeletePublisher.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit(); // Commit transaction
                JOptionPane.showMessageDialog(this, "Publisher deleted successfully!");
                loadPublishers();
            } else {
                connection.rollback(); // Rollback transaction
                JOptionPane.showMessageDialog(this, "Failed to delete the publisher.");
            }
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of error
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the publisher. Please try again.");
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit
        }
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}

}

