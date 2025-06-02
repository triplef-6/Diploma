import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Task task0 = new Task(150, 1, 1500, 5, 1500);
        Solution solution = new Solution(task0);

        System.out.println(task0);
        solution.getF_advanced();
        solution.getM_route_final(100,true);
        System.out.println(solution);
    }
}