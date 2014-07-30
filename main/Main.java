package main;

import mesh.Mesh;

import java.io.IOException;

class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[] { "src\\td1", "src\\td2", "src\\td3" };
		}
		for (final String folderName : args) {
			try {
				Mesh.simplify(folderName);
			} catch (final IOException e) {
				System.err.print("IOException");
				e.printStackTrace();
				return;
			}
		}
	}
}
