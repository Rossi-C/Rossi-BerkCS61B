package byow.Core;

public class Edge {
    Room target;
    // Distance between room centers
    double weight;

    public Edge (Room target, double weight) {
        this.target = target;
        this.weight = weight;
    }
}
