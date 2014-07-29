package mesh;

import gml.ArcsPointsAndOffsets;
import gml.ExportGML;
import gml.ImportGML;

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
			final ArcsPointsAndOffsets imported = ImportGML.importGML(
					folderName + "\\points_out.txt", folderName
							+ "\\lines_out.txt");

			// do triangulation on arc endpoints and all constraint points.
			final List<Point> triangulationPoints = new LinkedList<Point>();

			// take first and last from lines_out for each line, excluding
			// duplicates
			for (final Point[] arc : imported.arcs) {
				// beginning of arc
				if (!triangulationPoints.contains(arc[0])) {
					triangulationPoints.add(arc[0]);
				}
				// end of line
				if (!triangulationPoints.contains(arc[arc.length - 1])) {
					triangulationPoints.add(arc[arc.length - 1]);
				}
			}
			for (final Point[] points : imported.points) {
				for (final Point point : points) {
					triangulationPoints.add(point);
				}
			}
			final Subdivision triangulation = delaunay.DelaunayTriangulation
					.triangulate(triangulationPoints);
			System.out.println(folderName);
			System.out.printf("Offset is %d, %d\n", imported.offsetLatitude,
					imported.offsetLongitude);

			final List<Point[]> simplifiedArcs = new LinkedList<Point[]>();
			for (int i = 0; i < imported.arcs.size(); i++) {
				final Point[] arc = imported.arcs.get(i);
				if (arc.length < 4) {
					// do not simplify short arcs
					simplifiedArcs.add(arc);
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

					// do the stacking/popping of triangles to get a sequence of
					// triangles that the shortest path must visit on its way
					// from start to end

					final Edge[] edgeStack = new Edge[arc.length + 1];
					final int[] edgeNumberStack = new int[arc.length + 1];
					int sp = 0;
					// push the first edge crossed onto the stack
					edgeStack[sp++] = locatedEdges[0];
					// for each subsequent edge
					for (int j = 1; j < arc.length; j++) {
						if (edgeStack[sp - 1].sym() == locatedEdges[j]) {
							// is this edge's reverse on top of the stack?
							// if so, pop it off
							System.out.println("popped edge");
							sp--;
						} else if (edgeStack[sp - 1] == locatedEdges[j]) {
							// is this edge on the top of the stack?
							// do nothing.
						} else {
							// else we're crossing a new edge, so push it onto
							// the stack
							System.out.println("crossed new edge");
							edgeNumberStack[sp] = j;
							edgeStack[sp++] = locatedEdges[j];

						}
					}
					// eliminate any looping around the start point
					// TODO
					if (sp < 1) {
						final Point[] simplified = { arc[0],
								arc[arc.length - 1] };
						simplifiedArcs.add(simplified);
					} else {
						final Point[] simplified = new Point[sp];
						for (int j = 0; j < sp; j++) {
							simplified[j] = arc[edgeNumberStack[j]];
						}
						simplifiedArcs.add(simplified);
					}
				}
			}
			ExportGML.exportGML(simplifiedArcs, imported.offsetLatitude,
					imported.offsetLongitude, folderName);
		}
	}
}
