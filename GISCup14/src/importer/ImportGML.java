package importer;

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

	public static PointsAndOffset importGML(String fileName) throws IOException {
		File f = new File(fileName);
		BufferedReader r = new BufferedReader(new FileReader(f));
		assert r.ready(); // the reader should be ready

		/**
		 * Size of the buffer in characters.
		 */
		int readLimit = 21;
		/**
		 * Store each line's points in this String queue.
		 */
		Queue<String> stringCoordinates;
		/**
		 * After completing a line, add that line's points to a new array, store
		 * all arrays in this list.
		 */
		List<double[]> doubleCoordinates = new ArrayList<double[]>();

		// read loop
		while (true) {
			// for each line
			// pass <gml:LineString...>
			while (r.read() != '>')
				;
			// pass <gml:points...>
			while (r.read() != '>')
				;
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
					if (read == ',')
						break;
					coordinate += (char) read;
				}
				stringCoordinates.add(coordinate);
				coordinate = "";
				// second coordinate
				for (int i = 0; i < readLimit; i++) {
					read = r.read();
					if (read == ' ')
						break;
					coordinate += (char) read;
				}
				stringCoordinates.add(coordinate);
			}
			// process readCoordinates
			double[] processedCoordinates = new double[stringCoordinates.size()];
			Iterator<String> it = stringCoordinates.iterator();
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

		/**
		 * Keep track of the minimum latitude (in index 0) and longitude (in
		 * index 1) so that we can normalize our results.
		 */
		double[] minLatLong = new double[2];
		// initialize
		minLatLong[0] = doubleCoordinates.get(0)[0];
		minLatLong[1] = doubleCoordinates.get(0)[1];
		// find minimum latitude longitude
		for (double[] line : doubleCoordinates) {
			for (int i = 0; i < line.length; i += 2) {
				if (line[i] < minLatLong[0]) {
					minLatLong[0] = line[i];
				}
				if (line[i + 1] < minLatLong[1]) {
					minLatLong[1] = line[i + 1];
				}
			}
		}
		// normalize points
		long dX = (long) (0 - Math.floor(minLatLong[0]));
		long dY = (long) (0 - Math.floor(minLatLong[1]));

		// create a data structure to return
		PointsAndOffset normalizedCoordinates = new PointsAndOffset();
		normalizedCoordinates.offset = new long[] { dX, dY };
		normalizedCoordinates.points = new ArrayList<Point[]>();

		for (double[] line : doubleCoordinates) {
			Point[] points = new Point[line.length / 2];
			normalizedCoordinates.points.add(points);
			for (int i = 0; i < line.length; i += 2) {
				points[i / 2] = new PointComponent((float) (line[i] + dX),
						(float) (line[i + 1] + dY));
			}
		}
		return normalizedCoordinates;
	}
}
