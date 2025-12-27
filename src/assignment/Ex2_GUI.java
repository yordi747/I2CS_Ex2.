package assignment;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * GUI for Map2D.
 * Simple English comments.
 */
public class Ex2_GUI {

    /**
     * Draws the map on the screen.
     */
    public static void drawMap(Map2D map) {
        if (map == null) return;

        int w = map.getWidth();
        int h = map.getHeight();

        // Setup window and scale
        StdDraw.setCanvasSize(w * 5, h * 5);
        StdDraw.setXscale(-0.5, w - 0.5);
        StdDraw.setYscale(-0.5, h - 0.5);

        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.WHITE);

        // Draw each cell
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int colorRGB = map.getPixel(x, y);
                try {
                    StdDraw.setPenColor(new Color(colorRGB));
                } catch (IllegalArgumentException e) {
                    StdDraw.setPenColor(Color.BLACK); // fallback
                }
                StdDraw.filledSquare(x, y, 0.5);
            }
        }

        StdDraw.show();
    }

    /**
     * Loads a map from a file.
     */
    public static Map2D loadMap(String mapFileName) {
        File file = new File(mapFileName);
        if (!file.exists()) {
            System.out.println("File not found: " + mapFileName);
            return null;
        }

        try (Scanner sc = new Scanner(file)) {
            int w = sc.nextInt();
            int h = sc.nextInt();

            Map2D map = new Map(w, h, Color.WHITE.getRGB());
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    map.setPixel(x, y, sc.nextInt());
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves a map to a file.
     */
    public static void saveMap(Map2D map, String mapFileName) {
        if (map == null) return;

        try (PrintWriter pw = new PrintWriter(new File(mapFileName))) {
            pw.println(map.getWidth());
            pw.println(map.getHeight());

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    pw.print(map.getPixel(x, y) + " ");
                }
                pw.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method – GUI demo only.
     */
    public static void main(String[] args) {
        Map2D map = new Map(100, 100, Color.WHITE.getRGB());

        map.drawCircle(new Index2D(50, 50), 30, Color.RED.getRGB());
        map.drawLine(new Index2D(10, 10), new Index2D(90, 90), Color.BLUE.getRGB());

        drawMap(map);
    }
}