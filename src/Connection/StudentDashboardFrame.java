package Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDashboardFrame extends JFrame {
    private JButton viewAllBooksButton = new JButton("View All Books");
    private JButton viewMyBooksButton = new JButton("View My Books");
    private JButton viewAllFinesButton = new JButton("View My Fines");
    private JButton backButton = new JButton("Return");
    private int student_id;

    public StudentDashboardFrame(int studentId) {
        this.student_id = student_id;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Student Dashboard");
        setSize(600, 400); // Increased size for a more spacious layout

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 20)); // Metallic black background

        // Title label
        JLabel titleLabel = new JLabel("Student Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Corbel", Font.BOLD, 30)); // Larger, more elegant font
        titleLabel.setForeground(new Color(255, 216, 1)); // Gold color for text
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 20)); // Metallic black background
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10)); // Adjust layout for neat arrangement

        buttonPanel.add(styleButton(viewAllBooksButton));
        buttonPanel.add(styleButton(viewMyBooksButton));
        buttonPanel.add(styleButton(viewAllFinesButton));
        buttonPanel.add(styleButton(backButton));

        viewAllBooksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new StdBooksFrame(student_id).setVisible(true);
                dispose();
            }
        });

        viewMyBooksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new MyBooksFrame(student_id).setVisible(true);
                dispose();
            }
        });

        viewAllFinesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new MyFinesFrame(student_id).setVisible(true);
                dispose();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new StudentLoginFrame().setVisible(true);
                dispose();
            }
        });

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private JButton styleButton(JButton button) {
        button.setFont(new Font("Corbel", Font.BOLD, 18)); // Change font and increase size
        button.setPreferredSize(new Dimension(150, 50)); // Increase button size
        button.setBackground(new Color(255, 216, 1)); // Gold color background
        button.setForeground(Color.BLACK); // Black color text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Add shading effect to the buttons
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);
                g2.dispose();
            }
        });
        return button;
    }

}
