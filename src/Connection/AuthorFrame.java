package Connection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorFrame extends JFrame {
    private JButton addButton = new JButton("Add Author");
    private JButton updateButton = new JButton("Update Author");
    private JButton deleteButton = new JButton("Delete Author");
    private JButton backButton = new JButton("Back");
    private JTable authorTable;
    private DefaultTableModel tableModel;

    public AuthorFrame() {
        initComponents();
        loadAuthors();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Authors");
        setSize(600, 400); // Set standard dialog box size

        String[] columnNames = {"Author ID", "Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        authorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(authorTable);

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

    private void loadAuthors() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Authors";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int author_id = resultSet.getInt("author_id");
                String author_name = resultSet.getString("author_name");
                
                tableModel.addRow(new Object[]{author_id, author_name});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addButtonActionPerformed(ActionEvent evt) {
      

        String name = JOptionPane.showInputDialog(this, "Enter Author Name");

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO Authors (author_name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Author added successfully!");
            loadAuthors();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isAuthorIDExists(String author_id) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Authors WHERE author_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(author_id));
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
        int selectedRow = authorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to update.");
            return;
        }

        String authorID = tableModel.getValueAt(selectedRow, 0).toString();
        String name = JOptionPane.showInputDialog(this, "Enter New Author Name", tableModel.getValueAt(selectedRow, 1).toString());
        

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE Authors SET author_name = ? WHERE author_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, Integer.parseInt(authorID));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Author updated successfully!");
            loadAuthors();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

private void deleteButtonActionPerformed(ActionEvent evt) {
    int selectedRow = authorTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an author to delete.");
        return;
    }

    String author_id = tableModel.getValueAt(selectedRow, 0).toString();

    try (Connection connection = DBConnection.getConnection()) {
        connection.setAutoCommit(false); // Start transaction

        try {
            // First, delete dependent rows in BookAuthors table
            String sqlDeleteBookAuthors = "DELETE FROM BookAuthors WHERE author_id = ?";
            PreparedStatement preparedStatementDeleteBookAuthors = connection.prepareStatement(sqlDeleteBookAuthors);
            preparedStatementDeleteBookAuthors.setInt(1, Integer.parseInt(author_id));
            preparedStatementDeleteBookAuthors.executeUpdate();

            // Then, delete the row in Authors table
            String sqlDeleteAuthor = "DELETE FROM Authors WHERE author_id = ?";
            PreparedStatement preparedStatementDeleteAuthor = connection.prepareStatement(sqlDeleteAuthor);
            preparedStatementDeleteAuthor.setInt(1, Integer.parseInt(author_id));
            int rowsAffected = preparedStatementDeleteAuthor.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit(); // Commit transaction
                JOptionPane.showMessageDialog(this, "Author deleted successfully!");
                loadAuthors();
            } else {
                connection.rollback(); // Rollback transaction in case of failure
                JOptionPane.showMessageDialog(this, "Failed to delete the author.");
            }
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of error
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred. Transaction rolled back.");
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit
        }
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}

}
