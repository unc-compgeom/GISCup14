package gml;

import delaunay.Point;

import java.util.List;

public class ArcsPointsAndOffsets {
	public final List<Point[]> arcs;
	public final List<Point[]> points;
	public final long offsetLatitude;
	public final long offsetLongitude;

	public ArcsPointsAndOffsets(final List<Point[]> arcs,
			final List<Point[]> points, final long offsetLatitude,
			final long offsetLongitude) {
		this.arcs = arcs;
		this.points = points;
		this.offsetLatitude = offsetLatitude;
		this.offsetLongitude = offsetLongitude;
	}
}
