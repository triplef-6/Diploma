import java.util.Arrays;

public class Task {
    private final int n; // размерность графа К + 1
    private final int[][] D; // массив длинн ребёр графа К
    private final int[] C; // массив потребностей вершин графа К
    private final int r; // грузоподемность
    private final int m; // количествово машин

    /**
     * Конструктор задачи (он же генератор)
     * @param nn размерность графа K
     * @param a_d минимальная длина ребра
     * @param b_d максимальная длина ребра
     * @param a_c минимальная потребность вершины
     * @param b_c максимальная потребность вершины
     */
    public Task(int nn, int a_d, int b_d, int a_c, int b_c) { // генератор
        // создаём матрицу маршрутов
        this.n = nn + 1;
        this.D = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    D[i][j] = Integer.MAX_VALUE;
                } else {
                    D[i][j] = a_d + (int) (Math.random() * (b_d - a_d + 1));
                    D[j][i] = D[i][j];
                }
            }
        }

        // создаём вектор потребностей
        this.C = new int[n - 1];
        int sum_c = 0;
        for (int i = 0; i < n - 1; i++) {
            C[i] = a_c + (int) (Math.random() * (b_c - a_c +1));
            sum_c += C[i];
        }

        // создаём грузоподъёмность и ограничения на машины
        int[] C_ = Arrays.copyOf(C, C.length);
        Arrays.sort(C_);
        this.r = C_[C_.length - 1] + C_[C_.length - 2] + 1;
        this.m = sum_c / r + 1;
    }

    /**
     * Конструктор по параметрам
     * @param nn размерность графа K
     * @param m ограничение на машины
     * @param D матрица рёбер
     * @param C вектор потребностей
     * @param r грузоподъёмность
     */
    public Task(int nn, int m, int[][] D, int[] C, int r) { // конструктор по параметрам
        this.n = nn + 1;
        if (n <= 0 || m <= 0 || r <= 0 || D.length != n || C.length != n - 1) {
            throw new IllegalArgumentException("Некоректные коэфициенты");
        }

        int sum = 0;
        for (int i : C) {
            sum += i;
        }
        if (m * r < sum) {
            throw new IllegalArgumentException("Задача не разрешима");
        }

        this.m = m;
        this.D = D;
        this.C = C;
        this.r = r;
    }

    public int getN() {
        return n;
    }

    public int[] getC() {
        return C;
    }

    public int getR() {
        return r;
    }

    public int getM() {
        return m;
    }

    public int[][] getD() {
        return D;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("<n->").append(n - 1).append("\n");
        str.append("<m->").append(m).append("\n");
        str.append("<r->").append(r).append("\n");
        str.append("<D->{\n");
        for (int[] _d : D) {
            str.append("{");
            for (int d : _d) {
                if (d == Integer.MAX_VALUE) {
                    str.append("0, ");
                } else {
                    str.append(d).append(", ");
                }
            }
            str.append("}\n");
        }
        str.append("}\n");

        str.append("<C->{");
        for (int c : C) {
            str.append(c).append(", ");
        }
        str.append("}\n");


        return str.toString();
    }
}
