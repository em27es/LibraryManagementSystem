package Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginFrame extends JFrame {
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");
    private JButton backButton = new JButton("Back");

    public AdminLoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Admin Login");
        setSize(600, 400); // Increased size of the frame

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 20)); // Metallic black background

        // Title label
        JLabel titleLabel = new JLabel("Admin Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Corbel", Font.BOLD, 30)); // Larger, more elegant font
        titleLabel.setForeground(new Color(255, 216, 1)); // Gold color for text
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(20, 20, 20)); // Metallic black background
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        addLabeledField(formPanel, "Username:", usernameField);
        addLabeledField(formPanel, "Password:", passwordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 20)); // Metallic black background
        buttonPanel.add(styleButton(loginButton));
        buttonPanel.add(styleButton(backButton));

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new TitlePageFrame().setVisible(true);
                dispose();
            }
        });

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void loginButtonActionPerformed(ActionEvent evt) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Admin WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Logged in successfully!");
                new AdminDashboardFrame().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addLabeledField(JPanel panel, String label, JComponent field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(new Color(255, 216, 1)); // Gold color for text
        jLabel.setFont(new Font("Corbel", Font.BOLD, 18)); // Change font
        jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(field.getPreferredSize());

        panel.add(jLabel);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10)); // a spacer
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
