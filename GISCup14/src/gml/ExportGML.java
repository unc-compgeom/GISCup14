package gml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import delaunay.Point;

public class ExportGML {

	public static void exportGML(List<Point[]> simplified, long offsetLatitude,
			long offsetLongitude, String folderName) throws IOException {
		FileWriter writer = new FileWriter(new File(folderName
				+ "\\simplified.txt"));
		int lineCount = 1;
		for (Point[] points : simplified) {
			// line header
			writer.write(lineCount++
					+ ":<gml:LineString srsName=\"EPSG:54004\" xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates decimal=\".\" cs=\",\" ts=\" \">");
			for (Point point : points) {
				writer.write((double) (point.getX() + offsetLatitude) + ","
						+ (double) (point.getY() + offsetLongitude) + " ");
			}
			writer.write("</gml:coordinates></gml:LineString>\n");
		}
		writer.close();
	}
}
