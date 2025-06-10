import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Файл формата .csv: n,a_d,b_d,a_c,b_c,iteration1,iteration2,...\nВведите номер файла> ");
        String file = "./data/input" + scanner.nextLine() + ".csv";


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
        System.out.println(task0);

        Solution solution = fullAlgorithm(task0, iterations);

        System.out.println(":окончательное решение\n" + solution);
    }

    /**
     * Полный алгоритм построения финального решения задачи, основанный на нескольких начальных решениях
     * @param task задача
     * @param iterations максимальное кол-во итераций
     * @return лучшее решение
     **/
    public static Solution fullAlgorithm(Task task, int[] iterations) throws IOException {
        List<Solution> solutions = new ArrayList<>();
        for (int i : iterations) {
            System.out.println(":итерация " + i);

            Solution solution = new Solution(task);
//            solution.getM_route_basic(i);
            solution.getM_route_final(i);

            System.out.println(solution);
            solutions.add(solution);
        }
        return Collections.min(solutions);
    }

}


