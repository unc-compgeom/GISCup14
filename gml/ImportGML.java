package gml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import delaunay.Point;

public class ImportGML {

	public static ArcsPointsAndOffsets importGML(final String pointsFileName,
			final String arcsFileName) throws IOException {

		final List<double[]> doubleArcsCoordinates = readFile(arcsFileName);
		final List<double[]> doublePointsCoordinates = readFile(pointsFileName);

		// find the minimum latitude and longitude from both of these point sets
		// so that we can normalize these points
		double minimumLatitude = doubleArcsCoordinates.get(0)[0];
		double minimumLongitude = doubleArcsCoordinates.get(0)[1];
		// find minimum latitude longitude
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
		for (final double[] coordinateSet : doublePointsCoordinates) {
			for (int i = 0; i < coordinateSet.length; i += 2) {
				if (coordinateSet[i] < minimumLatitude) {
					minimumLatitude = coordinateSet[i];
				}
				if (coordinateSet[i + 1] < minimumLongitude) {
					minimumLongitude = coordinateSet[i + 1];
				}
			}
		}
		final long dX = (long) minimumLatitude;
		final long dY = (long) minimumLongitude;
		// normalize points and arcs
		final List<Point[]> points = new LinkedList<Point[]>();
		final List<Point[]> arcs = new LinkedList<Point[]>();
		for (final double[] doublePoints : doublePointsCoordinates) {
			final Point[] floatPoints = new Point[doublePoints.length / 2];
			points.add(floatPoints);
			for (int i = 0; i < floatPoints.length; i++) {
				floatPoints[i] = new Point(doublePoints[i * 2] - dX,
						doublePoints[i * 2 + 1] - dY);

			}
		}
		for (final double[] doubleArc : doubleArcsCoordinates) {
			final Point[] floatArc = new Point[doubleArc.length / 2];
			arcs.add(floatArc);
			for (int i = 0; i < floatArc.length; i++) {
				floatArc[i] = new Point(doubleArc[i * 2] - dX,
						doubleArc[i * 2 + 1] - dY);
			}
		}
		return new ArcsPointsAndOffsets(arcs, points, dX, dY);
	}

	private static List<double[]> readFile(final String fileName)
			throws IOException {
		final File f = new File(fileName);
		final BufferedReader r = new BufferedReader(new FileReader(f));
		assert r.ready(); // the reader should be ready
		/**
		 * Store the converted coordinates in this array
		 */
		final List<double[]> doubleCoordinates = new LinkedList<double[]>();
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
				// skip characters
			}
			// pass <gml:points...>
			while (r.read() != '>') {
				// skip characters
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
