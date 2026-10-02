package byow.Core;

import byow.TileEngine.TETile;
import static byow.Core.SerializationUtils.*;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static byow.Core.Engine.SEED_FILE;

public class GUI {

    private int width;
    private int height;
    private final long MAX_SEED = Long.MAX_VALUE;
    private Font font = new Font("Monaco", Font.BOLD, 20);
    TETile[][] map;
    private final Set<String> TITLE_INPUT = Set.of("n", "l", "r", "q");
    private final Set<String> MOVEMENT_KEYS = Set.of("w", "a", "s", "d");
    public Seed seed;

    public static void main(String[] args) {
        long seed = Long.parseLong(args[0]);
        GUI testGUI = new GUI(80,50);
        testGUI.displayStartScreen();
        String playerInput = testGUI.solicitPlayerInput();
        testGUI.enactPlayerInput(playerInput);
    }

    public Seed launch() {
        this.displayStartScreen();
        String playerInput = this.solicitPlayerInput();
        String seedString = this.enactPlayerInput(playerInput);
        seed = new Seed(seedString);
        return seed;
    }

    public GUI(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
    }

    public void displayStartScreen() {
        StdDraw.text(width/2, height/2 + height*.1, "CS 61B: THE GAME");
        StdDraw.text(width/2, height/2 + height*.05, "New Game (N)");
        StdDraw.text(width/2, height/2 + height*.025, "Load Game (L)");
        StdDraw.text(width/2, height/2, "Replay Game (R)");
        StdDraw.text(width/2, height/2 - height*.025, "Quit (Q)");
        StdDraw.show();
    }

    private void displayMessageScreen(String s) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(width/2, height/2 + height*.05, "Enter Seed followed by an S");
        StdDraw.text(width/2, height/2, s);
        StdDraw.show();
    }

    public void displayGameHeader(TETile[][] map, boolean hasKey, boolean gameOver) {
        clearHeader();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(font);
        if (hasKey) {
            StdDraw.picture(1, height - 1, "C:/Users/clint/Pictures/2dTilesets/Key.png");
        }
        if (!hasKey && !gameOver) {
            StdDraw.text(width/2, height -1, "Find the Key!");
        } else if (hasKey && !gameOver) {
            StdDraw.text(width/2, height -1, "Find and Open the Chest!");
        } else if (gameOver) {
            StdDraw.textRight(width/2, height - 1, "You Win!");
        }
        String mouseOverDescription = getMouseOverDescription(map);
        if (mouseOverDescription.equals("nothing")) {
            mouseOverDescription = "";
        }
        StdDraw.textRight(width - 1, height - 1, mouseOverDescription);
        StdDraw.line(0, height - 2, width, height - 2);
        StdDraw.show();
    }

    private void clearHeader() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(width / 2.0, height - 1.5, width / 2.0, 1.5);
    }

    private String getMouseOverDescription(TETile[][] map) {
        int mouseX = (int) Math.round(StdDraw.mouseX());
        int mouseY = (int) Math.round(StdDraw.mouseY());
        if (mouseX < 0 || mouseX >= map.length || mouseY < 0 || mouseY >= map[0].length) {
            return "";
        }
        return map[mouseX][mouseY].description();
    }

    public String solicitPlayerInput() {
        String playerInput = "";

        while (!TITLE_INPUT.contains(playerInput)) {
            if (StdDraw.hasNextKeyTyped()) {
                playerInput = String.valueOf(StdDraw.nextKeyTyped()).toLowerCase();
            }
        }

        return playerInput;
    }

    public String enactPlayerInput(String playerInput) {
        String seedString = "";
        if (playerInput.equals("n")) {
            seedString = getSeedInput();
        } else if (playerInput.equals("q")) {
            System.exit(0);
        } else if (playerInput.equals("l")) {
            seedString = loadGame();

        }
        return seedString;
    }

    private String getSeedInput() {
        String seedInput = "";
        String lastKeyPress= "";
        // regex string to check if input is a number
        String regex = "\\d+";
        displayMessageScreen("");

        while (!lastKeyPress.toLowerCase().equals("s")) {
            if(StdDraw.hasNextKeyTyped()) {
                lastKeyPress = String.valueOf(StdDraw.nextKeyTyped());
                if (lastKeyPress.matches(regex)) {
                    seedInput = seedInput + lastKeyPress;
                    displayMessageScreen(seedInput);
                }
            }
        }

        try {
            long seed = Long.parseLong(seedInput);
        } catch (NumberFormatException e) {
            displayMessageScreen("Given seed is too large. Please enter smaller seed.");
            StdDraw.pause(2000);
            seedInput = getSeedInput();
        }
        return seedInput;
    }

    public String detectPlayerMovement(TETile[][] map, boolean hasKey, boolean gameOver) {
        String keyPress = "";
        String previousPress = "";
        double lastMouseX = Double.NaN;
        double lastMouseY = Double.NaN;

        while (!MOVEMENT_KEYS.contains(keyPress)) {
            if (StdDraw.hasNextKeyTyped()) {
                previousPress = keyPress;
                keyPress = String.valueOf(StdDraw.nextKeyTyped()).toLowerCase();
            }

            // checking if quit command was given and save/quitting if so
            if (previousPress.equals(":") && keyPress.equals("q")) {
                saveAndQuit();
            }

            // updating game header with mouseover information
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            if (mouseX != lastMouseX || mouseY != lastMouseY) {
                displayGameHeader(map, hasKey, gameOver);
                lastMouseX = mouseX;
                lastMouseY = mouseY;
            }
            StdDraw.pause(20);
        }
        return keyPress;
    }

    private void saveAndQuit() {
        seed.saveSeed();
        System.exit(0);
    }

    private String loadGame() {
        String seedString = null;
        if (!SEED_FILE.exists()){
            displayMessageScreen("There are no previous files to load. You can start again with a new game!");
            StdDraw.pause(2000);
            return null;
        }
        try {
            seed = readObject(SEED_FILE, Seed.class);
            seedString = seed.getSeed();
        } catch (RuntimeException e) {
            displayMessageScreen("Could not load previous game. Goodbye!");
            StdDraw.pause(2000);
            System.exit(0);
        }
        return seedString;
    }
}
