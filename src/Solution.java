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

        m_route_advanced  = new ArrayList<>();
        for (List<Integer> H : m_route_basic) { // проходимся по каждой петле
            m_route_advanced.add(greedy_Algorithm(H));
        }

        return m_route_advanced;
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
            int[] H_i = new int[3]; // 3 хвоста
            // int[] u_i = new int[3]; // 3 "верхние" вершины
            int[] v_i = new int[6]; // 3 "нижние" вершины

            int[] d_u_v_i = new int[3]; // 3 самых тяжёлых ребра
            for (int k = 0; k < 3; k++) { // изначально они равны 0
                d_u_v_i[k] = 0;
            }
            TreeMap<Integer, Integer> heavy_d = new TreeMap<>(Collections.reverseOrder()); // список самый тяжёлых рёбер в каждой H
            for (int i = 0; i < m_route_final.size(); i++) { // ищем самые тяжёлые рёбра в каждом H в маршруте
                List<Integer> H = m_route_final.get(i);
                int d_u_v = 0;
                for (int j = 1; j < H.size() - 2; j++) { // ищем ребро в H
                    if (d_u_v < task.getD()[H.get(j)][H.get(j + 1)]) {
                        d_u_v = task.getD()[H.get(j)][H.get(j + 1)];
                    }
                }
                heavy_d.put(d_u_v, i);
            }

            int count = 0;
            for (Map.Entry<Integer, Integer> d : heavy_d.entrySet()) {
                if (count == 3) {
                    break;
                }
                d_u_v_i[count] = d.getKey();
                H_i[count] = d.getValue();
                for (int j = 1; j < m_route_final.get(H_i[count]).size() - 2; j++) {
                    if (d_u_v_i[count] == task.getD()[m_route_final.get(H_i[count]).get(j)][m_route_final.get(H_i[count]).get(j + 1)]) {
                        v_i[count] = j;
                        v_i[count + 3] = j + 1;
                        break;
                    }
                }
                count++;
            }


            // поиск допустимых рёбер и построение хвостов
            List<List<Integer>> H_i_new = new ArrayList<>();
            int[] sum_Hi = new int[6];
            for (int i = 0; i < 3; i++) {
                List<Integer> H_u_new = new ArrayList<>();
                sum_Hi[i] = 0;
                int k = 1;
                do {
                    sum_Hi[i] += task.getC()[m_route_final.get(H_i[i]).get(k)];
                    H_u_new.add(m_route_final.get(H_i[i]).get(k));
                    k++;
                } while (m_route_final.get(H_i[i]).get(k) != v_i[i]);

                H_i_new.add(H_u_new);
            }

            for (int i = 0; i < 3; i++) {
                List<Integer> H_v_new = new ArrayList<>();
                sum_Hi[i + 3] = 0;
                int k = m_route_final.get(H_i[i]).size() - 2;
                do {
                    sum_Hi[i + 3] += task.getC()[m_route_final.get(H_i[i]).get(k)];
                    H_v_new.add(m_route_final.get(H_i[i]).get(k));
                    k--;
                } while (m_route_final.get(H_i[i]).get(k) != v_i[i + 3]);

                Collections.reverse(H_v_new);
                H_i_new.add(H_v_new);
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
            // отделение недопустимых рёбер
            // сравнение рёбер
            int[][] triplets = {
                    { 0, 3, 1, 4, 2, 5 },
                    { 0, 3, 1, 5, 2, 4 },
                    { 0, 3, 1, 2, 4, 5 },

                    { 0, 4, 1, 3, 2, 5 },
                    { 0, 5, 1, 3, 2, 4 },
                    { 0, 2, 1, 3, 4, 5 },

                    { 0, 1, 4, 5, 2, 3 },
                    { 0, 5, 1, 4, 2, 3 },
                    { 0, 4, 1, 5, 2, 3 },

                    { 0, 4, 1, 2, 3, 5 },
                    { 0, 1, 2, 4, 3, 5 },
                    { 0, 2, 1, 4, 3, 5 },

                    { 0, 5, 1, 2, 3, 4 },
                    { 0, 1, 2, 5, 3, 4 },
                    { 0, 2, 1, 5, 3, 4 },
            };

            int min_sum_triple = Integer.MAX_VALUE;
            int min_triple = 0;
            for (int i = 0; i < 15; i++) {
                if (admissibility[triplets[i][0]][triplets[i][1]] && 
                        admissibility[triplets[i][2]][triplets[i][3]] && 
                        admissibility[triplets[i][4]][triplets[i][5]]) {
                    int sum_triple = task.getD()[triplets[i][0]][triplets[i][1]] +
                            task.getD()[triplets[i][2]][triplets[i][3]] +
                            task.getD()[triplets[i][4]][triplets[i][5]];
                    if (min_sum_triple > sum_triple) {
                        min_sum_triple = sum_triple;
                        min_triple = i;
                    }
                }
            }

            // замена
            if (min_triple != 0) {
                List<Integer> H1_new = new ArrayList<>(H_i_new.get(triplets[min_triple][0]));
                H1_new.addAll(H_i_new.get(triplets[min_triple][1]));

                List<Integer> H2_new = new ArrayList<>(H_i_new.get(triplets[min_triple][2]));
                H2_new.addAll(H_i_new.get(triplets[min_triple][3]));

                List<Integer> H3_new = new ArrayList<>(H_i_new.get(triplets[min_triple][4]));
                H3_new.addAll(H_i_new.get(triplets[min_triple][5]));
                
                m_route_final.remove(H_i[0]);
                m_route_final.remove(H_i[1]);
                m_route_final.remove(H_i[2]);

                m_route_final.add(H1_new);
                m_route_final.add(H2_new);
                m_route_final.add(H3_new);
            }
        }
        return m_route_final;
    }

    /**
     * Улучшение петли жадным алгоритмом
     * @param H петля
     * @return улучшенная петля
     */
    private List<Integer> greedy_Algorithm(List<Integer> H) {
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

        return H;
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

