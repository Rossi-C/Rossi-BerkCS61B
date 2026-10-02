package byow.Core;

import org.locationtech.jts.geom.Coordinate;

public class Room {

    int x;
    int y;
    int width;
    int height;
    int id;
    Coordinate centroid;

    public Room(int x, int y, int w, int h, int id) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        this.id = id;
        centroid = new Coordinate(this.x + width/2, this.y + height/2);
    }

    public boolean intersects(Room other) {
        boolean overlapX = this.x - 1 <= other.x + other.width && this.x + this.width + 1 >= other.x;
        boolean overlapY = this.y  - 1<= other.y + other.height && this.y + this.height + 1 >= other.y;
        return (overlapX && overlapY);
    }
}
