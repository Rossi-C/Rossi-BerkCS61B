package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> cmp;
    public MaxArrayDeque(Comparator<T> c) {
        cmp = c;
    }

    public T max() {
        if (isEmpty()) { return null; }
        T maxItem = this.get(0);
        for (T x : this) {
            if (cmp.compare(x, maxItem) > 0) {
                maxItem = x;
            }
        }
        return maxItem;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) { return null; }
        T maxItem = this.get(0);
        for (T x : this) {
            if (c.compare(x, maxItem) > 0) {
                maxItem = x;
            }
        }
        return maxItem;
    }

    private static void main(String[] args) {
        Comparator<Integer> cmp = (o1, o2) -> {
            if (o1 > o2) {
                return 1;
            } else if (o1 < o2) {
                return -1;
            } else {
                return 0;
            }
        };

        MaxArrayDeque mad1 = new MaxArrayDeque(cmp);

        int n = 99;

        for (int i = n; i >= 0; i--) {
            mad1.addFirst(i);
        }

        System.out.println(mad1.max());
        System.out.println(mad1.max(cmp));

        Comparator<String> cmp2 = new Comparator<>() {
            @Override
            public int compare(String strA, String strB) {
                return strA.compareTo(strB);
            }
        };

        MaxArrayDeque mad2 = new MaxArrayDeque(cmp2);

        mad2.addFirst("front");
        mad2.addLast("middle");
        mad2.addLast("back");

        System.out.println(mad2.max());
        System.out.println(mad2.max(cmp2));
    }
}
