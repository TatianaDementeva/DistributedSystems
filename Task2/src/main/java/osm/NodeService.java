package osm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import osm.dao.NodeDao;
import osm.dao.TagDao;
import osm.model.NodeDb;
import osm.model.TagDb;

@AllArgsConstructor
public class NodeService {
    private static final Logger LOG = LogManager.getLogger(NodeService.class);
    private static final int FLUSH_THRESHOLD = 10000;

    private final NodeDao nodeDao;
    private final TagDao tagDao;
    private final List<NodeDb> nodeBuffer = new ArrayList<>();

    public void putNodeWithPreparedStatement(NodeDb node) {
        try {
            nodeDao.insertPreparedNode(node);
            for (TagDb tag : node.getTags()) {
                tagDao.insertPreparedTag(tag);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void putNode(NodeDb node) {
        try {
            nodeDao.insertNode(node);
            for (TagDb tag : node.getTags()) {
                tagDao.insertTag(tag);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void putNodeBuffered(NodeDb node) {
        nodeBuffer.add(node);
        if (nodeBuffer.size() == FLUSH_THRESHOLD) {
            flush();
        }
    }

    public void flush() {
        try {
            nodeDao.batchInsertNodes(nodeBuffer);
            List<TagDb> tags = nodeBuffer.stream()
                    .flatMap(node -> node.getTags().stream())
                    .collect(Collectors.toList());
            tagDao.batchInsertTags(tags);
            nodeBuffer.clear();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public NodeDb getNode(long id) {
        try {
            return nodeDao.getNode(id);
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
