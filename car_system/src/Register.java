import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Register extends JPanel {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField contactNumField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public Register() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createRegisterPanel(), BorderLayout.WEST);
        mainPanel.add(createImagePanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setPreferredSize(new Dimension(540, 720));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Dimension textFieldSize = new Dimension(250, 30);

        // Title
        JLabel titleLabel = new JLabel("REGISTER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Create a new account.");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        registerPanel.add(subtitleLabel, gbc);

        // Name Label
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        registerPanel.add(nameLabel, gbc);

        // Name Field
        nameField = new JTextField();
        nameField.setPreferredSize(textFieldSize);
        gbc.gridx = 1;
        registerPanel.add(nameField, gbc);

        // Address Label
        JLabel addressLabel = new JLabel("Address:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(addressLabel, gbc);

        // Address Field
        addressField = new JTextField();
        addressField.setPreferredSize(textFieldSize);
        gbc.gridx = 1;
        registerPanel.add(addressField, gbc);

        // Contact Number Label
        JLabel contactNumLabel = new JLabel("Contact Number:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        registerPanel.add(contactNumLabel, gbc);

        // Contact Number Field
        contactNumField = new JTextField();
        contactNumField.setPreferredSize(textFieldSize);
        gbc.gridx = 1;
        registerPanel.add(contactNumField, gbc);

        // Email Label
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        registerPanel.add(emailLabel, gbc);

        // Email Field
        emailField = new JTextField();
        emailField.setPreferredSize(textFieldSize);
        gbc.gridx = 1;
        registerPanel.add(emailField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        registerPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(textFieldSize);
        gbc.gridx = 1;
        registerPanel.add(passwordField, gbc);

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        registerPanel.add(confirmPasswordLabel, gbc);

        // Confirm Password Field
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(textFieldSize);
        gbc.gridx = 1;
        registerPanel.add(confirmPasswordField, gbc);

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        registerPanel.add(registerButton, gbc);

        // Back to Login Button
        JPanel backToLoginPanel = new JPanel(new FlowLayout());
        JLabel backToLoginLabel = new JLabel("Already have an account?");
        JButton backToLoginButton = new JButton("Login");
        backToLoginButton.setForeground(Color.BLUE);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(Register.this);
                parentFrame.setContentPane(new Login());
                parentFrame.revalidate();
            }
        });
        backToLoginPanel.add(backToLoginLabel);
        backToLoginPanel.add(backToLoginButton);
        gbc.gridy = 9;
        registerPanel.add(backToLoginPanel, gbc);

        return registerPanel;
    }

    private JPanel createImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.ORANGE); // Set the background color

        // Add the "Car Selling" label with top margin
        JLabel carNameLabel = new JLabel("Car Selling");
        carNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        carNameLabel.setFont(new Font("Arial", Font.BOLD, 50));
        carNameLabel.setForeground(Color.WHITE);
        carNameLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        imagePanel.add(carNameLabel, BorderLayout.NORTH);

        // Add the image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon carImage = new ImageIcon("assets/registerBanner.png");
        imageLabel.setIcon(carImage);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        return imagePanel;
    }

    private void registerUser() {
        String name = nameField.getText();
        String address = addressField.getText();
        String contactNum = contactNumField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || address.isEmpty() || contactNum.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        // Database credentials
        String url = "jdbc:mysql://localhost:3306/cardb";
        String user = "root"; // Change this if you have a different username
        String pass = ""; // Change this if you have a password for the MySQL user

        String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        String insertQuery = "INSERT INTO users (name, address, contactNum, email, password) VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement checkStatement = null;
        PreparedStatement insertStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(url, user, pass);
            checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, email);
            resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Email is already in use.");
                return;
            }

            insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, name);
            insertStatement.setString(2, address);
            insertStatement.setString(3, contactNum);
            insertStatement.setString(4, email);
            insertStatement.setString(5, password);

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "User registered successfully!");
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(Register.this);
                        parentFrame.setContentPane(new Login());
                        parentFrame.revalidate();
                    }
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering user: " + ex.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (checkStatement != null) checkStatement.close();
                if (insertStatement != null) insertStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
