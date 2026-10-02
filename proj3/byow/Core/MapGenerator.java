package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

import java.util.*;

public class MapGenerator {

    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static final int MAX_ROOMS = 60;
    private long seed;
    private String movement;
    private Random random;
    private Avatar avatar;
    boolean hasKey;
    boolean gameOver = false;
    private List<Room> rooms = new ArrayList<>();

    TETile[][] map = new TETile[WIDTH][HEIGHT];
    // Tolerance for coordinate matching
    private static final double EPSILON = .0001;


    public MapGenerator(String seedInput) {
        clearMap();
        String[] splitArray = seedInput.split("(?<=\\d)(?=[A-Za-z])");
        seed = parseSeed(splitArray[0]);
        if (splitArray.length == 2) {
            movement = splitArray[1];
        } else {
            movement = "";
        }
        random = new Random(seed);
    }

    private long parseSeed(String seedString) {
        long seed = Long.parseLong(seedString);
        return seed;
    }

    public TETile[][] generateMap() {
        // creating up to MAX_ROOMS rooms
        for (int i = 0; i < MAX_ROOMS; i++) {
            createRoom(i);
        }

        // getting out multiLineStrings via Delaunay Triangulation
        MultiLineString triangulationEdges = getRoomConnections();

        // building a graph of our rooms and their connecting edges
        Map<Room, List<Edge>> roomAdjacencyList = buildGraph(triangulationEdges);

        // using our adjacency list to create MST for rooms/edges
        List<MasterEdge> mapHallways = generateFinalLayout(roomAdjacencyList);

        // use MST to carve hallways
        carveHallways(mapHallways);

        // place avatar,chest, and key at a random centroids on the map
        placeAvatar();
        placeChestAndKey();

        // Move avatar appropriately if movement was included in the seed
        moveAvatar();

        return map;
    }

    private void createRoom(int roomId) {
        int width = random.nextInt(6) + 1;
        int height = random.nextInt(6) + 1;
        int x = random.nextInt(WIDTH - width - 1) + 1;
        int y = random.nextInt(HEIGHT - height - 1) + 1;
        int id = roomId;

        Room newRoom = new Room(x, y, width, height, id);

        boolean overlap = false;
        for (Room other : rooms) {
            if (newRoom.intersects(other)) {
                overlap = true;
                break;
            }
        }

        if (!overlap) {
            carveRoom(newRoom);
            rooms.add(newRoom);
        }
    }

    private void carveRoom(Room room) {
        // Carving the tiles of each room with the floor tileset
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                map[x][y] = Tileset.FLOOR;
            }
        }

        // Carving the tiles around each room with the wall tileset
        for (int x = room.x - 1; x <= room.x + room.width; x++) {
            for (int y = room.y - 1; y <= room.y + room.height; y++) {
                if (map[x][y].character() != '·') {
                    map[x][y] = Tileset.WALL;
                }
            }
        }
    }

    private MultiLineString getRoomConnections() {
        // Initialize list of Coordinates and then populating it with the center coordinate for each room
        List<Coordinate> sites = new ArrayList<>();
        for (Room room : rooms) {
            sites.add(room.centroid);
        }

        // Initialize the JTS Delaunay Builder and feeding it the center point coordinates
        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(sites);

        // Extract the triangulation grid as a collection of lines (edges)
        GeometryFactory factory = new GeometryFactory();
        Geometry edges = builder.getEdges(factory);

        // Cast to MultiLineString in order to easily iterate over individual connections later
        if (edges instanceof MultiLineString) {
            return (MultiLineString) edges;
        }

        throw new IllegalStateException("Failed to generate a valid Triangulation edge network.");
    }

    private Map<Room, List<Edge>> buildGraph (MultiLineString triangulationEdges) {
        Map<Room, List<Edge>> roomAdjacencyList = new HashMap<>();

        // Initialize Adjacency list for every room
        for (Room room : rooms) {
            roomAdjacencyList.put(room, new ArrayList<>());
        }

        // Loop through each connection segment in the triangulation
        for (int i = 0; i < triangulationEdges.getNumGeometries(); i++) {
            Geometry geometry = triangulationEdges.getGeometryN(i);

            if (geometry instanceof LineString) {
                LineString line = (LineString) geometry;
                Coordinate p1 = line.getCoordinateN(0);
                Coordinate p2 = line.getCoordinateN(1);

                //Identify which rooms match the start and end coordinates
                Room roomA = findRoomByCoordinate(p1);
                Room roomB = findRoomByCoordinate(p2);

                if (roomA != null && roomB != null && roomA != roomB) {
                    double distance = p1.distance(p2);

                    //Add bidirectional graph entries
                    roomAdjacencyList.get(roomA).add(new Edge(roomB, distance));
                    roomAdjacencyList.get(roomB).add(new Edge(roomA, distance));
                }
            }
        }
        return roomAdjacencyList;
    }

    private Room findRoomByCoordinate(Coordinate coord) {
        for (Room room : rooms) {
            if (Math.abs(room.centroid.getX() - coord.getX()) < EPSILON &&
                    Math.abs(room.centroid.getY() - coord.getY()) < EPSILON) {
                return room;
            }
        }
        return null;
    }

    private List<MasterEdge> generateFinalLayout(Map<Room, List<Edge>> roomAdjacencyList) {
        List<MasterEdge> allEdges = extractUniqueEdges(roomAdjacencyList);
        List<MasterEdge> finalLayout = new ArrayList<>();

        // Sort all triangulation edges from shortest to longest
        Collections.sort(allEdges);

        // Initialize DSU for all rooms
        DisjointSet dsu = new DisjointSet();
        for (Room room : roomAdjacencyList.keySet()) {
            dsu.makeSet(room.id);
        }

        // Build MST using Kruskal's Core Loop
        for (MasterEdge edge : allEdges) {
            if (dsu.union(edge.u.id, edge.v.id)) {
                finalLayout.add(edge);
            }
        }

        return finalLayout;
    }

    private List<MasterEdge> extractUniqueEdges(Map<Room, List<Edge>> roomAdjacencyList) {
        List<MasterEdge> uniqueEdges = new ArrayList<>();
        Set<String> seenPairs = new HashSet<>();
        String pairId;

        for (Map.Entry<Room, List<Edge>> entry : roomAdjacencyList.entrySet()) {
            Room u = entry.getKey();
            for (Edge edge : entry.getValue()) {
                Room v = edge.target;

                // Track IDs lexicographically to skip the reverse direction of the edge
                if (u.id < v.id) {
                    pairId = u.id + "-" + v.id;
                } else {
                    pairId = v.id + "-" + u.id;
                }

                if (!seenPairs.contains(pairId)) {
                    seenPairs.add(pairId);
                    // Always store lower room id as u so hallway carving is deterministic.
                    if (u.id < v.id) {
                        uniqueEdges.add(new MasterEdge(u, v, edge.weight));
                    } else {
                        uniqueEdges.add(new MasterEdge(v, u, edge.weight));
                    }
                }
            }
        }
        return uniqueEdges;
    }

    private void carveHallways(List<MasterEdge> mapHallways) {
        for (MasterEdge edge : mapHallways) {
            int startX = (int) Math.round(edge.u.centroid.getX());
            int startY = (int) Math.round(edge.u.centroid.getY());
            int endX = (int) Math.round(edge.v.centroid.getX());
            int endY = (int) Math.round(edge.v.centroid.getY());

            carveHorizontal(startX, endX, startY);
            carveVertical(startY, endY, endX);
        }
    }

    private void carveHorizontal(int x1, int x2, int y) {
        int start = Math.min(x1, x2);
        int end = Math.max(x1, x2);

        for (int x = start; x <= end; x++) {
            map[x][y] = Tileset.FLOOR;
            if (map[x][y+1].character() != '·') {
                map[x][y+1] = Tileset.WALL;
            }
            if (map[x][y-1].character() != '·') {
                map[x][y-1] = Tileset.WALL;
            }
        }
    }

    private void carveVertical(int y1, int y2, int x) {
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);

        for (int y = start; y <= end; y++) {
            map[x][y] = Tileset.FLOOR;
            if (map[x+1][y].character() != '·') {
                map[x+1][y] = Tileset.WALL;
            }
            if (map[x-1][y].character() != '·') {
                map[x-1][y] = Tileset.WALL;
            }

        }
    }

    private void placeAvatar() {
        Room startingRoom = rooms.get(random.nextInt(rooms.size()));
        int startX = (int) Math.round(startingRoom.centroid.getX());
        int startY = (int) Math.round(startingRoom.centroid.getY());
        avatar = new Avatar(startX, startY);
        map[startX][startY] = Tileset.AVATAR;
    }

    private void placeChestAndKey() {
        Coordinate avatarPosition = avatar.getPosition();
        Coordinate chestPosition = rooms.get(random.nextInt(rooms.size())).centroid;
        Coordinate keyPosition = rooms.get(random.nextInt(rooms.size())).centroid;

        // placing chest in a room that is separate from the avatar
        while (Math.abs(avatarPosition.getX() - chestPosition.getX()) < EPSILON &&
                Math.abs(avatarPosition.getY() - chestPosition.getY()) < EPSILON) {
            chestPosition = rooms.get(random.nextInt(rooms.size())).centroid;
        }
        map[(int) chestPosition.getX()][(int) chestPosition.getY()] = Tileset.CLOSED_CHEST;

        // placing key in a room that is separate from the avatar and the chest
        while ((Math.abs(avatarPosition.getX() - keyPosition.getX()) < EPSILON &&
                Math.abs(avatarPosition.getY() - keyPosition.getY()) < EPSILON) &&
                (Math.abs(chestPosition.getX() - keyPosition.getX()) < EPSILON &&
                Math.abs(chestPosition.getY() - keyPosition.getY()) < EPSILON)) {
            chestPosition = rooms.get(random.nextInt(rooms.size())).centroid;
        }
        map[(int) keyPosition.getX()][(int) keyPosition.getY()] = Tileset.KEY;
    }

    private void moveAvatar() {
        for (char move : movement.toCharArray()) {
            switch (move) {
                case 'w':
                    moveAvatarUp();
                    break;
                case 'a':
                    moveAvatarLeft();
                    break;
                case 's':
                    moveAvatarDown();
                    break;
                case 'd':
                    moveAvatarRight();
                    break;
            }
        }
    }

    private void moveAvatarUp(){
        Coordinate avatarPosition = avatar.getPosition();
        int startX = (int) avatarPosition.getX();
        int startY = (int) avatarPosition.getY();

        if (map[(startX)][startY + 1].character() == '·')  {
            map[startX][startY] = Tileset.FLOOR;
            map[startX][startY + 1] = Tileset.AVATAR;
            avatarPosition.setY(startY + 1.0);
        } else if (map[(startX)][startY + 1].character() == '!') {
            map[startX][startY] = Tileset.FLOOR;
            map[startX][startY + 1] = Tileset.AVATAR;
            avatarPosition.setY(startY + 1.0);
            hasKey = true;
        } else if (map[(startX)][startY + 1].character() == '$' && hasKey) {
            map[startX][startY + 1] = Tileset.OPEN_CHEST;
            gameOver = true;
        }
    }

    private void moveAvatarDown(){
        Coordinate avatarPosition = avatar.getPosition();
        int startX = (int) avatarPosition.getX();
        int startY = (int) avatarPosition.getY();

        if (map[(startX)][startY - 1].character() == '·')  {
            map[startX][startY] = Tileset.FLOOR;
            map[startX][startY - 1] = Tileset.AVATAR;
            avatarPosition.setY(startY - 1.0);
        } else if (map[(startX)][startY - 1].character() == '!') {
            map[startX][startY] = Tileset.FLOOR;
            map[startX][startY - 1] = Tileset.AVATAR;
            avatarPosition.setY(startY - 1.0);
            hasKey = true;
        } else if (map[(startX)][startY - 1].character() == '$' && hasKey) {
            map[startX][startY - 1] = Tileset.OPEN_CHEST;
            gameOver = true;
        }
    }

    private void moveAvatarLeft(){
        Coordinate avatarPosition = avatar.getPosition();
        int startX = (int) avatarPosition.getX();
        int startY = (int) avatarPosition.getY();

        if (map[(startX - 1)][startY].character() == '·')  {
            map[startX][startY] = Tileset.FLOOR;
            map[startX - 1][startY] = Tileset.AVATAR;
            avatarPosition.setX(startX - 1.0);
        } else if (map[(startX - 1)][startY].character() == '!') {
            map[startX][startY] = Tileset.FLOOR;
            map[startX - 1][startY] = Tileset.AVATAR;
            avatarPosition.setX(startX - 1.0);
            hasKey = true;
        } else if (map[(startX - 1)][startY].character() == '$' && hasKey) {
            map[startX - 1][startY] = Tileset.OPEN_CHEST;
            gameOver = true;
        }
    }

    private void moveAvatarRight(){
        Coordinate avatarPosition = avatar.getPosition();
        int startX = (int) avatarPosition.getX();
        int startY = (int) avatarPosition.getY();

        if (map[(startX + 1)][startY].character() == '·')  {
            map[startX][startY] = Tileset.FLOOR;
            map[startX + 1][startY] = Tileset.AVATAR;
            avatarPosition.setX(startX + 1.0);
        } else if (map[(startX + 1)][startY].character() == '!') {
            map[startX][startY] = Tileset.FLOOR;
            map[startX + 1][startY] = Tileset.AVATAR;
            avatarPosition.setX(startX + 1.0);
            hasKey = true;
        } else if (map[(startX + 1)][startY].character() == '$' && hasKey) {
            map[startX + 1][startY] = Tileset.OPEN_CHEST;
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean hasKey() {
        return hasKey;
    }

    private void clearMap() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }
}
