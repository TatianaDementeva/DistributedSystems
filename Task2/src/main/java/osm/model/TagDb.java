package osm.model;

import lombok.Data;
import osm.model.generated.Node;

@Data
public class TagDb {
    private final long nodeId;
    private final String key;
    private final String value;

    public static TagDb convert(Node.Tag tag, long nodeId) {
        return new TagDb(nodeId, tag.getK(), tag.getV());
    }
}
