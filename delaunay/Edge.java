package delaunay;

/**
 * An class that specifies the edge data structure which implements the quadedge
 * algebra. The quadedge algebra was described in a paper by Guibas and Stolfi,
 * "Primitives for the manipulation of general subdivisions and the computation of Voronoi diagrams"
 * , ACM Transactions on Graphics, 4(2), 1985, 75-123.
 *
 * Each edge is grouped into a collection of 4 edges, linked via their rot
 * references, called a {@link QuadEdge}. Any edge in the group may be accessed
 * using a series of rot() operations. QuadEdges in a {@link Subdivision} are
 * linked together via their next references.
 */
public class Edge {
	private Edge next;
	private Point o;
	private Edge rot;

	/**
	 * Gets the {@link Point} for the edge's destination
	 *
	 * @return the destination point
	 */
	public Point dest() {
		return sym().orig();
	}

	/**
	 * Gets the next CCW {@link Edge} around (into) the destination of this edge
	 *
	 * @return the next destination edge
	 */
	public Edge dNext() {
		return sym().oNext().sym();
	}

	/**
	 * Gets the next CW {@link Edge} around (into) the destination of this edge
	 *
	 * @return the previous destination edge
	 */
	public Edge dPrev() {
		return invRot().oNext().invRot();
	}

	/**
	 * Gets the dual of this edge, directed from its left to its right
	 *
	 * @return the inverse rotated edge
	 */
	Edge invRot() {
		return rot.rot().rot();
	}

	/**
	 * Gets the CCW {@link Edge} around the left face following this edge
	 *
	 * @return the next left face edge
	 */
	public Edge lNext() {
		return invRot().oNext().rot();
	}

	/**
	 * Gets the CCW {@link Edge} around the left face before this edge
	 *
	 * @return the previous left face edge
	 */
	public Edge lPrev() {
		return oNext().sym();
	}

	/**
	 * Gets the next CCW {@link Edge} around the origin of this edge
	 *
	 * @return the next linked edge
	 */
	public Edge oNext() {
		return next;
	}

	/**
	 * Gets the next CW {@link Edge} around (from) the origin of this edge
	 *
	 * @return the previous edge
	 */
	public Edge oPrev() {
		return rot().oNext().rot();
	}

	/**
	 * Gets the {@link Point} for the edge's origin
	 *
	 * @return the origin point
	 */
	public Point orig() {
		return o;
	}

	/**
	 * Gets the {@link Edge} around the right face ccw following this edge
	 *
	 * @return the next right face edge
	 */
	public Edge rNext() {
		return rot().oNext().invRot();
	}

	/**
	 * Gets the dual of this edge, directed from its right to its left
	 *
	 * @return the rotated edge
	 */
	public Edge rot() {
		return rot;
	}

	/**
	 * Gets the {@link Edge} around the right face ccw before this edge
	 *
	 * @return the previous right face edge
	 */
	public Edge rPrev() {
		return sym().oNext();
	}

	/**
	 * Sets the points of this edge.
	 *
	 * @param origin
	 *            the new origin
	 * @param destination
	 *            the new destination
	 */
	public void setCoordinates(final Point origin, final Point destination) {
		setOrig(origin);
		setDest(destination);
	}

	/**
	 * Sets the {@link Point} for this edge's destination
	 *
	 * @param d
	 *            the destination point
	 */
	void setDest(final Point d) {
		sym().setOrig(d);
	}

	/**
	 * Sets the connected {@link Edge}
	 *
	 * @param next
	 *            the next edge
	 */
	public void setNext(final Edge next) {
		this.next = next;
	}

	/**
	 * Sets the {@link Point} for this edge's origin
	 *
	 * @param o
	 *            the origin point
	 */
	void setOrig(final Point o) {
		this.o = o;
	}

	/**
	 * Sets the dual of this edge, directed from its right to its left
	 *
	 * @param rot
	 *            the rotated edge
	 */
	public void setRot(final Edge rot) {
		this.rot = rot;
	}

	/**
	 * Gets the {@link Edge} from the destination to the origin of this edge
	 *
	 * @return the sym of this edge
	 */
	public Edge sym() {
		return rot.rot();
	}

	@Override
	public String toString() {
		return orig() + "-" + dest();
	}
}
