package org.usfirst.frc.team6749.robot;


public class Auto {
	
	DriveController dc;
	
	public enum AutoState {Idle, DriveToLocation, WaypointDrive};
	
	double positionalAccuracy = 0.04; //Within 4 cm
	double rotationalAccuracy = 3; //Within 3 degrees
	double autoMoveSpeed = 0.5; //Max speed when auto driving
	double autoRotateSpeed = 0.5; //Max rotation when auto driving.
	
	AutoState autoState = AutoState.Idle;
	
	public Auto (DriveController driveController) {
		dc = driveController;
	}
	
	//This class gets called to keep the auto actually functioning
	public void AutoPeriodic (RobotPosition robotPosition) {
		if(autoState == AutoState.DriveToLocation) {
			DriveToLocationPeriodic (robotPosition);
		}
		if(autoState == AutoState.WaypointDrive) {
			WaypointDrivePeriodic (robotPosition);
		}
	}
	
	public void AutoDriveToLocation (RobotPosition targetPosition) {
		autoState = AutoState.DriveToLocation;
	}
	
	private void DriveToLocationPeriodic (RobotPosition robotPosition) {
		//We are trying to drive to a position
		//We need to turn to face the location then drive there.
		//So we need to calcuate the degrees to look at that location
		//TODO
		
	}
	
	private void WaypointDrivePeriodic (RobotPosition robotPosition) {
		
	}
}