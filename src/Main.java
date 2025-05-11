public class Main {
    public static void main(String[] args) {
        Task task0 = new Task(200, 1, 1500, 5, 1500);
        Solution solution = new Solution(task0);

        System.out.println(task0);
        solution.getF_advanced();
        solution.getM_route_final(true);
        System.out.println(solution);
    }
}