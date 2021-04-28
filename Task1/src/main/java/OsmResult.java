import java.util.HashMap;
import java.util.Map;

public class OsmResult {
    private final Map<String, Integer> users = new HashMap<>();
    private final Map<String, Integer> tags = new HashMap<>();

    public void userEditsCount(String user) {
        users.compute(user, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public void tagCount(String tag) {
        tags.compute(tag, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public Map<String, Integer> getUsers() {
        return users;
    }

    public Map<String, Integer> getTags() {
        return tags;
    }
}
