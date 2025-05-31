import java.util.Objects;

public class DWithVU implements Comparable<DWithVU> {
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
