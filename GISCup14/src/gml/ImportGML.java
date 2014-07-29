package gml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import delaunay.Point;
import delaunay.PointComponent;

public class ImportGML {

	public static PointsArcsAndOffsets importGML(final String pointsFileName,
			final String linesFileName) throws IOException {

		final List<double[]> doublePointsCoordinates = readFile(pointsFileName);
		final List<double[]> doubleArcsCoordinates = readFile(linesFileName);
		/**
		 * Keep track of the minimum latitude (in index 0) and longitude (in
		 * index 1) so that we can normalize our results.
		 */
		double minimumLatitude = doublePointsCoordinates.get(0)[0];
		double minimumLongitude = doublePointsCoordinates.get(0)[1];
		// find minimum latitude longitude
		for (final double[] points : doublePointsCoordinates) {
			for (int i = 0; i < points.length; i += 2) {
				if (points[i] < minimumLatitude) {
					minimumLatitude = points[i];
				}
				if (points[i + 1] < minimumLongitude) {
					minimumLongitude = points[i + 1];
				}
			}
		}
		for (final double[] arc : doubleArcsCoordinates) {
			for (int i = 0; i < arc.length; i += 2) {
				if (arc[i] < minimumLatitude) {
					minimumLatitude = arc[i];
				}
				if (arc[i + 1] < minimumLongitude) {
					minimumLongitude = arc[i + 1];
				}
			}
		}
		// normalize points and arcs
		final long dX = (long) minimumLatitude;
		final long dY = (long) minimumLongitude;

		final List<Point[]> points = new ArrayList<Point[]>();
		final List<Point[]> lines = new ArrayList<Point[]>();
		for (final double[] doublePoints : doublePointsCoordinates) {
			final Point[] floatPoints = new Point[doublePoints.length / 2];
			points.add(floatPoints);
			for (int i = 0; i < doublePoints.length; i += 2) {
				floatPoints[i / 2] = new PointComponent(
						(float) (doublePoints[i] - dX),
						(float) (doublePoints[i + 1] - dY));
			}
		}
		for (final double[] doubleArc : doubleArcsCoordinates) {
			final Point[] floatArc = new Point[doubleArc.length / 2];
			points.add(floatArc);
			for (int i = 0; i < doubleArc.length; i += 2) {
				floatArc[i / 2] = new PointComponent(
						(float) (doubleArc[i] - dX) / 32,
						(float) (doubleArc[i + 1] - dY) / 32);
			}
		}
		return new PointsArcsAndOffsets(points, lines, dX, dY);
	}

	private static List<double[]> readFile(String fileName) throws IOException {
		final File f = new File(fileName);
		final BufferedReader r = new BufferedReader(new FileReader(f));
		assert r.ready(); // the reader should be ready
		/**
		 * Store the converted coordinates in this array
		 */
		List<double[]> doubleCoordinates = new LinkedList<double[]>();
		/**
		 * Store each line's unprocessed coordinates in this String queue.
		 */
		Queue<String> stringCoordinates;
		/**
		 * Size of the buffer in characters.
		 */
		final int readLimit = 21;

		// read loop
		while (true) {
			// for each line
			// pass <gml:LineString...>
			while (r.read() != '>') {
				;
			}
			// pass <gml:points...>
			while (r.read() != '>') {
				;
			}
			stringCoordinates = new LinkedList<String>();
			String coordinate;
			int read;
			while (true) {
				coordinate = "";
				// first coordinate
				// handle first character differently
				read = r.read();
				if (read == '<') {
					break;
				} else {
					coordinate += (char) read;
				}
				for (int i = 1; i < readLimit; i++) {
					read = r.read();
					if (read == ',') {
						break;
					}
					coordinate += (char) read;
				}
				stringCoordinates.add(coordinate);
				coordinate = "";
				// second coordinate
				for (int i = 0; i < readLimit; i++) {
					read = r.read();
					if (read == ' ') {
						break;
					}
					coordinate += (char) read;
				}
				stringCoordinates.add(coordinate);
			}
			// process readCoordinates
			final double[] processedCoordinates = new double[stringCoordinates
					.size()];
			final Iterator<String> it = stringCoordinates.iterator();
			for (int i = 0; i < processedCoordinates.length; i++) {
				processedCoordinates[i] = Double.parseDouble(it.next());
			}
			doubleCoordinates.add(processedCoordinates);
			// finish the line
			r.readLine();
			if (r.read() == -1) {
				break;
			}
		}
		r.close();
		return doubleCoordinates;
	}
}
