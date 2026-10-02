package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 29;
    private static final int HEIGHT = 30;

    private static Random random = new Random();

    public class Hexagon{
        private int xCoord;
        private int yCoord;
        private int sideLength;
        private String tileTerrain;

        public Hexagon (int x, int y, int s, String t) {
            xCoord = x;
            yCoord = y;
            sideLength = s;
            tileTerrain = t;
        }
    }

    public static void createHexagonWorld(TETile[][] world) {
        int startX = WIDTH/2 - 1;
        int startY = HEIGHT - 1;
        int sideLength = 3;
        clearWorld(world);
        hexagonWorldHelper(sideLength, startX, startY, world);
    }

    private static void hexagonWorldHelper(int sideLength, int xCoord, int yCoord, TETile[][] world) {
        if (isOutOfBounds(sideLength, xCoord, yCoord) || world[xCoord][yCoord].character() != ' ') {
            return;
        }

        if ((xCoord <= sideLength*2 && yCoord <= sideLength*2) || (xCoord >= WIDTH - sideLength*2 && yCoord <= sideLength*2)) {
            return;
        }

        String randHexTileset = randomTileset();
        addHexagon(sideLength, xCoord, yCoord, world, randHexTileset);

        int nextLeftX = xCoord - (2 * sideLength - 1);
        int nextRightX = xCoord + (2 * sideLength - 1);
        int nextY = yCoord - sideLength;

        hexagonWorldHelper(sideLength, nextLeftX, nextY, world);
        hexagonWorldHelper(sideLength, nextRightX, nextY, world);
    }

    private static String randomTileset() {
        int randNum = random.nextInt(5);
        switch (randNum) {
            case 0:
                return "grass";
            case 1:
                return "sand";
            case 2:
                return "flower";
            case 3:
                return "mountain";
            case 4:
                return "tree";
            default:
                return "water";
        }
    }

    public static void clearWorld(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void addTopHalfHexagon(int sideLength, int xCoord, int yCoord, TETile[][] world, String tileTerrain) {
        if (isOutOfBounds(sideLength, xCoord, yCoord)) {
            System.out.println("Tile is off the board");
            System.exit(0);
        }
        for (int yPos = yCoord, xPos = xCoord, rowLength = sideLength; yPos > yCoord-sideLength; yPos--, xPos--, rowLength+=2) {
            for (int rowTileCount = 0; rowTileCount < rowLength; rowTileCount++, xPos++) {
                switch (tileTerrain.toLowerCase()) {
                    case "grass":
                        world[xPos][yPos] = Tileset.GRASS;
                        break;
                    case "sand":
                        world[xPos][yPos] = Tileset.SAND;
                        break;
                    case "flower":
                        world[xPos][yPos] = Tileset.FLOWER;
                        break;
                    case "mountain":
                        world[xPos][yPos] = Tileset.MOUNTAIN;
                        break;
                    case "tree":
                        world[xPos][yPos] = Tileset.TREE;
                        break;
                    default:
                        world[xPos][yPos] = Tileset.WATER;
                }
            }
            xPos -= rowLength;
        }
    }

    private static void addBottomHalfHexagon(int sideLength, int xCoord, int yCoord, TETile[][] world, String tileTerrain){
        if (isOutOfBounds(sideLength, xCoord, yCoord)) {
            System.out.println("Tile is off the board");
            System.exit(0);
        }
        for (int yPos = yCoord - sideLength, xPos = xCoord - (sideLength - 1), rowLength = sideLength + 2*(sideLength - 1);  rowLength >= sideLength; yPos--, xPos++, rowLength-=2) {
            for (int rowTileCount = 0; rowTileCount < rowLength; rowTileCount++, xPos++) {
                switch (tileTerrain.toLowerCase()) {
                    case "grass":
                        world[xPos][yPos] = Tileset.GRASS;
                        break;
                    case "sand":
                        world[xPos][yPos] = Tileset.SAND;
                        break;
                    case "flower":
                        world[xPos][yPos] = Tileset.FLOWER;
                        break;
                    case "mountain":
                        world[xPos][yPos] = Tileset.MOUNTAIN;
                        break;
                    case "tree":
                        world[xPos][yPos] = Tileset.TREE;
                        break;
                    default:
                        world[xPos][yPos] = Tileset.WATER;
                }
            }
            xPos -= rowLength;
        }
    }

    private static boolean isOutOfBounds(int sideLength, int xCoord, int yCoord) {
        int rightMostCoord = xCoord + 2 * (sideLength - 1);
        int leftMostCoord = xCoord - (sideLength - 1);
        int bottomMostCoord = yCoord - 2 * sideLength - 1;
        if (rightMostCoord > WIDTH || leftMostCoord < 0 || yCoord > HEIGHT || bottomMostCoord < -2) {
            return true;
        }
        return false;
    }

    public static void addHexagon(int sideLength, int xCoord, int yCoord, TETile[][] world, String tileTerrain) {
        addTopHalfHexagon(sideLength, xCoord, yCoord, world, tileTerrain);
        addBottomHalfHexagon(sideLength, xCoord, yCoord, world, tileTerrain);
    }

    public static void main(String[] args){
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        createHexagonWorld(world);

        ter.renderFrame(world);
    }
}
