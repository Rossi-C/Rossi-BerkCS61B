package byow.Core;

public class MasterEdge implements Comparable<MasterEdge>{
    Room u;
    Room v;
    double weight;

    public MasterEdge(Room u, Room v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    @Override
    public int compareTo(MasterEdge other) {
        int byWeight = Double.compare(this.weight, other.weight);
        if (byWeight != 0) return byWeight;
        int minA = Math.min(this.u.id, this.v.id);
        int minB = Math.min(other.u.id, other.v.id);
        if (minA != minB) return Integer.compare(minA, minB);
        return Integer.compare(Math.max(this.u.id, this.v.id), Math.max(other.u.id, other.v.id));
    }
}
