package odev;

import odev.entity.Point;

public class LineIntersection {

	public static boolean areLinesIntersecting(Point l1_p1, Point l1_p2, Point l2_p1, Point l2_p2, boolean shouldIncludeEndPoints) {
		//To avoid floating point precision issues we can add a small value
		float epsilon = 0.00001f;

		boolean isIntersecting = false;

		double denominator = (l2_p2.y - l2_p1.y) * (l1_p2.x - l1_p1.x) - (l2_p2.x - l2_p1.x) * (l1_p2.y - l1_p1.y);

		//Make sure the denominator is > 0, if not the lines are parallel
		if(denominator != 0f){
			double u_a = ((l2_p2.x - l2_p1.x) * (l1_p1.y - l2_p1.y) - (l2_p2.y - l2_p1.y) * (l1_p1.x - l2_p1.x)) / denominator;
			double u_b = ((l1_p2.x - l1_p1.x) * (l1_p1.y - l2_p1.y) - (l1_p2.y - l1_p1.y) * (l1_p1.x - l2_p1.x)) / denominator;

			//Are the line segments intersecting if the end points are the same
			if(shouldIncludeEndPoints){
				//Is intersecting if u_a and u_b are between 0 and 1 or exactly 0 or 1
				if(u_a >= 0f + epsilon && u_a <= 1f - epsilon && u_b >= 0f + epsilon && u_b <= 1f - epsilon){
					isIntersecting = true;
				}
			} else {
				//Is intersecting if u_a and u_b are between 0 and 1
				if(u_a > 0f + epsilon && u_a < 1f - epsilon && u_b > 0f + epsilon && u_b < 1f - epsilon){
					isIntersecting = true;
				}
			}
		}

		return isIntersecting;
	}
}
