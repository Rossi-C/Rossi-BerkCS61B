package byow.Core;

import org.locationtech.jts.geom.Coordinate;

public class Avatar {
    private Coordinate position;

    public Avatar(int x, int y) {
        position = new Coordinate(x, y);
    }

    public Coordinate getPosition() {
        return position;
    }
}
