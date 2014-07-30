package delaunay;

import java.util.Iterator;

/**
 * A collection of four directed edges designed for representing general
 * subdivisions of orientable manifolds. This data structure is described in a
 * paper by Guibas and Stolfi (1985).
 *
 * @author Vance Miller
 *
 */
class QuadEdge {
	private final Edge first;

	QuadEdge() {
		final int scale = (int) Math.pow(2, 29); // TODO correctly initialize
													// the quadedge
		final Point a = new Point(-1 * scale - 1, 2 * scale);
		final Point b = new Point(-1 * scale, -1 * scale);
		final Point c = new Point(2 * scale, -1 * scale);

		final Edge ea = makeEdge();
		ea.setCoordinates(a, b);

		final Edge eb = makeEdge();
		eb.setCoordinates(b, c);
		splice(ea.sym(), eb);

		final Edge ec = makeEdge();
		ec.setCoordinates(c, a);
		splice(eb.sym(), ec);

		splice(ec.sym(), ea);
		first = ec;
	}

	/**
	 * Creates a new QuadEdge collection that connects the destination of
	 * <tt>a</tt> to the origin of <tt>b</tt>, so that all three have the same
	 * left face after the connection is complete and the data pointers of the
	 * new edge are set.
	 *
	 * @param a
	 *            an edge
	 * @param b
	 *            an edge
	 * @return the new edge connecting <tt>a</tt> and <tt>b</tt>
	 */
	public Edge connect(final Edge a, final Edge b) {
		final Edge e = makeEdge();
		e.setCoordinates(a.dest(), b.orig());
		splice(e, a.lNext());
		splice(e.sym(), b);
		return e;
	}

	/**
	 * Disconnects edge e from the collection of edges.
	 *
	 * @param e
	 *            the edge to delete
	 */
	public void deleteEdge(final Edge e) {
		splice(e, e.oPrev());
		splice(e.sym(), e.sym().oPrev());
	}

	/**
	 * Gets the {@link Edge} at index i. Edges are numbered in the order in
	 * which they are traversed. Order is arbitrary and will not be consistent
	 * between splices.
	 *
	 * @param i
	 *            edge number
	 * @return the edge at traversal location i
	 */
	public Edge get(final int i) {
		int count = 0;
		Edge e = first;
		do {
			if (i == count++) {
				return e;
			}
			if (isWall(e) || isWall(e.sym())) {
				e = e.rPrev();
			} else {
				e = e.oNext();
			}
		} while (e != first);
		throw new IndexOutOfBoundsException();
	}

	private boolean isWall(final Edge e) {
		return e.orig().compareTo(e.lNext().orig()) >= 0
				&& e.lNext().orig().compareTo(e.lPrev().orig()) > 0;

	}

	public Iterator<Edge> iterator() {
		return new Iterator<Edge>() {
			private final Edge e = first;
			private Edge next = e;
			private boolean firstCase = first != null;

			@Override
			public boolean hasNext() {
				if (firstCase) {
					firstCase = false;
					return true;
				} else {
					return next != e;
				}
			}

			@Override
			public Edge next() {
				final Edge tmp = next;
				if (isWall(next) || isWall(next.sym())) {
					next = next.rPrev();
				} else {
					next = next.oNext();
				}
				return tmp;
			}
		};
	}

	/**
	 * Creates an empty edge quartet.
	 *
	 * @return the edge
	 */
	public Edge makeEdge() {
		final Edge[] edges = new Edge[4];
		edges[0] = new Edge();
		edges[1] = new Edge();
		edges[2] = new Edge();
		edges[3] = new Edge();

		edges[0].setRot(edges[1]);
		edges[1].setRot(edges[2]);
		edges[2].setRot(edges[3]);
		edges[3].setRot(edges[0]);

		edges[0].setNext(edges[0]);
		edges[1].setNext(edges[3]);
		edges[2].setNext(edges[2]);
		edges[3].setNext(edges[1]);
		return edges[0];
	}

	/**
	 * Splices two edges together or apart. Splice affects the two edge rings
	 * around the origins of a and b, and, independently, the two edge rings
	 * around the left faces of a and b. In each case, (i) if the two rings are
	 * distinct, Splice will combine them into one, or (ii) if the two are the
	 * same ring, Splice will break it into two separate pieces. Thus, Splice
	 * can be used both to attach the two edges together, and to break them
	 * apart.
	 *
	 * @param a
	 *            an edge
	 * @param b
	 *            an edge
	 */
	public void splice(final Edge a, final Edge b) {
		final Edge alpha = a.oNext().rot();
		final Edge beta = b.oNext().rot();

		final Edge t1 = b.oNext();
		final Edge t2 = a.oNext();
		final Edge t3 = beta.oNext();
		final Edge t4 = alpha.oNext();

		a.setNext(t1);
		b.setNext(t2);
		alpha.setNext(t3);
		beta.setNext(t4);
	}

	/**
	 * Turns an edge counterclockwise inside its enclosing quadrilateral.
	 *
	 * @param e
	 *            the edge to swap
	 */
	public void swap(final Edge e) {
		final Edge a = e.oPrev();
		final Edge b = e.sym().oPrev();
		e.setCoordinates(a.dest(), b.dest());
		splice(e, a);
		splice(e.sym(), b);
		splice(e, a.lNext());
		splice(e.sym(), b.lNext());
	}
}
