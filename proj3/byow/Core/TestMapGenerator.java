package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class TestMapGenerator {

    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] mapLayout = engine.interactWithInputString(args[0]);
        System.out.println(mapLayout.toString());

        TERenderer ter = new TERenderer();
        ter.initialize(80, 50);
        ter.renderFrame(mapLayout);


    }
}
