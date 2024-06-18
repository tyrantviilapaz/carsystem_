import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class SalesView extends JPanel {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel totalSalesLabel;

    public SalesView() {
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        loadSalesData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.ORANGE);
        headerPanel.setPreferredSize(new Dimension(1080, 100));

        JLabel titleLabel = new JLabel("Sales");
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

        String[] columnNames = {"Sale ID", "Customer Name", "Car Model", "Sale Price", "Sale Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        salesTable.setFillsViewportHeight(true);

        totalSalesLabel = new JLabel("Total Sales: $0.00");
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalSalesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalSalesLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(totalSalesLabel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private void loadSalesData() {
        tableModel.setRowCount(0); // Clear existing data
        double totalSales = 0.0;
        String query = "SELECT saleId, customerName, carModel, salePrice, saleDate FROM sales";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Object[] rowData = new Object[5];
                rowData[0] = resultSet.getInt("saleId");
                rowData[1] = resultSet.getString("customerName");
                rowData[2] = resultSet.getString("carModel");
                rowData[3] = resultSet.getDouble("salePrice");
                rowData[4] = resultSet.getDate("saleDate");

                totalSales += resultSet.getDouble("salePrice");

                tableModel.addRow(rowData);
            }

            totalSalesLabel.setText("Total Sales: $" + totalSales);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sales data: " + e.getMessage());
        }
    }
}
