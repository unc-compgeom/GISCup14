package delaunay;

class EdgeComponent implements Edge {

	private Edge next;
	private Point o;
	private Edge rot; // the dual of this edge (counterclockwise)

	EdgeComponent() {
		super();

	}

	@Override
	public Point dest() {
		return sym().orig();
	}

	@Override
	public Edge dNext() {
		return sym().oNext().sym();
	}

	@Override
	public Edge dPrev() {
		return invRot().oNext().invRot();
	}

	@Override
	public Edge invRot() {
		return rot.rot().rot();
	}

	@Override
	public Edge lNext() {
		return invRot().oNext().rot();
	}

	@Override
	public Edge lPrev() {
		return oNext().sym();
	}

	@Override
	public Edge oNext() {
		return next;
	}

	@Override
	public Edge oPrev() {
		return rot().oNext().rot();
	}

	@Override
	public Point orig() {
		return o;
	}

	@Override
	public Edge rNext() {
		return rot().oNext().invRot();
	}

	@Override
	public Edge rot() {
		return rot;
	}

	@Override
	public Edge rPrev() {
		return sym().oNext();
	}

	@Override
	public void setCoordinates(final Point origin, final Point destination) {
		if (o == null || dest() == null) {
			setOrig(origin);
			setDest(destination);
		} else {
			setOrig(origin);
			setDest(destination);
		}

	}

	@Override
	public void setDest(final Point d) {
		sym().setOrig(d);
	}

	@Override
	public void setNext(final Edge next) {
		this.next = next;
	}

	@Override
	public void setOrig(final Point o) {
		this.o = o;
	}

	@Override
	public void setRot(final Edge rot) {
		this.rot = rot;
	}

	@Override
	public Edge sym() {
		return rot.rot();
	}

	@Override
	public String toString() {
		return orig() + "-" + dest();
	}
}
