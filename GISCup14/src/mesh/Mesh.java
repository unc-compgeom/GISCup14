package mesh;

import gml.ExportGML;
import gml.ImportGML;
import gml.PointsArcsAndOffsets;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import delaunay.DuplicatePointException;
import delaunay.Edge;
import delaunay.Point;
import delaunay.Subdivision;

public class Mesh {
	public static void main(String[] args) throws IOException {
		args = new String[] { "src\\td1", "src\\td2", "src\\td3" };
		for (final String folderName : args) {
			// import points
			final PointsArcsAndOffsets imported = ImportGML.importGML(
					folderName + "\\points_out.txt", folderName
							+ "\\lines_out.txt");

			// do triangulation on arc endpoints and all constraint points.
			final List<Point> triangulationPoints = new LinkedList<Point>();

			// take first and last from lines_out for each line, excluding
			// duplicates
			for (final Point[] points : imported.arcs) {
				// beginning of line
				if (!triangulationPoints.contains(points[0])) {
					triangulationPoints.add(points[0]);
				}
				// end of line
				if (!triangulationPoints.contains(points[points.length - 1])) {
					triangulationPoints.add(points[points.length - 1]);
				}
			}
			for (final Point[] points : imported.points) {
				for (final Point point : points) {
					triangulationPoints.add(point);
				}
			}
			System.out.println(triangulationPoints.size());
			final Subdivision triangulation = delaunay.DelaunayTriangulation
					.triangulate(triangulationPoints);
			System.out.println(folderName);
			System.out.printf("Offset is %d, %d\n", imported.offsetLatitude,
					imported.offsetLongitude);

			final List<Point[]> simplifiedLines = new LinkedList<Point[]>();
			for (int i = 1; i < imported.arcs.size(); i++) {
				final Point[] arc = imported.arcs.get(i);
				final int arcLength = arc.length;
				if (arcLength < 4) {
					// do not simplify short arcs
					simplifiedLines.add(arc);
				} else {
					// locate edges for each line point
					final Edge[] locatedEdges = new Edge[arc.length];
					for (int j = 0; j < locatedEdges.length; j++) {
						try {
							locatedEdges[j] = triangulation.locate(arc[j]);
						} catch (final DuplicatePointException ignored) {
							// should not be thrown
						}
					}
					// arc startpoints
					final int start = 0; // for now
					// how to find first arc point outside of the ring around
					// arc startPoint?

					// arc endpoints
					final int term = arc.length; // for now
					// how to find a ring around the endpoint?

					final Edge[] edgeStack = new Edge[arc.length + 1];
					final int[] edgeNumberStack = new int[arc.length + 1];
					int sp = 1;
					for (int j = start + 1; j < term; j++) {
						if (locatedEdges[j] != edgeStack[sp]) {
							// if next edge is different from the current
							if (locatedEdges[j] != edgeStack[sp - 1]) {
								// if previous edge is different from the
								// current
								sp++;
								edgeStack[sp] = locatedEdges[j];
								edgeNumberStack[sp] = j;
							} else {
								// eliminate loop bug if path segment crosses
								// several triangles only to return across a
								// different edge than it left
								sp--;
							}
						}
					}
					int[] idx;
					if (sp < 1) {
						idx = new int[] { 0, arcLength - 1 };
						final Point[] simplified = { arc[idx[0]], arc[idx[1]] };
						simplifiedLines.add(simplified);
					} else {
						final Point[] simplified = new Point[sp];
						simplified[0] = arc[0];
						for (int j = 1; j < sp; j++) {
							simplified[j] = arc[edgeNumberStack[j]];
						}
						simplifiedLines.add(simplified);
					}
				}
			}
			ExportGML.exportGML(simplifiedLines, imported.offsetLatitude,
					imported.offsetLongitude, folderName);
		}
	}
}
