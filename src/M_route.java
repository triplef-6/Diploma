import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class M_route {
    private List<List<Integer>> m_route;

    public M_route() {
        this.m_route = new ArrayList<>();
    }
    public M_route(List<List<Integer>> m_route) {
        this.m_route = m_route;
    }
    public M_route(M_route m_route) {
        this.m_route = m_route.m_route;
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
