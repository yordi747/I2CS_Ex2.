package assignment;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A concrete implementation of Map2D.
 * Represents a 2D raster map backed by int[][].
 */
public class Map implements Map2D, Serializable {

    private int[][] _map;

    /* ---------- Constructors ---------- */

    public Map(int w, int h, int v) {
        init(w, h, v);
    }

    public Map(int size) {
        this(size, size, 0);
    }

    public Map(int[][] data) {
        init(data);
    }

    /* ---------- Initialization ---------- */

    @Override
    public void init(int w, int h, int v) {
        _map = new int[w][h];
        for (int x = 0; x < w; x++) {
            Arrays.fill(_map[x], v);
        }
    }

    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0) return;
        int w = arr.length;
        int h = arr[0].length;
        _map = new int[w][h];
        for (int x = 0; x < w; x++) {
            System.arraycopy(arr[x], 0, _map[x], 0, h);
        }
    }

    /* ---------- Basic getters ---------- */

    @Override
    public int[][] getMap() {
        int[][] copy = new int[getWidth()][getHeight()];
        for (int x = 0; x < getWidth(); x++) {
            System.arraycopy(_map[x], 0, copy[x], 0, getHeight());
        }
        return copy;
    }

    @Override
    public int getWidth() {
        return _map.length;
    }

    @Override
    public int getHeight() {
        return _map[0].length;
    }

    @Override
    public int getPixel(int x, int y) {
        return _map[x][y];
    }

    @Override
    public int getPixel(Pixel2D p) {
        return getPixel(p.getX(), p.getY());
    }

    @Override
    public void setPixel(int x, int y, int v) {
        _map[x][y] = v;
    }

    @Override
    public void setPixel(Pixel2D p, int v) {
        setPixel(p.getX(), p.getY(), v);
    }

    /* ---------- Utilities ---------- */

    @Override
    public boolean isInside(Pixel2D p) {
        if (p == null) return false;
        int x = p.getX(), y = p.getY();
        return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
    }

    @Override
    public boolean sameDimensions(Map2D p) {
        return p != null &&
                getWidth() == p.getWidth() &&
                getHeight() == p.getHeight();
    }

    @Override
    public void addMap2D(Map2D p) {
        if (!sameDimensions(p)) return;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                _map[x][y] += p.getPixel(x, y);
            }
        }
    }

    @Override
    public void mul(double scalar) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                _map[x][y] = (int)(_map[x][y] * scalar);
            }
        }
    }

    @Override
    public void rescale(double sx, double sy) {
        int newW = (int)(getWidth() * sx);
        int newH = (int)(getHeight() * sy);
        int[][] newMap = new int[newW][newH];

        for (int x = 0; x < newW; x++) {
            for (int y = 0; y < newH; y++) {
                newMap[x][y] = _map[(int)(x / sx)][(int)(y / sy)];
            }
        }
        _map = newMap;
    }

    /* ---------- Drawing ---------- */

    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        int cx = center.getX();
        int cy = center.getY();
        double r2 = rad * rad;

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                double dx = x - cx;
                double dy = y - cy;
                if (dx * dx + dy * dy <= r2) {
                    setPixel(x, y, color);
                }
            }
        }
    }

    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        int x0 = p1.getX(), y0 = p1.getY();
        int x1 = p2.getX(), y1 = p2.getY();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            setPixel(x0, y0, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx)  { err += dx; y0 += sy; }
        }
    }

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        int minX = Math.min(p1.getX(), p2.getX());
        int maxX = Math.max(p1.getX(), p2.getX());
        int minY = Math.min(p1.getY(), p2.getY());
        int maxY = Math.max(p1.getY(), p2.getY());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                setPixel(x, y, color);
            }
        }
    }

    /* ---------- BFS Algorithms ---------- */

    @Override
    public int fill(Pixel2D start, int newColor, boolean cyclic) {
        int oldColor = getPixel(start);
        if (oldColor == newColor) return 0;

        int count = 0;
        ArrayDeque<Pixel2D> q = new ArrayDeque<>();
        q.add(start);
        setPixel(start, newColor);
        count++;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!q.isEmpty()) {
            Pixel2D p = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = p.getX() + dx[i];
                int ny = p.getY() + dy[i];

                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                Pixel2D np = new Index2D(nx, ny);
                if (isInside(np) && getPixel(np) == oldColor) {
                    setPixel(np, newColor);
                    q.add(np);
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map dist = new Map(getWidth(), getHeight(), -1);
        if (!isInside(start) || getPixel(start) == obsColor) return dist;

        ArrayDeque<Pixel2D> q = new ArrayDeque<>();
        q.add(start);
        dist.setPixel(start, 0);

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!q.isEmpty()) {
            Pixel2D p = q.poll();
            int d = dist.getPixel(p);

            for (int i = 0; i < 4; i++) {
                int nx = p.getX() + dx[i];
                int ny = p.getY() + dy[i];

                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                Pixel2D np = new Index2D(nx, ny);
                if (isInside(np) && dist.getPixel(np) == -1 && getPixel(np) != obsColor) {
                    dist.setPixel(np, d + 1);
                    q.add(np);
                }
            }
        }
        return dist;
    }

    @Override
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
        Map2D dist = allDistance(p1, obsColor, cyclic);
        if (dist.getPixel(p2) == -1) return null;

        ArrayList<Pixel2D> path = new ArrayList<>();
        Pixel2D curr = p2;
        path.add(curr);

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (dist.getPixel(curr) != 0) {
            for (int i = 0; i < 4; i++) {
                int nx = curr.getX() + dx[i];
                int ny = curr.getY() + dy[i];

                if (cyclic) {
                    nx = (nx + getWidth()) % getWidth();
                    ny = (ny + getHeight()) % getHeight();
                }

                Pixel2D np = new Index2D(nx, ny);
                if (isInside(np) && dist.getPixel(np) == dist.getPixel(curr) - 1) {
                    curr = np;
                    path.add(0, curr);
                    break;
                }
            }
        }
        return path.toArray(new Pixel2D[0]);
    }
}