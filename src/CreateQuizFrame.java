import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class CreateQuizFrame extends JFrame {
    JTextField quizNameField;
    JTextField[] questions = new JTextField[3];
    JTextField[][] options = new JTextField[3][4];
    JComboBox<String>[] correctOptions = new JComboBox[3];

    public CreateQuizFrame() {
        setTitle("Create New Quiz");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel heading = new JLabel("Create New Quiz");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(heading);
        mainPanel.add(Box.createVerticalStrut(20));

        // Quiz name
        JPanel namePanel = new JPanel(new BorderLayout(5, 5));
        namePanel.add(new JLabel("Quiz Name:"), BorderLayout.WEST);
        quizNameField = new JTextField();
        namePanel.add(quizNameField, BorderLayout.CENTER);
        mainPanel.add(namePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < 3; i++) {
            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBorder(BorderFactory.createTitledBorder("Question " + (i + 1)));

            questions[i] = new JTextField();
            questions[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            questionPanel.add(new JLabel("Question:"));
            questionPanel.add(questions[i]);

            for (int j = 0; j < 4; j++) {
                options[i][j] = new JTextField();
                questionPanel.add(new JLabel("Option " + (j + 1) + ":"));
                questionPanel.add(options[i][j]);
            }

            correctOptions[i] = new JComboBox<>(new String[]{"1", "2", "3", "4"});
            JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            comboPanel.add(new JLabel("Correct Option:"));
            comboPanel.add(correctOptions[i]);
            questionPanel.add(comboPanel);

            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(questionPanel);
        }

        JButton submitBtn = new JButton("Create Quiz");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitBtn.addActionListener(e -> saveQuiz());

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(submitBtn);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
        setVisible(true);
    }

    private void saveQuiz() {
        String quizName = quizNameField.getText().trim();

        if (quizName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quiz name is required.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Insert quiz
            String quizSQL = "INSERT INTO Quizzes (name) OUTPUT INSERTED.id VALUES (?)";
            PreparedStatement quizStmt = conn.prepareStatement(quizSQL);
            quizStmt.setString(1, quizName);
            ResultSet rs = quizStmt.executeQuery();
            rs.next();
            int quizId = rs.getInt(1);

            // Insert questions
            String questionSQL = "INSERT INTO Questions (quiz_id, question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement questionStmt = conn.prepareStatement(questionSQL);

            for (int i = 0; i < 3; i++) {
                String qText = questions[i].getText().trim();
                if (qText.isEmpty()) throw new Exception("Question " + (i + 1) + " is empty.");

                questionStmt.setInt(1, quizId);
                questionStmt.setString(2, qText);

                for (int j = 0; j < 4; j++) {
                    String opt = options[i][j].getText().trim();
                    if (opt.isEmpty()) throw new Exception("Option " + (j + 1) + " for question " + (i + 1) + " is empty.");
                    questionStmt.setString(j + 3, opt);
                }

                int correct = Integer.parseInt((String) correctOptions[i].getSelectedItem());
                questionStmt.setInt(7, correct);

                questionStmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Quiz created successfully!");
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
