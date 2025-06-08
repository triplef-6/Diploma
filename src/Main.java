import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Task task0 = new Task(100, 1, 1500, 5, 1500);
        System.out.println(task0);

        Solution solution = fullAlgorithm(task0, 1);

        System.out.println(":окончательное решение \n" + solution);
    }

    /*
     * Полный алгоритм построения финального решения задачи, основанный на нескольких начальных решениях
     */
    public static Solution fullAlgorithm(Task task, double epsilon) throws IOException {
        List<Solution> solutions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            System.out.println(":итерация " + i);
            Solution solution = new Solution(task);

            solution.getM_route_basic(i);
            System.out.println(":начальное решение получено!");

            solution.getM_route_final(epsilon, i);
            System.out.println(":финальное решение получено!");

            System.out.println(solution);

            solutions.add(solution);
        }
        return Collections.min(solutions);
    }

}


