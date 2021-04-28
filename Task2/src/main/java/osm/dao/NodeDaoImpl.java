package osm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import osm.model.NodeDb;

import static osm.DbUtils.*;

public class NodeDaoImpl implements NodeDao {

    private static void prepareStatement(PreparedStatement statement, NodeDb node) throws SQLException {
        statement.setLong(1, node.getId());
        statement.setString(2, node.getUser());
        statement.setDouble(3, node.getLongitude());
        statement.setDouble(4, node.getLatitude());
    }

    private static NodeDb mapNode(ResultSet rs) throws SQLException {
        return new NodeDb(rs.getLong("id"), rs.getString("username"),
                rs.getDouble("longitude"), rs.getDouble("latitude"));
    }

    @Override
    public NodeDb getNode(long nodeId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SqlConstants.SQL_GET);
        statement.setLong(1, nodeId);
        ResultSet resultSet = statement.executeQuery(SqlConstants.SQL_GET);
        return resultSet.next() ? mapNode(resultSet) : null;
    }

    @Override
    public void insertNode(NodeDb node) throws SQLException {
//        Connection connection = getConnection();
//        Statement statement = connection.createStatement();
        Statement statement = getStatement();
        String sql = "insert into nodes(id, username, longitude, latitude) " +
                "values (" + node.getId() + ", " + "'" + node.getUser() + "'" + ", " + node.getLongitude() +
                ", " + node.getLatitude() + ")";
        statement.executeUpdate(sql);
    }

    @Override
    public void insertPreparedNode(NodeDb node) throws SQLException {
//        Connection connection = getConnection();
//        PreparedStatement statement = connection.prepareStatement(SqlConstants.SQL_INSERT);
        PreparedStatement statement = getPrepareStatement();
        prepareStatement(statement, node);
        statement.execute();
    }

    @Override
    public void batchInsertNodes(List<NodeDb> nodes) throws SQLException {
//        Connection connection = getConnection();
//        PreparedStatement statement = connection.prepareStatement(SqlConstants.SQL_INSERT);
        PreparedStatement statement = getPrepareStatement();
        for (NodeDb node : nodes) {
            prepareStatement(statement, node);
            statement.addBatch();
        }
        statement.executeBatch();
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
