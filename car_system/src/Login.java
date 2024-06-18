import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JPanel {
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private UserService userService;

    public Login() {
        setLayout(new BorderLayout());

        userService = new UserService();

        // Split the panel into two parts
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createLoginPanel(), BorderLayout.WEST);
        mainPanel.add(createImagePanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(540, 720));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Enter your credentials to access your account.");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        loginPanel.add(subtitleLabel, gbc);

        // Email Label
        JLabel usernameLabel = new JLabel("Email:");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);

        // Email Field
        loginUsernameField = new JTextField();
        gbc.gridx = 1;
        loginPanel.add(loginUsernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(passwordLabel, gbc);

        // Password Field
        loginPasswordField = new JPasswordField();
        gbc.gridx = 1;
        loginPanel.add(loginPasswordField, gbc);

        // Remember Me Checkbox
        JCheckBox rememberMeCheckBox = new JCheckBox("Remember me");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginPanel.add(rememberMeCheckBox, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        // Social Login
        JPanel socialLoginPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton googleButton = new JButton("Google");
        JButton facebookButton = new JButton("Facebook");
        socialLoginPanel.add(googleButton);
        socialLoginPanel.add(facebookButton);
        gbc.gridy = 6;
        loginPanel.add(socialLoginPanel, gbc);

        // Register Button
        JPanel registerPanel = new JPanel(new FlowLayout());
        JLabel registerLabel = new JLabel("Don't have an account?");
        JButton registerButton = new JButton("Sign Up");
        registerButton.setForeground(Color.BLUE);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(Login.this);
                parentFrame.setContentPane(new Register());
                parentFrame.revalidate();
            }
        });
        registerPanel.add(registerLabel);
        registerPanel.add(registerButton);
        gbc.gridy = 7;
        loginPanel.add(registerPanel, gbc);

        return loginPanel;
    }

    private JPanel createImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.ORANGE);

        // Add the car name
        JLabel carNameLabel = new JLabel("Car Selling");
        carNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        carNameLabel.setFont(new Font("Arial", Font.BOLD, 50));
        carNameLabel.setForeground(Color.WHITE);
        carNameLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        imagePanel.add(carNameLabel, BorderLayout.NORTH);

        // Add the image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon carImage = new ImageIcon("assets/carbanner.png");
        imageLabel.setIcon(carImage);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        return imagePanel;
    }

    private void loginUser() {
        String email = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }

        User user = userService.getUserByUsername(email);

        if (user != null && user.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "Logged in successfully!");

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (user.isAdmin()) {
                parentFrame.setContentPane(new Admin(parentFrame));
            } else {
                parentFrame.setContentPane(new UserView(user.getName(), parentFrame));
            }
            parentFrame.revalidate();
            parentFrame.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    }
}
