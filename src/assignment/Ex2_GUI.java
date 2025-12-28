package assignment;

import java.awt.Color;
import java.util.Random;

/**
 * Ex2 GUI using StdDraw.
 *
 * Controls:
 * - M : create a simple maze (walls)
 * - S : randomize Start (green) and Target (blue)
 * - A : show distance map (numbers) from Start (BFS)
 * - P : show shortest path from Start to Target (BFS shortest path)
 * - Click: flood fill from clicked cell (paint RED)
 */
public class Ex2_GUI {

    // --- Visual settings ---
    private static final int CELL_SIZE = 18;     // pixels per cell
    private static final double HALF = 0.5;      // square radius (in grid units)

    // --- Map settings ---
    private static final int W = 25;
    private static final int H = 25;

    // --- Colors stored INSIDE map as RGB int ---
    private static final int FREE  = Color.WHITE.getRGB();
    private static final int WALL  = Color.BLACK.getRGB();
    private static final int FILL  = Color.RED.getRGB();
    private static final int START = new Color(0, 170, 0).getRGB();   // green
    private static final int TARGET = new Color(0, 90, 255).getRGB(); // blue
    private static final int PATH  = new Color(255, 210, 0).getRGB(); // yellow

    private static final Random rnd = new Random();

    // State
    private static Map2D map;
    private static Pixel2D startP = null;
    private static Pixel2D targetP = null;

    // Mode flags
    private static boolean showDistances = false;

    public static void main(String[] args) {
        map = new Map(W, H, FREE);

        // create initial maze + start/target
        createMaze(map);
        randomizeStartTarget();

        setupCanvas(W, H);
        redraw();

        // main loop
        while (true) {
            handleKeyboard();
            handleMouse();
            StdDraw.pause(20);
        }
    }

    // ---------- Setup ----------
    private static void setupCanvas(int w, int h) {
        StdDraw.setCanvasSize(w * CELL_SIZE, h * CELL_SIZE);
        StdDraw.setXscale(-0.5, w - 0.5);
        StdDraw.setYscale(-0.5, h - 0.5);
        StdDraw.enableDoubleBuffering();
    }

    // ---------- Input ----------
    private static void handleKeyboard() {
        if (!StdDraw.hasNextKeyTyped()) return;

        char c = Character.toUpperCase(StdDraw.nextKeyTyped());

        if (c == 'M') {
            // New maze
            showDistances = false;
            map.init(W, H, FREE);
            createMaze(map);
            randomizeStartTarget();
            redraw();
        }
        else if (c == 'S') {
            // New random start & target
            showDistances = false;
            randomizeStartTarget();
            redraw();
        }
        else if (c == 'A') {
            // Show distance map numbers
            showDistances = true;
            redraw();
        }
        else if (c == 'P') {
            // Show shortest path
            showDistances = false;
            drawShortestPath();
        }
    }

    private static void handleMouse() {
        if (!StdDraw.isMousePressed()) return;

        int x = (int) Math.round(StdDraw.mouseX());
        int y = (int) Math.round(StdDraw.mouseY());

        if (x < 0 || x >= W || y < 0 || y >= H) {
            StdDraw.pause(150);
            return;
        }

        // If click on wall - do nothing
        if (map.getPixel(x, y) == WALL) {
            StdDraw.pause(150);
            return;
        }

        // Flood fill on click (RED)
        // This will NOT paint the whole map because maze has WALLs that separate areas.
        map.fill(new Index2D(x, y), FILL, false);

        showDistances = false;
        redraw();

        StdDraw.pause(150); // avoid repeated fill while holding mouse
    }

    // ---------- Core Actions ----------
    private static void randomizeStartTarget() {
        // Clear old markers (turn back to FREE if they are not walls)
        clearMarkers();

        startP = randomFreeCell();
        targetP = randomFreeCell();

        while (targetP.equals(startP)) {
            targetP = randomFreeCell();
        }

        map.setPixel(startP, START);
        map.setPixel(targetP, TARGET);
    }

    private static void clearMarkers() {
        if (startP != null && map.isInside(startP) && map.getPixel(startP) == START) {
            map.setPixel(startP, FREE);
        }
        if (targetP != null && map.isInside(targetP) && map.getPixel(targetP) == TARGET) {
            map.setPixel(targetP, FREE);
        }
    }

    private static Pixel2D randomFreeCell() {
        while (true) {
            int x = rnd.nextInt(W);
            int y = rnd.nextInt(H);
            int v = map.getPixel(x, y);
            if (v != WALL) return new Index2D(x, y);
        }
    }

    private static void drawShortestPath() {
        if (startP == null || targetP == null) return;

        // NOTE: shortestPath returns Pixel2D[] or null
        Pixel2D[] path = map.shortestPath(startP, targetP, WALL, false);

        // First redraw base
        redraw();

        // If no path, just write message
        if (path == null) {
            drawMessage("No path found (blocked). Press M to rebuild maze.");
            StdDraw.show();
            return;
        }

        // Paint path cells (yellow) but don't overwrite START/TARGET
        for (Pixel2D p : path) {
            int v = map.getPixel(p);
            if (v == START || v == TARGET) continue;
            map.setPixel(p, PATH);
        }

        redraw(); // show painted path
    }

    // ---------- Drawing ----------
    private static void redraw() {
        StdDraw.clear(Color.WHITE);

        // base map draw
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int v = map.getPixel(x, y);
                drawCell(x, y, v);
            }
        }

        // optional distance overlay
        if (showDistances && startP != null) {
            Map2D dist = map.allDistance(startP, WALL, false);
            drawDistances(dist);
        }

        // grid lines (nice look)
        drawGridLines();

        // legend text (small)
        drawLegend();

        StdDraw.show();
    }

    private static void drawCell(int x, int y, int rgb) {
        StdDraw.setPenColor(new Color(rgb));
        StdDraw.filledSquare(x, y, HALF);

        // If WALL - keep it black.
        // If FREE etc - already painted.
    }

    private static void drawDistances(Map2D dist) {
        // Draw numbers on top of the cells (small)
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

        for (int x = 0; x < dist.getWidth(); x++) {
            for (int y = 0; y < dist.getHeight(); y++) {
                // do not print on walls
                if (map.getPixel(x, y) == WALL) continue;

                int d = dist.getPixel(x, y);
                if (d >= 0) {
                    // put the number
                    StdDraw.text(x, y, String.valueOf(d));
                }
            }
        }
    }

    private static void drawGridLines() {
        StdDraw.setPenColor(new Color(0, 0, 0, 40));
        for (int x = 0; x <= W; x++) {
            StdDraw.line(x - 0.5, -0.5, x - 0.5, H - 0.5);
        }
        for (int y = 0; y <= H; y++) {
            StdDraw.line(-0.5, y - 0.5, W - 0.5, y - 0.5);
        }
    }

    private static void drawLegend() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        StdDraw.textLeft(-0.45, H - 0.2,
                "Keys: M=maze | S=start/target | A=distances | P=path | Click=fill");
    }

    private static void drawMessage(String msg) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        StdDraw.text(W / 2.0, H - 1.2, msg);
    }

    // ---------- Maze ----------
    private static void createMaze(Map2D m) {
        // Very simple maze:
        // 1) border walls
        // 2) random internal walls, but keep enough free space

        // borders
        for (int x = 0; x < W; x++) {
            m.setPixel(x, 0, WALL);
            m.setPixel(x, H - 1, WALL);
        }
        for (int y = 0; y < H; y++) {
            m.setPixel(0, y, WALL);
            m.setPixel(W - 1, y, WALL);
        }

        // internal random walls
        int walls = (W * H) / 5; // ~20%
        for (int i = 0; i < walls; i++) {
            int x = 1 + rnd.nextInt(W - 2);
            int y = 1 + rnd.nextInt(H - 2);
            m.setPixel(x, y, WALL);
        }

        // small “corridors”: clear a plus in center so we usually have paths
        int cx = W / 2;
        int cy = H / 2;
        for (int dx = -4; dx <= 4; dx++) {
            if (cx + dx > 0 && cx + dx < W - 1) m.setPixel(cx + dx, cy, FREE);
        }
        for (int dy = -4; dy <= 4; dy++) {
            if (cy + dy > 0 && cy + dy < H - 1) m.setPixel(cx, cy + dy, FREE);
        }
    }
}
