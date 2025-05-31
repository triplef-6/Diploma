import java.util.*;
import java.util.stream.Collectors;

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
            V.put(k, task.getC(k));
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
                r_i -= task.getC(i); // вычитаем из грузоподъёмности при обходе потребность выбранной вершины
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
            m_route_advanced.add(greedy_Algorithm(H)); // используем для отого отдельную функцию
        }

        return m_route_advanced;
    }

    /**
     * @param epsilon до какого придела улучшаем
     * @param if_we_get_advanced true - если мы будем строить от улучшенного начального решения, false - если от базового 
     * @return финальное решение
     */
    public List<List<Integer>> getM_route_final(double epsilon, boolean if_we_get_advanced) {
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
        int improvement = 0; // на сколько мы улучшаем финальный результат
//        do {
        for (int e = 0; e < 100; e++) {
            // найти 3 самых тяжёлых ребра
            int[] H_with_the_heaviest_d = new int[3]; // 3 петли с самыми тяжёлыми рёбрами
            int[] v_i = new int[6]; // 6 вершин между которыми мы будем строить рёбра

            TreeMap<Integer, DWithVU> heavy_d = new TreeMap<>(Collections.reverseOrder()); // список самый тяжёлых рёбер в каждой H
            for (int i = 0; i < m_route_final.size(); i++) { // ищем самые тяжёлые рёбра в каждом H в маршруте
                List<Integer> H = m_route_final.get(i); // выбираем петлю
                DWithVU d_max = new DWithVU(0, 0, Integer.MIN_VALUE);
                for (int j = 1; j < H.size() - 2; j++) { // ищем его
                    DWithVU d = new DWithVU(j, j + 1, task.getD(H.get(j), H.get(j + 1)));
                    if (d_max.getD() < d.getD() /* && !stop_d.contains(d)*/) {
                        d_max.setV(d.getV());
                        d_max.setU(d.getU());
                        d_max.setD(d.getD());
                    }
                }
                heavy_d.put(i, d_max);
            }
            Map<Integer, DWithVU> heavy_d_sorted = heavy_d.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new)); // сортировка

            int k = 0;
            for (Map.Entry<Integer, DWithVU> H : heavy_d_sorted.entrySet()) { // берём три петли с самыми тяжёлыми рёбрами
                if (k == 3) { // счётчик
                    break;
                }

                H_with_the_heaviest_d[k] = H.getKey(); // петля с этим ребром
                v_i[k] = H.getValue().getV(); // верхняя вершина
                v_i[k + 3] = H.getValue().getU(); // нижняя вершина

                k++; // счётчик
            }


            // поиск допустимых рёбер и построение хвостов
            Map<Integer, List<Integer>> H_new = new HashMap<>(); // новые петли
            int[] sum_H = new int[6]; // сумма потребностей для каждого хвоста

            for (int i = 0; i < 3; i++) { // строим 6 хвостов и считаем сумму потребностей в них
                List<Integer> H_u_new = new ArrayList<>(); // хвост от верхней вершины
                sum_H[i] = 0;

                List<Integer> H_v_new = new ArrayList<>(); // хвост от нижней вершины
                sum_H[i + 3] = 0;

                // ищем индекс верхней вершины
                int uIndex = -1;
                for (int j = 1; j < m_route_final.get(H_with_the_heaviest_d[i]).size() - 1; j++) {
                    if (m_route_final.get(H_with_the_heaviest_d[i]).get(j).equals(v_i[i])) {
                        uIndex = j;
                        break;
                    }
                }

                // верхний хвост
                for (int j = 0; j <= uIndex; j++) {
                    int v = m_route_final.get(H_with_the_heaviest_d[i]).get(j);
                    H_u_new.add(v);
                    if (v != 0) {
                        sum_H[i] += task.getC(v);
                    }
                }

                // нижний хвост
                for (int j = m_route_final.get(H_with_the_heaviest_d[i]).size() - 1; j >= uIndex + 1; j--) {
                    int v = m_route_final.get(H_with_the_heaviest_d[i]).get(j);
                    H_v_new.add(v);
                    if (v != 0) {
                        sum_H[i + 3] += task.getC(v);
                    }
                }

                H_new.put(i, H_u_new); // добавляем верхний хвост
                H_new.put(i + 3, H_v_new); // добавляем нижний хвост
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
                        System.out.println(":0 sum triple: " + sum_triple);
                    }
                    if (min_sum_triple > sum_triple) {
                        min_sum_triple = sum_triple;
                        min_triple = i;
                    }
                }
            }
            improvement -= min_sum_triple;
//            System.out.println(":min triple " + min_triple);

            // замена
            if (min_triple != 0) { // если нам нужно что-то менять
                // строим новые H из хвостов в соответствии с выбранной тройкой
                List<Integer> H1_new = new ArrayList<>(H_new.get(triplets[min_triple][0]));
                Collections.reverse(H_new.get(triplets[min_triple][1]));
                H1_new.addAll(H_new.get(triplets[min_triple][1]));

                List<Integer> H2_new = new ArrayList<>(H_new.get(triplets[min_triple][2]));
                Collections.reverse(H_new.get(triplets[min_triple][3]));
                H2_new.addAll(H_new.get(triplets[min_triple][3]));

                List<Integer> H3_new = new ArrayList<>(H_new.get(triplets[min_triple][4]));
                Collections.reverse(H_new.get(triplets[min_triple][5]));
                H3_new.addAll(H_new.get(triplets[min_triple][5]));

                // строим улучшение жадным алгоритмом 
                List<Integer> H1_new_advanced = greedy_Algorithm(H1_new);
                if (F_H(H1_new_advanced) < F_H(H1_new)) {
                    m_route_final.set(H_with_the_heaviest_d[0], H1_new_advanced);
                } else {
                    m_route_final.set(H_with_the_heaviest_d[0], H1_new);
                }

                List<Integer> H2_new_advanced = greedy_Algorithm(H2_new);
                if (F_H(H2_new_advanced) < F_H(H2_new)) {
                    m_route_final.set(H_with_the_heaviest_d[1], H2_new_advanced);
                } else {
                    m_route_final.set(H_with_the_heaviest_d[1], H2_new);
                }

                List<Integer> H3_new_advanced = greedy_Algorithm(H3_new);
                if (F_H(H3_new_advanced) < F_H(H3_new)) {
                    m_route_final.set(H_with_the_heaviest_d[2], H3_new_advanced);
                } else {
                    m_route_final.set(H_with_the_heaviest_d[2], H3_new);
                }
            } else {
            }
            // логи
            System.out.println(":улучшение на " + improvement);
            System.out.println(":меняем петли: " + H_with_the_heaviest_d[0] + ", " +
                    H_with_the_heaviest_d[1] + ", " +
                    H_with_the_heaviest_d[2]);
            System.out.println(":F final " + F(m_route_final));
            System.out.println(toStringM_route(m_route_final));
//        } while (improvement >= epsilon /* || task.getN() * (task.getN() - 1) / 2 - stop_d.size() >= 3 */);
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
                    .append(toStringM_route(m_route_basic))
                    .append(">>>F(basic) = ").append(getF_basic()).append("\n\n");
        }

        if (m_route_advanced != null) {
            str.append(">M route advanced:\n")
                    .append(toStringM_route(m_route_advanced))
                    .append(">>>F(advanced) = ").append(getF_advanced()).append("\n\n");
        }

        if (m_route_final != null) {
            str.append(">M route final:\n")
                    .append(toStringM_route(m_route_final))
                    .append(">>>F(final) = ").append(getF_final()).append("\n\n");
        }
        return str.toString();
    }
    private StringBuilder toStringM_route(List<List<Integer>> m_route) {
        StringBuilder str = new StringBuilder();

        int k = 0;
        for (List<Integer> H : m_route) {
            str.append(">>").append(k).append("->");
            for (Integer i : H) {
                str.append(i).append("-");
            }
            str.append("\n");
            k++;
        }

        return str;
    }
}


class DWithVU implements Comparable<DWithVU> {
    private int v;
    private int u;
    private int d;

    public DWithVU(int v, int u, int d) {
        this.v = v;
        this.d = d;
        this.u = u;
    }

    public void setV(int v) {
        this.v = v;
    }

    public void setU(int u) {
        this.u = u;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getU() {
        return u;
    }

    public int getD() {
        return d;
    }

    public int getV() {
        return v;
    }

    @Override
    public String toString() {
        return "v=" + v +
                ", u=" + u +
                ", d=" + d;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DWithVU dWithVU = (DWithVU) o;
        return v == dWithVU.v && u == dWithVU.u && d == dWithVU.d;
    }

    @Override
    public int hashCode() {
        return Objects.hash(v, u, d);
    }

    @Override
    public int compareTo(DWithVU other) {
        return Integer.compare(this.d, other.d);
    }
}
