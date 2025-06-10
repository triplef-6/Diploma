import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
}
