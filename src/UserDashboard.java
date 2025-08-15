import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UserDashboard extends JFrame {
    String username;
    int userId;

    public UserDashboard(String username) {
        this.username = username;
        setTitle("User Dashboard - " + username);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel heading = new JLabel("Welcome, " + username);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(heading);
        mainPanel.add(Box.createVerticalStrut(20));

        try (Connection conn = DBConnection.getConnection()) {
            // Get user ID
            PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM Users WHERE username=?");
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) userId = userRs.getInt("id");

            // Get quizzes
            Statement quizStmt = conn.createStatement();
            ResultSet quizRs = quizStmt.executeQuery("SELECT * FROM Quizzes");

            while (quizRs.next()) {
                int quizId = quizRs.getInt("id");
                String quizName = quizRs.getString("name");

                JPanel quizPanel = new JPanel(new BorderLayout());
                quizPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                quizPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                quizPanel.setBackground(Color.WHITE);

                JLabel nameLabel = new JLabel(quizName);
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                quizPanel.add(nameLabel, BorderLayout.WEST);

                JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                rightPanel.setOpaque(false);

                // Check if user attempted this quiz
                PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT score FROM Attempts WHERE user_id=? AND quiz_id=?");
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, quizId);
                ResultSet attemptRs = checkStmt.executeQuery();

                if (attemptRs.next()) {
                    int score = attemptRs.getInt("score");
                    JLabel statusLabel = new JLabel("Attempted (Score: " + score + "/3)");
                    statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    statusLabel.setForeground(new Color(0, 128, 0));
                    rightPanel.add(statusLabel);
                } else {
                    JButton attemptBtn = new JButton("Attempt");
                    attemptBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    int finalQuizId = quizId;
                    attemptBtn.addActionListener(e -> {
                        new AttemptQuizFrame(userId, finalQuizId);
                        this.dispose();
                    });
                    rightPanel.add(attemptBtn);
                }

                quizPanel.add(rightPanel, BorderLayout.EAST);
                mainPanel.add(quizPanel);
                mainPanel.add(Box.createVerticalStrut(10));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading quizzes.");
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
        setVisible(true);
    }
}
