package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class WorldGeneration {

    public static List<Room> worldGen(TETile[][] world, Random r) {
        emptyWorld(world);
        List<Room> rooms = Room.randomRooms(world, r);
        int w = world.length;
        int h = world[0].length;
        int tracker = -5;
        for (int i = 1; i < w - 1; i++) {
            if (tracker > -5) {
                break;
            }
            for (int j = 1; j < h - 1; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    world[i][j] = Tileset.AVATAR;
                    tracker = i;
                    break;
                }
            }
        }

        return rooms;
    }

    private static void emptyWorld(TETile[][] world) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }
    public static void main(String[] args) throws IOException {
        Engine engine = new Engine();
        engine.interactWithInputString("N999SDDD:Q");
        engine.interactWithInputString("L:Q");
        engine.interactWithInputString("L:Q");
        engine.interactWithInputString("LWWWDDD");
    }

    public static class Room {
        private int width;
        private int height;
        public Coordinate startCoor;

        public Room(int widthS, int heightS, Coordinate startC) {
            width = widthS;
            height = heightS;
            startCoor = startC;
        }
    }

    public static Room createRoom(TETile[][] world, Random r) {
        int w = 5 * r.nextInt(3) + 7;
        int h = 5 * r.nextInt(3) + 7;

        Coordinate c = new Coordinate(r.nextInt(world.length - 1), r.nextInt(world[0].length - 6));

        return new Room(w, h, c);
    }

    private Coordinate midCoor() {
        return new Coordinate(startCoor.getX() + ((this.width - 1) / 2), startCoor.getY() + ((this.height - 1) / 2));
    }

    private boolean intersect(Coordinate c) {
        return c.getX() >= startCoor.getX() - 1 && c.getX() <= startCoor.getX() + width + 1 && c.getY() >= startCoor.getY() - 1 && c.getY() <= startCoor.getY() + height + 1;
    }

    private boolean overlap(Room r) {
        return intersect(r.startCoor)  intersect(new Coordinate(r.startCoor.getX(), r.startCoor.getY() + r.height - 1))
        intersect(new Coordinate(r.startCoor.getX() + r.width - 1, r.startCoor.getY())) ||
                intersect(new Coordinate(r.startCoor.getX() + r.width - 1, r.startCoor.getY() + r.height - 1));
    }
    private static void removeOverlap(List<Room> rooms) {
        for (int x = 0; x < rooms.size(); x++) {
            for (int y = x + 1; y < rooms.size(); y++) {
                if (rooms.get(x).overlap(rooms.get(y)) || rooms.get(y).overlap(rooms.get(x))) {
                    rooms.remove(y);
                    y--;
                }
            }
        }
    }

    private static void removeEmptyFloors(TETile[][] world, List<Room> rooms, int y) {
        return;
    }

    private void generateRoom(TETile[][] world) {
        if (startCoor.getX() + width - 1 >= world.length - 1) {
            width = world.length - startCoor.getX();
        }
        if (startCoor.getY() + height - 1 >= world[0].length - 1) {
            height = world[0].length - startCoor.getY();
        }

        westWall(world);
        southWall(world);
        northWall(world);
        eastWall(world);
        printFloor(world);
    }

    private void westWall(TETile[][] world) {
        for (int i = 0; i < height; i++) {
            world[startCoor.getX()][startCoor.getY() + i] = Tileset.WALL;
        }
    }

    private void southWall(TETile[][] world) {
        for (int j = 0; j < width; j++) {
            world[startCoor.getX() + j][startCoor.getY()] = Tileset.WALL;
        }
    }

    private void northWall(TETile[][] world) {
        for (int k = 0; k < width; k++) {
            world[startCoor.getX() + k][startCoor.getY() + height - 1] = Tileset.WALL;
        }
    }

    private void eastWall(TETile[][] world) {
        for (int l = 0; l < height; l++) {
            world[startCoor.getX() + width - 1][startCoor.getY() + l] = Tileset.WALL;
        }
    }

    private void printFloor(TETile[][] world) {
        for (int i = 1 + startCoor.getX(); i < startCoor.getX() + width - 1; i++) {
            for (int j = 1 + startCoor.getY(); j < startCoor.getY() + height - 1; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
    }
    private void hHallway(TETile[][] world, int x1, int x2, int y) {
        int minX = min(x1, x2);
        int maxX = max(x1, x2);

        makeHHallway(world, minX, maxX, y);
        hHallwayEdgeCases(world, minX, maxX, y);
    }

    private void makeHHallway(TETile[][] world, int min, int max, int y) {
        for (int i = min; i <= max; i++) {
            world[i][y] = Tileset.FLOOR;

            if (world[i][y + 1] == Tileset.NOTHING) {
                world[i][y + 1] = Tileset.WALL;

            }
            if (world[i][y - 1] == Tileset.NOTHING) {
                world[i][y - 1] = Tileset.WALL;
            }
        }
    }

    private void hHallwayEdgeCases(TETile[][] world, int min, int max, int y) {
        if (world[max + 1][y + 1] == Tileset.NOTHING) {
            world[max + 1][y + 1] = Tileset.WALL;
        }
        if (world[max + 1][y - 1] == Tileset.NOTHING) {
            world[max + 1][y - 1] = Tileset.WALL;
        }

        if (world[min - 1][y + 1] == Tileset.NOTHING) {
            world[min - 1][y + 1] = Tileset.WALL;
        }
        if (world[min - 1][y - 1] == Tileset.NOTHING) {
            world[min - 1][y - 1] = Tileset.WALL;
        }
    }

    private void vHallway(TETile[][] world, int y1, int y2, int x) {
        int minY = min(y1, y2);
        int maxY = max(y1, y2);

        makeVHallway(world, minY, maxY, x);
    }

    private void makeVHallway(TETile[][] world, int min, int max, int x) {
        for (int i = min; i <= max; i++) {
            world[x][i] = Tileset.FLOOR;
            if (world[x + 1][i] == Tileset.NOTHING) {
                world[x + 1][i] = Tileset.WALL;

            }
            if (world[x - 1][i] == Tileset.NOTHING) {
                world[x - 1][i] = Tileset.WALL;
            }
        }
    }

    public static List<Room> randomRooms(TETile[][] world, Random r) {
        int minRooms = 30;
        int randomInt = r.nextInt(world.length);
        int numRooms = minRooms + randomInt;
        List<Room> rooms = new ArrayList<>();

        for (int i = 0; i < numRooms; i++) {
            Room currRoom = createRoom(world, r);
            rooms.add(currRoom);
        }

        removeOverlap(rooms);

        for (Room room : rooms) {
            room.generateRoom(world);
        }

        int maxNum = rooms.size() - 1;
        for (int i = 0; i < maxNum; i++) {
            Coordinate currMid = rooms.get(i).midCoor();
            int nextR = i + 1;
            Coordinate nextMid = rooms.get(nextR).midCoor();
            int cMX = currMid.getX();
            int cMY = currMid.getY();
            int nMX = nextMid.getX();
            int nMY = nextMid.getY();
            rooms.get(i).hHallway(world, cMX, nMX, cMY);
            rooms.get(i).vHallway(world, cMY, nMY, nMX);
        }

        return rooms;
    }

}
