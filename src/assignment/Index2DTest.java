package assignment;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class Index2DTest {

    @Test
    public void testConstructorAndGetters() {
        Index2D p = new Index2D(3, 7);
        assertEquals(3, p.getX());
        assertEquals(7, p.getY());
    }

    @Test
    public void testCopyConstructor() {
        Index2D p1 = new Index2D(5, 9);
        Index2D p2 = new Index2D(p1);

        assertEquals(p1.getX(), p2.getX());
        assertEquals(p1.getY(), p2.getY());
        assertEquals(p1, p2);
    }

    @Test
    public void testEqualsSamePoint() {
        Index2D p1 = new Index2D(4, 6);
        Index2D p2 = new Index2D(4, 6);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testEqualsDifferentPoint() {
        Index2D p1 = new Index2D(1, 2);
        Index2D p2 = new Index2D(2, 1);

        assertNotEquals(p1, p2);
    }

    @Test
    public void testDistance2D() {
        Index2D p1 = new Index2D(0, 0);
        Index2D p2 = new Index2D(3, 4);

        // distance should be 5 (3-4-5 triangle)
        assertEquals(5.0, p1.distance2D(p2), 0.0001);
    }

    @Test
    public void testDistanceSamePoint() {
        Index2D p = new Index2D(8, 8);
        assertEquals(0.0, p.distance2D(p), 0.0001);
    }

    @Test
    public void testToString() {
        Index2D p = new Index2D(2, 10);
        assertEquals("2,10", p.toString());
    }

    @Test
    public void testDistanceWithNull() {
        Index2D p = new Index2D(1, 1);
        assertThrows(IllegalArgumentException.class, () -> {
            p.distance2D(null);
        });
    }
}
