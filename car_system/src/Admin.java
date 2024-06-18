import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Admin extends JPanel {
    private JFrame parentFrame;

    public Admin(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Create a panel for the title and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.ORANGE);

        JLabel titleLabel = new JLabel("Car Selling System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        carView carViewPanel = new carView();
        tabbedPane.addTab("Add Car", new carAdd(carViewPanel));
        tabbedPane.addTab("View Cars", carViewPanel);
        tabbedPane.addTab("Manage Users", new UserManagementView(true)); // Pass 'true' for isAdmin
        tabbedPane.addTab("Sales", new SalesView()); // Add the Sales tab

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void logout() {
        parentFrame.setContentPane(new Login());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
