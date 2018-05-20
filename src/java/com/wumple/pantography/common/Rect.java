package com.wumple.pantography.common;

//a simple rectangle (x1,z1) to (x2,z2)
class Rect {
	public int x1, z1, x2, z2;

	public Rect() {
		x1 = z1 = x2 = z2 = 0;
	}

	public Rect(int _x1, int _z1, int _x2, int _z2) {
		x1 = _x1;
		z1 = _z1;
		x2 = _x2;
		z2 = _z2;
	}

	public String str() {
		return "(" + x1 + "," + z1 + ") (" + x2 + "," + z2 + ")";
	}

	// get intersection of r1 and r2 as Rect, or null if no intersection
	public static Rect intersection(Rect r1, Rect r2) {
		int xmin = Math.max(r1.x1, r2.x1);
		int xmax = Math.min(r1.x2, r2.x2);
		if (xmax > xmin) {
			int zmin = Math.max(r1.z1, r2.z1);
			int zmax = Math.min(r1.z2, r2.z2);
			if (zmax > zmin) {
				return new Rect(xmin, zmin, xmax, zmax);
			}
		}
		return null;
	}
}
