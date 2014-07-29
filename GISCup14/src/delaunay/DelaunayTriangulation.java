package delaunay;

import java.util.Collection;

public class DelaunayTriangulation {

	/**
	 * Computes the Delaunay triangulation of the point set.
	 * 
	 * @param points
	 *            the point set
	 * @return a {@link Subdivision} representing the Delaunay Triangulation
	 */
	public static Subdivision triangulate(Collection<Point> points) {
		final Subdivision s = new SubdivisionComponent();
		int i = 1;
		for (final Point p : points) {
			try {
				System.out.println(i++ + ": " + p);
				s.insertSite(p);
			} catch (final DuplicatePointException ignored) {
				// don't insert duplicates.
			}
		}
		return s;
	}
}
