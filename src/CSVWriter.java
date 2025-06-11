import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CSVWriter {
    public static void writeData(String filename, List<Integer> values, int iteration) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
//            writer.write("Итерация,Значение,Исход\n");
            for (int i = 0; i < values.size(); i++) {
                writer.write((i + 1) + "," + values.get(i) + "," + iteration + "\n");
            }
        }
    }

    public static void cleanData(String filename) throws IOException {
        Path path = Paths.get(filename);
        List<String> lines = Files.readAllLines(path);
        if (!lines.isEmpty()) {
            Files.write(path, List.of(lines.get(0)), StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
