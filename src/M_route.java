import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class M_route {
    private List<List<Integer>> m_route;
    public Integer F;

    public M_route() {
        this.m_route = new ArrayList<>();
    }
    public M_route(List<List<Integer>> m_route) {
        this.m_route = new ArrayList<>(m_route);
    }

    public int size() {
        return m_route.size();
    }

    public Integer getF(Task task) {
        this.F = 0;
        for (List<Integer> H : m_route) { // суммируем все рёбра во всех H
            F += F_H(H, task);
        }
        return F;
    }
    private int F_H(List<Integer> H, Task task) {
        int F = 0;
        for (int i = 0; i < H.size() - 1; i++) {
            F += task.getD(H.get(i), H.get(i + 1));
        }
        return F;
    }

    public DWithVU getBiggestDFromH(int H_i, Task task) {
        DWithVU d_max = new DWithVU(m_route.get(H_i).get(1),
                m_route.get(H_i).get(2),
                task.getD(m_route.get(H_i).get(1), m_route.get(H_i).get(2)));
        if (m_route.get(H_i).size() > 4) {
            for (int i = 2; i <= m_route.get(H_i).size() - 2; i++) {
                DWithVU d = new DWithVU(m_route.get(H_i).get(i), m_route.get(H_i).get(i + 1),
                        task.getD(m_route.get(H_i).get(i),
                                m_route.get(H_i).get(i + 1)));
                if (d_max.getD() < d.getD()) {
                    d_max.setV(d.getV());
                    d_max.setU(d.getU());
                    d_max.setD(d.getD());
                }
            }
        }

        return d_max;
    }

    public List<Integer> getH(int i) {
        return m_route.get(i);
    }

    public void addH(List<Integer> H) {
        m_route.add(H);
    }

    public void setH(int i, List<Integer> H) {
        m_route.set(i, H);
    }
    public List<List<Integer>> getM_route() {
        return m_route;
    }

    public void setM_route(List<List<Integer>> m_route) {
        this.m_route = m_route;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        M_route mRoute = (M_route) o;
        return Objects.equals(m_route, mRoute.m_route);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(m_route);
    }

    @Override
    public String toString() {
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

        return str.toString();
    }
}
