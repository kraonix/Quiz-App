import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class AttemptQuizFrame extends JFrame {
    int userId, quizId;
    List<Question> questions = new ArrayList<>();
    ButtonGroup[] optionGroups = new ButtonGroup[3];

    public AttemptQuizFrame(int userId, int quizId) {
        this.userId = userId;
        this.quizId = quizId;

        setTitle("Attempt Quiz");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM Questions WHERE quiz_id=?");
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            int i = 0;
            while (rs.next()) {
                Question q = new Question(
                        rs.getString("question_text"),
                        new String[]{
                            rs.getString("option1"),
                            rs.getString("option2"),
                            rs.getString("option3"),
                            rs.getString("option4")
                        },
                        rs.getInt("correct_option")
                );
                questions.add(q);

                // Question panel
                JPanel questionPanel = new JPanel();
                questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
                questionPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                questionPanel.setBackground(Color.WHITE);

                JLabel qLabel = new JLabel("Q" + (i + 1) + ": " + q.text);
                qLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                questionPanel.add(qLabel);
                questionPanel.add(Box.createVerticalStrut(5));

                optionGroups[i] = new ButtonGroup();
                for (int j = 0; j < 4; j++) {
                    JRadioButton btn = new JRadioButton(q.options[j]);
                    btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    btn.setActionCommand(String.valueOf(j + 1));
                    optionGroups[i].add(btn);
                    questionPanel.add(btn);
                }

                contentPanel.add(questionPanel);
                contentPanel.add(Box.createVerticalStrut(15));
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions.");
        }

        JButton submitBtn = new JButton("Submit Quiz");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitBtn.setBackground(new Color(33, 150, 243));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(200, 40));
        submitBtn.addActionListener(e -> submit());

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(submitBtn);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
        setVisible(true);
    }

    private void submit() {
        int score = 0;

        for (int i = 0; i < questions.size(); i++) {
            ButtonModel selected = optionGroups[i].getSelection();
            if (selected != null) {
                int selectedOption = Integer.parseInt(selected.getActionCommand());
                if (selectedOption == questions.get(i).correctOption) {
                    score++;
                }
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Attempts (user_id, quiz_id, score) VALUES (?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quiz submitted!\nScore: " + score + "/3");
            new UserDashboard(usernameFromId(userId));
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting quiz.");
        }
    }

    private String usernameFromId(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT username FROM Users WHERE id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    static class Question {
        String text;
        String[] options;
        int correctOption;

        public Question(String text, String[] options, int correctOption) {
            this.text = text;
            this.options = options;
            this.correctOption = correctOption;
        }
    }
}
