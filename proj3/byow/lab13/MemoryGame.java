package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    Font font = new Font("Monaco", Font.BOLD, 30);
    Font headerFont = new Font("Monaco", Font.BOLD, 20);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        String randString = "";
        for (int i = 0; i < n; i++){
            randString = randString + (CHARACTERS[rand.nextInt(26)]);
        }
        return randString;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(width/2, height/2, s);

        //TODO: If game is not over, display relevant game information at the top of the screen
        drawHeader();
        StdDraw.show();
    }

    public void drawHeader() {
        if (!gameOver) {
            StdDraw.setFont(headerFont);
            if (playerTurn) {
                StdDraw.text(width/2, height - 1, "Type!");
            } else {
                StdDraw.text(width/2, height - 1, "Watch!");
            }
            StdDraw.textLeft(0, height - 1, "Round: " + round);
            StdDraw.textRight(width, height - 1, getRandomEncouragement());
            StdDraw.line(0, height - 2, width, height - 2);
        }
    }

    private String getRandomEncouragement() {
         String randomEncouragement = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
         return randomEncouragement;
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++){
            drawFrame(String.valueOf(letters.charAt(i)));
            StdDraw.pause(1000);
            StdDraw.clear(StdDraw.BLACK);
            drawHeader();
            StdDraw.show();
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        drawFrame("");
        String typedString = "";
        String keyPress;
        for (int i = 1; i <= n; i++) {
            while (typedString.length() != i) {
                if (StdDraw.hasNextKeyTyped()) {
                    keyPress = String.valueOf(StdDraw.nextKeyTyped());
                    typedString = typedString + keyPress;
                    drawFrame(typedString);
                }
            }
        }
        return typedString;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        String randString;
        String inputString;

        //TODO: Establish Engine loop
        while (!gameOver) {
            playerTurn = false;
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
            randString  = generateRandomString(round);
            flashSequence(randString);
            playerTurn = true;
            inputString = solicitNCharsInput(round);
            StdDraw.pause(500 );
            if (!inputString.equals(randString)) {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + round);
            }
            round++;
        }
    }

}
