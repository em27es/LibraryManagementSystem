package Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitlePageFrame extends JFrame {
    private JButton adminLoginButton = new JButton("Admin Login");
    private JButton studentLoginButton = new JButton("Student Login");

    public TitlePageFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Library Management Assistant");
        setSize(600, 400); // Increased size of the frame

        // Create a panel for the title label
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(20, 20, 20)); // Metallic black background
        JLabel titleLabel = new JLabel("Library Management Assistant", JLabel.CENTER);
        titleLabel.setFont(new Font("Corbel", Font.BOLD, 36)); // Increase font size and change font
        titleLabel.setForeground(new Color(255, 216, 1)); // Gold color for text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        titlePanel.setLayout(new GridBagLayout()); // Use GridBagLayout to center the title
        titlePanel.add(titleLabel);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 20)); // Metallic black background
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Arrange buttons vertically

        // Style and add buttons
        styleButton(adminLoginButton);
        styleButton(studentLoginButton);

        buttonPanel.add(Box.createVerticalGlue()); // Spacer to push buttons to the center
        buttonPanel.add(adminLoginButton);
        buttonPanel.add(Box.createVerticalStrut(30)); // Space between buttons
        buttonPanel.add(studentLoginButton);
        buttonPanel.add(Box.createVerticalGlue()); // Spacer to push buttons to the center

        // Add action listeners for the buttons
        adminLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new AdminLoginFrame().setVisible(true);
                dispose();
            }
        });

        studentLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new StudentLoginFrame().setVisible(true);
                dispose();
            }
        });

        // Add the title panel and button panel to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(titlePanel, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null); // Center the frame on the screen
    }

    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Corbel", Font.BOLD, 18)); // Change font and increase size
        button.setPreferredSize(new Dimension(250, 60)); // Increase button size
        button.setBackground(new Color(255, 216, 1)); // Gold color background
        button.setForeground(Color.BLACK); // Black color text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Make buttons curved
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
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TitlePageFrame().setVisible(true);
            }
        });
    }
}
