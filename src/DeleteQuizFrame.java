import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class DeleteQuizFrame extends JFrame {
    private JComboBox<String> quizComboBox;
    private Map<String, Integer> quizMap = new HashMap<>();

    public DeleteQuizFrame() {
        setTitle("Delete Quiz");
        setSize(400, 150);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Delete Quiz", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        quizComboBox = new JComboBox<>();
        panel.add(new JLabel("Select Quiz:"));
        panel.add(quizComboBox);

        add(panel, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteBtn.addActionListener(e -> deleteQuiz());

        add(deleteBtn, BorderLayout.SOUTH);

        loadQuizzes();
        setVisible(true);
    }

    private void loadQuizzes() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, name FROM Quizzes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            quizComboBox.removeAllItems();
            quizMap.clear();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                quizComboBox.addItem(name);
                quizMap.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteQuiz() {
        String selected = (String) quizComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a quiz.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this quiz?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = quizMap.get(selected);

        try (Connection conn = DBConnection.getConnection()) {
            String deleteQuestions = "DELETE FROM Questions WHERE quiz_id = ?";
            PreparedStatement qStmt = conn.prepareStatement(deleteQuestions);
            qStmt.setInt(1, id);
            qStmt.executeUpdate();

            String deleteQuiz = "DELETE FROM Quizzes WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteQuiz);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quiz deleted.");
            loadQuizzes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
