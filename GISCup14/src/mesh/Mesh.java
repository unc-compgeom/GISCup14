package mesh;

import importer.ImportGML;
import importer.PointsAndOffset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import delaunay.Point;

public class Mesh {
	public static void main(String[] args) {
		args = new String[] { // "src\\td1\\lines_out.txt",
		"src\\td1\\points_out.txt", // "src\\td2\\lines_out.txt",
				"src\\td2\\points_out.txt", // "src\\td3\\lines_out.txt",
				"src\\td3\\points_out.txt" };
		for (String string : args) {
			try {
				PointsAndOffset im = ImportGML.importGML(string);
				System.out.println("Normalized points are:");
				for (Point[] f : im.points) {
					for (int i = 0; i < f.length; i += 2) {
						System.out.println(f[i]);
					}
				}

				// remove duplicates from lines_out
				// take first and last from lines_out for each line

				List<Point> p = new ArrayList<Point>();
				for (Point[] points : im.points) {
					for (Point pt : points) {
						p.add(pt);
					}
				}

				delaunay.DelaunayTriangulation.triangulate(p);

				System.out.printf("Offset is %d, %d\n", im.offset[0],
						im.offset[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
