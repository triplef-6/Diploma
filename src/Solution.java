import java.io.IOException;
import java.util.*;

public class Solution implements Comparable<Solution> {
    private final Task task; // задача
    private M_route m_route_basic; // m-маршрут (начальное решение)
    private M_route m_route_final; // m-маршрут (начальное решение + алгоритм улучшения)

    public Solution(Task task) {
        this.task = task;
    }

    /**
     * @param iteration номер итерации начального решения (если 0 => построение с ориентацией на минимальные c)
     * @return начальное решение
     */
    public M_route getM_route_basic(int iteration) {
        if (iteration < 0 || iteration >= task.getN()) {
            throw new IllegalArgumentException("Недопустимое значение iteration!");
        }
        // если начальное решение уже построено
        if (m_route_basic != null) {
            return m_route_basic;
        }

        m_route_basic = new M_route();

        // создаём соотношение номер вершины->потребность
        Map<Integer, Integer> V = HashMap.newHashMap(task.getN());
        for (int k = 1; k < task.getN(); k++) {
            V.put(k, task.getC(k));
        }

        // строим m-маршрут
        for (int i_m = 0; i_m < task.getM(); i_m++) { // необходимо построить m петель H
            List<Integer> H = new ArrayList<>(); // петля H

            int r_i = task.getR(); // грузоподъёмность во время обхода (изначально равно r, но потом уменьшается)

            H.add(0); // добавляем базу в начало петли

            int i = iteration;
            do { // обход будет идти, пока либо все вершины в K не будут посещены, либо грузоподъёмность во время обхода не станет меньше потребность любой из оставшихся вершин
                if (iteration == 0) { // если мы выбрали изначальный метод построения
                    for (Map.Entry<Integer, Integer> entry : V.entrySet()) { // берём пару вершины->потребность
                        if (Objects.equals(Collections.min(V.values()), entry.getValue())) { // если у вершины минимальная потребность среди всех
                            i = entry.getKey(); // берём номер этой вершины
                        }
                    }
                } else {
                    int k = i + 1;
                    boolean flag_find = true;
                    do {
                        if (V.get(k) != null && V.get(k) <= r_i) {
                            i = k;
                            flag_find = false;
                        } else if (k == task.getN() - 1) {
                            k = 1;
                        } else {
                            k++;
                        }
                    } while (flag_find && k != i);
                }

                H.add(i); // добавляем её в маршрут
                r_i -= task.getC(i); // вычитаем из грузоподъёмности при обходе потребность выбранной вершины
                V.remove(i); // удаляем выбранную вершину из соотношения

                // if (i == task.getN() - 1) {
                //     i = 1;
                // } else {
                //     i++;
                // }

            } while (!V.isEmpty() && r_i >= Collections.min(V.values()));

            H.add(0); // добавляем базу в конец петли

            m_route_basic.addH(H); // добавляем петлю в m-маршрут
        }

        // возможное улучшение жадным алгоритмом
        M_route m_route_advanced = new M_route();
        for (List<Integer> H : m_route_basic.getM_route()) { // проходимся по каждой петле
            m_route_advanced.addH(greedy_Algorithm(H)); // используем для этого отдельную функцию
        }
        if (m_route_advanced.getF(task) < m_route_basic.getF(task)) {
            m_route_basic = m_route_advanced;
        }

        return m_route_basic;
    }

    /**
     * @param epsilon до какого придела улучшаем
     * @return финальное решение
     */
    public M_route getM_route_final(double epsilon, int iteration) throws IOException {
        // если финальное решение уже построено
        if (m_route_final != null) {
            return m_route_final;
        }
        if (m_route_basic == null) {
            return null;
        }
        this.m_route_final = new M_route(m_route_basic.getM_route());

        // здесь будет логика построения
        int improvement; // на сколько мы улучшаем финальный результат
        Record record = new Record(); // наилучшая тройка для улучшения

        List<Integer> history_record_improvements = new ArrayList<>(); // список улучшений (записывается в файл для визуализации)
        List<Integer> history_F = new ArrayList<>();
        do {
            record.reset();
            // поиск рекорда
            for (int H_1 = 0; H_1 < m_route_final.size() - 2; H_1++) {
                for (int H_2 = H_1 + 1; H_2 < m_route_final.size() - 1; H_2++) {
                    for (int H_3 = H_2 + 1; H_3 < m_route_final.size(); H_3++) { // проходимся по всем петлям

                        for (int d_1 = 1; d_1 < m_route_final.getH(H_1).size() - 2; d_1++) {
                            for (int d_2 = 1; d_2 < m_route_final.getH(H_2).size() - 2; d_2++) {
                                for (int d_3 = 1; d_3 < m_route_final.getH(H_3).size() - 2; d_3++) {

                                    // найти 3 самых тяжёлых ребра
                                    int[] H_with_the_heaviest_d = new int[3]; // 3 петли с самыми тяжёлыми рёбрами
                                    int[] v_i = new int[6]; // 6 вершин между которыми мы будем строить рёбра


                                    H_with_the_heaviest_d[0] = H_1;
                                    v_i[0] = m_route_final.getH(H_with_the_heaviest_d[0]).get(d_1);
                                    v_i[3] = m_route_final.getH(H_with_the_heaviest_d[0]).get(d_1 + 1);

                                    H_with_the_heaviest_d[1] = H_2;
                                    v_i[1] = m_route_final.getH(H_with_the_heaviest_d[1]).get(d_2);
                                    v_i[4] = m_route_final.getH(H_with_the_heaviest_d[1]).get(d_2 + 1);

                                    H_with_the_heaviest_d[2] = H_3;
                                    v_i[2] = m_route_final.getH(H_with_the_heaviest_d[2]).get(d_3);
                                    v_i[5] = m_route_final.getH(H_with_the_heaviest_d[2]).get(d_3 + 1);


                                    // поиск допустимых рёбер и построение хвостов
                                    int[] sum_H = new int[6]; // сумма потребностей для каждого хвоста

                                    for (int i = 0; i < 3; i++) { // строим 6 хвостов и считаем сумму потребностей в них
                                        sum_H[i] = 0;

                                        sum_H[i + 3] = 0;

                                        // ищем индекс верхней вершины
                                        int uIndex = -1;
                                        for (int j = 1; j < m_route_final.getH(H_with_the_heaviest_d[i]).size() - 1; j++) {
                                            if (m_route_final.getH(H_with_the_heaviest_d[i]).get(j).equals(v_i[i])) {
                                                uIndex = j;
                                                break;
                                            }
                                        }

                                        // верхний хвост
                                        for (int j = 0; j <= uIndex; j++) {
                                            int v = m_route_final.getH(H_with_the_heaviest_d[i]).get(j);
                                            if (v != 0) {
                                                sum_H[i] += task.getC(v);
                                            }
                                        }

                                        // нижний хвост
                                        for (int j = m_route_final.getH(H_with_the_heaviest_d[i]).size() - 1; j >= uIndex + 1; j--) {
                                            int v = m_route_final.getH(H_with_the_heaviest_d[i]).get(j);
                                            if (v != 0) {
                                                sum_H[i + 3] += task.getC(v);
                                            }
                                        }
                                    }

                                    boolean[][] admissibility = new boolean[6][6]; // можно ли строить ребро между этими вершинами
                                    for (int i = 0; i < 6; i++) {
                                        for (int j = 0; j < 6; j++) {
                                            admissibility[i][j] = i == j || sum_H[i] + sum_H[j] <= task.getR(); // если суммы потребностей на хвостах больше r то нельзя
                                        }
                                    }

                                    // отделение недопустимых рёбер
                                    // сравнение рёбер
                                    int[][] triplets = {
                                            {0, 3, 1, 4, 2, 5},
                                            {0, 3, 1, 5, 2, 4},
                                            {0, 3, 1, 2, 4, 5},

                                            {0, 4, 1, 3, 2, 5},
                                            {0, 5, 1, 3, 2, 4},
                                            {0, 2, 1, 3, 4, 5},
                                            {0, 1, 4, 5, 2, 3},
                                            {0, 5, 1, 4, 2, 3},
                                            {0, 4, 1, 5, 2, 3},

                                            {0, 4, 1, 2, 3, 5},
                                            {0, 1, 2, 4, 3, 5},
                                            {0, 2, 1, 4, 3, 5},

                                            {0, 5, 1, 2, 3, 4},
                                            {0, 1, 2, 5, 3, 4},
                                            {0, 2, 1, 5, 3, 4},
                                    }; // таблица возможных троек

                                    improvement = 0;
                                    int min_sum_triple = Integer.MAX_VALUE;
                                    int min_triple = 0;
                                    for (int i = 0; i < 15; i++) {
                                        if (admissibility[triplets[i][0]][triplets[i][1]] &&
                                                admissibility[triplets[i][2]][triplets[i][3]] &&
                                                admissibility[triplets[i][4]][triplets[i][5]]) {
                                            int sum_triple = task.getD(v_i[triplets[i][0]], v_i[triplets[i][1]]) +
                                                    task.getD(v_i[triplets[i][2]], v_i[triplets[i][3]]) +
                                                    task.getD(v_i[triplets[i][4]], v_i[triplets[i][5]]);
                                            if (i == 0) {
                                                improvement = sum_triple;
                                            }
                                            if (min_sum_triple > sum_triple) {
                                                min_sum_triple = sum_triple;
                                                min_triple = i;
                                            }
                                        }
                                    }
                                    improvement -= min_sum_triple;

                                    if (record.getImprovement() < improvement) {
                                        record.set(H_with_the_heaviest_d, v_i, triplets[min_triple], improvement);
                                    }

                                }
                            }
                        }
                    }
                }
            }

            history_record_improvements .add(record.getImprovement()); // данный для записи в файл
//            System.out.println(record);

            //замена
            if (record.getImprovement() != 0) {
                // строим хвосты
                Map<Integer, List<Integer>> H_new = new HashMap<>();
                for (int i = 0; i < 3; i++) { // строим 6 хвостов и считаем сумму потребностей в них
                    List<Integer> H_u_new = new ArrayList<>(); // хвост от верхней вершины
                    List<Integer> H_v_new = new ArrayList<>(); // хвост от нижней вершины

                    // ищем индекс верхней вершины
                    int uIndex = -1;
                    for (int j = 1; j < m_route_final.getH(record.getH_i()[i]).size() - 1; j++) {
                        if (m_route_final.getH(record.getH_i()[i]).get(j).equals(record.getV_i()[i])) {
                            uIndex = j;
                            break;
                        }
                    }

                    // верхний хвост
                    for (int j = 0; j <= uIndex; j++) {
                        int v = m_route_final.getH(record.getH_i()[i]).get(j);
                        H_u_new.add(v);
                    }

                    // нижний хвост
                    for (int j = m_route_final.getH(record.getH_i()[i]).size() - 1; j >= uIndex + 1; j--) {
                        int v = m_route_final.getH(record.getH_i()[i]).get(j);
                        H_v_new.add(v);
                    }

                    H_new.put(i, H_u_new);
                    H_new.put(i + 3, H_v_new);
                }

                // делаем замену
                List<Integer> H1_new = new ArrayList<>(H_new.get(record.getTriple()[0]));
                Collections.reverse(H_new.get(record.getTriple()[1]));
                H1_new.addAll(H_new.get(record.getTriple()[1]));

                List<Integer> H2_new = new ArrayList<>(H_new.get(record.getTriple()[2]));
                Collections.reverse(H_new.get(record.getTriple()[3]));
                H2_new.addAll(H_new.get(record.getTriple()[3]));

                List<Integer> H3_new = new ArrayList<>(H_new.get(record.getTriple()[4]));
                Collections.reverse(H_new.get(record.getTriple()[5]));
                H3_new.addAll(H_new.get(record.getTriple()[5]));

                // строим улучшение жадным алгоритмом
                List<Integer> H1_new_advanced = greedy_Algorithm(H1_new);
                if (F_H(H1_new_advanced) < F_H(H1_new)) {
                    m_route_final.setH(record.getH_i()[0], H1_new_advanced);
                } else {
                    m_route_final.setH(record.getH_i()[0], H1_new);
                }

                List<Integer> H2_new_advanced = greedy_Algorithm(H2_new);
                if (F_H(H2_new_advanced) < F_H(H2_new)) {
                    m_route_final.setH(record.getH_i()[1], H2_new_advanced);
                } else {
                    m_route_final.setH(record.getH_i()[1], H2_new);
                }

                List<Integer> H3_new_advanced = greedy_Algorithm(H3_new);
                if (F_H(H3_new_advanced) < F_H(H3_new)) {
                    m_route_final.setH(record.getH_i()[2], H3_new_advanced);
                } else {
                    m_route_final.setH(record.getH_i()[2], H3_new);
                }
            }
            history_F.add(m_route_final.getF(task));
        } while (record.getImprovement() > epsilon);


        CSVWriter.writeData("./data/data_final_improvement" + iteration + ".csv", history_record_improvements);
        CSVWriter.writeData("./data/data_final_F" + iteration + ".csv", history_F);

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
                if (!i.equals(j) && task.getD(i, j) < min_D) {
                    min_D = task.getD(j, i);
                    j = i;
                }
            }

            H_new.add(j); // добавляем вершину ребра ji

            Integer finalJ = j;
            H_.removeIf(i -> i.equals(finalJ)); // удаляем найденную вершину
        } while (!H_.isEmpty());

        H_new.add(0); // добавляем базу в конец петли

        return H_new;
    }
    
    /**
     * @return значения целевой функции для начального решения
     */
    public Integer getF_basic() {
        // если начальное решение ещё не построено
        if (m_route_basic == null) {
            return null;
        }

        return m_route_basic.getF(task);
    }

    /**
     * @return значение целевой функции для финального решения, null - если оно ещё не построено
     */
    public Integer getF_final() {
        // если начальное решение ещё не построено
        if (m_route_final == null) {
            return null;
        }

        return m_route_final.getF(task);
    }

    /**
     * Вычисление значения целевой функции
     * @param m_route m-маршрут
     * @return значение целевой функции
     */
    private int F(List<List<Integer>> m_route) {
        int F = 0;
        for (List<Integer> H : m_route) { // суммируем все рёбра во всех H
            F += F_H(H);
        }
        return F;
    }

    private int F_H(List<Integer> H) {
        int F = 0;
        for (int i = 0; i < H.size() - 1; i++) {
            F += task.getD(H.get(i), H.get(i + 1));
        }
        return F;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if (m_route_basic != null) {
            str.append(">M route basic:\n")
                    .append(m_route_basic)
                    .append(">>>F(basic) = ").append(getF_basic()).append("\n\n");
        }

        if (m_route_final != null) {
            str.append(">M route final:\n")
                    .append(m_route_final)
                    .append(">>>F(final) = ").append(getF_final()).append("\n\n");
        }
        return str.toString();
    }

    @Override
    public int compareTo(Solution other) {
        return Integer.compare(this.getF_final(), other.getF_final());
    }
}
