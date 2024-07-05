package Connection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationsFrame extends JFrame {
    private JButton backButton = new JButton("Back");
    private JTable reservationsTable;
    private DefaultTableModel tableModel;

    public ReservationsFrame() {
        initComponents();
        loadReservations();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reservations");
        setSize(800, 600);

        String[] columnNames = {"Reservation ID", "Book ID", "Student ID", "Approve Request"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only the "Approve Request" column is editable
            }
        };
        reservationsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationsTable);

        reservationsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), (evt) -> {
            int row = reservationsTable.getSelectedRow();
            int reservation_id = (int) reservationsTable.getValueAt(row, 0);
            int book_id = (int) reservationsTable.getValueAt(row, 1);
            int student_id = (int) reservationsTable.getValueAt(row, 2);
            approveRequest(reservation_id, book_id, student_id);
        }));

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new AdminDashboardFrame().setVisible(true);
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        getContentPane().add(scrollPane, "Center");
        getContentPane().add(buttonPanel, "South");

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loadReservations() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Reservations";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int reservation_id = resultSet.getInt("reservation_id");
                int book_id = resultSet.getInt("book_id");
                int student_id = resultSet.getInt("student_id");
                tableModel.addRow(new Object[]{reservation_id, book_id, student_id, "Approve"});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void approveRequest(int reservation_id, int book_id, int student_id) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String sqlApprove = "INSERT INTO MyBooks (student_id, book_id) VALUES (?, ?)";
                PreparedStatement preparedStatementApprove = connection.prepareStatement(sqlApprove);
                preparedStatementApprove.setInt(1, student_id);
                preparedStatementApprove.setInt(2, book_id);
                preparedStatementApprove.executeUpdate();

                String sqlDeleteReservation = "DELETE FROM Reservations WHERE reservation_id = ?";
                PreparedStatement preparedStatementDelete = connection.prepareStatement(sqlDeleteReservation);
                preparedStatementDelete.setInt(1, reservation_id);
                preparedStatementDelete.executeUpdate();

                connection.commit();
                JOptionPane.showMessageDialog(this, "Reservation approved successfully!");
                loadReservations();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while approving the reservation. Please try again.");
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
