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

public class ImportGML {
	public static void main(String[] args) {
		try {
			ImportedCoordinates im = importGML("src\\td1\\lines_out.txt");
			System.out.println("Normalized coordinates are:");
			for (float[] f : im.coordinates) {
				for (int i = 0; i < f.length; i += 2) {
					System.out.printf("%16f, %16f\n", f[i], f[i + 1]);
				}
			}
			System.out.printf("Offset is %d, %d", im.offset[0], im.offset[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ImportedCoordinates importGML(String fileName)
			throws IOException {
		File f = new File(fileName);
		BufferedReader r = new BufferedReader(new FileReader(f));
		assert r.ready(); // the reader should be ready

		/**
		 * Size of the buffer in characters.
		 */
		int readLimit = 21;
		/**
		 * Store each line's coordinates in this String queue.
		 */
		Queue<String> stringCoordinates;
		/**
		 * After completing a line, add that line's coordinates to a new array,
		 * store all arrays in this list.
		 */
		List<double[]> doubleCoordinates = new ArrayList<double[]>();

		// read loop
		while (true) {
			// for each line
			// pass <gml:LineString...>
			while (r.read() != '>')
				;
			// pass <gml:coordinates...>
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
		// normalize coordinates
		long dX = (long) (0 - Math.floor(minLatLong[0]));
		long dY = (long) (0 - Math.floor(minLatLong[1]));

		// create a data structure to return
		ImportedCoordinates normalizedCoordinates = new ImportedCoordinates();
		normalizedCoordinates.offset = new long[] { dX, dY };
		normalizedCoordinates.coordinates = new ArrayList<float[]>();

		for (double[] line : doubleCoordinates) {
			float[] floatCoordinate = new float[line.length];
			normalizedCoordinates.coordinates.add(floatCoordinate);
			for (int i = 0; i < line.length; i += 2) {
				floatCoordinate[i] = (float) (line[i] + dX);
				floatCoordinate[i + 1] = (float) (line[i + 1] + dY);
			}
		}
		return normalizedCoordinates;
	}
}

class ImportedCoordinates {
	List<float[]> coordinates;
	long[] offset;
}
