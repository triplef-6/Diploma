import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Task task0 = new Task(150, 1, 1500, 5, 1500);
        Solution solution = fullAlgorithm(task0, 1);

        System.out.println(task0);
        System.out.println(solution);
    }

    /*
     * Полный алгоритм построения финального решения задачи, основанный на нескольких начальных решениях
     */
    public static Solution fullAlgorithm(Task task, double epsilon) throws IOException {
        Solution solution = new Solution(task);

        solution.getM_route_basic(1);
        solution.getM_route_final(1, 1);

        return solution;
    }

}


