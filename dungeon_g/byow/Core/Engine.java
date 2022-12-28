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

import static java.lang.Character.TYPE;
import static java.lang.Character.toLowerCase;

public class Engine {
    static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 81;
    public static final int HEIGHT = 31;
    private boolean inGame = false;
    private TETile[][] world;
    private Random RANDOM;
    private long seed;
    private Point avatarLocation;
    private StringBuilder keyboardInputs = new StringBuilder();
    private String currSave = "";
    private int numOfTokens;
    private int level = 1;
    private List<Room> worldRooms;
    private TETile lightSource = Tileset.LIGHT_BULB;
    private TETile prevTile;
    private TETile light = Tileset.WHITE;
    private ArrayList<Point> lightSLocations = new ArrayList<>();
    private boolean lightOn = false;

    public Engine() {
        ter.initialize(WIDTH, HEIGHT);
    }



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        homeScreen();
        while (true) {
            if (inGame) {
                int mouseXCoor = Math.floorDiv((int) StdDraw.mouseX(), 1);
                int mouseYCoor = Math.floorDiv((int) StdDraw.mouseY(), 1);
                Point mousePoint = new Point(mouseXCoor, mouseYCoor);
                if (mousePoint.getX() >= 0 && mousePoint.getX() < WIDTH && mousePoint.getY() >= 0 && mousePoint.getY() < HEIGHT) {
                    TETile mouseTile = world[mouseXCoor][mouseYCoor];
                    tileMouseIsOver(mouseTile);
                }
            }
            if (StdDraw.hasNextKeyTyped()) {
                char keyPressed = StdDraw.nextKeyTyped();
                File path = new File("/Users/saikolasani/cs61b/fa22-proj3-g438/proj3/byow/Core/saves.txt");
                if (keyPressed == 'n') {
                    if (path.exists()) {
                        path.delete();
                    }
                }
                currSave += keyPressed;
                allActions(keyPressed);
            }
        }
    }

    private void tileMouseIsOver(TETile tile) {
        StdDraw.setPenColor(Color.BLACK);
        if (tile == Tileset.AVATAR) {
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.WALL.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.FLOOR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.NOTHING.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light source");
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.AVATAR.description());
        } else if (tile == Tileset.WALL) {
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.AVATAR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.FLOOR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.NOTHING.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light source");
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.WALL.description());
        } else if (tile == Tileset.FLOOR) {
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.WALL.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.AVATAR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.NOTHING.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light source");
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.FLOOR.description());
        } else if (tile == lightSource) {
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.WALL.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.AVATAR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.NOTHING.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light source");
        } else if (tile == light) {
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.WALL.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.AVATAR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.NOTHING.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light source");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light");
        } else {
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.WALL.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.FLOOR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.AVATAR.description());
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light source");
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, "light");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - (WIDTH - 4), HEIGHT - 1, Tileset.NOTHING.description());
        }
        StdDraw.show();
    }

    public void homeScreen() {
        StdDraw.clear(Color.BLACK);
        drawTitle();
        drawNewGame();
        drawLoad();
        drawQuit();
        StdDraw.show();
    }

    private void drawTitle() {
        int middleOfScreenX = WIDTH / 2;
        int quarterOfScreenY = HEIGHT / 4 * 3;
        Font titleFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(titleFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(middleOfScreenX, quarterOfScreenY, "CS61B: The Game");
    }

    private void drawNewGame() {
        int middleOfScreenX = WIDTH / 2;
        int middleOfScreenY = HEIGHT / 2;
        Font bodyFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(bodyFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(middleOfScreenX, middleOfScreenY, "New Game (N)");
    }

    private void drawLoad() {
        int middleOfScreenX = WIDTH / 2;
        int middleOfScreenY = HEIGHT / 2;
        Font bodyFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(bodyFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(middleOfScreenX, middleOfScreenY - 1, "Load (L)");
    }

    private void drawQuit() {
        int middleOfScreenX = WIDTH / 2;
        int middleOfScreenY = HEIGHT / 2;
        Font bodyFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(bodyFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(middleOfScreenX, middleOfScreenY - 2, "Quit (Q)");
    }

    private void allActions(char c) throws IOException {
        char loweredChar = toLowerCase(c);
        keyboardInputs.append(loweredChar);
        if (inGame) {
            switch (loweredChar) {
                case ('w'):
                    moveUp();
                    break;
                case ('a'):
                    moveLeft();
                    break;
                case ('s'):
                    moveDown();
                    break;
                case ('d'):
                    moveRight();
                    break;
                case ('q'):
                    if (keyboardInputs.charAt(keyboardInputs.length() - 2) == ':') {
                        saveAndQuit();
                        System.exit(0);
                    }
                    break;
                case ('n'):
                    level = 1;
                    seedScreen();
                    break;
                case ('o'):
                    turnLightOnAndOff();
                    break;
            }
        } else if (!inGame) {
            switch (loweredChar) {
                case ('n'):
                    seedScreen();
                    break;
                case ('l'):
                    loadState();
                    break;
                case ('q'):
                    System.exit(0);
                    break;
            }
        }
    }

    private void turnLightOnAndOff() {
        if (lightOn) {
            dimLight(lightSLocations);
        } else {
            emitLight(lightSLocations);
        }
        ter.renderFrame(world);
    }

    private void createLightSources() {
        lightSLocations.clear();
        for (Room rm : worldRooms) {
            if (rm.startingPoint.getX() + 1 < WIDTH && rm.startingPoint.getY() + 2 < HEIGHT) {
                if (world[rm.startingPoint.getX() + 1][rm.startingPoint.getY() + 2] == Tileset.FLOOR) {
                    int lightX = rm.startingPoint.getX() + 1;
                    int lightY = rm.startingPoint.getY() + 2;
                    world[lightX][lightY] = lightSource;
                    Point lightLoc = new Point(lightX, lightY);
                    lightSLocations.add(lightLoc);
                }
            }
        }
        emitLight(lightSLocations);
    }

    private void emitLight(ArrayList<Point> lights) {
        for (Point lightS : lights) {
            for (int i = lightS.getX() - 2; i <= lightS.getX() + 2; i++) {
                for (int j = lightS.getY() - 2; j <= lightS.getY() + 2; j++) {
                    if (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT && world[i][j] == Tileset.FLOOR) {
                        world[i][j] = light;
                    }

                }
            }
        }
        lightOn = true;
    }

    private void dimLight(ArrayList<Point> lights) {
        for (Point lightS : lights) {
            for (int i = lightS.getX() - 2; i <= lightS.getX() + 2; i++) {
                for (int j = lightS.getY() - 2; j <= lightS.getY() + 2; j++) {
                    if (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT && world[i][j] == light) {
                        world[i][j] = Tileset.FLOOR;
                    }
                }
            }
        }
        lightOn = false;
    }

    private void seedScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT / 1.5, "Please enter a seed number:");
        StdDraw.show();
        String seedString = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char nextKey = StdDraw.nextKeyTyped();
                currSave += nextKey;
                if (toLowerCase(nextKey) == 's') {
                    seed = Long.parseLong(seedString);
                    newWorldGenerator(seed);
                    break;
                }
                seedString += nextKey;
                StdDraw.clear(Color.BLACK);
                StdDraw.text(WIDTH / 2, HEIGHT / 1.5, "Please enter a seed number:");
                StdDraw.text(WIDTH / 2, HEIGHT / 2, seedString);
                StdDraw.show();
            }
        }
    }

    private void newWorldGenerator(long seed) {
        RANDOM = new Random(seed);
        fillWorldWithNothing(WIDTH, HEIGHT);
        worldRooms = WorldGenerator.worldGenerator(RANDOM, world);
        findAvatarLocation(-1, -1);
        createLightSources();
        makeTokens(5);
        showLevelScreen();
        inGame = true;
        ter.renderFrame(world);
    }

    private void fillWorldWithNothing(int width, int height) {
        world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void findAvatarLocation(int initialX, int initialY) {
        for (int x = 0; x < world.length; x++) {
            if (avatarLocation != null && avatarLocation.equals(new Point(initialX, initialY))) {
                break;
            }
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.AVATAR) {
                    avatarLocation = new Point(x, y);
                    initialX = x;
                    initialY = y;
                    break;
                }
            }
        }
    }

    private void makeTokens(int tokens) {
        numOfTokens = tokens;
        while (tokens > 0) {
            int xCoor = RANDOM.nextInt(WIDTH);
            int yCoor = RANDOM.nextInt(HEIGHT);
            if (world[xCoor][yCoor] == Tileset.FLOOR) {
                world[xCoor][yCoor] = Tileset.FLOWER;
                tokens--;
            }
        }
    }

    private void showLevelScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.show();
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Level " + level);
        StdDraw.show();
        StdDraw.pause(1500);
    }

    private void saveAndQuit() throws IOException {
        inGame = false;
        System.out.println(currSave);
        File path = new File("./saves.txt");
        if (!path.exists()) {
            path.createNewFile();
            Files.writeString(path.toPath(), currSave);
        } else if (path.exists()) {
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(currSave);
            bw.close();
        }

    }

    private void loadState() throws IOException {
        File path = new File("./saves.txt");
        if (path.exists()) {
            String content = new String(Files.readAllBytes(Paths.get(path.getPath())));
            String interact = "";

            for (int i = 0; i < content.length(); i++) {
                if (content.charAt(i) == ':' || content.charAt(i) == 'q' || content.charAt(i) == 'l') {
                    continue;
                }
                interact += Character.toUpperCase(content.charAt(i));
            }
            System.out.println(interact);
            interactWithInputString(interact);
            inGame = true;



        } else {
            System.exit(0);
        }
    }

    private void checkIfTokenPresent(int x, int y) {
        if (world[x][y] == Tileset.FLOWER) {
            numOfTokens--;
        }
    }

    private void moveUp() {
        if (world[avatarLocation.getX()][avatarLocation.getY() + 1] != Tileset.WALL) {
            Point newAvLocation = new Point(avatarLocation.getX(), avatarLocation.getY() + 1);
            checkIfTokenPresent(avatarLocation.getX(), avatarLocation.getY() + 1);
            if (numOfTokens == 0) {
                level++;
                long newSeed = (long) RANDOM.nextInt();
                newWorldGenerator(newSeed);
            } else if (prevTile == lightSource) {
                prevTile = world[avatarLocation.getX()][avatarLocation.getY() + 1];
                world[avatarLocation.getX()][avatarLocation.getY()] = lightSource;
                world[avatarLocation.getX()][avatarLocation.getY() + 1] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else if (prevTile == light) {
                prevTile = world[avatarLocation.getX()][avatarLocation.getY() + 1];
                world[avatarLocation.getX()][avatarLocation.getY()] = light;
                world[avatarLocation.getX()][avatarLocation.getY() + 1] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else {
                prevTile = world[avatarLocation.getX()][avatarLocation.getY() + 1];
                world[avatarLocation.getX()][avatarLocation.getY()] = Tileset.FLOOR;
                world[avatarLocation.getX()][avatarLocation.getY() + 1] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            }
        }
    }

    private void moveDown() {
        if (world[avatarLocation.getX()][avatarLocation.getY() - 1] != Tileset.WALL) {
            Point newAvLocation = new Point(avatarLocation.getX(), avatarLocation.getY() - 1);
            checkIfTokenPresent(avatarLocation.getX(), avatarLocation.getY() - 1);
            if (numOfTokens == 0) {
                level++;
                long newSeed = (long) RANDOM.nextInt();
                newWorldGenerator(newSeed);
            } else if (prevTile == lightSource) {
                prevTile = world[avatarLocation.getX()][avatarLocation.getY() - 1];
                world[avatarLocation.getX()][avatarLocation.getY()] = lightSource;
                world[avatarLocation.getX()][avatarLocation.getY() - 1] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else if (prevTile == light) {
                prevTile = world[avatarLocation.getX()][avatarLocation.getY() - 1];
                world[avatarLocation.getX()][avatarLocation.getY()] = light;
                world[avatarLocation.getX()][avatarLocation.getY() - 1] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else {
                prevTile = world[avatarLocation.getX()][avatarLocation.getY() - 1];
                world[avatarLocation.getX()][avatarLocation.getY()] = Tileset.FLOOR;
                world[avatarLocation.getX()][avatarLocation.getY() - 1] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            }
        }
    }

    private void moveLeft() {
        if (world[avatarLocation.getX() - 1][avatarLocation.getY()] != Tileset.WALL) {
            Point newAvLocation = new Point(avatarLocation.getX() - 1, avatarLocation.getY());
            checkIfTokenPresent(avatarLocation.getX() - 1, avatarLocation.getY());
            if (numOfTokens == 0) {
                level++;
                long newSeed = (long) RANDOM.nextInt();
                newWorldGenerator(newSeed);
            } else if (prevTile == lightSource) {
                prevTile = world[avatarLocation.getX() - 1][avatarLocation.getY()];
                world[avatarLocation.getX()][avatarLocation.getY()] = lightSource;
                world[avatarLocation.getX() - 1][avatarLocation.getY()] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else if (prevTile == light) {
                prevTile = world[avatarLocation.getX() - 1][avatarLocation.getY()];
                world[avatarLocation.getX()][avatarLocation.getY()] = light;
                world[avatarLocation.getX() - 1][avatarLocation.getY()] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else {
                prevTile = world[avatarLocation.getX() - 1][avatarLocation.getY()];
                world[avatarLocation.getX()][avatarLocation.getY()] = Tileset.FLOOR;
                world[avatarLocation.getX() - 1][avatarLocation.getY()] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            }
        }
    }

    private void moveRight() {
        if (world[avatarLocation.getX() + 1][avatarLocation.getY()] != Tileset.WALL) {
            Point newAvLocation = new Point(avatarLocation.getX() + 1, avatarLocation.getY());
            checkIfTokenPresent(avatarLocation.getX() + 1, avatarLocation.getY());
            if (numOfTokens == 0) {
                level++;
                long newSeed = (long) RANDOM.nextInt();
                newWorldGenerator(newSeed);
            } else if (prevTile == lightSource) {
                prevTile = world[avatarLocation.getX() + 1][avatarLocation.getY()];
                world[avatarLocation.getX()][avatarLocation.getY()] = lightSource;
                world[avatarLocation.getX() + 1][avatarLocation.getY()] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else if (prevTile == light) {
                prevTile = world[avatarLocation.getX() + 1][avatarLocation.getY()];
                world[avatarLocation.getX()][avatarLocation.getY()] = light;
                world[avatarLocation.getX() + 1][avatarLocation.getY()] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            } else {
                prevTile = world[avatarLocation.getX() + 1][avatarLocation.getY()];
                world[avatarLocation.getX()][avatarLocation.getY()] = Tileset.FLOOR;
                world[avatarLocation.getX() + 1][avatarLocation.getY()] = Tileset.AVATAR;
                avatarLocation = newAvLocation;
                ter.renderFrame(world);
            }
        }
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
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) throws IOException {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.


        while (!input.equals("") && !(input.charAt(0) =='N' || input.charAt(0) == 'L')) {
            input = input.substring(1);
        }
        if (input.equals("")) {
            throw new IllegalArgumentException();
        } else if (input.charAt(0) == 'N') {
            StringBuilder seedBuilder = new StringBuilder();
            input = input.substring(1);
            while (input.charAt(0) != 'S') {
                seedBuilder.append(input.charAt(0));
                input = input.substring(1);
            }
            input = input.substring(1);
            newWorldGenerator(Long.parseLong(seedBuilder.toString()));
        } else if (input.charAt(0) == 'L') {
            loadState();
        }

        while (!input.equals("")) {
            allActions(input.charAt(0));
            input = input.substring(1);
        }

        return world;

    }




}
