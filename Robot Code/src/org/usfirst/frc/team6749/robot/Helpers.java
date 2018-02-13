package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Helpers {
	static double ClampN11 (double d) {
		if(d < -1) {
			d = -1;
		}
		if(d > 1) {
			d = 1;
		}
		return d;
	}
	
	static double CalculateRotationBetweenPoints (Position pos1, Position pos2) {
		
		double offsetX = pos2.x - pos1.x;
		double offsetY = pos2.y - pos1.y;
		double i = 0;
		
		if(offsetY != 0) {
			i = Math.toDegrees(Math.atan(offsetX / offsetY));
		} else {
			if(offsetX > 0) {
				i = 90;
			}
			if(offsetX < 0) {
				i = -90;
			}
			if(offsetX == 0) {
				i = 0;
			}
			return i;
		}
		
		//At this point we know we are not at a specific direction.
		if(offsetX >= 0 && offsetY > 0) {
			return i;
		}
		if(offsetX > 0 && offsetY < 0) {
			return 180 + i;
		}
		if(offsetX < 0 && offsetY < 0) {
			return -180 + i;
		}
		if(offsetX < 0 && offsetY > 0) {
			return i;
		}
		//This should NEVER happen
		return 0;
	}
	
	static double Convert180to360 (double value) {
		if(value >= 0) {
			//It will be between 0 and 180
			return value;
		}
		if(value < 0) {
			//it will be between <-0 and >-179
			return 360 - Math.abs(value);
		}
		//This should never happen
		return 0;
	}
	
	public static double PositiveOrNegitive (double number) {
		if(number > 0) {
			return 1;
		}
		if(number < 0) {
			return -1;
		}
		return 1;
	}
}

class Position {
	double x, y;
	
	public Position (double x, double y) {
		this.x = x;
		this.y = y;
	}
}