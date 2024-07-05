package Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardFrame extends JFrame {
    private JButton showAllStudentsButton = new JButton("Show All Students");
    private JButton showAllBooksButton = new JButton("Show All Books");
    private JButton showAllAuthorsButton = new JButton("Show All Authors");
    private JButton showAllPublishersButton = new JButton("Show All Publishers");
    private JButton showAllFinesButton = new JButton("Show All Fines");
    private JButton showCategoriesButton = new JButton("Show Categories");
    private JButton showReservationsButton = new JButton("Show Reservations");
    private JButton backButton = new JButton("Return");

    public AdminDashboardFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Admin Dashboard");
        setSize(600, 400); // Increased size for additional buttons

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 20)); // Metallic black background

        // Title label
        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Corbel", Font.BOLD, 30)); // Larger, more elegant font
        titleLabel.setForeground(new Color(255, 216, 1)); // Gold color for text
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 20)); // Metallic black background
        buttonPanel.setLayout(new GridLayout(4, 2, 10, 10)); // Adjust layout for neat arrangement

        buttonPanel.add(styleButton(showAllStudentsButton));
        buttonPanel.add(styleButton(showAllBooksButton));
        buttonPanel.add(styleButton(showAllAuthorsButton));
        buttonPanel.add(styleButton(showAllPublishersButton));
        buttonPanel.add(styleButton(showAllFinesButton));
        buttonPanel.add(styleButton(showCategoriesButton));
        buttonPanel.add(styleButton(showReservationsButton));
        buttonPanel.add(styleButton(backButton));

        showAllStudentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new StudentFrame().setVisible(true);
                dispose();
            }
        });

        showAllBooksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new BookFrame().setVisible(true);
                dispose();
            }
        });

        showAllAuthorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new AuthorFrame().setVisible(true);
                dispose();
            }
        });

        showAllPublishersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new PublisherFrame().setVisible(true);
                dispose();
            }
        });

        showAllFinesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new FinesFrame().setVisible(true);
                dispose();
            }
        });

        showCategoriesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new CategoriesFrame().setVisible(true);
                dispose();
            }
        });

        showReservationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new ReservationsFrame().setVisible(true);
                dispose();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new TitlePageFrame().setVisible(true);
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
