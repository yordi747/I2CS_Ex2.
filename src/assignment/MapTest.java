package assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Intro2CS, 2026A
 * JUnit test class for Map.
 */
class MapTest {

    private int[][] _map_3_3 = {
            {0, 1, 0},
            {1, 0, 1},
            {0, 1, 0}
    };

    private Map2D _m0, _m1, _m3_3;

    @BeforeEach
    void setup() {
        _m0 = new Map(3, 3, 0);
        _m1 = new Map(3, 3, 0);
        _m3_3 = new Map(_map_3_3);
    }

    /* ---------- Helper ---------- */

    private void assertSameMap(Map2D a, Map2D b) {
        assertEquals(a.getWidth(), b.getWidth());
        assertEquals(a.getHeight(), b.getHeight());
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                assertEquals(a.getPixel(x, y), b.getPixel(x, y),
                        "Mismatch at (" + x + "," + y + ")");
            }
        }
    }

    /* ---------- Tests ---------- */

    @Test
    @Timeout(value = 1, unit = SECONDS)
    void testInitLarge() {
        int[][] bigArr = new int[500][500];
        _m1.init(bigArr);

        assertEquals(500, _m1.getWidth());
        assertEquals(500, _m1.getHeight());

        Pixel2D p = new Index2D(3, 2);
        _m1.fill(p, 1, true);
        assertEquals(1, _m1.getPixel(p));
    }

    @Test
    void testInitFromArray() {
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);
        assertSameMap(_m0, _m1);
    }

    @Test
    void testSetGetPixel() {
        _m0.setPixel(1, 1, 7);
        assertEquals(7, _m0.getPixel(1, 1));
    }

    @Test
    void testShortestPath() {
        // Maze:
        // S 1 0
        // 0 1 0
        // 0 0 E

        _m0.init(3, 3, 0);
        _m0.setPixel(1, 0, 1);
        _m0.setPixel(1, 1, 1);

        Pixel2D start = new Index2D(0, 0);
        Pixel2D end = new Index2D(2, 2);

        Pixel2D[] path = _m0.shortestPath(start, end, 1, false);

        assertNotNull(path);
        assertEquals(start, path[0]);
        assertEquals(end, path[path.length - 1]);
    }

    @Test
    void testFill() {
        _m0.init(3, 3, 0);
        int changed = _m0.fill(new Index2D(1, 1), 7, false);

        assertEquals(9, changed);
        assertEquals(7, _m0.getPixel(0, 0));
        assertEquals(7, _m0.getPixel(2, 2));
    }

    @Test
    void testAllDistance() {
        _m0.init(3, 3, 0);
        Map2D dist = _m0.allDistance(new Index2D(0, 0), 1, false);

        assertEquals(0, dist.getPixel(0, 0));
        assertEquals(1, dist.getPixel(0, 1));
        assertEquals(2, dist.getPixel(1, 1));
        assertEquals(4, dist.getPixel(2, 2));
    }
}