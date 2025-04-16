import java.util.*;

public class Solution {
    private final Task task; // задача
    private List<List<Integer>> m_route_basic; // m-маршрут (начальное решение)
    private List<List<Integer>> m_route_advanced; // m-маршрут (улучшенное жадным алгоритмом)
    private Integer F_basic; // значение целевой функции для начального решения
    private Integer F_advanced; // значение целевой функции для улучшенного начального решения

    public Solution(Task task) {
        this.task = task;
    }

    /**
     * @return начальное решение
     */
    public List<List<Integer>> getM_route_basic() {
        // если начальное решение уже построено
        if (m_route_basic != null) {
            return m_route_basic;
        }

        m_route_basic = new ArrayList<>();

        // создаём соотношение номер вершины->потребность
        Map<Integer, Integer> V = HashMap.newHashMap(task.getN());
        for (int k = 1; k < task.getN(); k++) {
            V.put(k, task.getC()[k - 1]);
        }

        // строим m-маршрут
        for (int i_m = 0; i_m < task.getM(); i_m++) { // необходимо построить m петель H
            List<Integer> H = new ArrayList<>(); // петля H

            int r_i = task.getR(); // грузоподъёмность во время обхода (изначально равно r, но потом уменьшается)

            H.add(0); // добавляем базу в начало петли

            do { // обход будет идти, пока либо все вершины в K не будут посещены, либо грузоподъёмность во время обхода не станет меньше потребность любой из оставшихся вершин
                int i = -1;
                for (Map.Entry<Integer, Integer> entry : V.entrySet()) { // берём пару вершины->потребность
                    if (Objects.equals(Collections.min(V.values()), entry.getValue())) { // если у вершины минимальная потребность среди всех
                        i = entry.getKey(); // берём номер этой вершины
                    }
                }
                H.add(i); // добавляем её в маршрут
                r_i -= task.getC()[i - 1]; // вычитаем из грузоподъёмности при обходе потребность выбранной вершины
                V.remove(i); // удаляем выбранную вершину из соотношения
            } while (!V.isEmpty() && r_i >= Collections.min(V.values()));

            H.add(0); // добавляем базу в конец петли

            m_route_basic.add(H); // добавляем петлю в m-маршрут
        }

        return m_route_basic;
    }

    /**
     * @return улучшенное начальное решение
     */
    public List<List<Integer>> getM_route_advanced() {
        // если улучшенное начальное решение уже построено
        if (m_route_advanced != null) {
            return m_route_advanced;
        }

        // если начальное решение ещё не построено
        // из него мы будем брать вершины в петлях и просто поменяем их порядок
        if (m_route_basic == null) {
            getM_route_basic();
        }
        m_route_advanced = new ArrayList<>();

        for (List<Integer> H : m_route_basic) { // проходимся по каждой петле
            // создаём новую петлю из тех-же вершин (только без базы)
            List<Integer> H_ = new ArrayList<>(H); // множество вершин в петле
            H_.removeIf(i -> i.equals(0));
            H_.removeIf(i -> i.equals(0));
            List<Integer> H_new = new ArrayList<>(); // улучшенная петля

            H_new.add(0); // добавляем базу в начало петли

            Integer j = 0; // начинаем с базы
            do { // работаем пока не обойдём все вершины в петле
                int min_D = Integer.MAX_VALUE;
                for (Integer i : H_) { // ищем минимальное ребро ji в петле
                    if (i != j && task.getD()[i][j] < min_D) {
                        min_D = task.getD()[j][i];
                        j = i;
                    }
                }

                H_new.add(j); // добавляем вершину ребра ji

                Integer finalJ = j;
                H_.removeIf(i -> i.equals(finalJ)); // удаляем найденную вершину
            } while (!H_.isEmpty());

            H_new.add(0); // добавляем базу в конец петли

            m_route_advanced.add(H_new); // добавляем петлю в m-маршрут
        }

        return m_route_advanced;
    }

    /**
     * @return значения целевой функции для начального решения
     */
    public int getF_basic() {
        if (F_basic != null) {
            return F_basic;
        }
        if (m_route_basic == null) {
            getM_route_basic();
        }

        F_basic = 0;
        for (List<Integer> H : m_route_basic) {
            for (int i = 0; i < H.size() - 1; i++) {
                F_basic += task.getD()[H.get(i)][H.get(i + 1)];
            }
        }

        return F_basic;
    }

    /**
     * @return значения целевой функции для улучшенного начального решения
     */
    public int getF_advanced() {
        if (F_advanced != null) {
            return F_advanced;
        }
        if (m_route_advanced == null) {
            getM_route_advanced();
        }

        F_advanced = 0;
        for (List<Integer> H : m_route_advanced) {
            for (int i = 0; i < H.size() - 1; i++) {
                F_advanced += task.getD()[H.get(i)][H.get(i + 1)];
            }
        }

        return F_advanced;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (List<Integer> H : getM_route_basic()) {
            for (Integer i : H) {
                str.append(i).append("-");
            }
            str.append("\n");
        }
        str.append("F_b = ").append(getF_basic()).append("\n");

        for (List<Integer> H : getM_route_advanced()) {
            for (Integer i : H) {
                str.append(i).append("-");
            }
            str.append("\n");
        }
        str.append(getF_advanced());

        return str.toString();
    }
}

