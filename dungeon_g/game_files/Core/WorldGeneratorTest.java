package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class WorldGeneratorTest {

    public static List<Room> worldGenerator(Random rand, TETile[][] world) {
        fillBoardWithNothing(world);
        List<Room> worldRooms = Room.randomRoomGenerator(rand, world);
        int newX = -1;
        int newY = -1;
        for (int x = 0; x < world.length; x++) {
            if ((newX > 0 && newY > 0) && world[newX][newY] == Tileset.AVATAR) {
                break;
            }
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    world[x][y] = Tileset.AVATAR;
                    newX = x;
                    newY = y;
                    break;
                }
            }
        }
        return worldRooms;
    }



    private static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }


    public static void main(String[] args) throws IOException {
        Engine engine = new Engine();
        engine.interactWithInputString("N134SDDDDDDDDDDDDDDDDDDDDDDDDDDDA");
    }

}
