package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GPS {

	//Global positions
	double x;
	double y;
	
	double xG;
	double yG;
	
	double rot;
	
	double accelInputX;
	double accelInputY;
	double accelOldX;
	double accelOldY;
	
	void SubmitGyroData (double rotation) {
		rot = Math.round((rotation * 10)) / 10f;
	}
	
	void SubmitAccelerometerData (double x, double y) {
		accelOldX = (x*0.2) + (0.8*accelOldX);
		accelOldY = (y*0.2) + (0.8*accelOldY);
	}
	
	public void Calculate () {
		xG += Math.round(accelOldX * 100) / 100;
		yG += Math.round(accelOldY * 100) / 100;
		
		x = xG;
		y = yG;
		
		SmartDashboard.putNumber("GPS X", x);
		SmartDashboard.putNumber("GPS Y", y);
		SmartDashboard.putNumber("GPS Rot", rot);
	}
	
}
