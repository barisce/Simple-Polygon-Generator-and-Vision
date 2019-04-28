package odev.entity;

import java.util.List;

public final class Point implements Comparable<Point> {

	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return String.format("Point(%g, %g)", x, y);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Point))
			return false;
		else {
			Point other = (Point)obj;
			return x == other.x && y == other.y;
		}
	}

	public int compareTo(Point other) {
		if (x != other.x)
			return Double.compare(x, other.x);
		else
			return Double.compare(y, other.y);
	}

	public int getXInt(Point point) {
		return (int)point.x;
	}

	public int getYInt(Point point) {
		return (int)point.y;
	}

	public boolean contains(List<Point> points, Point newPoint) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
			if ((points.get(i).y > newPoint.y) != (points.get(j).y > newPoint.y) &&
					(newPoint.x < (points.get(j).x - points.get(i).x) * (newPoint.y - points.get(i).y) / (points.get(j).y-points.get(i).y) + points.get(i).x)) {
				result = !result;
			}
		}
		return result;
	}

}