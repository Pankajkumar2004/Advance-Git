import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSnapshot {
    public static String readFileContent(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
