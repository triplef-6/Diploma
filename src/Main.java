public class Main {
    public static void main(String[] args) {
        Task task0 = new Task(200, 1, 500, 5, 500);
        Solution solution = new Solution(task0);

        System.out.println(task0);
        System.out.println(solution);
        solution.getM_route_final(false);
        System.out.println(solution);
    }
}