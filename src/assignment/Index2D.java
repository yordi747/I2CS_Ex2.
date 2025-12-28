package assignment;


public class Index2D implements Pixel2D {

    private int _x;
    private int _y;


    public Index2D(int x, int y) {
        this._x = x;
        this._y = y;
    }


    public Index2D(Pixel2D other) {
        this._x = other.getX();
        this._y = other.getY();
    }

    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }


    @Override
    public double distance2D(Pixel2D p2) {
        if (p2 == null) {
            throw new IllegalArgumentException("Pixel2D is null");
        }
        double dx = _x - p2.getX();
        double dy = _y - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return _x + "," + _y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pixel2D)) return false;

        Pixel2D other = (Pixel2D) obj;
        return _x == other.getX() && _y == other.getY();
    }

    @Override
    public int hashCode() {
        return 31 * _x + _y;
    }
}