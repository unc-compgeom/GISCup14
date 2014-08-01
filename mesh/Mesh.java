package mesh;

import gml.ArcsPointsAndOffsets;
import gml.ExportGML;
import gml.ImportGML;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import delaunay.DuplicatePointException;
import delaunay.Edge;
import delaunay.Point;
import delaunay.Subdivision;

public class Mesh {
	private static boolean edgeIsPartOfRing(final Edge test,
			final Edge fromOrigin) {
		Edge e = fromOrigin;
		do {
			if (e == test || e.lNext() == test || e.lNext().lNext() == test) {
				return true;
			}
			e = e.oNext();
		} while (e != fromOrigin);
		return false;
	}

	public static void simplify(int pointsToRemove, final String lines,
			String points, String outfile) throws IOException {
		// import points
		final ArcsPointsAndOffsets imported = ImportGML
				.importGML(points, lines);

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
		for (final Point[] pts : imported.points) {
			Collections.addAll(triangulationPoints, pts);
		}
		final Subdivision triangulation = delaunay.DelaunayTriangulation
				.triangulate(triangulationPoints);
		System.out.printf(lines);
		System.out.printf("...done\n");

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

				// do the stacking/popping of triangles to getFirst a sequence
				// of
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
						edgeNumberStack[sp] = j;
						edgeStack[sp++] = locatedEdges[j];

					}
				}
				// eliminate any looping around the start point
				// leave the first point, remove at most up to index sp - 2;
				/**
				 * The index of the last point (inclusive) inside the
				 * triangulation ring around the start point before the path
				 * leaves the ring.
				 */
				int start = 1;
				// for (int j = 2; j < sp - 1; j++) {
				// if (edgeIsPartOfRing(edgeStack[j], edgeStack[0])) {
				// start = j;
				// } else {
				// break;
				// }
				// }
				// eliminate any looping around the end point
				// leave the last point, remove at must up to index 1;
				/**
				 * The index of the last point (inclusive) inside the
				 * triangulation ring around the termination point before the
				 * path leaves the ring.
				 */
				int term = sp - 2;
				// for (int j = term - 1; j > 0; j--) {
				// if (edgeIsPartOfRing(edgeStack[j], edgeStack[sp - 1])) {
				// term = j;
				// } else {
				// break;
				// }
				// }
				if (term < start) {
					term = start;
				}
				if (sp < 1) {
					final Point[] simplified = { arc[0], arc[arc.length - 1] };
					simplifiedArcs.add(simplified);
				} else {
					final int size = term - start + 3;
					int index = 0;
					final Point[] simplified = new Point[size];
					simplified[index++] = arc[0];
					for (int j = start; j <= term; j++) {
						simplified[index++] = arc[edgeNumberStack[j]];
					}
					simplified[index] = arc[arc.length - 1];
					simplifiedArcs.add(simplified);
				}
			}
		}
		ExportGML.exportGML(simplifiedArcs, imported.offsetLatitude,
				imported.offsetLongitude, outfile);
		// statistics
		int originalSize = 0;
		for (final Point[] arc : imported.arcs) {
			originalSize += arc.length;
		}
		int simplifiedSize = 0;
		for (final Point[] arc : simplifiedArcs) {
			simplifiedSize += arc.length;
		}
		System.out.printf("  simplification: %f percent\n",
				(originalSize - simplifiedSize) / (double) originalSize * 100);
	}
}
