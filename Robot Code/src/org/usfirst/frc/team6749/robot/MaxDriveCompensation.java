package org.usfirst.frc.team6749.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MaxDriveCompensation {
	
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
			
			
			
			return 0;
		}
	}
	
	public double Maxx (double inputRotation, RobotPosition currentPos) {
		
		double dy = Math.tan((startPostion.GetRotationCyclic()));
		double dx;
		
		return 0;
	}
}
