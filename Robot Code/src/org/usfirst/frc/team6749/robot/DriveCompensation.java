package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveCompensation {
	
	double intensity = 8;
	double cancelThreshold = 70;
	
	double rotationTarget;
	boolean inMove = false;
	
	public void StartMove (double rotation) {
		
		if(inMove == false) {
			rotationTarget = rotation;
		}
		inMove = true;
	}
	
	public void EndMove () {
		inMove = false;
	}
	
	public double ProcessRotation (double inputRotation, double gyroReading) {
		SmartDashboard.putNumber("rotationTarget", rotationTarget);
		if(!inMove) {
			return inputRotation;
		} else {
			//Check if we are too far out and just cancel
			if(Math.abs(rotationTarget - gyroReading) > cancelThreshold) {
				EndMove();
			}
			double compensationAmount = (rotationTarget - gyroReading) / 360 * intensity;
			compensationAmount = Helpers.ClampN11(compensationAmount);
			SmartDashboard.putNumber("C", compensationAmount);
			return inputRotation + compensationAmount;
		}
	}
	
	
}
