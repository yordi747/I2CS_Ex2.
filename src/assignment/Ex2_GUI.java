package assignment;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class Ex2_GUI {

    private static final int CELL_PX = 22;
    private static final int TOP_BAR_PX = 70;

    private static final int FREE = 0;
    private static final int WALL = 1;
    private static final int FILLED = 2;

    private enum Mode { FILL, DIST, PATH }
    private static Mode mode = Mode.FILL;

    private static Map2D map;
    private static Map2D distMap;
    private static Pixel2D[] path;

    private static Pixel2D pathStart = null;
    private static Pixel2D pathEnd   = null;

    private static final Random rnd = new Random();

    private static boolean wasMousePressed = false;

    public static void main(String[] args) {
        int w = 30, h = 20;

        map = new Map(w, h, FREE);
        createRandomMaze(map, 0.28);
        distMap = null;
        path = null;

        setupCanvas(map);
        redrawAll();

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
        StdDraw.setYscale(-0.5, h - 0.5 + (double) TOP_BAR_PX / CELL_PX);

        StdDraw.enableDoubleBuffering();
    }

    private static void handleKeyboard() {
        if (!StdDraw.hasNextKeyTyped()) return;

        char c = Character.toUpperCase(StdDraw.nextKeyTyped());

        if (c == 'M') {
            clearOverlays();
            createRandomMaze(map, 0.28);
            redrawAll();
        } else if (c == 'F') {
            mode = Mode.FILL;
            clearOverlays();
            redrawAll();
        } else if (c == 'D') {
            mode = Mode.DIST;
            clearOverlays();
            redrawAll();
        } else if (c == 'P') {
            mode = Mode.PATH;
            clearOverlays();
            redrawAll();
        } else if (c == 'C') {
            clearOverlays();
            redrawAll();
        } else if (c == 'R') {
            resetFilledToFree();
            clearOverlays();
            redrawAll();
        }
    }

    private static void handleMouse() {
        boolean nowPressed = StdDraw.isMousePressed();

        if (!(nowPressed && !wasMousePressed)) {
            wasMousePressed = nowPressed;
            return;
        }
        wasMousePressed = true;

        int x = (int) Math.floor(StdDraw.mouseX() + 0.5);
        int y = (int) Math.floor(StdDraw.mouseY() + 0.5);

        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) {
            return;
        }

        Pixel2D p = new Index2D(x, y);

        if (mode == Mode.FILL) {
            if (map.getPixel(p) == FREE) {
                map.fill(p, FILLED, false);
            }
            clearOverlays();
            redrawAll();

        } else if (mode == Mode.DIST) {
            clearOverlays();
            distMap = map.allDistance(p, WALL, false);
            redrawAll();

        } else {
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

    private static void redrawAll() {
        StdDraw.clear(Color.WHITE);
        drawTopBar();
        drawGrid();
        StdDraw.show();
    }

    private static void drawTopBar() {
        double topY = map.getHeight() - 0.5 + (double) TOP_BAR_PX / CELL_PX / 2.0;

        StdDraw.setPenColor(new Color(245, 245, 245));
        StdDraw.filledRectangle((map.getWidth() - 1) / 2.0, topY,
                map.getWidth() / 2.0, (double) TOP_BAR_PX / CELL_PX / 2.0);

        StdDraw.setPenColor(Color.DARK_GRAY);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 14));

        String modeText =
                (mode == Mode.FILL) ? "Mode: FILL (click a 0 cell to fill)" :
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

        int maxDist = 0;
        if (distMap != null) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int d = distMap.getPixel(x, y);
                    if (d > maxDist) maxDist = d;
                }
            }
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                int v = map.getPixel(x, y);
                Color base = (v == WALL) ? Color.BLACK
                        : (v == FILLED) ? Color.RED
                        : Color.WHITE;

                if (distMap != null) {
                    int d = distMap.getPixel(x, y);
                    if (d >= 0 && map.getPixel(x, y) != WALL) {
                        int shade = 240;
                        if (maxDist > 0) {
                            shade = 240 - (int) Math.round(170.0 * d / maxDist);
                            shade = clamp(shade, 40, 240);
                        }
                        base = new Color(shade, shade, shade);
                    } else if (map.getPixel(x, y) != WALL) {
                        base = new Color(230, 230, 230);
                    }
                }

                StdDraw.setPenColor(base);
                StdDraw.filledSquare(x, y, 0.5);

                StdDraw.setPenColor(new Color(210, 210, 210));
                StdDraw.square(x, y, 0.5);

                if (distMap != null) {
                    int d = distMap.getPixel(x, y);
                    if (d >= 0 && map.getPixel(x, y) != WALL) {
                        StdDraw.setPenColor(Color.BLUE);
                        StdDraw.setFont(new Font("Arial", Font.PLAIN, 11));
                        StdDraw.text(x, y, String.valueOf(d));
                    }
                } else {
                    StdDraw.setFont(new Font("Arial", Font.PLAIN, 10));
                    if (v == WALL) StdDraw.setPenColor(Color.WHITE);
                    else StdDraw.setPenColor(Color.GRAY);
                    StdDraw.text(x, y, String.valueOf(v));
                }
            }
        }

        if (path != null && path.length > 0) {
            for (Pixel2D p : path) {
                if (p == null) continue;
                if (map.isInside(p)) {
                    StdDraw.setPenColor(new Color(0, 120, 255));
                    StdDraw.filledSquare(p.getX(), p.getY(), 0.35);
                }
            }
        }

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

    private static void createRandomMaze(Map2D m, double wallProb) {
        for (int x = 0; x < m.getWidth(); x++) {
            for (int y = 0; y < m.getHeight(); y++) {
                int val = (rnd.nextDouble() < wallProb) ? WALL : FREE;
                m.setPixel(x, y, val);
            }
        }
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
