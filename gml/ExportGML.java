package gml;

import delaunay.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportGML {

	public static void exportGML(final List<Point[]> simplified,
			final long offsetLatitude, final long offsetLongitude,
			final String folderName) throws IOException {
		final FileWriter writer = new FileWriter(new File(folderName
				+ "\\simplified.txt"));
		int lineCount = 1;
		for (final Point[] points : simplified) {
			// line header
			writer.write(lineCount++
					+ ":<gml:LineString srsName=\"EPSG:54004\" xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates decimal=\".\" cs=\",\" ts=\" \">");
			for (final Point point : points) {
				writer.write((double) (point.x + offsetLatitude) + ","
						+ (double) (point.y + offsetLongitude) + " ");
			}
			writer.write("</gml:coordinates></gml:LineString>\n");
		}
		writer.close();
	}
}
