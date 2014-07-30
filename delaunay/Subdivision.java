package delaunay;

/**
 * This interface specifies the requirements of an object that can be used to
 * represent a Delaunay Triangulation or a generic subdivision of a polygon.
 *
 * @author Vance Miller
 *
 */
public class Subdivision {
	private Edge startingEdge;
	private final QuadEdge qe;

	public Subdivision() {
		// TODO find a triangle large enough to encompass <tt>points</tt>
		qe = new QuadEdge();
		startingEdge = qe.getFirst();
	}

	/**
	 * Inserts {@link Point} p into the subdivision.
	 *
	 * @param p
	 *            the Point to insert
	 * @throws DuplicatePointException
	 *             iff p is already part of the subdivision
	 */
	public void insertSite(final Point p) throws DuplicatePointException {
		Edge e = locate(p);
		if (Predicate.onEdge(p, e)) {
			e = e.oPrev();
			qe.deleteEdge(e.oNext());
		}
		// connect the new point to the vertices of the containing triangle
		Edge base = qe.makeEdge();
		base.setCoordinates(e.orig(), new Point(p.x, p.y));
		qe.splice(base, e);
		startingEdge = base;
		// add edges
		do {
			base = qe.connect(e, base.sym());
			e = base.oPrev();
		} while (e.lNext() != startingEdge);
		// examine suspect edges and ensure that the Delaunay condition is
		// satisfied
		do {
			final Edge t = e.oPrev();
			if (Predicate.rightOf(t.dest(), e)
					&& Predicate.isPointInCircle(p, e.orig(), t.dest(),
							e.dest())) {
				qe.swap(e);
				e = e.oPrev();
			} else if (e.oNext() == startingEdge) {
				return;
			} else {
				e = e.oNext().lPrev();
			}
		} while (true);
	}

	/**
	 * Returns the <tt>Edge</tt> that contains <tt>p</tt> or the edge of a
	 * triangle containing <tt>p</tt>.
	 *
	 * @param q
	 *            the point to locate
	 * @return the edge that p is on;
	 * @throws DuplicatePointException
	 *             iff <tt>p</tt> is already in this subdivision
	 *
	 */
	public Edge locate(final Point q) throws DuplicatePointException {

		Edge e = startingEdge;
		if (!Predicate.rightOrAhead(e.dest(), e.orig(), q)) {
			e = e.sym();
		}
		final Point p = e.orig();
		if (p == q) {
			throw new DuplicatePointException();
		}
		// invariant: e intersects pq with e.dest() on, right, or ahead of pq.
		do {
			if (q == e.dest()) {
				// duplicate point
				throw new DuplicatePointException();
			} else if (!Predicate.leftOrAhead(q, e.orig(), e.dest())) {
				// q is on an edge or inside a triangle edge
				return e.sym();
			} else if (Predicate.rightOrAhead(e.oNext().dest(), p, q)) {
				e = e.oNext();
			} else {
				e = e.lNext().sym();
			}
		} while (true);

	}
}
