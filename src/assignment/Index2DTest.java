package assignment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Index2DTest {

    @Test
    public void testGetters() {
        Index2D p = new Index2D(3, 7);
        assertEquals(3, p.getX());
        assertEquals(7, p.getY());
    }

    @Test
    public void testCopyConstructor() {
        Pixel2D a = new Index2D(1, 2);
        Index2D b = new Index2D(a);
        assertEquals(1, b.getX());
        assertEquals(2, b.getY());
        assertEquals(a, b);
    }

    @Test
    public void testDistance2D() {
        Pixel2D p1 = new Index2D(0, 0);
        Pixel2D p2 = new Index2D(3, 4);
        assertEquals(5.0, p1.distance2D(p2), 1e-9);
    }

    @Test
    public void testDistance2DNull() {
        Pixel2D p1 = new Index2D(0, 0);
        assertThrows(IllegalArgumentException.class, () -> p1.distance2D(null));
    }

    @Test
    public void testToString() {
        Index2D p = new Index2D(10, 20);
        assertEquals("10,20", p.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        Index2D a = new Index2D(5, 6);
        Index2D b = new Index2D(5, 6);
        Index2D c = new Index2D(5, 7);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());

        assertNotEquals(a, null);
        assertNotEquals(a, "5,6");
    }
}
