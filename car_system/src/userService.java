import java.sql.*;
import java.util.ArrayList;
import java.util.List;

 class UserService {

    public User getUserByUsername(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setAddress(resultSet.getString("address"));
                user.setContactNum(resultSet.getString("contactNum"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setAdmin(resultSet.getBoolean("isAdmin"));
                user.setDateOfPurchase(resultSet.getDate("dateOfPurchase"));
                user.setAmountPaid(resultSet.getDouble("amountPayed"));

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] fetchCarsForSale() {
        String query = "SELECT * FROM cars WHERE status = 'For Sale'";
        List<String> carsForSale = new ArrayList<>();

        try (Connection connection = DatabaseUtils.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String carDetails = "Model: " + resultSet.getString("model") +
                        ", Price: " + resultSet.getDouble("price") +
                        ", Mileage: " + resultSet.getInt("mileage") +
                        ", Color: " + resultSet.getString("color");
                carsForSale.add(carDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carsForSale.toArray(new String[0]);
    }
}
