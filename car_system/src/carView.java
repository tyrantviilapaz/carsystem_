import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class carView extends JPanel {
    private JTable carsTable;
    private DefaultTableModel tableModel;

    public carView() {
        setLayout(new BorderLayout());

        // Create the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.ORANGE);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Car Inventory");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Create the table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"Car Info", "Actions"};
        Object[][] data = fetchCarData();

        tableModel = new DefaultTableModel(data, columnNames);
        carsTable = new JTable(tableModel);
        carsTable.setRowHeight(150);
        carsTable.getColumn("Car Info").setCellRenderer(new CarInfoRenderer());
        carsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        carsTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(carsTable);
        carsTable.setFillsViewportHeight(true);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add title and table panels to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private Object[][] fetchCarData() {
        try (Connection connection = DatabaseUtils.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery("SELECT * FROM cars")) {

            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            Object[][] data = new Object[rowCount][2];
            int rowIndex = 0;
            while (resultSet.next()) {
                CarInfo carInfo = new CarInfo(
                        resultSet.getInt("carId"),
                        resultSet.getString("model"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("mileage"),
                        resultSet.getString("color"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getDate("date_listed"),
                        resultSet.getString("photos")
                );
                data[rowIndex][0] = carInfo;
                data[rowIndex][1] = "Edit/Delete";
                rowIndex++;
            }
            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching car data.");
            return new Object[0][0];
        }
    }

    public void deleteCar(int carId) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM cars WHERE carId = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, carId);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Car deleted successfully!");
                    reloadTableData();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting car: " + ex.getMessage());
        }
    }

    public void editCar(int carId) {
        // Open a dialog to edit car details
        EditCarDialog editCarDialog = new EditCarDialog(carId, this);
        editCarDialog.setVisible(true);
    }

    public void reloadTableData() {
        tableModel.setDataVector(fetchCarData(), new Object[]{"Car Info", "Actions"});
        carsTable.getColumn("Car Info").setCellRenderer(new CarInfoRenderer());
        carsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        carsTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), this));
    }

    class CarInfoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            CarInfo carInfo = (CarInfo) value;
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            panel.setBackground(Color.WHITE);

            // Car image
            JLabel imageLabel = new JLabel();
            if (carInfo.photos != null && !carInfo.photos.isEmpty()) {
                ImageIcon icon = new ImageIcon(carInfo.photos);
                Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
            }
            panel.add(imageLabel, BorderLayout.WEST);

            // Car details
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            detailsPanel.add(new JLabel("<html><b>Model:</b> " + carInfo.model + "</html>"));
            detailsPanel.add(new JLabel("<html><b>Price:</b> $" + carInfo.price + "</html>"));
            detailsPanel.add(new JLabel("<html><b>Mileage:</b> " + carInfo.mileage + " miles</html>"));
            detailsPanel.add(new JLabel("<html><b>Color:</b> " + carInfo.color + "</html>"));
            detailsPanel.add(new JLabel("<html><b>Description:</b> " + carInfo.description + "</html>"));
            detailsPanel.add(new JLabel("<html><b>Status:</b> " + carInfo.status + "</html>"));
            detailsPanel.add(new JLabel("<html><b>Date Listed:</b> " + carInfo.date_listed + "</html>"));

            panel.add(detailsPanel, BorderLayout.CENTER);

            return panel;
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton;
        private final JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);

            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");

            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        private carView parentPanel;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, carView parentPanel) {
            super(checkBox);
            this.parentPanel = parentPanel;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentRow >= 0 && currentRow < carsTable.getRowCount()) {
                        CarInfo carInfo = (CarInfo) carsTable.getValueAt(currentRow, 0);
                        parentPanel.editCar(carInfo.carId);
                    }
                    fireEditingStopped();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentRow >= 0 && currentRow < carsTable.getRowCount()) {
                        CarInfo carInfo = (CarInfo) carsTable.getValueAt(currentRow, 0);
                        parentPanel.deleteCar(carInfo.carId);
                    }
                    fireEditingStopped();
                }
            });

            panel.add(editButton);
            panel.add(deleteButton);
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
            return "Edit/Delete";
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

    class CarInfo {
        int carId;
        String model;
        double price;
        int mileage;
        String color;
        String description;
        String status;
        java.util.Date date_listed;
        String photos;

        public CarInfo(int carId, String model, double price, int mileage, String color, String description, String status, java.util.Date date_listed, String photos) {
            this.carId = carId;
            this.model = model;
            this.price = price;
            this.mileage = mileage;
            this.color = color;
            this.description = description;
            this.status = status;
            this.date_listed = date_listed;
            this.photos = photos;
        }
    }
}
