package gml;

import java.util.List;

import delaunay.Point;

public class PointsArcsAndOffsets {
	public List<Point[]> points;
	public List<Point[]> arcs;
	public long offsetLatitude;
	public long offsetLongitude;

	public PointsArcsAndOffsets(final List<Point[]> points,
			final List<Point[]> lines, final long offsetLatitude,
			final long offsetLongitude) {
		this.points = points;
		this.arcs = lines;
		this.offsetLatitude = offsetLatitude;
		this.offsetLongitude = offsetLongitude;
	}
}
