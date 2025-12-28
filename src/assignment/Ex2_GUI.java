package assignment;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

/**
 * Ex2 GUI using StdDraw.
 * Supports:
 * 1) Flood fill (BFS)   - mode F, click to fill connected component
 * 2) Distance map (BFS) - mode D, click to compute distances + show numbers
 * 3) Shortest path      - mode P, click start then click end to show path
 *
 * Map values (maze):
 * 0 = free cell (white)
 * 1 = obstacle/wall (black)
 * 2 = filled area (red) - produced by fill
 */
public class Ex2_GUI {

    // --- UI sizes ---
    private static final int CELL_PX = 22;   // size of each grid cell in pixels (small squares)
    private static final int TOP_BAR_PX = 70;

    // --- Maze values ---
    private static final int FREE = 0;
    private static final int WALL = 1;
    private static final int FILLED = 2;

    // --- Modes ---
    private enum Mode { FILL, DIST, PATH }

    private static Mode mode = Mode.FILL;

    // Current state
    private static Map2D map;         // the main maze map (0/1/2)
    private static Map2D distMap;     // distances overlay (-1 unreachable)
    private static Pixel2D[] path;    // current shortest path overlay

    private static Pixel2D pathStart = null;
    private static Pixel2D pathEnd   = null;

    private static final Random rnd = new Random();

    public static void main(String[] args) {
        int w = 30, h = 20; // not too huge so numbers are readable

        map = new Map(w, h, FREE);
        createRandomMaze(map, 0.28); // 28% walls
        distMap = null;
        path = null;

        setupCanvas(map);
        redrawAll();

        // main loop
        while (true) {
            handleKeyboard();
            handleMouse();
            StdDraw.pause(20);
        }
    }

    private static void setupCanvas(Map2D m) {
        int w = m.getWidth();
        int h = m.getHeight();

        StdDraw.setCanvasSize(w * CELL_PX, h * CELL_PX + TOP_BAR_PX);
        StdDraw.setXscale(-0.5, w - 0.5);
        // y from -0.5..h-0.5 for grid, plus extra top bar area
        StdDraw.setYscale(-0.5, h - 0.5 + (double)TOP_BAR_PX / CELL_PX);

        StdDraw.enableDoubleBuffering();
    }

    // ---------------------------
    // Input handling
    // ---------------------------

    private static void handleKeyboard() {
        if (!StdDraw.hasNextKeyTyped()) return;

        char c = Character.toUpperCase(StdDraw.nextKeyTyped());

        if (c == 'M') {
            // New random maze
            clearOverlays();
            createRandomMaze(map, 0.28);
            redrawAll();
        }
        else if (c == 'F') {
            mode = Mode.FILL;
            clearOverlays();
            redrawAll();
        }
        else if (c == 'D') {
            mode = Mode.DIST;
            clearOverlays();
            redrawAll();
        }
        else if (c == 'P') {
            mode = Mode.PATH;
            clearOverlays();
            redrawAll();
        }
        else if (c == 'C') {
            // clear overlays only
            clearOverlays();
            redrawAll();
        }
        else if (c == 'R') {
            // reset filled cells back to FREE (keep walls)
            resetFilledToFree();
            clearOverlays();
            redrawAll();
        }
    }

    private static void handleMouse() {
        if (!StdDraw.isMousePressed()) return;

        int x = (int)Math.round(StdDraw.mouseX());
        int y = (int)Math.round(StdDraw.mouseY());

        // click must be inside grid area (not top bar)
        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) {
            StdDraw.pause(150);
            return;
        }

        Pixel2D p = new Index2D(x, y);

        if (mode == Mode.FILL) {
            // flood fill only on free or filled; if clicked wall do nothing
            if (map.getPixel(p) != WALL) {
                // Fill connected component with red-mark value=2
                map.fill(p, FILLED, false);
            }
            clearOverlays();
            redrawAll();
        }
        else if (mode == Mode.DIST) {
            // compute BFS distance map from clicked point, walls are obstacles
            clearOverlays();
            distMap = map.allDistance(p, WALL, false);
            redrawAll();
        }
        else if (mode == Mode.PATH) {
            // choose start then end
            if (pathStart == null) {
                pathStart = p;
                pathEnd = null;
                path = null;
            } else {
                pathEnd = p;
                path = map.shortestPath(pathStart, pathEnd, WALL, false);
            }
            distMap = null;
            redrawAll();
        }

        StdDraw.pause(180); // avoid multi-click spam
    }

    private static void clearOverlays() {
        distMap = null;
        path = null;
        pathStart = null;
        pathEnd = null;
    }

    private static void resetFilledToFree() {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getPixel(x, y) == FILLED) {
                    map.setPixel(x, y, FREE);
                }
            }
        }
    }

    // ---------------------------
    // Drawing
    // ---------------------------

    private static void redrawAll() {
        StdDraw.clear(Color.WHITE);
        drawTopBar();
        drawGrid();
        StdDraw.show();
    }

    private static void drawTopBar() {
        double topY = map.getHeight() - 0.5 + (double)TOP_BAR_PX / CELL_PX / 2.0;

        // bar background
        StdDraw.setPenColor(new Color(245, 245, 245));
        StdDraw.filledRectangle((map.getWidth() - 1) / 2.0, topY,
                map.getWidth() / 2.0, (double)TOP_BAR_PX / CELL_PX / 2.0);

        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 14));

        String modeText =
                (mode == Mode.FILL) ? "Mode: FILL (click to fill area)" :
                        (mode == Mode.DIST) ? "Mode: DIST (click to show BFS distances)" :
                                "Mode: PATH (click start, then click end)";

        StdDraw.textLeft(-0.3, topY + 0.4, modeText);

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 12));
        StdDraw.textLeft(-0.3, topY - 0.3,
                "Keys: [M] new maze   [F] fill   [D] distances   [P] path   [C] clear overlay   [R] reset filled");
    }

    private static void drawGrid() {
        int w = map.getWidth();
        int h = map.getHeight();

        // for distance visualization scaling
        int maxDist = 0;
        if (distMap != null) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int d = distMap.getPixel(x, y);
                    if (d > maxDist) maxDist = d;
                }
            }
        }

        // base cells
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                // base color from map value
                int v = map.getPixel(x, y);
                Color base = (v == WALL) ? Color.BLACK
                        : (v == FILLED) ? Color.RED
                        : Color.WHITE;

                // overlay for distance map (if exists and cell is reachable)
                if (distMap != null) {
                    int d = distMap.getPixel(x, y);
                    if (d >= 0 && map.getPixel(x, y) != WALL) {
                        // grayscale: near=light, far=dark (simple)
                        int shade = 240;
                        if (maxDist > 0) {
                            shade = 240 - (int)Math.round(170.0 * d / maxDist);
                            shade = clamp(shade, 40, 240);
                        }
                        base = new Color(shade, shade, shade);
                    } else if (map.getPixel(x, y) != WALL) {
                        base = new Color(230, 230, 230); // unreachable
                    }
                }

                StdDraw.setPenColor(base);
                StdDraw.filledSquare(x, y, 0.5);

                // grid border
                StdDraw.setPenColor(new Color(210, 210, 210));
                StdDraw.square(x, y, 0.5);

                // numbers
                if (distMap != null) {
                    // show distance numbers
                    int d = distMap.getPixel(x, y);
                    if (d >= 0 && map.getPixel(x, y) != WALL) {
                        StdDraw.setPenColor(Color.BLUE);
                        StdDraw.setFont(new Font("Arial", Font.PLAIN, 11));
                        StdDraw.text(x, y, String.valueOf(d));
                    }
                } else {
                    // show maze values (0/1/2) small
                    StdDraw.setFont(new Font("Arial", Font.PLAIN, 10));
                    if (v == WALL) StdDraw.setPenColor(Color.WHITE);
                    else StdDraw.setPenColor(Color.GRAY);
                    StdDraw.text(x, y, String.valueOf(v));
                }
            }
        }

        // draw shortest path overlay
        if (path != null && path.length > 0) {
            for (Pixel2D p : path) {
                if (p == null) continue;
                if (map.isInside(p)) {
                    StdDraw.setPenColor(new Color(0, 120, 255)); // blue
                    StdDraw.filledSquare(p.getX(), p.getY(), 0.35);
                }
            }
        }

        // mark start/end for PATH mode
        if (pathStart != null) {
            StdDraw.setPenColor(Color.GREEN.darker());
            StdDraw.filledCircle(pathStart.getX(), pathStart.getY(), 0.25);
        }
        if (pathEnd != null) {
            StdDraw.setPenColor(Color.ORANGE.darker());
            StdDraw.filledCircle(pathEnd.getX(), pathEnd.getY(), 0.25);
        }
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ---------------------------
    // Maze generation
    // ---------------------------

    private static void createRandomMaze(Map2D m, double wallProb) {
        for (int x = 0; x < m.getWidth(); x++) {
            for (int y = 0; y < m.getHeight(); y++) {
                int val = (rnd.nextDouble() < wallProb) ? WALL : FREE;
                m.setPixel(x, y, val);
            }
        }

        // Make a guaranteed free border line or two (so you can see algorithms clearly)
        for (int x = 0; x < m.getWidth(); x++) {
            m.setPixel(x, 0, FREE);
            m.setPixel(x, m.getHeight() - 1, FREE);
        }
        for (int y = 0; y < m.getHeight(); y++) {
            m.setPixel(0, y, FREE);
            m.setPixel(m.getWidth() - 1, y, FREE);
        }
    }
}
