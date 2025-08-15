import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    private final String adminUsername;

    public AdminDashboard(String username) {
        this.adminUsername = username;
        setTitle("Admin Dashboard - " + adminUsername);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton createQuizBtn = createStyledButton("Create New Quiz");
        JButton updateQuizBtn = createStyledButton("Update Existing Quiz");
        JButton deleteQuizBtn = createStyledButton("Delete Quiz");

        createQuizBtn.addActionListener(e -> new CreateQuizFrame());
        updateQuizBtn.addActionListener(e -> new UpdateQuizFrame());
        deleteQuizBtn.addActionListener(e -> new DeleteQuizFrame());

        buttonPanel.add(createQuizBtn);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(updateQuizBtn);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(deleteQuizBtn);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setMaximumSize(new Dimension(250, 40));
        return btn;
    }
}
