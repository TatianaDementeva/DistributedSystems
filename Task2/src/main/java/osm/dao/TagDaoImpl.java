package osm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import osm.model.TagDb;

import static osm.DbUtils.getConnection;

public class TagDaoImpl implements TagDao {

    private static void prepareStatement(PreparedStatement statement, TagDb tag) throws SQLException {
        statement.setLong(1, tag.getNodeId());
        statement.setString(2, tag.getKey());
        statement.setString(3, tag.getValue());
    }

    private static TagDb mapTag(ResultSet rs) throws SQLException {
        return new TagDb(rs.getLong("node_id"), rs.getString("key"),
                rs.getString("value"));
    }

    @Override
    public List<TagDb> getTags(long nodeId) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SqlConstants.SQL_GET);
        statement.setLong(1, nodeId);
        ResultSet resultSet = statement.executeQuery(SqlConstants.SQL_GET);
        List<TagDb> tags = new ArrayList<>();
        while (resultSet.next()) {
            tags.add(mapTag(resultSet));
        }
        return tags;
    }

    @Override
    public void insertTag(TagDb tag) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        String sql = "insert into tags(node_id, key, value) " +
                "values (" + tag.getNodeId() + ", " + "'" + tag.getKey() + "'" +
                ", " + "'" + tag.getValue() + "'" + ")";
        statement.execute(sql);
    }

    @Override
    public void insertPreparedTag(TagDb tag) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(TagDaoImpl.SqlConstants.SQL_INSERT);
        prepareStatement(statement, tag);
        statement.execute();
    }

    @Override
    public void batchInsertTags(List<TagDb> tags) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(TagDaoImpl.SqlConstants.SQL_INSERT);
        for (TagDb tag : tags) {
            prepareStatement(statement, tag);
            statement.addBatch();
        }
        statement.executeBatch();
    }

    private static class SqlConstants {
        private static final String SQL_GET = "" +
                "select node_id, key, value " +
                "from tags " +
                "where node_id = ?";

        private static final String SQL_INSERT = "" +
                "insert into tags(node_id, key, value) " +
                "values (?, ?, ?)";

    }
}
