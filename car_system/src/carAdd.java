import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class carAdd extends JPanel {
    private JTextField modelField;
    private JTextField priceField;
    private JTextField mileageField;
    private JTextField colorField;
    private JTextArea descriptionArea;
    private JTextField statusField;
    private JTextField photosField;
    private JTextField stocksField; // New field for stocks
    private carView parentView;

    public carAdd(carView parentView) {
        this.parentView = parentView;
        setLayout(new BorderLayout());

        // Create the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.ORANGE);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Add Car");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Model Label and Field
        JLabel modelLabel = new JLabel("Model:");
        modelField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(modelLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(modelField, gbc);

        // Price Label and Field
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(priceLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        // Mileage Label and Field
        JLabel mileageLabel = new JLabel("Mileage:");
        mileageField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(mileageLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(mileageField, gbc);

        // Color Label and Field
        JLabel colorLabel = new JLabel("Color:");
        colorField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(colorLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(colorField, gbc);

        // Description Label and Area
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionArea = new JTextArea(5, 20);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Status Label and Field
        JLabel statusLabel = new JLabel("Status:");
        statusField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusField, gbc);

        // Stocks Label and Field
        JLabel stocksLabel = new JLabel("Stocks:");
        stocksField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(stocksLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(stocksField, gbc);

        // Photos Label and Field with File Chooser
        JLabel photosLabel = new JLabel("Photos:");
        photosField = new JTextField();
        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    photosField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(photosLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(photosField, gbc);
        gbc.gridx = 2;
        formPanel.add(chooseFileButton, gbc);

        // Add Car Button
        JButton addButton = new JButton("Add Car");
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addCar());
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);

        // Add title and form panels to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void addCar() {
        String model = modelField.getText();
        String price = priceField.getText();
        String mileageStr = mileageField.getText();
        String color = colorField.getText();
        String description = descriptionArea.getText();
        String status = statusField.getText();
        String photos = photosField.getText();
        String stocksStr = stocksField.getText();
        LocalDate dateListed = LocalDate.now();

        if (model.isEmpty() || price.isEmpty() || mileageStr.isEmpty() || stocksStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Model, Price, Mileage, and Stocks must be filled out.");
            return;
        }

        int mileage, stocks;
        try {
            mileage = Integer.parseInt(mileageStr);
            stocks = Integer.parseInt(stocksStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mileage and Stocks must be valid integers.");
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO cars (model, price, mileage, color, description, status, stocks, date_listed, photos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, model);
                statement.setString(2, price);
                statement.setInt(3, mileage);
                statement.setString(4, color);
                statement.setString(5, description);
                statement.setString(6, status);
                statement.setInt(7, stocks);
                statement.setDate(8, java.sql.Date.valueOf(dateListed));
                statement.setString(9, photos);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Car added successfully!");
                    parentView.reloadTableData();  // Refresh the car view table
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding car.");
        }
    }
}
