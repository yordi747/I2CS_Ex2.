package assignment;

import java.awt.Color;

/**
 * Simple GUI for Ex2: Fill (flood fill), Distances (BFS), Shortest Path (BFS).
 * Keys:
 *  F = Fill mode (click -> flood fill in RED)
 *  D = Dist mode (click -> show distance map numbers)
 *  P = Path mode (click start, click end -> show shortest path in RED)
 *  R = Reset map
 */
public class Ex2_GUI {

    // You can change these sizes
    private static final int W = 30;
    private static final int H = 30;

    // Maze values (not RGB): 0 = free, 1 = wall
    private static final int FREE = 0;
    private static final int WALL = 1;

    private enum Mode { FILL, DIST, PATH }
    private static Mode mode = Mode.FILL;

    private static Map2D baseMaze;     // the maze itself (0/1)
    private static Map2D viewMap;      // what we render (can be same as base, or distance map)
    private static Pixel2D pathStart = null;

    public static void drawMap(Map2D map) {
        if (map == null) return;

        int w = map.getWidth();
        int h = map.getHeight();

        // square cells, visible grid
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, w);
        StdDraw.setYscale(0, h);
        StdDraw.enableDoubleBuffering();

        render(map, true);
    }

    private static void render(Map2D map, boolean withNumbers) {
        int w = map.getWidth();
        int h = map.getHeight();

        StdDraw.clear(Color.WHITE);

        // draw cells
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                int v = map.getPixel(x, y);

                // If this is the base maze: v is FREE/WALL
                // If this is a distance map: v is -1 or distance
                Color cellColor = colorForValue(map, v);

                StdDraw.setPenColor(cellColor);
                StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);

                // grid lines
                StdDraw.setPenColor(new Color(220, 220, 220));
                StdDraw.square(x + 0.5, y + 0.5, 0.5);
            }
        }

        // numbers: only if map is not huge (so it stays readable)
        if (withNumbers && w <= 35 && h <= 35) {
            StdDraw.setPenColor(Color.DARK_GRAY);
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int v = map.getPixel(x, y);
                    // show FREE/WALL as 0/1, distances as number, unreachable as blank
                    String s = "";
                    if (map == baseMaze) {
                        s = String.valueOf(v);
                    } else {
                        if (v >= 0) s = String.valueOf(v);
                        else s = ""; // unreachable
                    }
                    if (!s.isEmpty()) {
                        StdDraw.text(x + 0.5, y + 0.5, s);
                    }
                }
            }
        }

        // top status line
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.textLeft(0.2, h - 0.3, "Mode: " + mode + "  (F=Fill, D=Dist, P=Path, R=Reset)");

        StdDraw.show();
    }

    // Choose colors for base maze OR distance map
    private static Color colorForValue(Map2D current, int v) {
        if (current == baseMaze) {
            // base maze coloring
            if (v == WALL) return Color.BLACK;
            return Color.WHITE;
        } else {
            // distance map coloring: -1 unreachable -> light gray
            if (v < 0) return new Color(230, 230, 230);

            // simple blue-ish gradient without complex stuff
            // (distance 0 will be light, larger distance a bit darker)
            int t = Math.min(200, v * 12);
            int r = 255 - t;
            int g = 255 - t;
            int b = 255;
            return new Color(clamp(r), clamp(g), clamp(b));
        }
    }

    private static int clamp(int c) {
        if (c < 0) return 0;
        if (c > 255) return 255;
        return c;
    }

    // Create a simple maze with thick walls (like your screenshot)
    private static Map2D createMaze(int w, int h) {
        Map2D m = new Map(w, h, FREE);

        // border walls
        for (int x = 0; x < w; x++) {
            m.setPixel(x, 0, WALL);
            m.setPixel(x, h - 1, WALL);
        }
        for (int y = 0; y < h; y++) {
            m.setPixel(0, y, WALL);
            m.setPixel(w - 1, y, WALL);
        }

        // some internal walls (rectangles / lines)
        m.drawRect(new Index2D(4, 4), new Index2D(6, h - 6), WALL);
        m.drawRect(new Index2D(10, 10), new Index2D(w - 6, 12), WALL);
        m.drawRect(new Index2D(w - 8, 6), new Index2D(w - 6, h - 6), WALL);
        m.drawRect(new Index2D(6, 18), new Index2D(18, 20), WALL);

        // small gaps (doors)
        m.setPixel(6, 10, FREE);
        m.setPixel(w - 6, 14, FREE);
        m.setPixel(14, 12, FREE);

        return m;
    }

    public static void main(String[] args) {
        baseMaze = createMaze(W, H);
        viewMap = baseMaze;

        drawMap(viewMap);

        while (true) {
            // keys
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (c == 'F') { mode = Mode.FILL; viewMap = baseMaze; pathStart = null; }
                if (c == 'D') { mode = Mode.DIST; viewMap = baseMaze; pathStart = null; }
                if (c == 'P') { mode = Mode.PATH; viewMap = baseMaze; pathStart = null; }
                if (c == 'R') { baseMaze = createMaze(W, H); viewMap = baseMaze; pathStart = null; }
                render(viewMap, true);
            }

            // mouse click
            if (StdDraw.isMousePressed()) {
                int x = (int) StdDraw.mouseX();
                int y = (int) StdDraw.mouseY();
                Index2D p = new Index2D(x, y);

                if (baseMaze != null && baseMaze instanceof Map && ((Map) baseMaze).isInside(p)) {

                    if (mode == Mode.FILL) {
                        // Fill only if clicked on FREE (optional)
                        if (baseMaze.getPixel(p) == FREE) {
                            // flood fill in RED (but baseMaze is values 0/1, so we color by overlay):
                            // easiest: temporarily convert FREE area to a special number, and render it as red by using RGB map
                            // Instead: create an RGB map view and keep baseMaze for BFS.
                            // We'll do: make a copy map with "colors" and apply fill on that copy.
                            Map2D colored = mazeToRgb(baseMaze);
                            // BFS on colored map uses "oldColor" matching, so click must match the RGB of FREE.
                            ((Map) colored).fill(p, Color.RED.getRGB(), false);
                            viewMap = colored;
                        }

                    } else if (mode == Mode.DIST) {
                        // distance from p, obstacles are WALL
                        Map2D dist = baseMaze.allDistance(p, WALL, false);
                        viewMap = dist;

                    } else if (mode == Mode.PATH) {
                        if (pathStart == null) {
                            if (baseMaze.getPixel(p) != WALL) {
                                pathStart = p;
                            }
                        } else {
                            Pixel2D pathEnd = p;
                            Pixel2D[] path = baseMaze.shortestPath(pathStart, pathEnd, WALL, false);

                            Map2D colored = mazeToRgb(baseMaze);
                            if (path != null) {
                                for (Pixel2D q : path) {
                                    colored.setPixel(q, Color.RED.getRGB());
                                }
                            }
                            viewMap = colored;
                            pathStart = null;
                        }
                    }

                    render(viewMap, true);
                }

                StdDraw.pause(180);
            }

            StdDraw.pause(10);
        }
    }

    // Convert maze values (0/1) into RGB map for nice colors + fill/path painting
    private static Map2D mazeToRgb(Map2D maze) {
        Map2D rgb = new Map(maze.getWidth(), maze.getHeight(), Color.WHITE.getRGB());
        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                int v = maze.getPixel(x, y);
                if (v == WALL) rgb.setPixel(x, y, Color.BLACK.getRGB());
                else rgb.setPixel(x, y, Color.WHITE.getRGB());
            }
        }
        return rgb;
    }
}
