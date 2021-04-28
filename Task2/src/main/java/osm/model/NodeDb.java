package osm.model;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import osm.model.generated.Node;

@Data
public class NodeDb {
    private long id;
    private String user;
    private Double longitude;
    private Double latitude;
    private List<TagDb> tags;

    public NodeDb(long id, String user, Double longitude, Double latitude, List<TagDb> tags) {
        this.id = id;
        this.user = user;
        this.longitude = longitude;
        this.latitude = latitude;
        this.tags = tags;
    }

    public NodeDb(long id, String user, Double longitude, Double latitude) {
        this.id = id;
        this.user = user;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static NodeDb convert(Node node) {
        List<TagDb> tags = node.getTag().stream()
                .map(tag -> TagDb.convert(tag, node.getId()))
                .collect(Collectors.toList());
        return new NodeDb(node.getId(), node.getUser(), node.getLon(), node.getLat(), tags);
    }
}
