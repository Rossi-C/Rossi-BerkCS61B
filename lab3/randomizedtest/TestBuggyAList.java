package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> broken = new BuggyAList<>();
        AListNoResizing<Integer> correct = new AListNoResizing<>();

        for (int i = 0; i < 3; i++) {
            broken.addLast(i + 3);
            correct.addLast(i + 3);
        }

        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
             int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int lSize = L.size();
                int bSize = B.size();
                System.out.println("L size: " + lSize + " and B size: " + bSize);
            } else if (operationNumber == 2) {
                //getLast
                if (L.size() > 0) {
                    int lLast = L.getLast();
                    System.out.println("L Last item: " + lLast);
                }
                if (B.size() > 0) {
                    int bLast = B.getLast();
                    System.out.println("B Last item: " + bLast);
                }
            } else if (operationNumber == 3) {
                //removeLast
                if (L.size() > 0) {
                    int lLast = L.removeLast();
                    System.out.println("L removeLast:(" + lLast + ")");
                }
                if (B.size() > 0) {
                    int bLast = B.removeLast();
                    System.out.println("B removeLast:(" + bLast + ")");
                }
            }
        }
    }
}
