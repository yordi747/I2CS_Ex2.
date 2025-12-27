package assignment;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Map implementation with BFS algorithms.
 * Simple English comments.
 */
public class Map implements Map2D, Serializable {

    private int[][] _map;

    public Map(int w, int h, int v) { init(w, h, v); }
    public Map(int size) { this(size, size, 0); }
    public Map(int[][] data) { init(data); }

    @Override
    public void init(int w, int h, int v) {
        _map = new int[w][h];
        for (int x = 0; x < w; x++) Arrays.fill(_map[x], v);
    }

    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0) return;
        _map = new int[arr.length][arr[0].length];
        for (int x = 0; x < arr.length; x++) {
            System.arraycopy(arr[x], 0, _map[x], 0, arr[0].length);
        }
    }

    @Override
    public int[][] getMap() { return _map; }
    @Override
    public int getWidth() { return _map.length; }
    @Override
    public int getHeight() { return _map[0].length; }
    @Override
    public int getPixel(int x, int y) { return _map[x][y]; }
    @Override
    public int getPixel(Pixel2D p) { return getPixel(p.getX(), p.getY()); }
    @Override
    public void setPixel(int x, int y, int v) { _map[x][y] = v; }
    @Override
    public void setPixel(Pixel2D p, int v) { setPixel(p.getX(), p.getY(), v); }

    @Override
    public boolean isInside(Pixel2D p) {
        if (p == null) return false;
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < getWidth() && p.getY() < getHeight();
    }

    @Override
    public boolean sameDimensions(Map2D p) {
        return p != null && getWidth() == p.getWidth() && getHeight() == p.getHeight();
    }

    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        double r2 = rad * rad;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                double d2 = Math.pow(x - center.getX(), 2) + Math.pow(y - center.getY(), 2);
                if (d2 <= r2) setPixel(x, y, color);
            }
        }
    }

    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        int x0 = p1.getX(), y0 = p1.getY(), x1 = p2.getX(), y1 = p2.getY();
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            setPixel(x0, y0, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
    }

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        for (int x = Math.min(p1.getX(), p2.getX()); x <= Math.max(p1.getX(), p2.getX()); x++) {
            for (int y = Math.min(p1.getY(), p2.getY()); y <= Math.max(p1.getY(), p2.getY()); y++) {
                setPixel(x, y, color);
            }
        }
    }

    // This is the fill method for the GUI
    public int fill(Pixel2D p, int color) { return fill(p, color, false); }

    @Override
    public int fill(Pixel2D start, int newColor, boolean cyclic) {
        int oldColor = getPixel(start);
        if (oldColor == newColor) return 0;
        int count = 0;
        ArrayDeque<Pixel2D> q = new ArrayDeque<>();
        q.add(start);
        setPixel(start, newColor);
        count++;
        int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
        while (!q.isEmpty()) {
            Pixel2D p = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = p.getX() + dx[i], ny = p.getY() + dy[i];
                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }
                Index2D next = new Index2D(nx, ny);
                if (isInside(next) && getPixel(next) == oldColor) {
                    setPixel(next, newColor);
                    q.add(next);
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map res = new Map(getWidth(), getHeight(), -1);
        if (!isInside(start) || getPixel(start) == obsColor) return res;
        ArrayDeque<Pixel2D> q = new ArrayDeque<>();
        q.add(start);
        res.setPixel(start, 0);
        int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
        while (!q.isEmpty()) {
            Pixel2D curr = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = curr.getX() + dx[i], ny = curr.getY() + dy[i];
                if (cyclic) { nx = (nx + getWidth()) % getWidth(); ny = (ny + getHeight()) % getHeight(); }
                Index2D next = new Index2D(nx, ny);
                if (isInside(next) && getPixel(next) != obsColor && res.getPixel(next) == -1) {
                    res.setPixel(next, res.getPixel(curr) + 1);
                    q.add(next);
                }
            }
        }
        return res;
    }

    @Override
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
        Map2D distMap = allDistance(p1, obsColor, cyclic);
        if (distMap.getPixel(p2) == -1) return null;
        ArrayList<Pixel2D> path = new ArrayList<>();
        Pixel2D curr = p2;
        while (distMap.getPixel(curr) != 0) {
            path.add(0, curr);
            int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
            for (int i = 0; i < 4; i++) {
                int nx = curr.getX() + dx[i], ny = curr.getY() + dy[i];
                if (cyclic) { nx = (nx + getWidth()) % getWidth(); ny = (ny + getHeight()) % getHeight(); }
                Index2D next = new Index2D(nx, ny);
                if (isInside(next) && distMap.getPixel(next) == distMap.getPixel(curr) - 1) {
                    curr = next; break;
                }
            }
        }
        path.add(0, p1);
        return path.toArray(new Pixel2D[0]);
    }

    @Override public void mul(double scalar) {}
    @Override public void addMap2D(Map2D p) {}
    @Override public void rescale(double sx, double sy) {}
}