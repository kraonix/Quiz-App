import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class UpdateQuizFrame extends JFrame {
    private JComboBox<String> quizComboBox;
    private JTextField quizNameField;
    private Map<String, Integer> quizMap = new HashMap<>();

    public UpdateQuizFrame() {
        setTitle("Update Quiz");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Update Quiz Name", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Select Quiz:"));
        quizComboBox = new JComboBox<>();
        formPanel.add(quizComboBox);

        formPanel.add(new JLabel("New Name:"));
        quizNameField = new JTextField();
        formPanel.add(quizNameField);

        add(formPanel, BorderLayout.CENTER);

        JButton updateBtn = new JButton("Update");
        updateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateBtn.addActionListener(e -> updateQuizName());

        add(updateBtn, BorderLayout.SOUTH);

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

    private void updateQuizName() {
        String selectedName = (String) quizComboBox.getSelectedItem();
        String newName = quizNameField.getText().trim();

        if (selectedName == null || newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a quiz and enter a new name.");
            return;
        }

        int id = quizMap.get(selectedName);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Quizzes SET name = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Quiz name updated.");
                loadQuizzes();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
