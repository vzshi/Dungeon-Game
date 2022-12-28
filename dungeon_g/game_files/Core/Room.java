package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Room {

    private int height;
    private int width;
    public Point startingPoint;

    public Room(int height, int width, Point point) {
        this.height = height;
        this.width = width;
        this.startingPoint = point;
    }


    //Create a random room with startingPoint, width and height.
    private static Room createRandomRoom(Random rand, TETile[][] world) {
        // Heights and widths are contained in the set {4, 7, 10}.
        // Can change the sizes later if we want smaller rooms.
        // But we want values that differ by a lot to make rectangles.
        int height = (3 * rand.nextInt(3)) + 4;
        int width = (3 * rand.nextInt(3)) + 4;

        // Randomly choose the start point of the rooms in any part of the world.
        int x = rand.nextInt(world.length - 1);
        int y = rand.nextInt(world[0].length - 1);
        Point newStartingP = new Point(x, y);

        return new Room(height, width, newStartingP);
    }


    // Helper method to check if a point in contained in the room
    // Extended the boundary to one less than the starting point and one more than the far corners to return yes if two rooms touch
    private boolean containsOrTouch(Point point) {
        if (point.getX() >= startingPoint.getX() - 1 && point.getX() <= startingPoint.getX() + width + 1 ) {
            if (point.getY() >= startingPoint.getY() - 1 && point.getY() <= startingPoint.getY() + height + 1 ) {
                return true;
            }
        }
        return false;
    }


    // Checks to see if two rooms are overlapping.
    // We can do this by checking if any of the four points of a room are contained in the current room.
    private boolean overlapRoom(Room room) {
        if (containsOrTouch(room.startingPoint)) {
            return true;
        }
        if (containsOrTouch(new Point(room.startingPoint.getX(),room.startingPoint.getY()+ room.height - 1))) {
            return true;
        }
        if (containsOrTouch(new Point(room.startingPoint.getX()+ room.width - 1, room.startingPoint.getY()))) {
            return true;
        }
        if (containsOrTouch(new Point(room.startingPoint.getX()+ room.width - 1, room.startingPoint.getY()+ room.height - 1))) {
            return true;
        }
        return false;
    }


    // Removes overlapping rooms.
    private static void removeOverlappingAndEmptyRooms(List<Room> rooms, TETile[][] world) {
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i+1; j < rooms.size(); j++) {
                if (rooms.get(i).overlapRoom(rooms.get(j)) || rooms.get(j).overlapRoom(rooms.get(i))) {
                    rooms.remove(j);
                    j--;
                }
                // Removes rooms that have empty floors
                if (rooms.get(j).startingPoint.getY() >= world[0].length - 3 || rooms.get(j).startingPoint.getX() >= world.length - 3) {
                    rooms.remove(j);
                    j--;
                }
                if (rooms.get(j).startingPoint.getY()+rooms.get(j).height >= world[0].length) {
                    rooms.remove(j);
                    j--;
                }
            }
        }
    }


    // Display the rooms in the world.
    private void printRoom(TETile[][] world) {

        // Need to make sure that width is in bounds
        if (startingPoint.getX() + width - 1 >= world.length - 1) {
            width = world.length - startingPoint.getX();
        }
        // Adjust height so that it is in bounds if necessary
        if (startingPoint.getY() + height - 1 >= world[0].length - 1) {
            height = world[0].length - startingPoint.getY();
        }

        // Print left vertical wall.
        for (int i = 0; i < height; i++) {
            world[startingPoint.getX()][startingPoint.getY() + i] = Tileset.WALL;
        }

        // Print bottom horizontal wall.
        for (int i = 0; i < width; i++) {
            world[startingPoint.getX() + i][startingPoint.getY()] = Tileset.WALL;
        }
        // Print top horizontal wall.
        for (int i = 0; i < width; i++) {
            world[startingPoint.getX() + i][startingPoint.getY() + height - 1] = Tileset.WALL;
        }
        // Print right vertical wall.
        for (int i = 0; i < height; i++) {
            world[startingPoint.getX() + width - 1][startingPoint.getY() + i] = Tileset.WALL;

        }
        // Print floor.
        for (int i = 1 + startingPoint.getX(); i < startingPoint.getX() + width - 1; i++) {
            for (int j = 1 + startingPoint.getY(); j < startingPoint.getY() + height - 1; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }

    }

    //Returns the midpoint of a room
    private Point midPoint() {
        int middleX = startingPoint.getX() + ((width - 1)/2);
        int middleY = startingPoint.getY() + ((height - 1)/2);
        Point newPoint = new Point(middleX, middleY);
        return newPoint;
    }
    // Creates a horizontalHallway between two rooms using their midpoints
    private void horizontalHallway(int x1, int x2, int y, TETile[][] world) {
        int minx = min(x1, x2);
        int maxx = max(x1, x2);
        for (int i = minx; i <= maxx; i++) {
            world[i][y] = Tileset.FLOOR;

            if (world[i][y+1] == Tileset.NOTHING) {
                world[i][y+1] = Tileset.WALL;

            }
            if (world[i][y-1] == Tileset.NOTHING) {
                world[i][y-1] = Tileset.WALL;
            }
        }

        // Fixes edge cases where there is a missing tile
        /** eg.
         *          @@@@@@!
         *          @@@@@@@
         *
         * In this case the '!' tile would be made a @
         */

        if (world[maxx+1][y+1] == Tileset.NOTHING) {
            world[maxx+1][y+1] = Tileset.WALL;
        }
        if (world[maxx+1][y-1] == Tileset.NOTHING) {
            world[maxx+1][y-1] = Tileset.WALL;
        }

        if (world[minx-1][y+1] == Tileset.NOTHING) {
            world[minx-1][y+1] = Tileset.WALL;
        }
        if (world[minx-1][y-1] == Tileset.NOTHING) {
            world[minx-1][y-1] = Tileset.WALL;
        }
    }

    // Creates a verticalHallway between two rooms using their midpoints
    private void verticalHallway(int y1, int y2, int x, TETile[][] world) {
        int miny = min(y1, y2);
        int maxy = max(y1, y2);
        for (int i = miny; i <= maxy; i++) {
            world[x][i] = Tileset.FLOOR;
            if (world[x+1][i] == Tileset.NOTHING) {
                world[x+1][i] = Tileset.WALL;

            }
            if (world[x-1][i] == Tileset.NOTHING) {
                world[x-1][i] = Tileset.WALL;
            }
        }

    }

    private void connectRooms(int x1, int x2, int y1, int y2, TETile[][] world) {
        horizontalHallway(x1, x2, y1, world);
        verticalHallway(y1, y2, x1, world);
    }


    // Randomly make the rooms and display them on the world.
    public static List<Room> randomRoomGenerator(Random rand, TETile[][] world) {
        int numRooms = 30 + rand.nextInt(world.length);
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            rooms.add(createRandomRoom(rand, world));
        }
        removeOverlappingAndEmptyRooms(rooms, world);
        for (Room r : rooms) {
            r.printRoom(world);
        }

        //Creates hallways between two adjacent rooms in the list;
        for (int i = 0; i < rooms.size()-1; i++) {
            Point currMid = rooms.get(i).midPoint();
            Point nextMid = rooms.get(i+1).midPoint();
            rooms.get(i).horizontalHallway(currMid.getX(), nextMid.getX(), currMid.getY(), world);
            rooms.get(i).verticalHallway(currMid.getY(), nextMid.getY(), nextMid.getX(), world);
        }

        return rooms;

    }






}
