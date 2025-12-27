package assignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

    public class Index2DTest {

        @Test
        void testGetters() {
            Pixel2D p = new Index2D(3, 7);
            assertEquals(3, p.getX());
            assertEquals(7, p.getY());
        }

        @Test
        void testDistance() {
            Pixel2D p1 = new Index2D(0, 0);
            Pixel2D p2 = new Index2D(3, 4);
            assertEquals(5.0, p1.distance2D(p2), 1e-9);
        }

        @Test
        void testEquals() {
            Pixel2D p1 = new Index2D(2, 2);
            Pixel2D p2 = new Index2D(2, 2);
            Pixel2D p3 = new Index2D(2, 3);

            assertEquals(p1, p2);
            assertNotEquals(p1, p3);
            assertNotEquals(p1, null);
        }

        @Test
        void testToString() {
            Pixel2D p = new Index2D(5, 9);
            assertEquals("5,9", p.toString());
        }
    }

