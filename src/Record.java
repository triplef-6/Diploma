import java.util.Arrays;
import java.util.Objects;

public class Record {
    private int[] H_i;
    private int[] v_i;
    private int[] triple;
    private int improvement;

    public Record() {
        this.improvement = Integer.MIN_VALUE;
    }
    public Record(int[] H_i, int[] v_i, int[] triple, int improvement) {
        this.H_i = H_i;
        this.v_i = v_i;
        this.improvement = improvement;
        this.triple = triple;
    }

    public void set(int[] H_i, int[] v_i, int[] triple, int improvement) {
        this.H_i = H_i;
        this.v_i = v_i;
        this.improvement = improvement;
        this.triple = triple;
    }

    public void reset() {
        this.H_i = null;
        this.v_i = null;
        this.triple = null;
        this.improvement = Integer.MIN_VALUE;
    }

    public int[] getH_i() {
        return H_i;
    }

    public int[] getV_i() {
        return v_i;
    }

    public int[] getTriple() {
        return triple;
    }

    public int getImprovement() {
        return improvement;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return improvement == record.improvement && Objects.deepEquals(H_i, record.H_i) && Objects.deepEquals(v_i, record.v_i) && Objects.deepEquals(triple, record.triple);
    }

    @Override
    public String toString() {
        return "Record{" +
                "H_i=" + Arrays.toString(H_i) +
                ", v_i=" + Arrays.toString(v_i) +
                ", triple=" + Arrays.toString(triple) +
                ", improvement=" + improvement +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(H_i), Arrays.hashCode(v_i), Arrays.hashCode(triple), improvement);
    }
}
