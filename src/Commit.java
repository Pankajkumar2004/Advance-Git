import java.io.Serializable;
import java.util.HashMap;

public class Commit implements Serializable {
    private static final long serialVersionUID = 1L;

    public String commitId;
    public String message;
    public Commit parent;
    public HashMap<String, String> snapshot;
    public long timestamp;

    public Commit(String message, Commit parent, HashMap<String, String> snapshot) {
        this.commitId = generateCommitId();
        this.message = message;
        this.parent = parent;
        this.snapshot = new HashMap<>(snapshot);
        this.timestamp = System.currentTimeMillis();
        storeSnapshot();  // optional
    }

    private String generateCommitId() {
        return Integer.toHexString((message + System.nanoTime()).hashCode()).substring(0, 7);
    }

    private void storeSnapshot() {
        // You can add actual file saving logic here if needed
    }
}
