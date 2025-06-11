import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Файл формата .csv: n,a_d,b_d,a_c,b_c,iteration1,iteration2,...\nВведите номер файла> ");
        String file = "./data/inputs/input" + scanner.nextLine() + ".csv";


        int n = 0, a_d = 0, b_d = 0, a_c = 0, b_c = 0;
        int[] iterations = new int[0];
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          String str = br.readLine();
          if (str != null) {
              String[] parts = str.split(",");

              n = Integer.parseInt(parts[0].trim());
              a_d = Integer.parseInt(parts[1].trim());
              b_d = Integer.parseInt(parts[2].trim());
              a_c = Integer.parseInt(parts[3].trim());
              b_c = Integer.parseInt(parts[4].trim());

              iterations = new int[parts.length - 5];
              for (int i = 0; i < iterations.length; i++) {
                  iterations[i] = Integer.parseInt(parts[i + 5].trim());
              }
          }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }

        Task task0 = new Task(n, a_d, b_d, a_c, b_c);

        Map<Integer, Solution> solutions = transitionOfM(task0, iterations);
        int minKey = -1;

        Solution bestSol = Collections.min(solutions.values());
        for (Map.Entry<Integer, Solution> entry : solutions.entrySet()) {
            if (entry.getValue() == bestSol) {
                minKey = entry.getKey();
            }
        }
        System.out.println(":!!!Наилучшее решение: m = " + minKey + "\n" + bestSol);
//        System.out.println(":окончательное решение\n" + solution);
    }

    public static Map<Integer, Solution> transitionOfM(Task task, int[] iterations) throws IOException {
        // удаление файлов
        Path dir = Paths.get("./data/outputs/ofM");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при удалении файлов: " + e.getMessage());
        }

        Map<Integer, Solution> solutions = new HashMap<>(); // соотношение m-лучшее решение
        int maxM = (task.getN() - 1) / 2; // самое большое m для задачи task
        int plus = (maxM - task.getM()) / 4;

        do {
            System.out.println(task);

            CSVWriter.cleanData("./data/outputs/data_final_improvement.csv");
            CSVWriter.cleanData("./data/outputs/data_final_F.csv");

            Solution solution = fullAlgorithm(task, iterations);
            solutions.put(task.getM(), solution);
            System.out.println(":окончательное решение\n" + solution);

            Path source_data_final_improvement = Paths.get("./data/outputs/data_final_improvement.csv");
            Path target_data_final_improvement = Paths.get("./data/outputs/ofM/data_final_improvement" + task.getM() + ".csv");
            Path source_data_final_F = Paths.get("./data/outputs/data_final_F.csv");
            Path target_data_final_F = Paths.get("./data/outputs/ofM/data_final_F" + task.getM() + ".csv");
            try {
                Files.copy(source_data_final_improvement, target_data_final_improvement, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(source_data_final_F, target_data_final_F, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println("Ошибка при переименовании файла: " + e.getMessage());
            }

            task.setM(plus);
        } while (maxM >= task.getM() + plus);

        return solutions;
    }

    /**
     * Полный алгоритм построения финального решения задачи, основанный на нескольких начальных решениях
     * @param task задача
     * @param iterations максимальное кол-во итераций
     * @return лучшее решение
     **/
    public static Solution fullAlgorithm(Task task, int[] iterations) throws IOException {
        List<Solution> solutions = new ArrayList<>();
        for (int i = 0; i < task.getN() - 1; i++) {
//        for (int i : iterations) {
            System.out.println(":итерация " + i);

            Solution solution = new Solution(task);
            if (solution.getM_route_final(i) != null) {
//            System.out.println(solution);
                solutions.add(solution);
            }
        }
        return Collections.min(solutions);
    }

}


