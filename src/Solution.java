import java.util.*;

public class Solution {
    private final Task task; // задача
    private List<List<Integer>> m_route_basic; // m-маршрут (начальное решение)
    private List<List<Integer>> m_route_advanced; // m-маршрут (улучшенное жадным алгоритмом)
    private List<List<Integer>> m_route_final; // m-маршрут (начальное решение + алгоритм улучшения)   
    private Integer F_basic; // значение целевой функции для начального решения
    private Integer F_advanced; // значение целевой функции для улучшенного начального решения
    private Integer F_final; // значение целевой функции (начальное решение + алгоритм улучшения)   

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
        // если значение уже вычислено
        if (F_basic != null) {
            return F_basic;
        }
        // если начальное решение ещё не построено
        if (m_route_basic == null) {
            getM_route_basic();
        }

        F_basic = F(m_route_basic);
        return F_basic;
    }

    /**
     * @return значения целевой функции для улучшенного начального решения
     */
    public int getF_advanced() {
        // если значение уже вычислено
        if (F_advanced != null) {
            return F_advanced;
        }
        // если начальное решение ещё не построено
        if (m_route_advanced == null) {
            getM_route_advanced();
        }

        F_advanced = F(m_route_advanced);
        return F_advanced;
    }

    /**
     * @param if_we_get_advanced true - если мы будем строить от улучшенного начального решения, false - если от базового 
     * @return финальное решение
     */
    public List<List<Integer>> getM_route_final(boolean if_we_get_advanced) {
        // если финальное решение уже построено
        if (m_route_final != null) {
            return m_route_final;
        }
        // если мы выбрали улучшенное решение
        if (if_we_get_advanced) {
            m_route_final = getM_route_advanced();
        } else { // если мы выбрали базовое решение
            m_route_final = getM_route_basic();
        }

        // здесь будет логика построения
        for (int e = 0; e < 2; e++) { // пока 2 повтора
            // найти 3 самых тяжёлых ребра
            int[] H_i = new int[3];
            int[] u_i = new int[3];
            int[] v_i = new int[3];
            int[] d_u_v_i = new int[3];
            for (int k = 0; k < 3; k++) {
                d_u_v_i[k] = 0;
            }

            for (int i = 0; i < m_route_final.size(); i++) {
                List<Integer> H = m_route_final.get(i);
                for (int j = 1; j < H.size() - 2; j++) {
                    int d_u_v = task.getD()[H.get(j)][H.get(j + 1)];
                    for (int k = 0; k < 3; k++) {
                        if (d_u_v > d_u_v_i[k]) {
                            d_u_v_i[k] = d_u_v;
                            u_i[k] = j;
                            v_i[k] = j + 1;
                            H_i[k] = i;
                            break;
                        }
                    }
                }
            }
            // поиск допустимых рёбер
            int[] sum_Hi = new int[6];
            for (int i = 0; i < 3; i++) {
                sum_Hi[i] = 0;
                int k = 1;
                do {
                    sum_Hi[i] += task.getC()[m_route_final.get(H_i[i]).get(k)];
                    k++;
                } while (m_route_final.get(H_i[i]).get(k) != u_i[i]);

                sum_Hi[i + 3] = 0;
                k = m_route_final.get(H_i[i]).size() - 2;
                do {
                    sum_Hi[i + 3] += task.getC()[m_route_final.get(H_i[i]).get(k)];
                    k--;
                } while (m_route_final.get(H_i[i]).get(k) != v_i[i]);
            }

            boolean[][] admissibility = new boolean[6][6];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                   if (i == j || sum_Hi[i] + sum_Hi[j] <= task.getR()) {
                       admissibility[i][j] = true;
                   } else {
                       admissibility[i][j] = false;
                   }
                }
            }
            // сравнение рёбер

            // замена


        }



        return m_route_final;
    }

    /**
     * @return значение целевой функции для финального решения, null - если оно ещё не построено
     */
    public Integer getF_final() {
        // если значение уже вычислено
        if (F_final != null) {
            return F_final ;
        }
        // если начальное решение ещё не построено
        if (m_route_final == null) {
            return null;
        }

        F_final  = F(m_route_final);
        return F_final;
    }

    /**
     * Вычисление значения целевой функции
     * @param m_route m-маршрут
     * @return значение целевой функции
     */
    private int F(List<List<Integer>> m_route) {
        int F = 0;
        for (List<Integer> H : m_route) { // суммируем все рёбра во всех H
            for (int i = 0; i < H.size() - 1; i++) {
                F += task.getD()[H.get(i)][H.get(i + 1)];
            }
        }
        return F;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(">M route basic:\n");
        int k = 0;
        for (List<Integer> H : getM_route_basic()) {
            str.append(">>").append(k).append("->");
            for (Integer i : H) {
                str.append(i).append("-");
            }
            str.append("\n");
            k++;
        }
        str.append(">>>F(basic) = ").append(getF_basic()).append("\n\n");

        str.append(">M route advanced:\n");
        k = 0;
        for (List<Integer> H : getM_route_advanced()) {
            str.append(">>").append(k).append("->");
            for (Integer i : H) {
                str.append(i).append("-");
            }
            str.append("\n");
            k++;
        }
        str.append(">>>F(advanced) = ").append(getF_advanced()).append("\n\n");

        if (m_route_final != null) {
            str.append(">M route final:\n");
            k = 0;
            for (List<Integer> H : m_route_final) {
                str.append(">>").append(k).append("->");
                for (Integer i : H) {
                    str.append(i).append("-");
                }
                str.append("\n");
                k++;
            }
            str.append(">>>F(final) = ").append(getF_final()).append("\n\n");
        }
        return str.toString();
    }
}

