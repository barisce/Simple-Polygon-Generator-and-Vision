package odev.entity;

public final class Edge {

	public Point p;
	public Point q;
	public int pIndex;
	public int qIndex;

	public Edge (Point x, Point y) {
		this.p = x;
		this.q = y;
	}

	public Edge (Point x, Point y, int pIndex, int qIndex) {
		this.p = x;
		this.q = y;
		this.pIndex = pIndex;
		this.qIndex = qIndex;
	}

}