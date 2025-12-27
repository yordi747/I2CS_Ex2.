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
     * Draws the map and sets up the window.
     */
    public static void drawMap(Map2D map) {
        if (map == null) return;

        int w = map.getWidth();
        int h = map.getHeight();

        // Setup window size and scale
        StdDraw.setCanvasSize(w * 5, h * 5);
        StdDraw.setXscale(-0.5, w - 0.5);
        StdDraw.setYscale(-0.5, h - 0.5);

        // Enable double buffering for smooth drawing
        StdDraw.enableDoubleBuffering();

        render(map);
    }

    /**
     * Helper method to refresh the screen.
     */
    private static void render(Map2D map) {
        StdDraw.clear(Color.WHITE);
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int colorRGB = map.getPixel(x, y);
                StdDraw.setPenColor(new Color(colorRGB));
                StdDraw.filledSquare(x, y, 0.5);
            }
        }
        StdDraw.show();
    }

    /**
     * Loads a map from a text file.
     */
    public static Map2D loadMap(String mapFileName) {
        File file = new File(mapFileName);
        if (!file.exists()) return null;

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
            return null;
        }
    }

    /**
     * Saves the map to a text file.
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
     * Main method: Runs the GUI and handles mouse clicks.
     */
    public static void main(String[] args) {
        int width = 100;
        int height = 100;
        Map2D map = new Map(width, height, Color.WHITE.getRGB());

        // Draw initial shapes
        map.drawCircle(new Index2D(50, 50), 30, Color.RED.getRGB());
        map.drawLine(new Index2D(10, 10), new Index2D(90, 90), Color.BLUE.getRGB());

        drawMap(map);

        // Interaction loop
        while (true) {
            // Check if mouse is clicked
            if (StdDraw.isMousePressed()) {
                int x = (int) Math.round(StdDraw.mouseX());
                int y = (int) Math.round(StdDraw.mouseY());

                // Check boundaries
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    Index2D p = new Index2D(x, y);

                    // Example: Fill area with Yellow on click
                    // This uses your BFS implementation in Map.java
                    map.fill(p, Color.YELLOW.getRGB(),false);

                    render(map); // Update screen
                }
                StdDraw.pause(200); // Wait to avoid multiple clicks
            }
        }
    }
}