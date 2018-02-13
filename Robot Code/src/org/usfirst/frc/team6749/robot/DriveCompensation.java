package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveCompensation {
	
	double gyroIntensity = 5;
	
	double cancelThreshold = 70;
	
	double rotationTarget;
	RobotPosition startPostion;
	
	boolean inMove = false;
	
	public void StartMove (RobotPosition robotPos) {
		
		if(inMove == false) {
			rotationTarget = robotPos.rotation;
			startPostion = robotPos;
		}
		inMove = true;
	}
	
	public void EndMove () {
		inMove = false;
	}
	
	public double ProcessRotation (double inputRotation, RobotPosition currentRobotPos) {
		if(!inMove) {
			return inputRotation;
		} else {
			//Check if we are too far out and just cancel
			if(Math.abs(rotationTarget - currentRobotPos.rotation) > cancelThreshold) {
				EndMove();
			}
			
			double gyroCompensation = (rotationTarget - currentRobotPos.rotation) / 360 * gyroIntensity;
			
			double compensationAmount = gyroCompensation;
			
			compensationAmount = Helpers.ClampN11(compensationAmount);
			
			SmartDashboard.putNumber("Compensation", compensationAmount);
			
			return inputRotation + compensationAmount;
		}
	}
}
