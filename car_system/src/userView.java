import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class UserView extends JPanel {
    private JLabel userNameLabel;
    private String userName;
    private JFrame parentFrame;

    public UserView(String userName, JFrame parentFrame) {
        this.userName = userName;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Navigation Bar
        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        add(mainContent, BorderLayout.CENTER);

        // Title
        JLabel titleLabel = new JLabel("Cars for Sale");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainContent.add(titleLabel);

        // Car Listings
        JPanel carListings = new JPanel(new GridLayout(0, 4, 10, 10)); // GridLayout with 4 columns
        JScrollPane scrollPane = new JScrollPane(carListings);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContent.add(scrollPane);

        loadCarData(carListings);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.ORANGE);
        navBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        userNameLabel = new JLabel("Logged in as: " + userName);
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userNameLabel.setForeground(Color.WHITE);
        navBar.add(userNameLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.ORANGE); // Make sure the panel background matches the nav bar

        JButton userDetailsButton = new JButton("User Details");
        userDetailsButton.setFocusPainted(false);
        userDetailsButton.setBackground(Color.WHITE);
        userDetailsButton.setForeground(Color.BLACK);
        Dimension buttonSize = new Dimension(120, 30); // Adjust the size as needed
        userDetailsButton.setPreferredSize(buttonSize);
        userDetailsButton.setMinimumSize(buttonSize);
        userDetailsButton.setMaximumSize(buttonSize);
        userDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserDetails();
            }
        });
        buttonPanel.add(userDetailsButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(80, 30)); // Adjust size as needed
        logoutButton.setMinimumSize(new Dimension(80, 30));
        logoutButton.setMaximumSize(new Dimension(80, 30));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        buttonPanel.add(logoutButton);

        navBar.add(buttonPanel, BorderLayout.EAST);

        return navBar;
    }

    private void loadCarData(JPanel carListings) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT carId, model, price, mileage, color, description, photos, stocks FROM cars WHERE stocks > 0";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    int carId = resultSet.getInt("carId");
                    String model = resultSet.getString("model");
                    double price = resultSet.getDouble("price");
                    int mileage = resultSet.getInt("mileage");
                    String color = resultSet.getString("color");
                    String description = resultSet.getString("description");
                    String photos = resultSet.getString("photos");
                    int stocks = resultSet.getInt("stocks");

                    JPanel carCard = createCarCard(carId, model, price, mileage, color, description, photos, stocks);
                    carListings.add(carCard);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading car data: " + ex.getMessage());
        }
    }

    private JPanel createCarCard(int carId, String model, double price, int mileage, String color, String description, String photos, int stocks) {
        JPanel carCard = new JPanel();
        carCard.setLayout(new BorderLayout());
        carCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        carCard.setPreferredSize(new Dimension(250, 250));
        carCard.setBackground(Color.WHITE);

        // Car image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (photos != null && !photos.isEmpty()) {
            ImageIcon icon = new ImageIcon(photos);
            Image image = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        }
        carCard.add(imageLabel, BorderLayout.NORTH);

        // Car details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailsPanel.setBackground(Color.WHITE);

        detailsPanel.add(new JLabel("<html><b>Model:</b> " + model + "</html>"));
        detailsPanel.add(new JLabel("<html><b>Price:</b> $" + price + "</html>"));
        detailsPanel.add(new JLabel("<html><b>Stocks:</b> " + stocks + "</html>"));

        carCard.add(detailsPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonsPanel.setBackground(Color.WHITE);

        JButton buyButton = new JButton("Buy");
        buyButton.setBackground(Color.GREEN);
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPurchaseForm(carId, model, price, mileage, color, description, photos, stocks);
            }
        });
        buttonsPanel.add(buyButton);

        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBackground(Color.BLUE);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCarDetails(carId);
            }
        });
        buttonsPanel.add(viewDetailsButton);

        carCard.add(buttonsPanel, BorderLayout.SOUTH);

        return carCard;
    }

    private void showCarDetails(int carId) {
        removeAll();
        revalidate();
        repaint();

        final JPanel carDetailsPanel = new JPanel(new BorderLayout());
        carDetailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        final String query = "SELECT * FROM cars WHERE carId = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, carId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String model = resultSet.getString("model");
                    double price = resultSet.getDouble("price");
                    int mileage = resultSet.getInt("mileage");
                    String color = resultSet.getString("color");
                    String description = resultSet.getString("description");
                    String status = resultSet.getString("status");
                    java.sql.Date dateListed = resultSet.getDate("date_listed");
                    String photos = resultSet.getString("photos");
                    int stocks = resultSet.getInt("stocks");

                    JLabel carDetails = new JLabel("<html>" +
                            "<b>Model:</b> " + model + "<br>" +
                            "<b>Price:</b> $" + price + "<br>" +
                            "<b>Mileage:</b> " + mileage + " miles<br>" +
                            "<b>Color:</b> " + color + "<br>" +
                            "<b>Description:</b> " + description + "<br>" +
                            "<b>Status:</b> " + status + "<br>" +
                            "<b>Date Listed:</b> " + dateListed + "<br>" +
                            "</html>");
                    carDetailsPanel.add(carDetails, BorderLayout.CENTER);

                    // Display photo
                    if (photos != null && !photos.isEmpty()) {
                        ImageIcon icon = new ImageIcon(photos);
                        Image image = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
                        JLabel photoLabel = new JLabel(new ImageIcon(image));
                        carDetailsPanel.add(photoLabel, BorderLayout.NORTH);
                    }

                    // Add buy button
                    JButton buyButton = new JButton("Buy");
                    buyButton.setBackground(Color.GREEN);
                    buyButton.setForeground(Color.WHITE);
                    buyButton.setFocusPainted(false);
                    buyButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            showPurchaseForm(carId, model, price, mileage, color, description, photos, stocks);
                        }
                    });
                    carDetailsPanel.add(buyButton, BorderLayout.SOUTH);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading car details: " + ex.getMessage());
        }

        add(carDetailsPanel, BorderLayout.CENTER);

        // Add back button to return to the car listings
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                revalidate();
                repaint();
                setLayout(new BorderLayout());

                // Navigation Bar
                JPanel navBar = createNavBar();
                add(navBar, BorderLayout.NORTH);

                // Main content
                JPanel mainContent = new JPanel();
                mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
                add(mainContent, BorderLayout.CENTER);

                // Title
                JLabel titleLabel = new JLabel("Cars for Sale");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                mainContent.add(titleLabel);

                // Car Listings
                JPanel carListings = new JPanel(new GridLayout(0, 4, 10, 10));
                JScrollPane scrollPane = new JScrollPane(carListings);
                scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
                mainContent.add(scrollPane);

                loadCarData(carListings);
            }
        });
        add(backButton, BorderLayout.NORTH);
    }

    private void showPurchaseForm(int carId, String model, double price, int mileage, String color, String description, String photos, int stocks) {
        removeAll();
        revalidate();
        repaint();

        JPanel purchasePanel = new JPanel(new GridBagLayout());
        purchasePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Car details at the top
        if (photos != null && !photos.isEmpty()) {
            ImageIcon icon = new ImageIcon(photos);
            Image image = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(image));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            purchasePanel.add(photoLabel, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        purchasePanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1;
        purchasePanel.add(new JLabel(model), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        purchasePanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        purchasePanel.add(new JLabel("$" + price), gbc);

        // Customer Full Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        purchasePanel.add(new JLabel("Customer Full Name:"), gbc);

        JTextField customerNameField = new JTextField(20);
        gbc.gridx = 1;
        purchasePanel.add(customerNameField, gbc);

        // Customer Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        purchasePanel.add(new JLabel("Address:"), gbc);

        JTextField addressField = new JTextField(20);
        gbc.gridx = 1;
        purchasePanel.add(addressField, gbc);

        // Customer Contact Number
        gbc.gridx = 0;
        gbc.gridy = 5;
        purchasePanel.add(new JLabel("Contact Number:"), gbc);

        JTextField contactNumberField = new JTextField(20);
        gbc.gridx = 1;
        purchasePanel.add(contactNumberField, gbc);

        // Customer Payment Amount
        gbc.gridx = 0;
        gbc.gridy = 6;
        purchasePanel.add(new JLabel("Amount Paid:"), gbc);

        JTextField amountPaidField = new JTextField(20);
        gbc.gridx = 1;
        purchasePanel.add(amountPaidField, gbc);

        // Buy Button
        final String finalModel = model;
        final double finalPrice = price;
        final int finalMileage = mileage;
        final String finalColor = color;
        final String finalDescription = description;
        final String finalPhotos = photos;
        final int finalStocks = stocks;

        JButton confirmPurchaseButton = new JButton("Confirm Purchase");
        confirmPurchaseButton.setBackground(Color.GREEN);
        confirmPurchaseButton.setForeground(Color.WHITE);
        confirmPurchaseButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        confirmPurchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerName = customerNameField.getText();
                String address = addressField.getText();
                String contactNumber = contactNumberField.getText();
                double amountPaid;

                try {
                    amountPaid = Double.parseDouble(amountPaidField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UserView.this, "Please enter a valid amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (amountPaid < finalPrice) {
                    JOptionPane.showMessageDialog(UserView.this, "Insufficient amount. The car costs $" + finalPrice, "Insufficient Amount", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Insert customer details into the database
                    try (Connection connection = DatabaseUtils.getConnection()) {
                        String insertQuery = "INSERT INTO customer (fullname, address, contactNum, payment) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            insertStatement.setString(1, customerName);
                            insertStatement.setString(2, address);
                            insertStatement.setString(3, contactNumber);
                            insertStatement.setDouble(4, amountPaid);
                            insertStatement.executeUpdate();

                            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                int customerId = generatedKeys.getInt(1);

                                // Update car stocks
                                String updateStocksQuery = "UPDATE cars SET stocks = stocks - 1 WHERE carId = ?";
                                try (PreparedStatement updateStocksStatement = connection.prepareStatement(updateStocksQuery)) {
                                    updateStocksStatement.setInt(1, carId);
                                    updateStocksStatement.executeUpdate();
                                }

                                double change = amountPaid - finalPrice;
                                showReceipt(customerId, customerName, address, contactNumber, carId, finalModel, finalPrice, amountPaid, change);

                                // Insert sale details into the sales table
                                insertSale(customerName, finalModel, finalPrice);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(UserView.this, "Error processing transaction: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        purchasePanel.add(confirmPurchaseButton, gbc);

        // Add back button to return to the car listings
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        purchasePanel.add(backButton, gbc);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                revalidate();
                repaint();
                setLayout(new BorderLayout());

                // Navigation Bar
                JPanel navBar = createNavBar();
                add(navBar, BorderLayout.NORTH);

                // Main content
                JPanel mainContent = new JPanel();
                mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
                add(mainContent, BorderLayout.CENTER);

                // Title
                JLabel titleLabel = new JLabel("Cars for Sale");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                mainContent.add(titleLabel);

                // Car Listings
                JPanel carListings = new JPanel(new GridLayout(0, 4, 10, 10)); // GridLayout with 4 columns
                JScrollPane scrollPane = new JScrollPane(carListings);
                scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
                mainContent.add(scrollPane);

                loadCarData(carListings);
            }
        });

        add(purchasePanel, BorderLayout.CENTER);
    }

    private void insertSale(String customerName, String carModel, double salePrice) {
        String query = "INSERT INTO sales (customerName, carModel, salePrice, saleDate) VALUES (?, ?, ?, NOW())";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, customerName);
            statement.setString(2, carModel);
            statement.setDouble(3, salePrice);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting sale: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceipt(int customerId, String customerName, String address, String contactNumber, int carId, String model, double price, double amountPaid, double change) {
        removeAll();
        revalidate();
        repaint();

        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel receiptLabel = new JLabel("<html>" +
                "<h1>Receipt</h1>" +
                "<b>Customer ID:</b> " + customerId + "<br>" +
                "<b>Customer Name:</b> " + customerName + "<br>" +
                "<b>Address:</b> " + address + "<br>" +
                "<b>Contact Number:</b> " + contactNumber + "<br>" +
                "<b>Car Model:</b> " + model + "<br>" +
                "<b>Price:</b> $" + price + "<br>" +
                "<b>Amount Paid:</b> $" + amountPaid + "<br>" +
                "<b>Change:</b> $" + change + "<br>" +
                "<b>Handled by:</b> " + userName + "<br>" +
                "</html>");
        receiptLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        receiptPanel.add(receiptLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Listings");
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                revalidate();
                repaint();
                setLayout(new BorderLayout());

                // Navigation Bar
                JPanel navBar = createNavBar();
                add(navBar, BorderLayout.NORTH);

                // Main content
                JPanel mainContent = new JPanel();
                mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
                add(mainContent, BorderLayout.CENTER);

                // Title
                JLabel titleLabel = new JLabel("Cars for Sale");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                mainContent.add(titleLabel);

                // Car Listings
                JPanel carListings = new JPanel(new GridLayout(0, 4, 10, 10)); // GridLayout with 4 columns
                JScrollPane scrollPane = new JScrollPane(carListings);
                scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
                mainContent.add(scrollPane);

                loadCarData(carListings);
            }
        });

        receiptPanel.add(backButton, BorderLayout.SOUTH);

        add(receiptPanel, BorderLayout.CENTER);
    }

    private void showUserDetails() {
        // Fetch user details from the database
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT name, address, contactNum, email, dateOfPurchase FROM users WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, userName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String address = resultSet.getString("address");
                        String contactNum = resultSet.getString("contactNum");
                        String email = resultSet.getString("email");
                        java.sql.Date dateOfPurchase = resultSet.getDate("dateOfPurchase");

                        // Display the user details
                        String userDetails = String.format(
                                "<html>" +
                                        "<b>Name:</b> %s<br>" +
                                        "<b>Address:</b> %s<br>" +
                                        "<b>Contact Number:</b> %s<br>" +
                                        "<b>Email:</b> %s<br>" +
                                        "<b>Date of Purchase:</b> %s<br>" +
                                        "</html>",
                                name, address, contactNum, email, dateOfPurchase
                        );

                        JOptionPane.showMessageDialog(this, userDetails, "User Details", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "User details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching user details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        parentFrame.setContentPane(new Login());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
