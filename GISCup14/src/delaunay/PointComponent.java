package delaunay;

public class PointComponent implements Point {
	private float x;
	private float y;

	public PointComponent(final float x, final float y) {
		super();
		this.x = x;
		this.y = y;
	}

	PointComponent(final Point p) {
		super();
		x = p.getX();
		y = p.getY();
	}

	@Override
	public int compareTo(final Point p) {
		// lexicographical comparison
		return x < p.getX() ? -1 : x > p.getX() ? 1 : y < p.getY() ? -1 : y > p
				.getY() ? 1 : 0;
	}

	@Override
	public double distanceSquared(final Point v) {
		final double x = Math.pow(this.x - v.getX(), 2);
		final double y = Math.pow(this.y - v.getY(), 2);
		return x + y;
	}

	@Override
	public Point div(final float i) {
		return new PointComponent(x / i, y / i);
	}

	@Override
	public double dot(final Point v) {
		return x * v.getX() + y * v.getY();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			return compareTo((Point) obj) == 0;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public Point mult(final float i) {
		return new PointComponent(x * i, y * i);
	}

	@Override
	public Point plus(final Point p1) {
		return new PointComponent(x + p1.getX(), y + p1.getY());
	}

	@Override
	public void setX(final float x) {
		this.x = x;
	}

	@Override
	public void setY(final float y) {
		this.y = y;
	}

	@Override
	public Point sub(final Point p1) {
		return new PointComponent(x - p1.getX(), y - p1.getY());
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
