package assignment;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Map
 * ---
 * Implementation of Map2D using a 2D int grid: _map[x][y].
 *
 * Main ideas:
 * 1) The map is a grid of colors/values (integers).
 * 2) Neighbors are 4-connected: up, down, left, right (no diagonals).
 * 3) We use BFS (queue) for:
 *    - Flood fill (fill an area)
 *    - Distance map (minimum steps from a start)
 *    - Shortest path (by backtracking on the distance map)
 *
 * About "cyclic":
 * If cyclic == true, the map behaves like a wrap-around world:
 * going left from x=0 jumps to x=width-1, etc.
 */
public class Map implements Map2D, Serializable {


    private int[][] _map;

    public Map(int w, int h, int v) { init(w, h, v); }

    public Map(int size) { this(size, size, 0); }

    public Map(int[][] data) { init(data); }

    /**
     * Initialize map with width (w), height (h) and fill value (v).
     * We allocate a new array and fill every cell with v.
     */
    @Override
    public void init(int w, int h, int v) {
        _map = new int[w][h];
        for (int x = 0; x < w; x++) {
            Arrays.fill(_map[x], v);
        }
    }

    /**
     * Initialize map from an existing 2D array.
     * We do a deep copy so later changes to arr will NOT affect this map.
     */
    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0) return;
        _map = new int[arr.length][arr[0].length];
        for (int x = 0; x < arr.length; x++) {
            System.arraycopy(arr[x], 0, _map[x], 0, arr[0].length);
        }
    }

    // Basic getters and setters (direct access to the grid).
    @Override public int[][] getMap() { return _map; }
    @Override public int getWidth() { return _map.length; }
    @Override public int getHeight() { return _map[0].length; }
    @Override public int getPixel(int x, int y) { return _map[x][y]; }
    @Override public int getPixel(Pixel2D p) { return getPixel(p.getX(), p.getY()); }
    @Override public void setPixel(int x, int y, int v) { _map[x][y] = v; }
    @Override public void setPixel(Pixel2D p, int v) { setPixel(p.getX(), p.getY(), v); }

    /**
     * Check if p is a valid coordinate inside the grid boundaries.
     * Returns false if p is null or outside [0..width-1] x [0..height-1].
     */
    @Override
    public boolean isInside(Pixel2D p) {
        if (p == null) return false;
        return p.getX() >= 0 && p.getY() >= 0 &&
                p.getX() < getWidth() && p.getY() < getHeight();
    }

    /**
     * True if both maps have the same width and height.
     * Useful before doing operations that require same size.
     */
    @Override
    public boolean sameDimensions(Map2D p) {
        return p != null && getWidth() == p.getWidth() && getHeight() == p.getHeight();
    }

    /**
     * Draw a filled circle by scanning all cells and checking distance to center.
     * If (x-centerX)^2 + (y-centerY)^2 <= rad^2 then the pixel is inside the circle.
     */
    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        double r2 = rad * rad;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                double d2 = Math.pow(x - center.getX(), 2) +
                        Math.pow(y - center.getY(), 2);
                if (d2 <= r2) setPixel(x, y, color);
            }
        }
    }

    /**
     * Draw a line between p1 and p2 using an integer step algorithm (Bresenham style).
     * It moves in x and/or y each step until reaching the destination.
     */
    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        int x0 = p1.getX(), y0 = p1.getY();
        int x1 = p2.getX(), y1 = p2.getY();
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            setPixel(x0, y0, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx)  { err += dx; y0 += sy; }
        }
    }

    /**
     * Draw a filled rectangle between p1 and p2.
     * We go over the bounding box (minX..maxX, minY..maxY) and paint all pixels.
     */
    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        for (int x = Math.min(p1.getX(), p2.getX());
             x <= Math.max(p1.getX(), p2.getX()); x++) {
            for (int y = Math.min(p1.getY(), p2.getY());
                 y <= Math.max(p1.getY(), p2.getY()); y++) {
                setPixel(x, y, color);
            }
        }
    }

    /** Convenience overload: default cyclic = false. */
    public int fill(Pixel2D p, int color) {
        return fill(p, color, false);
    }

    /**
     * Flood Fill (BFS):
     * - Start from 'start'
     * - Fill only cells that have the SAME original color as start (oldColor)
     * - Replace them with newColor
     *
     * Rules here:
     * - If start is out of bounds -> return 0
     * - If oldColor is 0 -> we do not fill (0 is treated like "do not paint")
     * - BFS prevents recursion/stack overflow and guarantees we visit each cell once.
     *
     * Returns: number of painted cells.
     */
    @Override
    public int fill(Pixel2D start, int newColor, boolean cyclic) {
        if (start == null || !isInside(start)) return 0;

        int oldColor = getPixel(start);
        if (oldColor == 0 || oldColor == newColor) return 0;

        int count = 0;
        ArrayDeque<Pixel2D> q = new ArrayDeque<>();
        q.add(start);

        // Mark as visited immediately by painting it, so we don't enqueue it again later.
        setPixel(start, newColor);
        count++;

        // 4 directions: right, left, up, down
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!q.isEmpty()) {
            Pixel2D p = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = p.getX() + dx[i];
                int ny = p.getY() + dy[i];

                // If cyclic, wrap around the borders.
                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                Index2D next = new Index2D(nx, ny);

                // We only fill pixels that match oldColor.
                if (isInside(next) && getPixel(next) == oldColor) {
                    setPixel(next, newColor);
                    q.add(next);
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Distance map (BFS):
     * Creates a new map 'res' where res[x][y] = minimum number of steps from 'start'.
     * - Obstacles are cells in THIS map with value obsColor (they are blocked).
     * - Unreachable cells stay as -1.
     * - BFS works because each move costs 1 step and BFS explores by layers.
     */
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map res = new Map(getWidth(), getHeight(), -1);
        if (!isInside(start) || getPixel(start) == obsColor) return res;

        ArrayDeque<Pixel2D> q = new ArrayDeque<>();
        q.add(start);
        res.setPixel(start, 0);

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!q.isEmpty()) {
            Pixel2D curr = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = curr.getX() + dx[i];
                int ny = curr.getY() + dy[i];

                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                Index2D next = new Index2D(nx, ny);

                // Visit neighbor only if:
                // 1) It's inside bounds
                // 2) It's not an obstacle in the original map
                // 3) We didn't assign it a distance yet in res (-1 means unvisited)
                if (isInside(next) &&
                        getPixel(next) != obsColor &&
                        res.getPixel(next) == -1) {

                    res.setPixel(next, res.getPixel(curr) + 1);
                    q.add(next);
                }
            }
        }
        return res;
    }

    /**
     * Shortest path between p1 and p2:
     * 1) Build distance map from p1 (BFS).
     * 2) If p2 is unreachable -> return null.
     * 3) Reconstruct path by stepping backwards:
     *    from p2 go to a neighbor with distance = currentDistance - 1,
     *    until reaching distance 0 (which is p1).
     *
     * Returns: array of Pixel2D from p1 to p2.
     */
    @Override
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2,
                                  int obsColor, boolean cyclic) {
        Map2D distMap = allDistance(p1, obsColor, cyclic);
        if (distMap.getPixel(p2) == -1) return null;

        ArrayList<Pixel2D> path = new ArrayList<>();
        Pixel2D curr = p2;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (distMap.getPixel(curr) != 0) {
            path.add(0, curr); // insert at start, so final order is p1 -> p2

            for (int i = 0; i < 4; i++) {
                int nx = curr.getX() + dx[i];
                int ny = curr.getY() + dy[i];

                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                Index2D next = new Index2D(nx, ny);

                // Move to any neighbor that is exactly one step closer to p1
                if (isInside(next) &&
                        distMap.getPixel(next) == distMap.getPixel(curr) - 1) {
                    curr = next;
                    break;
                }
            }
        }

        path.add(0, p1);
        return path.toArray(new Pixel2D[0]);
    }

    /** Optional operations (left empty if not required right now). */
    @Override public void mul(double scalar) {}
    @Override public void addMap2D(Map2D p) {}
    @Override public void rescale(double sx, double sy) {}
}
