package delaunay;

public class Point implements Comparable<Point> {

	public final float x;
	public final float y;

	public Point(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(final Point p) {
		// lexicographical comparison
		return x < p.x ? -1 : x > p.x ? 1 : y < p.y ? -1 : y > p.y ? 1 : 0;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Point) {
			return compareTo((Point) obj) == 0;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
