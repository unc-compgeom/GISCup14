package gml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import delaunay.Point;

public class ExportGML {

	public static void exportGML(final List<Point[]> simplified,
			final long offsetLatitude, final long offsetLongitude,
			final String outfile) throws IOException {
		final FileWriter writer = new FileWriter(new File(outfile));
		int lineCount = 1;
		for (final Point[] points : simplified) {
			// line header
			writer.write(lineCount++
					+ ":<gml:LineString srsName=\"EPSG:54004\" xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates decimal=\".\" cs=\",\" ts=\" \">");
			for (final Point point : points) {
				writer.write(point.x + offsetLatitude + ","
						+ (point.y + offsetLongitude) + " ");
			}
			writer.write("</gml:coordinates></gml:LineString>\n");
		}
		writer.close();
	}
}
