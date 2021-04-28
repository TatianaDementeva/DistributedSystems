package osm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DbUtils {
    private static final char[] SQL = new char[1024];
    private static final String INIT_SQL_PATH = "init.sql";
    public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/osm";
    public static final String DATABASE_USERNAME = "postgres";
    public static final String DATABASE_PASSWORD = "postgres";

    private static Connection connection;

    public static void init() throws IOException, SQLException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(INIT_SQL_PATH)).getFile());
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            int count = reader.read(SQL);
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            Statement statement = connection.createStatement();
            String sql = new String(SQL, 0, count);
            statement.execute(sql);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private DbUtils() {
        throw new UnsupportedOperationException();
    }
}
