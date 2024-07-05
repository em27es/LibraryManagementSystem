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

public class MyFinesFrame extends JFrame {
    private JTable finesTable;
    private DefaultTableModel tableModel;
    private JButton backButton = new JButton("Back");
    private int student_id;

    public MyFinesFrame(int student_id) {
        this.student_id = student_id;
        initComponents();
        loadMyFines();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("My Fines");
        setSize(800, 600);

        String[] columnNames = {"Fine ID",  "Fine Status","Fine Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        finesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(finesTable);

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

        setLocationRelativeTo(null);
    }

    private void loadMyFines() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT f.fine_id,  f.fine_status, f.fine_amount " +
                         "FROM Fines f INNER JOIN MyFines mf ON f.fine_id = mf.fine_id WHERE mf.student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, student_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                int fine_id = resultSet.getInt("fine_id");
                
                String fine_status = resultSet.getString("fine_status");
                
                double fine_amount = resultSet.getDouble("fine_amount");
                tableModel.addRow(new Object[]{fine_id,  fine_status, fine_amount});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
