package delaunay;

public class Predicate {
	public enum Orientation {
		CLOCKWISE, COLINEAR, COUNTERCLOCKWISE
	}

	/**
	 * Tests if {@link Point} p is ahead of the segment from q to r.
	 *
	 * @param p
	 *            a point
	 * @param q
	 *            segment endpoint
	 * @param r
	 *            segment endpoint
	 * @return true iff <tt>p</tt> is ahead of <tt>qr</tt>.
	 */
	private static boolean ahead(final Point p, final Point q, final Point r) {
		return p.sub(q).dot(r.sub(q)) > distSquared(q, r);
	}

	/**
	 * Computes the distance squared between points p and q
	 *
	 * @param p
	 *            a point
	 * @param q
	 *            a point
	 * @return distance squared between points p and q.
	 */
	public static double distSquared(final Point p, final Point q) {
		// increment the call counter
		// do work
		final double dx = p.getX() - q.getX(), dy = p.getY() - q.getY();
		return dx * dx + dy * dy;
	}

	/**
	 * Tests if a {@link Point} is inside the {@link Circle} defined by points
	 * <tt>a</tt>, <tt>b</tt>, and <tt>color</tt>. Points must be oriented
	 * counterclockwise.
	 *
	 * @param test
	 *            Point to be tested
	 * @param a
	 *            Point on circle
	 * @param b
	 *            Point on circle
	 * @param c
	 *            Point on circle
	 * @return true iff <tt>test</tt> is in the circle
	 */
	public static boolean isPointInCircle(final Point test, final Point a,
			final Point b, final Point c) {
		final float ax = a.getX(), ay = a.getY();
		final float bx = b.getX(), by = b.getY();
		final float cx = c.getX(), cy = c.getY();
		final float dx = test.getX(), dy = test.getY();
		final double det = (ax * ax + ay * ay) * triArea(b, c, test)
				- (bx * bx + by * by) * triArea(a, c, test)
				+ (cx * cx + cy * cy) * triArea(a, b, test)
				- (dx * dx + dy * dy) * triArea(a, b, c);
		final boolean isInCircle = det > 0;
		return isInCircle;
	}

	public static boolean leftOf(final Point p, final Edge e) {
		return triArea(p, e.orig(), e.dest()) > 0;
	}

	public static boolean leftOrAhead(final Point p, final Point q,
			final Point r) {
		final double tmp = triArea(p, q, r);
		return tmp > 0 || tmp == 0 && ahead(p, q, r);
	}

	public static boolean onEdge(final Point p, final Edge e) {
		final Point a = e.orig();
		final Point b = e.dest();
		if (triArea(a, b, p) == 0) {
			final double dot = p.sub(a).dot(b.sub(a));
			final double distSq = distSquared(a, b);
			return 0 <= dot && dot <= distSq;
		} else {
			return false;
		}
	}

	public static boolean rightOf(final Point p, final Edge e) {
		return triArea(p, e.orig(), e.dest()) < 0;
	}

	public static boolean rightOrAhead(final Point p, final Point q,
			final Point r) {
		final double tmp = triArea(p, q, r);
		return tmp < 0 || tmp == 0 && ahead(p, q, r);
	}

	/**
	 * Calculates twice the signed area of the triangle defined by {@link Point}
	 * s a, b, and color. If a, b, and color are in counterclockwise order, the
	 * area is positive, if they are co-linear the area is zero, else the area
	 * is negative.
	 *
	 * @param a
	 *            a Point
	 * @param b
	 *            a Point
	 * @param c
	 *            a Point
	 * @return twice the signed area
	 */
	private static double triArea(final Point a, final Point b, final Point c) {
		return b.sub(a).getX() * c.sub(a).getY() - b.sub(a).getY()
				* c.sub(a).getX();
	}
}
