package Connection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriesFrame extends JFrame {
    private JButton addButton = new JButton("Add Category");
    private JButton updateButton = new JButton("Update Category");
    private JButton deleteButton = new JButton("Delete Category");
    private JButton backButton = new JButton("Back");
    private JTable categoriesTable;
    private DefaultTableModel tableModel;

    public CategoriesFrame() {
        initComponents();
        loadCategories();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Categories");
        setSize(600, 400);

        String[] columnNames = {"Category ID", "Category Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        categoriesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(categoriesTable);

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

    private void loadCategories() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Categories";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int category_id = resultSet.getInt("category_id");
                String category_name = resultSet.getString("category_name");
                tableModel.addRow(new Object[]{category_id, category_name});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addButtonActionPerformed(ActionEvent evt) {
        String category_name = JOptionPane.showInputDialog(this, "Enter Category Name");

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO Categories (category_name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
 
            preparedStatement.setString(1, category_name);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Category added successfully!");
            loadCategories();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to update.");
            return;
        }

        String category_id = tableModel.getValueAt(selectedRow, 0).toString();
        String category_name = JOptionPane.showInputDialog(this, "Enter New Category Name", tableModel.getValueAt(selectedRow, 1).toString());

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, category_name);
            preparedStatement.setInt(2, Integer.parseInt(category_id));
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Category updated successfully!");
            loadCategories();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteButtonActionPerformed(ActionEvent evt) {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.");
            return;
        }

        int category_id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection connection = DBConnection.getConnection()) {
            String sqlDeleteBookCategories = "DELETE FROM BookCategories WHERE category_id = ?";
            PreparedStatement preparedStatementDeleteBookCategories = connection.prepareStatement(sqlDeleteBookCategories);
            preparedStatementDeleteBookCategories.setInt(1, category_id);
            int rowsAffected = preparedStatementDeleteBookCategories.executeUpdate();
            
            String sql = "DELETE FROM Categories WHERE category_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, category_id);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Category deleted successfully!");
            loadCategories();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
