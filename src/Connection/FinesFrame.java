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

public class FinesFrame extends JFrame {
    private JButton addButton = new JButton("Add Fine");
    private JButton updateButton = new JButton("Update Fine");
    private JButton deleteButton = new JButton("Delete Fine");
    private JButton sendAlertButton = new JButton("Send Alert");
    private JButton backButton = new JButton("Back");
    private JTable finesTable;
    private DefaultTableModel tableModel;

    public FinesFrame() {
        initComponents();
        loadFines();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fines");
        setSize(800, 600);

        String[] columnNames = {"fine_id", "student_id", "fine_status", "fine_amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        finesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(finesTable);

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

        sendAlertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sendAlertButtonActionPerformed(evt);
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
        buttonPanel.add(sendAlertButton);
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void loadFines() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Fines";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                int fine_id = resultSet.getInt("fine_id");
                int student_id = resultSet.getInt("student_id");
                String fine_status = resultSet.getString("fine_status");
                double fine_amount = resultSet.getDouble("fine_amount");
                tableModel.addRow(new Object[]{fine_id, student_id, fine_status, fine_amount});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addButtonActionPerformed(ActionEvent evt) {
        try (Connection connection = DBConnection.getConnection()) {
            String studentIdStr = JOptionPane.showInputDialog(this, "Enter Student ID");
            int student_id = Integer.parseInt(studentIdStr);

            String fine_status = JOptionPane.showInputDialog(this, "Enter Fine Status");
            String fine_amountStr = JOptionPane.showInputDialog(this, "Enter Fine Amount");
            double fine_amount = Double.parseDouble(fine_amountStr);

            String sql = "INSERT INTO Fines (student_id, fine_status, fine_amount) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, student_id);
            preparedStatement.setString(2, fine_status);
            preparedStatement.setDouble(3, fine_amount);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Fine added successfully!");
            loadFines();
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while adding the fine. Please try again.");
        }
    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        int selectedRow = finesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to update.");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            int fine_id = (int) tableModel.getValueAt(selectedRow, 0);
            int student_id = (int) tableModel.getValueAt(selectedRow, 1);
            String fine_status = JOptionPane.showInputDialog(this, "Enter New Fine Status", tableModel.getValueAt(selectedRow, 2));
            String fine_amountStr = JOptionPane.showInputDialog(this, "Enter New Fine Amount", tableModel.getValueAt(selectedRow, 3));
            double fine_amount = Double.parseDouble(fine_amountStr);

            String sql = "UPDATE Fines SET fine_status = ?, fine_amount = ? WHERE fine_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, fine_status);
            preparedStatement.setDouble(2, fine_amount);
            preparedStatement.setInt(3, fine_id);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Fine updated successfully!");
            loadFines();
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the fine. Please try again.");
        }
    }

    private void deleteButtonActionPerformed(ActionEvent evt) {
        int selectedRow = finesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to delete.");
            return;
        }

        int fine_id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String sqlDeleteMyFines = "DELETE FROM MyFines WHERE fine_id = ?";
                PreparedStatement preparedStatementDeleteMyFines = connection.prepareStatement(sqlDeleteMyFines);
                preparedStatementDeleteMyFines.setInt(1, fine_id);
                preparedStatementDeleteMyFines.executeUpdate();

                String sqlDeleteFine = "DELETE FROM Fines WHERE fine_id = ?";
                PreparedStatement preparedStatementDeleteFine = connection.prepareStatement(sqlDeleteFine);
                preparedStatementDeleteFine.setInt(1, fine_id);
                int rowsAffected = preparedStatementDeleteFine.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                    JOptionPane.showMessageDialog(this, "Fine deleted successfully!");
                    loadFines();
                } else {
                    connection.rollback();
                    JOptionPane.showMessageDialog(this, "Failed to delete the fine.");
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while deleting the fine. Please try again.");
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while deleting the fine. Please try again.");
        }
    }

    private void sendAlertButtonActionPerformed(ActionEvent evt) {
        int selectedRow = finesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to send an alert.");
            return;
        }

        int fine_id = (int) tableModel.getValueAt(selectedRow, 0);
        int student_id = (int) tableModel.getValueAt(selectedRow, 1);

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO MyFines (fine_id, student_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, fine_id);
            preparedStatement.setInt(2, student_id);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Alert sent to the student successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while sending the alert. Please try again.");
        }
    }
}
