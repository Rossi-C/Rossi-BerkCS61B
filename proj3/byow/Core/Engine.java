package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;

public class Engine implements Serializable {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File BYOW_DIR = Paths.get(CWD.getPath(), ".byow").toFile();
    public static final File SEED_FILE = Paths.get(BYOW_DIR.getPath(), "seed").toFile();
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 53;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        GUI gameGUI = new GUI(WIDTH, HEIGHT);
        Seed seed = gameGUI.launch();
        while (seed == null) {seed = gameGUI.launch();}
        TETile[][] map = interactWithInputString(seed.getSeed());
        boolean hasKey = hasKey(seed.getSeed());
        ter.renderFrame(map);
        gameGUI.displayGameHeader(map, false, false);

        while (!isGameOver(seed.getSeed())) {
           gameGUI.displayGameHeader(map, hasKey, false);
           String movementInput = gameGUI.detectPlayerMovement(map, hasKey, false);
           seed.addToSeed(movementInput);
           map = interactWithInputString(seed.getSeed());
           hasKey = hasKey(seed.getSeed());
           ter.renderFrame(map);
           gameGUI.displayGameHeader(map, hasKey, false);
        }
        ter.renderFrame(map);
        gameGUI.displayGameHeader(map, hasKey, true);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.



        MapGenerator mapGenerator = new MapGenerator(input);
        TETile[][] finalWorldFrame = mapGenerator.generateMap();

        return finalWorldFrame;
    }

    private boolean isGameOver(String input) {
        MapGenerator mapGenerator = new MapGenerator(input);
        TETile[][] finalWorldFrame = mapGenerator.generateMap();

        return mapGenerator.isGameOver();
    }

    private boolean hasKey(String input) {
        MapGenerator mapGenerator = new MapGenerator(input);
        TETile[][] finalWorldFrame = mapGenerator.generateMap();

        return mapGenerator.hasKey();
    }
}
