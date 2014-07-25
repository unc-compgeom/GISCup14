package delaunay;


/**
 * This class can be used to represent a Delaunay Triangulation. It uses the
 * {@link QuadEdge} data structure to maintain edge data.
 *
 * @author Vance Miller
 *
 */
public class SubdivisionComponent implements Subdivision {
	private Edge startingEdge;
	private final QuadEdge qe;

	public SubdivisionComponent() {
		// TODO find a triangle large enough to encompass <tt>points</tt>
		qe = new QuadEdgeComponent();
		startingEdge = qe.get(0);
	}

	@Override
	public Subdivision getDual() {
		return null;
	}

	@Override
	public void insertSite(final Point p) throws DuplicatePointException {
		Edge e = locate(p);
		if (Predicate.onEdge(p, e)) {
			e = e.oPrev();
			qe.deleteEdge(e.oNext());
		}
		// connect the new point to the vertices of the containing triangle
		Edge base = qe.makeEdge();
		base.setCoordinates(e.orig(), new PointComponent(p));
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

	@Override
	public Edge locate(final Point q) throws DuplicatePointException {
		Edge e = startingEdge;
		if (!Predicate.rightOrAhead(e.dest(), e.orig(), q)) {
			e = e.sym();
		}
		final Point p = e.orig();
		if (p == q) {
			throw new DuplicatePointException(q);
		}
		// invariant: e intersects pq with e.dest() on, right, or ahead of pq.
		do {
			if (q == e.dest()) {
				// duplicate point
				throw new DuplicatePointException(q);
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