package main;

import java.io.IOException;

import mesh.Mesh;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[] { "src\\td1", "src\\td2", "src\\td3" };
		}
		for (final String folderName : args) {
			try {
				Mesh.simplify(folderName);
			} catch (IOException e) {
				System.err.print("IOException");
				e.printStackTrace();
				return;
			}
		}
	}
}
