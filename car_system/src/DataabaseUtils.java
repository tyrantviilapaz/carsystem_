import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 class DatabaseUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/cardb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
