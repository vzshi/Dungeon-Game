package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.parallel.ResourceAccessMode;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.file.Path;
import java.util.HashMap;

import static java.lang.Character.TYPE;
import static java.lang.Character.toLowerCase;

public class Engine {
    TERenderer ter = new TERenderer();

    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private int midW = WIDTH / 2;
    private int midH = HEIGHT / 2;
    private int quarterH = HEIGHT / 4 3;
    private static final int PAUSE_LEN = 2000;
    private static final double FACTOR = 1.5;
    private static final int TITLEF = 30;
    private static final int SUBF = 19;
    private TETile[][] world;
    private java.util.List<WorldGeneration.Room> rooms;
    private Random r;
    private int stage = 0;
    private boolean inMatch = false;
    private String pressed = "";
    private int[] up = {0, 1};
    private int[] down = {0, -1};
    private int[] left = {-1, 0};
    private int[] right = {1, 0};
    private HashMap<Character, int[]> directions = new HashMap<>();
    private Coordinate playerLoc;
    private TETile currTile = Tileset.FLOOR;
    private String saveString = "";
    private ArrayList<Coordinate> bulbLoc = new ArrayList<>();
    private boolean toggled = false;
    private int numToks = 0;
    private String inputStr;
    private static final int X_FINAL = -5;
    private static final int Y_FINAL = -5;
    public Engine() {
        directions.put('w', up);
        directions.put('a', left);
        directions.put('s', down);
        directions.put('d', right);

        ter.initialize(WIDTH, HEIGHT);
    }

    public void interactWithKeyboard() {
        mainMenu();
        while (true) {
            if (inMatch) {
                int mX = Math.floorDiv((int) StdDraw.mouseX(), 1);
                int mY = Math.floorDiv((int) StdDraw.mouseY(), 1);

                if (mX >= 0 && mX < WIDTH && mY >= 0 && mY < HEIGHT) {
                    mouseTile(world[mX][mY]);
                }
            }

            if (StdDraw.hasNextKeyTyped()) {
                char currKey = StdDraw.nextKeyTyped();
                currKey = toLowerCase(currKey);
                File currPath = new File("./saveFile.txt");
                if (currKey == 'n' && currPath.exists()) {
                    currPath.delete();
                }
                saveString += currKey;
                completeActions(currKey);
            }
        }
    }

    public TETile[][] interactWithInputString(String input) {
        saveString = "";
        while (!input.equals("") && !((input.charAt(0) == 'N')  (input.charAt(0) == 'n')
        (input.charAt(0) == 'L')  (input.charAt(0) == 'l'))) {
            saveString += input.charAt(0);
            input = input.substring(1);
        }
        if (input.equals("")) {
            throw new IllegalArgumentException();
        }
        saveString += input.charAt(0);
        if (input.charAt(0) == 'l'  input.charAt(0) == 'L') {
            File dirPath = new File("./saveFile.txt");
            boolean e = dirPath.exists();
            if (!e) {
                return world;
            } else {
                loadForString();
                input = input.substring(1);
            }
        } else if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {
            String theSeed = "";
            input = input.substring(1);
            while (input.charAt(0) != 's' && input.charAt(0) != 'S') {
                theSeed += input.charAt(0);
                saveString += input.charAt(0);
                input = input.substring(1);
            }
            saveString += input.charAt(0);
            input = input.substring(1);
            long currSeed = Long.parseLong(theSeed);
            worldGenerator(currSeed);
        }
        while (input.length() > 0) {
            if (input.charAt(0) == 'n'  input.charAt(0) == 'N') {
                saveString += input.charAt(0);
                String theSeed = "";
                input = input.substring(1);
                while (input.charAt(0) != 's' && input.charAt(0) != 'S') {
                    saveString += input.charAt(0);
                    theSeed += input.charAt(0);
                    input = input.substring(1);
                }
                saveString += input.charAt(0);
                input = input.substring(1);
                long currSeed = Long.parseLong(theSeed);
                worldGenerator(currSeed);
            }
            if ((input.charAt(0) == 'q'  input.charAt(0) == 'Q')
                    && saveString.charAt(saveString.length() - 1) == ':') {
                saveString = saveString.substring(0, saveString.length() - 1);
                return completeActionsString(input.charAt(0));
            } else {
                saveString += input.charAt(0);
                completeActionsString(input.charAt(0));
            }
            input = input.substring(1);
        }
        System.out.println(saveString);
        return world;
    }

    private void newWorldCase() {
        String theSeed = "";
        inputStr = inputStr.substring(1);
        while (inputStr.charAt(0) != 's' && inputStr.charAt(0) != 'S') {
            theSeed += inputStr.charAt(0);
            String newInput = inputStr.substring(1);
            inputStr = newInput;
        }
        String newInput = inputStr.substring(1);
        inputStr = newInput;
        long currSeed = Long.parseLong(theSeed);
        worldGenerator(currSeed);
    }
    private void completeActions(char character) {
        char c = Character.toLowerCase(character);
        pressed += c;
        if (inMatch) {
            switch (c) {
                case ('w') -> moveDirection('w');
                case ('a') -> moveDirection('a');
                case ('s') -> moveDirection('s');
                case ('d') -> moveDirection('d');
                case ('n') -> {
                    stage = 1;
                    displaySeed();
                }
                case ('q') -> {
                    int pressedSecondLast = pressed.length() - 2;
                    if (pressed.charAt(pressedSecondLast) == ':') {
                        inMatch = false;
                        sQ();
                        System.exit(0);
                    }
                }
                case ('i') -> toggleLight();
                default -> {

                }
            }
        } else if (!inMatch) {
            switch (c) {
                case ('n') -> displaySeed();
                case ('l') -> load();
                case ('q') -> System.exit(0);
                default -> {

                }
            }
        }
    }

    private TETile[][] completeActionsString(char character) {
        char c = Character.toLowerCase(character);
        pressed += c;
        if (inMatch) {
            switch (c) {
                case ('w') -> moveDirection('w');
                case ('a') -> moveDirection('a');
                case ('s') -> moveDirection('s');
                case ('d') -> moveDirection('d');
                case ('n') -> {
                    stage = 1;
                    displaySeed();
                }
                case ('q') -> {
                    int pressedSecondLast = pressed.length() - 2;
                    if (pressed.charAt(pressedSecondLast) == ':') {
                        sQ();
                        return world;
                    }
                }
                case ('i') -> toggleLight();
                default -> {

                }
            }
        } else if (!inMatch) {
            switch (c) {
                case ('n') -> displaySeed();
                case ('l') -> load();
                case ('q') -> {
                    return world;
                }
                default -> {

                }
            }
        }
        return world;
    }

    private void displaySeed() {
        StdDraw.clear(Color.BLACK);
        boolean run = true;
        StdDraw.text(midW, HEIGHT / FACTOR, "Enter a seed:");
        StdDraw.show();
        StringBuilder seedString = new StringBuilder();
        while (run) {
            if (StdDraw.hasNextKeyTyped()) {
                char currTyped = StdDraw.nextKeyTyped();
                saveString += currTyped;
                char currLowered = toLowerCase(currTyped);
                if (currLowered == 's') {
                    String sString = seedString.toString();
                    long currSeed = Long.parseLong(sString);
                    worldGenerator(currSeed);
                    break;
                }
                seedString.append(currTyped);
                displaySeedHelper(seedString.toString());
            }
        }
    }
    private void displaySeedHelper(String str) {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(midW, HEIGHT / FACTOR, "Enter a seed:");
        StdDraw.text(midW, midH, str);
        StdDraw.show();
    }

    private void worldGenerator(long s) {
        r = new Random(s);
        emptyWorld();
        rooms = WorldGeneration.worldGen(world, r);
        worldGeneratorHelper();
        ter.renderFrame(world);
    }

    private void worldGeneratorHelper() {
        findPlayer();
        makeBulbs();
        makeToks(3);
        displayLevel();
        inMatch = true;
    }
    private void emptyWorld() {
        world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    private void displayLevel() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(midW, midH, "Level " + stage);
        StdDraw.show();
        StdDraw.pause(PAUSE_LEN);
    }
    private void mainMenu() {
        StdDraw.clear(Color.BLACK);
        Font titleFont = new Font("Monaco", Font.BOLD, TITLEF);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(titleFont);
        StdDraw.text(midW, quarterH, "CS61B: The Game");
        Font bodyFont = new Font("Monaco", Font.BOLD, SUBF);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(bodyFont);
        StdDraw.text(midW, midH, "New Game (N)");
        StdDraw.text(midW, midH - 4, "Load (L)");
        StdDraw.text(midW, midH - 8, "Quit (Q)");
        StdDraw.show();
    }

    private void mouseTile(TETile tile) {
        StdDraw.setPenColor(Color.BLACK);

        ArrayList<TETile> possibleTiles = new ArrayList<>();
        possibleTiles.add(Tileset.AVATAR);
        possibleTiles.add(Tileset.WALL);
        possibleTiles.add(Tileset.FLOOR);
        possibleTiles.add(Tileset.NOTHING);
        possibleTiles.add(Tileset.LIGHT);
        possibleTiles.add(Tileset.LIGHT_BULB);

        mouseTileHelper(tile, possibleTiles);

        StdDraw.show();
    }

    private void mouseTileHelper(TETile inputTile, ArrayList<TETile> pTiles) {
        for (TETile tile : pTiles) {
            if (tile == inputTile) {
                continue;
            } else {
                StdDraw.text(4, HEIGHT - 1, tile.description());
            }
        }
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(4, HEIGHT - 1, inputTile.description());
    }
    private void moveDirection(char c) {
        int[] dir = directions.get(c);

        int currPLX = playerLoc.getX();
        int currPLY = playerLoc.getY();
        int nextPLX = playerLoc.getX() + dir[0];
        int nextPLY = playerLoc.getY() + dir[1];

        if (world[nextPLX][nextPLY] != Tileset.WALL) {
            Coordinate newLoc = new Coordinate(nextPLX, nextPLY);
            tokenLoc(nextPLX, nextPLY);
            if (numToks < 1) {
                stage++;
                long nextS = r.nextInt();
                worldGenerator(nextS);
                return;
            } else {
                moveDirectionHelper(currTile, currPLX, currPLY, nextPLX, nextPLY);
            }
            playerLoc = newLoc;
            ter.renderFrame(world);
        }
    }

    private void moveDirectionHelper(TETile tile, int currPLX, int currPLY, int nextPLX, int nextPLY) {
        currTile = world[nextPLX][nextPLY];
        world[currPLX][currPLY] = tile;
        if (world[currPLX][currPLY] == Tileset.GRASS) {
            world[currPLX][currPLY] = Tileset.FLOOR;
        }
        world[nextPLX][nextPLY] = Tileset.AVATAR;
    }

    private void findPlayer() {
        int currX = X_FINAL;
        int currY = Y_FINAL;
        int w = world.length;
        int h = world[0].length;
        for (int i = 0; i < w; i++) {
            Coordinate test = new Coordinate(currX, currY);
            if (playerLoc != null) {
                if (playerLoc.equals(test)) {
                    break;
                }
            }
            for (int j = 0; j < h; j++) {
                if (world[i][j] == Tileset.AVATAR) {
                    currX = i;
                    currY = j;
                    playerLoc = new Coordinate(i, j);
                    break;
                }
            }
        }
    }
    private int[] findPlayerHelper(int x, int y) {
        if (world[x][y] == Tileset.AVATAR) {
            playerLoc = new Coordinate(x, y);
        }
        return new int[]{x, y};
    }

    private void sQ() {
        try {
            inMatch = false;
            File dirPath = new File("./saveFile.txt");
            boolean e = dirPath.exists();
            if (e) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(dirPath, false));
                bw.write(saveString);
                bw.close();
            } else {
                boolean created = dirPath.createNewFile();
                if (!created) {
                    throw new IllegalArgumentException();
                }
                Files.writeString(dirPath.toPath(), saveString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() {
        try {
            File dirPath = new File("./saveFile.txt");
            boolean e = dirPath.exists();
            if (!e) {
                System.exit(0);
            } else {
                String dirPathPath = dirPath.getPath();
                Path currPath = Paths.get(dirPathPath);
                byte[] allBytes = Files.readAllBytes(currPath);
                String allBytesToStr = new String(allBytes);
                String content = allBytesToStr;
                StringBuilder interact = new StringBuilder();

                for (int i = 0; i < content.length(); i++) {
                    if (content.charAt(i) == 'l'  content.charAt(i) == ':'  content.charAt(i) == 'q') {
                        continue;
                    }
                    char currChar = content.charAt(i);
                    char c = Character.toUpperCase(currChar);
                    interact.append(c);
                }
                String interToStr = interact.toString();
                interactWithInputString(interToStr);
                inMatch = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private TETile[][] loadForString() {
        try {
            File dirPath = new File("./saveFile.txt");
            boolean e = dirPath.exists();
            if (!e) {
                return world;
            } else {
                FileReader reader = new FileReader("./saveFile.txt");
                ArrayList<Character> inputs = new ArrayList<>();
                int q;
                while ((q = reader.read()) != -1) {
                    inputs.add((char) q);
                }

                StringBuilder interact = new StringBuilder();

                for (char c : inputs) {
                    if (c == 'l'  c == ':'  c == 'q') {
                        continue;
                    }
                    char currChar = c;
                    char ch = Character.toUpperCase(currChar);
                    interact.append(ch);
                }
                String interToStr = interact.toString();
                interactWithInputString(interToStr);
                inMatch = true;
                return world;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeBulbs() {
        bulbLoc.clear();
        for (WorldGeneration.Room rm : rooms) {
            int rX = rm.startCoor.getX();
            int rY = rm.startCoor.getY();
            rX++;
            rY = rY + 2;
            if (rX < WIDTH && rY < HEIGHT) {
                if (world[rX][rY] == Tileset.FLOOR) {
                    world[rX][rY] = Tileset.LIGHT_BULB;
                    bulbLoc.add(new Coordinate(rX, rY));
                }
            }
        }
        for (Coordinate pos : bulbLoc) {
            int rX = pos.getX() - 2;
            int rY = pos.getY() - 2;
            int rXP = pos.getX() + 2;
            int rYP = pos.getY() + 2;
            for (int i = rX; i <= rXP; i++) {
                for (int j = rY; j <= rYP; j++) {
                    turnOn(i, j);
                }
            }
        }
        toggled = true;
    }
    private void toggleLight() {
        if (toggled) {
            for (Coordinate pos : bulbLoc) {
                int rX = pos.getX() - 2;
                int rY = pos.getY() - 2;
                int rXP = pos.getX() + 2;
                int rYP = pos.getY() + 2;
                for (int i = rX; i <= rXP; i++) {
                    for (int j = rY; j <= rYP; j++) {
                        turnOff(i, j);
                    }
                }
            }
            toggled = false;
        } else {
            for (Coordinate pos : bulbLoc) {
                int rX = pos.getX() - 2;
                int rY = pos.getY() - 2;
                int rXP = pos.getX() + 2;
                int rYP = pos.getY() + 2;
                for (int i = rX; i <= rXP; i++) {
                    for (int j = rY; j <= rYP; j++) {
                        turnOn(i, j);
                    }
                }
            }
            toggled = true;
        }

        ter.renderFrame(world);
    }
    private void makeToks(int t) {
        numToks = t;
        int counter = 0;
        while (counter < numToks) {
            int tokX = r.nextInt(WIDTH);
            int tokY = r.nextInt(HEIGHT);
            if (world[tokX][tokY] == Tileset.FLOOR) {
                world[tokX][tokY] = Tileset.GRASS;
                counter++;
            }
        }
    }

    private void tokenLoc(int i, int j) {
        if (world[i][j] == Tileset.GRASS) {
            numToks = numToks - 1;
        }
    }

    private void turnOn(int x, int y) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && world[x][y] == Tileset.FLOOR) {
            world[x][y] = Tileset.LIGHT;
        }
    }

    private void turnOff(int x, int y) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && world[x][y] == Tileset.LIGHT) {
            world[x][y] = Tileset.FLOOR;
        }
    }

}



