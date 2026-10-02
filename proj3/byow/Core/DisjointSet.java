package byow.Core;

import java.util.HashMap;
import java.util.Map;

public class DisjointSet {
    private final Map<Integer, Integer> parent = new HashMap<>();

    public void makeSet(int id) {
        parent.put(id, id);
    }

    public int find(int id) {
        // base case = at root
        if (parent.get(id) == id) {
            return id;
        }

        // Path compression optimization
        // Recursively finding until reached root and then setting current id value to equal root value
        int root = find(parent.get(id));
        parent.put(id, root);
        return root;
    }

    public boolean union(int id1, int id2) {
        int root1 = find(id1);
        int root2 = find(id2);

        if (root1 != root2) {
            parent.put(root1, root2);
            return true; // successful merge
        }
        return false; // already in same set
    }
}
