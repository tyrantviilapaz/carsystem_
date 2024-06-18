import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class UserManagementView extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementView(boolean isAdmin) {
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        loadUsers(isAdmin);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.ORANGE);
        headerPanel.setPreferredSize(new Dimension(1080, 100));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"User ID", "Name", "Address", "Contact Number", "Email", "Password", "Is Admin", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);
        userTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        userTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Set preferred width for Actions column

        JScrollPane scrollPane = new JScrollPane(userTable);
        userTable.setFillsViewportHeight(true);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private void loadUsers(boolean isAdmin) {
        tableModel.setRowCount(0); // Clear existing data
        String query = "SELECT userId, name, address, contactNum, email, password, isAdmin FROM users";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                if (!isAdmin && resultSet.getBoolean("isAdmin")) {
                    continue; // Skip admin users if not logged in as admin
                }
                Object[] rowData = new Object[8];
                rowData[0] = resultSet.getInt("userId");
                rowData[1] = resultSet.getString("name");
                rowData[2] = resultSet.getString("address");
                rowData[3] = resultSet.getString("contactNum");
                rowData[4] = resultSet.getString("email");
                rowData[5] = resultSet.getString("password");
                rowData[6] = resultSet.getBoolean("isAdmin");
                rowData[7] = "Actions";

                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
        }
    }

    public void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE userId = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }

    public void updateUser(int userId, String name, String address, String contactNum, String email, String password, boolean isAdmin) {
        String query = "UPDATE users SET name = ?, address = ?, contactNum = ?, email = ?, password = ?, isAdmin = ? WHERE userId = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setString(2, address);
            statement.setString(3, contactNum);
            statement.setString(4, email);
            statement.setString(5, password);
            statement.setBoolean(6, isAdmin);
            statement.setInt(7, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUsers(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
        }
    }

    private void showUpdateDialog(int userId, String name, String address, String contactNum, String email, String password, boolean isAdmin) {
        JTextField nameField = new JTextField(name);
        JTextField addressField = new JTextField(address);
        JTextField contactNumField = new JTextField(contactNum);
        JTextField emailField = new JTextField(email);
        JTextField passwordField = new JTextField(password);
        JCheckBox isAdminCheckbox = new JCheckBox("Is Admin", isAdmin);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactNumField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(isAdminCheckbox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            updateUser(userId, nameField.getText(), addressField.getText(), contactNumField.getText(), emailField.getText(), passwordField.getText(), isAdminCheckbox.isSelected());
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton deleteButton;
        private final JButton updateButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);

            deleteButton = new JButton("Delete");
            deleteButton.setBackground(Color.RED);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);

            updateButton = new JButton("Update");
            updateButton.setBackground(Color.BLUE);
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);

            add(deleteButton);
            add(updateButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton deleteButton;
        private final JButton updateButton;
        private UserManagementView parentPanel;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, UserManagementView parentPanel) {
            super(checkBox);
            this.parentPanel = parentPanel;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

            deleteButton = new JButton("Delete");
            deleteButton.setBackground(Color.RED);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentRow >= 0 && currentRow < userTable.getRowCount()) {
                        int userId = (int) userTable.getValueAt(currentRow, 0);
                        parentPanel.deleteUser(userId);
                    }
                    fireEditingStopped();
                }
            });

            updateButton = new JButton("Update");
            updateButton.setBackground(Color.BLUE);
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentRow >= 0 && currentRow < userTable.getRowCount()) {
                        int userId = (int) userTable.getValueAt(currentRow, 0);
                        String name = (String) userTable.getValueAt(currentRow, 1);
                        String address = (String) userTable.getValueAt(currentRow, 2);
                        String contactNum = (String) userTable.getValueAt(currentRow, 3);
                        String email = (String) userTable.getValueAt(currentRow, 4);
                        String password = (String) userTable.getValueAt(currentRow, 5);
                        boolean isAdmin = (boolean) userTable.getValueAt(currentRow, 6);
                        parentPanel.showUpdateDialog(userId, name, address, contactNum, email, password, isAdmin);
                    }
                    fireEditingStopped();
                }
            });

            panel.add(deleteButton);
            panel.add(updateButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            isPushed = true;
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return "Actions";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("User Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 720);
        frame.setLocationRelativeTo(null);

        UserManagementView userManagementView = new UserManagementView(true);
        frame.add(userManagementView);

        frame.setVisible(true);
    }
}
