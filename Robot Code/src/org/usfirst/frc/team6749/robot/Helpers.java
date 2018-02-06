package org.usfirst.frc.team6749.robot;

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
}
