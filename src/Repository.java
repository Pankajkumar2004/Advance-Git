import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Repository implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String GIT_FOLDER = ".mygit";
    private static final String COMMITS_FOLDER = ".mygit/commits";
    private static final String INDEX_FILE = ".mygit/index";
    private static final String REPO_DATA = ".mygit/repo.ser"; // ⬅ Added

    private HashMap<String, String> index = new HashMap<>();
    private HashMap<String, Commit> branches = new HashMap<>();
    private String currentBranch = "master";
    private Commit head = null;

    public void init() {
        try {
            Files.createDirectories(Paths.get(COMMITS_FOLDER));
            if (!Files.exists(Paths.get(INDEX_FILE))) {
                Files.createFile(Paths.get(INDEX_FILE));
            }
            loadIndex();
            branches.put(currentBranch, null);
            System.out.println("Initialized empty MiniGit repository on branch 'master'!");
            save(); // ⬅ Save after init
        } catch (IOException e) {
            System.out.println("Repository already initialized.");
        }
    }

    public void addFile(String filename) {
        try {
            String content = FileSnapshot.readFileContent(filename);
            index.put(filename, content);
            saveIndex();
            System.out.println("Added " + filename + " to staging area.");
        } catch (IOException e) {
            System.out.println("Error adding file: " + e.getMessage());
        }
    }

    public void commit(String message) {
        if (index.isEmpty()) {
            System.out.println("Nothing to commit.");
            return;
        }
        Commit newCommit = new Commit(message, head, index);
        head = newCommit;
        branches.put(currentBranch, head);
        System.out.println("Committed as " + newCommit.commitId + " on branch '" + currentBranch + "'");
        save(); // ⬅ Save after commit
    }

    public void log() {
        if (head == null) {
            System.out.println("No commits yet.");
            return;
        }
        Commit current = head;
        while (current != null) {
            System.out.println("Commit: " + current.commitId);
            System.out.println("Message: " + current.message);
            System.out.println("Files:");
            for (String file : current.snapshot.keySet()) {
                System.out.println(" - " + file);
            }
            System.out.println("-----------------------------");
            current = current.parent;
        }
    }
    

    public void checkout(String nameOrId) {
        if (branches.containsKey(nameOrId)) {
            currentBranch = nameOrId;
            head = branches.get(nameOrId);
            System.out.println("Switched to branch '" + currentBranch + "'");
        } else {
            Path commitPath = Paths.get(COMMITS_FOLDER, nameOrId);
            if (!Files.exists(commitPath)) {
                System.out.println("Commit ID or branch not found.");
                return;
            }
            try {
                Files.walk(commitPath).forEach(path -> {
                    try {
                        if (!Files.isDirectory(path)) {
                            Path relativePath = commitPath.relativize(path);
                            Files.copy(path, Paths.get(relativePath.toString()), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        System.out.println("Error during checkout: " + e.getMessage());
                    }
                });
                System.out.println("Checked out commit " + nameOrId + " (detached HEAD)");
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        save(); // ⬅ Save after checkout
    }

    public void createBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("Branch already exists.");
            return;
        }
        branches.put(branchName, head);
        System.out.println("Created branch '" + branchName + "' at commit " + (head != null ? head.commitId : "null"));
        save(); // ⬅ Save after branching
    }

    public void revert(String commitId) {
        Path commitPath = Paths.get(COMMITS_FOLDER, commitId);
        if (!Files.exists(commitPath)) {
            System.out.println("Commit ID not found.");
            return;
        }

        try {
            index.clear();
            Files.walk(commitPath).forEach(path -> {
                try {
                    if (!Files.isDirectory(path)) {
                        Path relativePath = commitPath.relativize(path);
                        String content = new String(Files.readAllBytes(path));
                        index.put(relativePath.toString(), content);
                        Files.write(Paths.get(relativePath.toString()), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    }
                } catch (IOException e) {
                    System.out.println("Error reverting: " + e.getMessage());
                }
            });

            saveIndex();
            commit("Revert to " + commitId);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void makeDirectory(String foldername) {
        try {
            Files.createDirectories(Paths.get(foldername));
            System.out.println("Directory created: " + foldername);
        } catch (IOException e) {
            System.out.println("Error creating directory: " + e.getMessage());
        }
    }

    private void saveIndex() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_FILE));
        for (String filename : index.keySet()) {
            writer.write(filename);
            writer.newLine();
        }
        writer.close();
    }

    private void loadIndex() {
        Path indexPath = Paths.get(INDEX_FILE);
        if (!Files.exists(indexPath)) return;

        try {
            List<String> lines = Files.readAllLines(indexPath);
            for (String filename : lines) {
                try {
                    String content = FileSnapshot.readFileContent(filename);
                    index.put(filename, content);
                } catch (IOException e) {
                    System.out.println("Warning: Failed to read file from index: " + filename);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading index: " + e.getMessage());
        }
    }

    // ✅ Save repo to disk
    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(REPO_DATA))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error saving repository: " + e.getMessage());
        }
    }

    // ✅ Load repo from disk
    public void load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(REPO_DATA))) {
            Repository loaded = (Repository) in.readObject();
            this.index = loaded.index;
            this.branches = loaded.branches;
            this.currentBranch = loaded.currentBranch;
            this.head = loaded.head;
        } catch (Exception e) {
            // Ignore if file doesn't exist
        }
    }
}
