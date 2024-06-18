import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 class EditCarDialog extends JDialog {
    private JTextField modelField;
    private JTextField priceField;
    private JTextField mileageField;
    private JTextField colorField;
    private JTextArea descriptionArea;
    private JTextField statusField;
    private JTextField photosField;
    private JTextField stocksField; // New field for stocks
    private int carId;
    private carView parentPanel;

    public EditCarDialog(int carId, carView parentPanel) {
        this.carId = carId;
        this.parentPanel = parentPanel;
        setTitle("Edit Car");
        setSize(400, 400);
        setLocationRelativeTo(parentPanel);
        setLayout(new GridLayout(10, 2, 10, 10));

        JLabel modelLabel = new JLabel("Model:");
        modelField = new JTextField();
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();
        JLabel mileageLabel = new JLabel("Mileage:");
        mileageField = new JTextField();
        JLabel colorLabel = new JLabel("Color:");
        colorField = new JTextField();
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionArea = new JTextArea();
        JLabel statusLabel = new JLabel("Status:");
        statusField = new JTextField();
        JLabel photosLabel = new JLabel("Photos:");
        photosField = new JTextField();
        JLabel stocksLabel = new JLabel("Stocks:");
        stocksField = new JTextField();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveCar());

        JButton uploadButton = new JButton("Upload Photo");
        uploadButton.addActionListener(e -> uploadPhoto());

        add(modelLabel);
        add(modelField);
        add(priceLabel);
        add(priceField);
        add(mileageLabel);
        add(mileageField);
        add(colorLabel);
        add(colorField);
        add(descriptionLabel);
        add(new JScrollPane(descriptionArea));
        add(statusLabel);
        add(statusField);
        add(photosLabel);
        add(photosField);
        add(stocksLabel);
        add(stocksField);
        add(uploadButton);
        add(saveButton);

        loadCarData();
    }

    private void loadCarData() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM cars WHERE carId = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, carId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    modelField.setText(resultSet.getString("model"));
                    priceField.setText(String.valueOf(resultSet.getDouble("price")));
                    mileageField.setText(String.valueOf(resultSet.getInt("mileage")));
                    colorField.setText(resultSet.getString("color"));
                    descriptionArea.setText(resultSet.getString("description"));
                    statusField.setText(resultSet.getString("status"));
                    photosField.setText(resultSet.getString("photos"));
                    stocksField.setText(String.valueOf(resultSet.getInt("stocks")));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading car data.");
        }
    }

    private void saveCar() {
        String model = modelField.getText();
        String price = priceField.getText();
        String mileageStr = mileageField.getText();
        String color = colorField.getText();
        String description = descriptionArea.getText();
        String status = statusField.getText();
        String photos = photosField.getText();
        String stocksStr = stocksField.getText();

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
            String query = "UPDATE cars SET model = ?, price = ?, mileage = ?, color = ?, description = ?, status = ?, stocks = ?, photos = ? WHERE carId = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, model);
                statement.setString(2, price);
                statement.setInt(3, mileage);
                statement.setString(4, color);
                statement.setString(5, description);
                statement.setString(6, status);
                statement.setInt(7, stocks);
                statement.setString(8, photos);
                statement.setInt(9, carId);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Car updated successfully!");
                    parentPanel.reloadTableData();
                    dispose();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating car.");
        }
    }

    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            photosField.setText(selectedFile.getAbsolutePath());
        }
    }
}
