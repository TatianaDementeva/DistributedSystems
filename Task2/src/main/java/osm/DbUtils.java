package osm;

import osm.dao.NodeDaoImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.Objects;

public class DbUtils {
    private static final char[] SQL = new char[1024];
    private static final String INIT_SQL_PATH = "init.sql";
    public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/osm";
    public static final String DATABASE_USERNAME = "postgres";
    public static final String DATABASE_PASSWORD = "postgres";

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    public static Connection init() throws IOException, SQLException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(INIT_SQL_PATH)).getFile());
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            int count = reader.read(SQL);
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
//
            statement = connection.createStatement();
            String sql = new String(SQL, 0, count);
            statement.execute(sql);
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SqlConstants.SQL_INSERT);
            return connection;
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
    public static Statement getStatement() {
        return statement;
    }

    public static PreparedStatement getPrepareStatement() {
        return preparedStatement;
    }

    private DbUtils() {
        throw new UnsupportedOperationException();
    }

    public static void commitAndCloseStatement() throws SQLException {
        connection.commit();
        statement.close();
        preparedStatement.close();
    }
    private static class SqlConstants {
        private static final String SQL_GET = "" +
                "select id, username, longitude, latitude " +
                "from nodes " +
                "where id = ?";

        private static final String SQL_INSERT = "" +
                "insert into nodes(id, username, longitude, latitude) " +
                "values (?, ?, ?, ?)";

    }
}
