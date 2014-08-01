package main;

import java.io.IOException;

import mesh.Mesh;

class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[] { "40000", "src\\td1\\lines_out.txt",
					"src\\td1\\points_out.txt", "src\\td1\\simplified.txt" };
		}
		int pointsToRemove = Integer.parseInt(args[0]);
		String lines = args[1];
		String points = args[2];
		String outfile = args[3];
		try {
			Mesh.simplify(pointsToRemove, lines, points, outfile);
		} catch (final IOException e) {
			System.err.print("IOException");
			e.printStackTrace();
			return;
		}

	}
}
