package osm.dao;

import java.sql.SQLException;
import java.util.List;

import osm.model.TagDb;

public interface TagDao {
    List<TagDb> getTags(long nodeId) throws SQLException;

    void insertTag(TagDb tag) throws SQLException;

    void insertPreparedTag(TagDb tag) throws SQLException;

    void batchInsertTags(List<TagDb> tags) throws SQLException;
}
